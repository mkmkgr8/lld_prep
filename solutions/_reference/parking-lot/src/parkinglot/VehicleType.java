package parkinglot;

/** Size ordering lets a smaller vehicle use a larger spot without if-else chains. */
public enum VehicleType {
    BIKE(1), CAR(2), TRUCK(3);

    private final int size;

    VehicleType(int size) { this.size = size; }

    public boolean fitsIn(VehicleType spotType) { return size <= spotType.size; }
}
