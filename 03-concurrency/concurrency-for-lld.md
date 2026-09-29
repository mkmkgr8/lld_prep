# Concurrency for LLD: the SDE-3 Separator

Most candidates stop at "I'll add `synchronized`". To stand out, **name the race, choose the granularity, and justify it.**

## 1. Find the race (the same shape every time)
Almost every LLD race is **check-then-act** or **read-modify-write** on a shared resource:
| Problem | Race |
|---|---|
| Movie/seat booking | Two users see seat A5 free, both book it |
| Parking lot | Two cars assigned the same spot |
| Wallet/Splitwise | Two debits read balance 100 and both subtract 80 |
| Inventory/cart | Oversell the last item |
| Rate limiter | Two requests both read count=9 (limit 10) and both pass |
| Cache (LRU) | Concurrent get/put corrupting the linked list |
| ID generation | Duplicate IDs from `count++` |

## 2. Fix toolbox, from coarse to fine
| Tool | When | Trade-off |
|---|---|---|
| `synchronized` method on the service | Quick correctness, low contention | Global bottleneck. Say "fine for MVP, won't scale". |
| **Per-resource lock** (`synchronized(spot)` or `ReentrantLock` per show/seat) | Unrelated resources shouldn't block each other | Must avoid deadlock when locking several resources |
| `ConcurrentHashMap.compute / putIfAbsent / merge` | Atomic per-key updates | Keep the lambda short and side-effect free |
| `AtomicInteger/AtomicLong`, `compareAndSet` | Counters, single-variable state | Only covers single-variable invariants |
| `ReadWriteLock` / `StampedLock` | Read-heavy (catalogue, config) | More complex |
| `BlockingQueue` | Producer-consumer, job queues, logger | Bounded vs unbounded (backpressure!) |
| Optimistic locking (version field + CAS/retry) | Low contention; maps to DB `version` column | Retries under contention |
| **Partitioning / single-writer** | One thread per partition (e.g. per symbol in an order book) | No locks at all. Best throughput and the strongest answer for trading firms. |

## 3. Multi-seat booking without deadlock
```java
// Lock seats in a GLOBAL ORDER (sorted by id) to prevent deadlock
List<Seat> sorted = seats.stream().sorted(comparing(Seat::id)).toList();
sorted.forEach(s -> s.lock().lock());
try {
    if (sorted.stream().anyMatch(s -> !s.isAvailable())) throw new SeatUnavailableException();
    sorted.forEach(Seat::reserve);
} finally {
    sorted.reversed().forEach(s -> s.lock().unlock());  // Java 21; else iterate backwards
}
```
**Alternative (often better):** a temporary *hold* with a TTL (the "seat locked for 10 min while you pay" model). State `AVAILABLE → HELD(userId, expiry) → BOOKED`, with the transition made by an atomic CAS on the seat's state.

## 4. Must-code-from-memory (trading firms / PhonePe / Uber)
1. **Thread-safe LRU cache**: HashMap + DLL guarded by a lock. Then discuss striping/segments.
2. **Bounded blocking queue** using `ReentrantLock` + two `Condition`s (`notFull`, `notEmpty`).
3. **Rate limiters**: token bucket (lazy refill with an injected `Clock`), sliding-window log, sliding-window counter. Per-user buckets in a `ConcurrentHashMap`.
4. **Producer-consumer** with `ExecutorService` + `BlockingQueue`, and graceful shutdown.
5. **Scheduled executor / delayed job scheduler** (`DelayQueue` or `PriorityQueue` + `Condition.awaitNanos`).
6. **Print odd/even alternately with 2 threads** (warm-up question).
7. **Pub-sub with multiple consumers and offsets** (Kafka-lite).

## 5. Vocabulary to use correctly
- Visibility vs atomicity (`volatile` gives visibility, **not** atomicity).
- Happens-before, safe publication, immutability (`final` fields).
- Deadlock (4 conditions; fix by lock ordering or `tryLock` with timeout), livelock, starvation, fairness.
- `synchronized` vs `ReentrantLock` (tryLock, fairness, multiple conditions, interruptible).
- `ExecutorService` sizing: CPU-bound ≈ #cores; IO-bound gets more.

## 6. How to say it in the interview
> "The critical section is check-availability-then-reserve on a seat. A global lock is correct but serialises every booking across all shows. I'll lock per show, or better, do a CAS on each seat's state from AVAILABLE to HELD. That's lock-free per seat, and a hold expires after 10 minutes if payment doesn't complete. If this moved to a DB, the same idea becomes `UPDATE seat SET status='HELD', version=v+1 WHERE id=? AND version=v`."
