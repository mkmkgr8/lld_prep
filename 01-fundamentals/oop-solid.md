# OOP + SOLID: The Interview-Relevant Parts Only

## OOP pillars: what interviewers actually check
| Pillar | What they look for in your code |
|---|---|
| Encapsulation | Private fields, no public setters on things that shouldn't change, state changes only through intention-revealing methods (`booking.cancel()`, not `booking.setStatus(CANCELLED)`) |
| Abstraction | Callers depend on `PaymentProcessor`, not `RazorpayProcessor` |
| Inheritance | Used **sparingly**. Prefer composition. Inheritance only for true is-a with shared behaviour. |
| Polymorphism | Replacing `if (type == X) ... else if (type == Y)` chains with overridden methods or strategies |

**Composition over inheritance** example: `Vehicle` shouldn't extend `ParkingBehaviour`. A `ParkingSpot` *has* a `SpotType` and a `canFit(Vehicle)` rule.

## SOLID, with the violation you'll be caught on

### S: Single Responsibility
One reason to change.
- ❌ `ParkingLot` that parks cars, computes fees, prints tickets and sends SMS.
- ✅ `ParkingLot` (orchestrates), `FeeCalculator`, `TicketPrinter`, `NotificationService`.

### O: Open/Closed
Add behaviour by adding classes, not editing them.
```java
// ❌
double fee(Vehicle v) {
    if (v.type() == CAR) return 20;
    else if (v.type() == BIKE) return 10;   // every new type edits this method
}
// ✅
interface PricingStrategy { Money price(Ticket t, Instant exit); }
class HourlyPricing implements PricingStrategy { ... }
class FlatWeekendPricing implements PricingStrategy { ... }
```

### L: Liskov Substitution
A subtype must be usable wherever its parent is, without surprises.
- ❌ `Square extends Rectangle` where `setWidth` also changes height.
- ❌ `ReadOnlyAccount extends Account` whose `withdraw()` throws `UnsupportedOperationException`.
- ✅ Split the interfaces: `Readable`, `Withdrawable`.

### I: Interface Segregation
Small, role-based interfaces.
- ❌ `interface Machine { print(); scan(); fax(); }`, which forces `SimplePrinter` to stub out `fax()`.
- ✅ `Printer`, `Scanner`, `Fax`.

### D: Dependency Inversion
High-level modules depend on abstractions, and dependencies are injected through the constructor.
```java
class OrderService {
    private final PaymentGateway gateway;          // interface
    private final OrderRepository repo;            // interface
    OrderService(PaymentGateway g, OrderRepository r) { this.gateway = g; this.repo = r; }
}
```
In interviews, **constructor injection + interfaces** is enough. No Spring.

## Other principles worth naming out loud
- **DRY**, **KISS**, **YAGNI**. YAGNI is your defence against over-engineering. Say it when you *don't* add a pattern.
- **Tell, don't ask**: `account.debit(amt)` instead of `if (account.getBalance() >= amt) account.setBalance(...)`.
- **Law of Demeter**: avoid `order.getCustomer().getAddress().getCity()` chains in business logic.
- **Fail fast**: validate in constructors and factory methods, and throw domain exceptions.

## Entity vs Value Object vs Service
| Kind | Identity? | Mutable? | Example |
|---|---|---|---|
| Entity | Yes (id) | Yes, via methods | `Booking`, `User`, `ParkingSpot` |
| Value object | No, equal by value | **No**, use a `record` | `Money`, `Location`, `TimeSlot` |
| Service | No | Stateless-ish | `BookingService`, `PricingStrategy` |
| Repository | No | Holds the store | `InMemoryBookingRepository` |

Using `record Money(long paise, Currency c)` instead of `double amount` is a quiet but strong signal.
