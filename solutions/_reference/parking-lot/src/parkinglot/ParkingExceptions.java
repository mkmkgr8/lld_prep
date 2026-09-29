package parkinglot;

public final class ParkingExceptions {
    private ParkingExceptions() {}

    public static class ParkingFullException extends RuntimeException {
        public ParkingFullException(Vehicle v) { super("No spot available for " + v); }
    }

    public static class TicketNotFoundException extends RuntimeException {
        public TicketNotFoundException(String id) { super("Unknown or already-used ticket " + id); }
    }

    public static class VehicleAlreadyParkedException extends RuntimeException {
        public VehicleAlreadyParkedException(String plate) { super("Already parked: " + plate); }
    }
}
