package parkinglot;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import parkinglot.ParkingExceptions.ParkingFullException;
import parkinglot.ParkingExceptions.TicketNotFoundException;
import parkinglot.ParkingExceptions.VehicleAlreadyParkedException;

/** Orchestrator only: allocation and pricing are delegated, time is injected. */
public final class ParkingLot {
    private final List<ParkingSpot> spots;
    private final SpotAllocationStrategy allocation;
    private final PricingStrategy pricing;
    private final Clock clock;
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
    private final Set<String> parkedPlates = ConcurrentHashMap.newKeySet();

    public ParkingLot(List<ParkingSpot> spots, SpotAllocationStrategy allocation,
                      PricingStrategy pricing, Clock clock) {
        this.spots = List.copyOf(spots);
        this.allocation = allocation;
        this.pricing = pricing;
        this.clock = clock;
    }

    public Ticket park(Vehicle vehicle) {
        if (!parkedPlates.add(vehicle.plate())) throw new VehicleAlreadyParkedException(vehicle.plate());
        try {
            ParkingSpot spot = allocation.candidates(spots, vehicle)
                    .filter(s -> s.tryOccupy(vehicle))   // CAS; losers of a race move to the next candidate
                    .findFirst()
                    .orElseThrow(() -> new ParkingFullException(vehicle));
            Ticket ticket = new Ticket(UUID.randomUUID().toString(), vehicle, spot, clock.instant());
            activeTickets.put(ticket.id(), ticket);
            return ticket;
        } catch (RuntimeException e) {
            parkedPlates.remove(vehicle.plate());
            throw e;
        }
    }

    public Money unpark(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);   // atomic: a ticket can be redeemed only once
        if (ticket == null) throw new TicketNotFoundException(ticketId);
        Instant exit = clock.instant();
        ticket.spot().release();
        parkedPlates.remove(ticket.vehicle().plate());
        return pricing.price(ticket, exit);
    }

    public long freeSpots() { return spots.stream().filter(ParkingSpot::isFree).count(); }
}
