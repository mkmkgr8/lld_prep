package parkinglot;

import java.time.Instant;

/** Change axis: pricing (hourly, flat, weekend, surge...). */
public interface PricingStrategy {
    Money price(Ticket ticket, Instant exitTime);
}
