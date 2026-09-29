package parkinglot;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    /** Test clock we can advance: why Clock is injected instead of calling Instant.now(). */
    static final class ManualClock extends Clock {
        private volatile Instant now = Instant.parse("2026-01-01T10:00:00Z");
        void advance(Duration d) { now = now.plus(d); }
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    public static void main(String[] args) throws InterruptedException {
        ManualClock clock = new ManualClock();
        List<ParkingSpot> spots = List.of(
                new ParkingSpot("F0-B1", 0, VehicleType.BIKE),
                new ParkingSpot("F0-C1", 0, VehicleType.CAR),
                new ParkingSpot("F1-C1", 1, VehicleType.CAR),
                new ParkingSpot("F1-T1", 1, VehicleType.TRUCK));
        PricingStrategy pricing = new HourlyPricing(Map.of(
                VehicleType.BIKE, Money.rupees(10),
                VehicleType.CAR, Money.rupees(30),
                VehicleType.TRUCK, Money.rupees(80)));
        ParkingLot lot = new ParkingLot(spots, new LowestFloorBestFit(), pricing, clock);

        // 1. Happy path + pricing
        Ticket t = lot.park(new Vehicle("KA01AB1234", VehicleType.CAR));
        System.out.println("Parked at " + t.spot().id());               // F0-C1 (lowest floor, best fit)
        clock.advance(Duration.ofMinutes(125));
        System.out.println("Fee: " + lot.unpark(t.id()));                // 3 started hours × ₹30 = ₹90.00

        // 2. Double redemption rejected
        try { lot.unpark(t.id()); }
        catch (ParkingExceptions.TicketNotFoundException e) { System.out.println("OK: " + e.getMessage()); }

        // 3. Concurrency: 20 cars race for 3 car-capable spots (C, C, T) -> exactly 3 win
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger parked = new AtomicInteger(), rejected = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            Vehicle v = new Vehicle("CAR-" + i, VehicleType.CAR);
            pool.submit(() -> {
                try { start.await(); lot.park(v); parked.incrementAndGet(); }
                catch (ParkingExceptions.ParkingFullException e) { rejected.incrementAndGet(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.printf("Concurrent: parked=%d rejected=%d freeSpots=%d%n",
                parked.get(), rejected.get(), lot.freeSpots());          // 3, 17, 1 (bike spot)
    }
}
