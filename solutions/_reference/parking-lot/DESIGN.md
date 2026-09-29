# Parking Lot: Reference Solution (target: SDE-3 bar)

Study this in **Week 1**, then delete your memory of it and rebuild it from a blank file.

## Run
```bash
cd ~/lld-prep/solutions/_reference/parking-lot
javac -d out src/parkinglot/*.java && java -cp out parkinglot.Main
```
Expected output:
```
Parked at F0-C1
Fee: ₹90.00
OK: Unknown or already-used ticket <uuid>
Concurrent: parked=3 rejected=17 freeSpots=1
```

## 1. Clarifying questions (what a strong candidate asks)
- Vehicle types? → Bike, Car, Truck. Can a smaller vehicle take a bigger spot? → Yes.
- Multiple floors, multiple entry gates? → Yes to both, **so concurrency matters**.
- Pricing? → Hourly by vehicle type, charged per started hour. Will this change? → Likely (weekend, surge).
- Payment, display boards, reservations? → Out of scope. Keep them extensible.
- Persistence? → In-memory.

## 2. Model
| Class | Kind | Why it exists |
|---|---|---|
| `VehicleType` | enum with behaviour | `fitsIn()` kills the if-else chain on sizes |
| `Vehicle`, `Ticket`, `Money` | records (immutable values) | Money in paise avoids `double` money bugs |
| `ParkingSpot` | entity | Owns its occupancy, with atomic CAS |
| `SpotAllocationStrategy` → `LowestFloorBestFit` | strategy | **Change axis:** allocation policy (EV-first, VIP, nearest-to-gate) |
| `PricingStrategy` → `HourlyPricing` | strategy | **Change axis:** pricing rules |
| `ParkingLot` | service / orchestrator | Coordinates only. No pricing or allocation logic inside it. |
| `Clock` (injected) | dependency | Testable time. `Main` advances a manual clock. |

**Deliberately *not* abstracted (YAGNI):** `Floor` as its own class (a spot's `floor` int is enough until floors get behaviour like per-floor display boards), `Gate`, `Payment`. Say this out loud.

## 3. Concurrency: the decisive part
- **Race 1:** two gates assign the same spot. Fixed with `AtomicReference.compareAndSet(null, v)` per spot. The loser of the CAS moves to the next candidate. **No global lock**, so gates never block each other on unrelated spots.
- **Race 2:** the same ticket redeemed twice. Fixed with `ConcurrentHashMap.remove(id)`, which is atomic, so only one caller gets the ticket.
- **Race 3:** the same plate parked twice at two gates. Fixed with `Set.add()` on a concurrent key set as an atomic guard, rolled back if parking fails.
- **Trade-off stated:** the allocation strategy scans spots in O(n). At 10k+ spots, keep per-type free lists (`ConcurrentLinkedDeque<ParkingSpot>` per `VehicleType`) for O(1) allocation, polling from the smallest fitting type upward.

## 4. Follow-ups: how to answer
| Follow-up | Answer |
|---|---|
| Add EV spots with charging | New `SpotFeature` set on the spot plus a new allocation strategy, with `EvChargingPricing` as a **Decorator** over the base pricing. Zero edits to `ParkingLot`. |
| Weekend pricing | `WeekendAwarePricing(PricingStrategy weekday, PricingStrategy weekend, Clock)`, i.e. a composite strategy |
| Display board of free spots per floor | Observer: `ParkingSpot` state changes publish to an `EventBus`, and `DisplayBoard` subscribes. Or compute on read if the board is refreshed rarely. |
| Persist to a DB | Put a `TicketRepository` interface in front of the map. For the spot CAS, use `UPDATE spot SET vehicle=? WHERE id=? AND vehicle IS NULL` and check that rows updated = 1. |
| Reservations in advance | Spot availability becomes time-interval based: `TreeMap<Instant, Reservation>` per spot and an overlap check. This is a bigger change, so say it honestly. |
| Lost ticket | Look up by plate. Add a `Map<plate, ticketId>` index and charge a penalty through a pricing rule. |

## 5. Self-check against the rubric
R1 4 · R2 4 · R3 4 · R4 3 · R5 4 · R6 4 · R7 4 · R8 n/a → **SDE-3 signal**.
