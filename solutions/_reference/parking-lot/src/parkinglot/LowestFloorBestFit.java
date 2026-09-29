package parkinglot;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Lowest floor first; within a floor prefer the smallest spot that fits (saves big spots for trucks). */
public final class LowestFloorBestFit implements SpotAllocationStrategy {
    @Override
    public Stream<ParkingSpot> candidates(List<ParkingSpot> spots, Vehicle vehicle) {
        return spots.stream()
                .filter(s -> s.isFree() && s.canFit(vehicle))
                .sorted(Comparator.comparingInt(ParkingSpot::floor)
                        .thenComparing(ParkingSpot::spotType));
    }
}
