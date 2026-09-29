package parkinglot;

import java.util.List;
import java.util.stream.Stream;

/** Change axis: how we pick a spot (nearest, best-fit, EV-first, VIP floor...). */
public interface SpotAllocationStrategy {
    /** Candidate spots in preference order. The lot tries each until a CAS succeeds. */
    Stream<ParkingSpot> candidates(List<ParkingSpot> spots, Vehicle vehicle);
}
