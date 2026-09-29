package parkinglot;

import java.time.Instant;

public record Ticket(String id, Vehicle vehicle, ParkingSpot spot, Instant entryTime) {}
