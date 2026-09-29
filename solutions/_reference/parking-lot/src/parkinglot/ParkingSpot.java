package parkinglot;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Occupancy is a CAS on an AtomicReference: two threads racing for the same spot
 * cannot both win, and unrelated spots never contend (no global lock).
 */
public final class ParkingSpot {
    private final String id;
    private final int floor;
    private final VehicleType spotType;
    private final AtomicReference<Vehicle> occupant = new AtomicReference<>();

    public ParkingSpot(String id, int floor, VehicleType spotType) {
        this.id = id;
        this.floor = floor;
        this.spotType = spotType;
    }

    boolean tryOccupy(Vehicle v) {
        return v.type().fitsIn(spotType) && occupant.compareAndSet(null, v);
    }

    void release() { occupant.set(null); }

    public boolean isFree() { return occupant.get() == null; }

    public boolean canFit(Vehicle v) { return v.type().fitsIn(spotType); }

    public String id() { return id; }

    public int floor() { return floor; }

    public VehicleType spotType() { return spotType; }
}
