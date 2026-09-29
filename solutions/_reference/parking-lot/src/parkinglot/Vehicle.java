package parkinglot;

import java.util.Objects;

public record Vehicle(String plate, VehicleType type) {
    public Vehicle {
        Objects.requireNonNull(type, "type");
        if (plate == null || plate.isBlank()) throw new IllegalArgumentException("plate required");
    }
}
