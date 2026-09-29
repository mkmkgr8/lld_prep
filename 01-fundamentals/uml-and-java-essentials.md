# UML (just enough) + Java Essentials for LLD

## UML: what to draw in an interview
Only the **class diagram**, and quickly. Skip sequence diagrams unless asked.

```
┌──────────────┐        1     *  ┌──────────────┐
│ ParkingLot   │◆───────────────▶│ ParkingFloor │
├──────────────┤                 └──────────────┘
│ park(v)      │                        ◆ 1..*
│ unpark(t)    │                        ▼
└──────────────┘                 ┌──────────────┐
       │ uses                    │ ParkingSpot  │
       ▼                         └──────────────┘
┌────────────────────┐
│ «interface»        │
│ PricingStrategy    │◁─ ─ ─ HourlyPricing
└────────────────────┘
```
| Arrow | Meaning | Java |
|---|---|---|
| `◆──` composition | Part can't exist without the whole | Field created and owned inside |
| `◇──` aggregation | Part can exist independently | Field passed in |
| `──▶` association | Uses / knows about | Field reference |
| `◁──` (solid) inheritance | extends | `extends` |
| `◁ ─ ─` (dashed) realisation | implements | `implements` |
| `- - ->` dependency | Uses temporarily | Method param or local variable |

## Java toolkit you must write fluently (no IDE)

```java
// Enum with behaviour: replaces switch statements
enum VehicleType {
    BIKE(1), CAR(2), TRUCK(4);
    private final int size;
    VehicleType(int size) { this.size = size; }
    int size() { return size; }
}

// Record = immutable value object (Java 16+)
record Money(long paise) {
    Money { if (paise < 0) throw new IllegalArgumentException("negative"); }
    Money plus(Money o) { return new Money(paise + o.paise); }
}

// Sealed hierarchy (Java 17): closed set of types, exhaustive switch
sealed interface Command permits Park, Unpark {}
record Park(String plate) implements Command {}
record Unpark(String ticketId) implements Command {}

// Custom exception
class SpotNotAvailableException extends RuntimeException {
    SpotNotAvailableException(String msg) { super(msg); }
}

// Optional for "may not exist": never return null from repositories
Optional<Booking> findById(String id);

// In-memory repository
class InMemoryBookingRepo implements BookingRepo {
    private final Map<String, Booking> store = new ConcurrentHashMap<>();
    public void save(Booking b) { store.put(b.id(), b); }
    public Optional<Booking> findById(String id) { return Optional.ofNullable(store.get(id)); }
}

// ID generation
UUID.randomUUID().toString();
new AtomicLong().incrementAndGet();

// Time
Instant.now(); Duration.between(a, b).toMinutes(); LocalDateTime; Clock (inject it so time is testable!)
```

### Collections cheat-sheet for LLD
| Need | Use |
|---|---|
| Lookup by id | `HashMap` / `ConcurrentHashMap` |
| Ordered by key (time, price) | `TreeMap` / `ConcurrentSkipListMap` (`floorKey`, `ceilingEntry`) |
| Priority (nearest spot, earliest expiry) | `PriorityQueue` / `PriorityBlockingQueue` |
| LRU | `LinkedHashMap(cap, 0.75f, true)` + `removeEldestEntry`, **or** HashMap + doubly linked list (they'll ask you to build it by hand) |
| FIFO queue between threads | `ArrayBlockingQueue` / `LinkedBlockingQueue` |
| Set with order | `LinkedHashSet` / `TreeSet` |
| Unmodifiable view | `List.copyOf`, `Collections.unmodifiableList` |

**Pro move:** inject `java.time.Clock` into anything time-based (pricing, expiry, rate limiters). It shows testability awareness.
