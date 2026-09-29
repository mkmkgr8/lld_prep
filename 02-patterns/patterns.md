# Design Patterns That Actually Show Up in LLD Rounds

Ranked by how often they appear. **Tier 1 = must write from memory in under 2 minutes.**

## Tier 1 (used in ~80% of problems)

### 1. Strategy: swap an algorithm at runtime
**Trigger words:** "pricing varies", "different algorithms", "pluggable rules", "payment methods".
```java
interface SplitStrategy { Map<User, Money> split(Money total, List<User> users, List<Double> params); }
class EqualSplit implements SplitStrategy { ... }
class PercentSplit implements SplitStrategy { ... }
class ExpenseService {
    Expense add(Money amt, User payer, List<User> users, SplitStrategy s, List<Double> p) { ... }
}
```
Seen in: Splitwise, Parking fees, Elevator scheduling, Rate limiter algorithms, Cab matching.

### 2. Observer: notify many parties on an event
**Trigger:** "notify users", "subscribers", "when X happens, update Y".
```java
interface EventListener<E> { void on(E event); }
class EventBus<E> {
    private final List<EventListener<E>> listeners = new CopyOnWriteArrayList<>();
    void subscribe(EventListener<E> l) { listeners.add(l); }
    void publish(E e) { listeners.forEach(l -> l.on(e)); }
}
```
Seen in: Stock price alerts, Notification systems, Auction bidding, Pub-sub.

### 3. Factory (simple factory or factory method): hide creation logic
**Trigger:** "create X based on type".
```java
class VehicleFactory {
    static Vehicle create(VehicleType t, String plate) {
        return switch (t) {
            case CAR -> new Car(plate);
            case BIKE -> new Bike(plate);
            case TRUCK -> new Truck(plate);
        };
    }
}
```

### 4. State: behaviour changes with internal state
**Trigger:** "vending machine", "order lifecycle", "ATM", "elevator states".
```java
interface VendingState {
    void insertCoin(VendingMachine m, Money c);
    void selectItem(VendingMachine m, String code);
    void dispense(VendingMachine m);
}
class IdleState implements VendingState { ... }       // selectItem → throw
class HasMoneyState implements VendingState { ... }
class DispensingState implements VendingState { ... }
class VendingMachine { private VendingState state; void setState(VendingState s) { state = s; } }
```
**Alternative for simple lifecycles:** an enum state machine with an allowed-transitions map. Mention the trade-off.

### 5. Singleton: one instance (use sparingly, be ready to criticise it)
```java
// Best: enum singleton (thread-safe, serialization-safe)
enum IdGenerator { INSTANCE; private final AtomicLong c = new AtomicLong(); long next() { return c.incrementAndGet(); } }

// Or the holder idiom
class Config {
    private Config() {}
    private static class Holder { static final Config I = new Config(); }
    static Config get() { return Holder.I; }
}
```
**SDE-3 answer:** "I'd avoid Singleton for services. It hides dependencies and hurts testability. I'd create one instance in `Main` and inject it."

### 6. Builder: many optional params and immutability
```java
Pizza p = new Pizza.Builder(Size.LARGE).cheese().topping("olive").build();
```
Seen in: Order creation, Query builders, Notification messages. Records + static factories often suffice in interviews.

## Tier 2 (appears in specific problems)

| Pattern | One-liner | Classic problem |
|---|---|---|
| **Chain of Responsibility** | Pass a request along handlers until one handles it | ATM cash dispenser (2000→500→100), Logger levels, Request validation/middleware, Approval workflow |
| **Decorator** | Wrap to add behaviour | Pizza/coffee toppings pricing, Stream wrappers, adding logging/caching around a service |
| **Command** | A request as an object (undo/redo, queueing) | Text editor undo, Remote control, Job scheduler |
| **Adapter** | Make an incompatible interface fit | Third-party payment gateways, legacy APIs |
| **Facade** | Simple API over a subsystem | `BookingFacade` over seat + payment + notification |
| **Template Method** | Fixed algorithm skeleton, variable steps | Game turn flow, report generation |
| **Composite** | Tree of uniform nodes | File system (File/Directory), Org chart, Menu |
| **Proxy** | Control access (lazy, cache, auth) | Caching proxy, rate-limited API client |
| **Iterator** | Traverse without exposing internals | Custom collections |
| **Flyweight** | Share intrinsic state | Chess pieces, text editor characters |
| **Visitor** | Add operations to a stable hierarchy | Rarely asked. Know the name only. |

## "Which pattern?" decision table
| You hear… | Reach for |
|---|---|
| "different ways to compute / choose" | Strategy |
| "notify / subscribe / alerts" | Observer |
| "lifecycle / status transitions with different allowed actions" | State (or enum FSM) |
| "sequence of checks / fallthrough / denominations" | Chain of Responsibility |
| "add-ons that stack" | Decorator |
| "undo / redo / queue operations" | Command |
| "tree / folder / nested" | Composite |
| "third-party with a different API" | Adapter |
| "complex object with many optional fields" | Builder |

## Anti-patterns interviewers penalise
- A pattern with no stated change axis (**pattern-itis**).
- Singletons everywhere, hiding dependencies.
- A `Manager` / `Util` god class.
- `instanceof` chains, a sign that polymorphism is missing.
