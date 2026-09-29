package parkinglot;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/** Per started hour, minimum one hour, rate by vehicle type. */
public final class HourlyPricing implements PricingStrategy {
    private final Map<VehicleType, Money> ratePerHour;

    public HourlyPricing(Map<VehicleType, Money> ratePerHour) {
        this.ratePerHour = new EnumMap<>(ratePerHour);
        for (VehicleType t : VehicleType.values()) {
            if (!this.ratePerHour.containsKey(t)) throw new IllegalArgumentException("missing rate for " + t);
        }
    }

    @Override
    public Money price(Ticket ticket, Instant exitTime) {
        long minutes = Duration.between(ticket.entryTime(), exitTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);
        return ratePerHour.get(ticket.vehicle().type()).times(hours);
    }
}
