# Concurrency Building Blocks: Internals + Trade-offs (Java 21)

Fourteen modules (C1–C14), in order. Each one has: **what it is → how it works inside → trade-offs → interview questions → build task**.
The day-by-day plan (`05-plan/day-by-day.md`) schedules each module. `concurrency-for-lld.md` in this folder shows how to *apply* it all in LLD rounds.

---

## C1: Threads & lifecycle
**What:** a `Thread` is an OS thread (a *platform* thread) with its own stack (~512KB–1MB). You submit `Runnable` (no result) or `Callable<V>` (returns a result, throws checked exceptions).

**Thread states:** `NEW → RUNNABLE ⇄ (BLOCKED | WAITING | TIMED_WAITING) → TERMINATED`
- `BLOCKED`: waiting to enter a `synchronized` monitor.
- `WAITING`: `wait()`, `join()`, `LockSupport.park()` (which is what `ReentrantLock` uses).
- `TIMED_WAITING`: `sleep(n)`, `wait(n)`, `parkNanos`.

**Internals:** Java threads map 1:1 to kernel threads. A context switch costs microseconds and flushes caches, which is why "one thread per request" stops scaling at thousands of threads (virtual threads fix this, see C11).

**Interrupts:** interruption is *cooperative*. `interrupt()` sets a flag, and blocking calls throw `InterruptedException` and **clear** the flag. The rule: either rethrow it, or restore the flag with `Thread.currentThread().interrupt()`. Never swallow it.

**Interview Qs:** What's the difference between `start()` and `run()`? What happens if an exception escapes `run()`? Daemon vs user threads?
**Build:** two threads each do `count++` 1M times on a shared `int`. Observe that the result is < 2M. Explain *why*: `count++` is a read-modify-write (3 steps).

---

## C2: Java Memory Model (JMM)
Concurrent code has three problems. **Atomicity**: is the operation indivisible? **Visibility**: does another thread *see* the write? **Ordering**: can the compiler or CPU reorder it?

**Internals:** CPUs have per-core caches and store buffers. The JIT and the CPU both reorder instructions if that's invisible *within one thread*. Without a **happens-before** (HB) edge, thread B may never see thread A's write, or may see writes in a different order.

**Happens-before edges (memorise these):**
1. Program order within a thread.
2. Monitor unlock → every subsequent lock of the *same* monitor.
3. `volatile` write → every subsequent read of that variable.
4. `Thread.start()` → everything in the started thread. Everything in a thread → another thread's `join()` returning.
5. Completion of a `final` field's constructor write → any thread that sees the reference (if `this` didn't escape during construction).
6. HB is transitive. So is `j.u.c.`: putting into a `BlockingQueue` HB the take, `Future` completion HB `get()`, and so on.

**`volatile`:** gives visibility and ordering (it acts as a memory fence) but **not** atomicity for compound ops. It's correct for a one-writer flag or a published immutable reference.

**Safe publication:** share an object through a `final` field, a `volatile` field, a lock, or a concurrent collection. Otherwise another thread may see a half-constructed object.

**Double-checked locking needs `volatile`:** `instance = new X()` is roughly *allocate → assign reference → run constructor*, and it can be reordered. Without `volatile`, another thread can see a non-null reference to an unconstructed object. Better: the **holder idiom** or an **enum** (class init is thread-safe by the JLS).

**Interview Qs:** Is `volatile long counter; counter++` safe? (No.) Why are immutable objects thread-safe? What is a data race vs a race condition?
**Build:** a stop-flag bug: a `while (!stop) {}` loop in thread A that never exits without `volatile`. Try it with `-server` and a hot loop. Then fix it.

---

## C3: `synchronized` & intrinsic monitors
**What:** every object has a monitor. `synchronized` is **reentrant**, and it releases on exception automatically.

**Internals (HotSpot):**
- The object header's *mark word* holds the lock state. It starts **thin/lightweight**: a CAS on the header, with a stack-allocated lock record. Under contention it **inflates** to a heavyweight `ObjectMonitor` (entry list + wait set, parked OS threads).
- *Biased locking* was disabled by default in JDK 15 and later removed, so don't mention it as current behaviour.
- JIT optimisations: **lock elision** (escape analysis proves the object is thread-local) and **lock coarsening** (it merges adjacent lock/unlock pairs).

**`wait/notify`:** you must hold the monitor. `wait()` releases it and parks the thread in the wait set. **Always wait in a `while` loop**, because of spurious wakeups and because the condition may have changed by the time you re-acquire the lock. Prefer `notifyAll` unless every waiter is interchangeable.

**Trade-offs vs `ReentrantLock`:**
| | `synchronized` | `ReentrantLock` |
|---|---|---|
| Syntax/safety | Block-scoped, auto-release | Must `unlock()` in `finally` |
| tryLock / timeout / interruptible | ✗ | ✓ |
| Fairness option | ✗ | ✓ (slower) |
| Multiple condition queues | ✗ (one wait set) | ✓ `newCondition()` |
| Virtual threads (JDK 21–23) | **Pins** the carrier while blocked inside | Doesn't pin |
| Performance | Comparable under modern JVMs | Comparable |

**Interview Qs:** What does `synchronized` on a static method lock? (The `Class` object.) Why use `while` and not `if` around `wait()`?
**Build:** (a) print odd/even alternately with 2 threads using `wait/notifyAll`. (b) A bounded buffer using `synchronized` + `wait/notifyAll`.

---

## C4: Atomics, CAS, LongAdder, false sharing
**CAS** (compare-and-swap) is a single CPU instruction (`lock cmpxchg` on x86) that means "set to X only if it's currently E". It's the basis of all lock-free code.
```java
// Canonical CAS retry loop
int prev, next;
do { prev = ai.get(); next = f(prev); } while (!ai.compareAndSet(prev, next));
// Or simply: ai.updateAndGet(this::f)  // f must be side-effect free (it may run several times)
```
**`AtomicReference` for state machines:** `state.compareAndSet(AVAILABLE, HELD)` makes the transition atomic, which is exactly what seat booking and parking spots need.

**ABA problem:** a value goes A→B→A, and the CAS wrongly succeeds. It matters for lock-free stacks and node reuse. Fix it with `AtomicStampedReference` (value + version). In LLD, a version counter plays the same role as the DB `version` column.

**`LongAdder` vs `AtomicLong`:** under heavy contention, CAS on one variable spins and bounces the cache line. `LongAdder` stripes the count across `Cell`s (one per contended thread) and `sum()` adds them up. The trade-off: **`sum()` isn't an atomic snapshot**. Use it for metrics and counters, not for "check the limit then act".

**False sharing:** two hot variables on the same 64-byte cache line, written by different cores, keep invalidating each other. The fixes are padding and `@Contended` (JDK internal, needs `-XX:-RestrictContended`). `LongAdder`'s cells are padded for this reason.

**Trade-off: lock vs CAS.** CAS wins at low to moderate contention on a *single* variable. For multi-variable invariants, use a lock (or pack the state into one immutable object and CAS the reference).

**Interview Qs:** Why can a CAS loop livelock? When is `LongAdder` wrong? How does `AtomicInteger.incrementAndGet` work?
**Build:** (a) a CAS-based seat state machine: `AVAILABLE→HELD→BOOKED` plus hold expiry. (b) A benchmark of 8 threads × 10M increments with `synchronized` vs `AtomicLong` vs `LongAdder`.

---

## C5: Explicit locks: AQS, ReentrantLock, ReadWriteLock, StampedLock
**AQS (`AbstractQueuedSynchronizer`)** is the engine behind `ReentrantLock`, `Semaphore`, `CountDownLatch`, `ReentrantReadWriteLock` and `ThreadPoolExecutor.Worker`. It holds an `int state` changed by CAS, plus a FIFO wait queue (a CLH variant) of parked threads (`LockSupport.park/unpark`).
- `ReentrantLock`: state = hold count. **Non-fair** (the default) lets a newly arriving thread *barge* ahead of the queue, which gives higher throughput but possible starvation. **Fair** means strict FIFO, which is slower.
- `Condition`: a separate wait queue per condition, so you can have `notFull` and `notEmpty` and **signal only the right side**. `synchronized` can't do this.

**`ReentrantReadWriteLock`:** many readers OR one writer.
- It **can't upgrade** read→write (it deadlocks if you try). Downgrading write→read is allowed.
- In non-fair mode, a constant stream of readers can starve writers.
- It only pays off when reads greatly outnumber writes *and* the critical section isn't tiny. Otherwise its bookkeeping costs more than a plain lock.

**`StampedLock`:** adds an **optimistic read**: `long s = tryOptimisticRead(); read fields; if (!validate(s)) { fall back to readLock }`. It's the fastest option for read-mostly data. Trade-offs: it's **not reentrant**, has no `Condition`, and is easy to misuse.

**Decision:** `synchronized` by default → `ReentrantLock` when you need tryLock, timeouts, fairness or multiple conditions → RW/Stamped only for measured read-heavy hot paths.

**Interview Qs:** Why must `unlock()` go in `finally`? Why doesn't an RW lock help an LRU cache? (Because `get` mutates the order.) What's the difference between fair and non-fair?
**Build:** (a) the bounded buffer again with `ReentrantLock` + 2 `Condition`s. (b) A read-heavy config cache three ways: `synchronized`, RWLock and StampedLock.

---

## C6: Coordination synchronizers
| Tool | Semantics | Reusable? | Typical LLD use |
|---|---|---|---|
| `Semaphore(n)` | n permits; `acquire/release` | ✓ | Connection/object pool, limiting concurrent calls to a downstream (bulkhead) |
| `CountDownLatch(n)` | Wait until counted down to 0 | ✗ one-shot | "Start gate" in concurrency tests; wait for N services to init |
| `CyclicBarrier(n)` | n threads wait for each other, optional barrier action | ✓ | Phased simulations |
| `Phaser` | Dynamic-party barrier | ✓ | Rare. Know the name only. |

**Trade-off:** a `Semaphore` has no ownership, so any thread can release. That makes it flexible but easy to leak permits. Always release in `finally`.
**Build:** a connection pool: `borrow(timeout)` with `Semaphore.tryAcquire` + `ConcurrentLinkedQueue` of objects, and `release` returns the object. Use a `CountDownLatch` start gate to hammer it with 50 threads.

---

## C7: Concurrent collections internals
**`ConcurrentHashMap` (Java 8+):**
- A `Node[] table`. `get()` is **lock-free** (volatile reads).
- `put` into an empty bin is a **CAS**. For a non-empty bin, it does `synchronized` on the **bin's head node**, so locking is per bucket, not per map.
- A bin becomes a red-black tree at 8 entries (if table ≥ 64).
- Resizing is *cooperative*: threads help move bins, and `ForwardingNode` marks moved bins.
- `size()` uses striped counter cells (the LongAdder idea), so it's an estimate under concurrency.
- **Atomic per key:** `compute`, `computeIfAbsent`, `merge`, `putIfAbsent`. **Not atomic:** `if (!map.containsKey(k)) map.put(k, v)`, which is check-then-act. Keep `compute` lambdas short and **never touch other keys of the same map inside one** (that risks deadlock or `IllegalStateException`).
- Null keys and values aren't allowed, because `get` returning null has to mean "absent".

**`CopyOnWriteArrayList`:** every write copies the array. Reads are lock-free, and iterators see a snapshot. It's ideal for listener lists (Observer). It's terrible for write-heavy data.

**`ConcurrentSkipListMap`/`Set`:** a sorted, lock-free (CAS) skip list with O(log n). Use it as the concurrent `TreeMap` for order books, time-indexed data, and `floorKey`/`ceilingKey` queries.

**`ConcurrentLinkedQueue`:** the lock-free Michael-Scott queue. It's unbounded and has no blocking, so use it when you don't need backpressure.

**`Collections.synchronizedMap`:** one lock for everything, and iteration still needs external locking. It's a legacy answer.

**Interview Qs:** How does CHM avoid locking the whole map? Why is `get` lock-free? Is `size()` exact?
**Build:** a word-frequency counter from 8 threads, three ways: `synchronized HashMap`, `CHM.merge(k, 1L, Long::sum)`, and `CHM<String, LongAdder>` with `computeIfAbsent(k, x -> new LongAdder()).increment()`. Compare the results and the speed.

---

## C8: BlockingQueues & backpressure
| Queue | Structure | Locks | Bounded | Use |
|---|---|---|---|---|
| `ArrayBlockingQueue` | Ring array | **1 lock**, 2 conditions | ✓ fixed | Predictable memory, fair option |
| `LinkedBlockingQueue` | Linked nodes | **2 locks** (put/take), so producers and consumers don't contend | Optional (default `Integer.MAX_VALUE` ⚠️) | Higher throughput producer-consumer |
| `PriorityBlockingQueue` | Heap | 1 lock | ✗ unbounded | Priority jobs |
| `DelayQueue` | Heap of `Delayed` | 1 lock | ✗ | Scheduled/expiring tasks, TTL cleanup |
| `SynchronousQueue` | No capacity: direct hand-off | CAS | 0 | `newCachedThreadPool`, strict hand-off |
| `LinkedTransferQueue` | Lock-free | CAS | ✗ | High-perf hand-off |

**Backpressure** is *the* trade-off. An unbounded queue hides overload until you run out of memory. A bounded queue forces a decision: **block** the producer (`put`), **fail fast** (`offer` returns false), or **time out** (`offer(e, t, unit)`). Say this out loud in interviews.

**Graceful shutdown:** use a *poison pill* (one per consumer), or interrupt the consumers plus a `volatile running` flag.
**Build:** a producer-consumer logger (async log sink) with a bounded queue, 3 consumers, poison-pill shutdown, and a drop-oldest policy when full.

---

## C9: Executors & ThreadPoolExecutor internals
**`ThreadPoolExecutor(core, max, keepAlive, queue, threadFactory, rejectionHandler)`**
**Order of operations for `execute(task)`:**
1. Fewer than `core` threads running → start a new thread.
2. Otherwise → **offer to the queue**.
3. Queue full → start a thread, up to `max`.
4. Still no room → **rejection handler**.

⚠️ With an **unbounded queue**, step 3 never happens, so `max` is meaningless.

**Built-in factories and their traps:**
| Factory | Queue | Trap |
|---|---|---|
| `newFixedThreadPool(n)` | Unbounded `LinkedBlockingQueue` | Memory runs out under overload |
| `newCachedThreadPool()` | `SynchronousQueue`, max=∞ | Unbounded threads |
| `newSingleThreadExecutor()` | Unbounded | Same as fixed |
| `newVirtualThreadPerTaskExecutor()` | none | See C11 |

**Rejection policies:** `AbortPolicy` (default, throws), `CallerRunsPolicy` (the submitter runs the task, giving natural backpressure), `DiscardPolicy`, `DiscardOldestPolicy`.

**Sizing (Goetz):** threads ≈ `Ncpu × (1 + wait/compute)`. CPU-bound work gets about `Ncpu` threads. IO-bound work gets more. Use **separate pools** for different workloads (bulkhead).

**Shutdown:** `shutdown()` then `awaitTermination()`, and if that fails, `shutdownNow()` (which interrupts). Exceptions in `submit()`ed tasks are captured in the `Future` and are **silently lost** if nobody calls `get()`.

**`ScheduledThreadPoolExecutor`:** built on a `DelayedWorkQueue` (a heap). `scheduleAtFixedRate` vs `WithFixedDelay`. An exception **cancels future runs**.

**Interview Qs:** Why is `newFixedThreadPool` dangerous in prod? How do you add backpressure to an executor? `submit` vs `execute` error handling?
**Build:** a bounded `ThreadPoolExecutor(4, 8, ArrayBlockingQueue(100), CallerRunsPolicy)`. Flood it with slow tasks and log which thread runs each one.

---

## C10: CompletableFuture & ForkJoin
**`CompletableFuture`:** composes async steps: `supplyAsync → thenApply / thenCompose (flatMap) / thenCombine / allOf / anyOf`, with `exceptionally/handle` for errors and `orTimeout/completeOnTimeout` (Java 9+) for timeouts.
⚠️ The `*Async` methods run on **`ForkJoinPool.commonPool()`** by default. It's shared JVM-wide and sized at `Ncpu-1`. **Blocking IO there starves everyone.** Always pass your own executor for IO.

**ForkJoinPool:** each worker has its own deque (a **work-stealing** queue). A worker pushes and pops from its own end, and idle workers *steal* from the other end. This is great for recursive divide-and-conquer (`RecursiveTask`). Parallel streams use the common pool, so the same caveat applies.

**Build:** a price aggregator that fans out to 3 "vendors" (with random sleep and random failure), takes the best price, with a 500ms timeout and a fallback, on a dedicated executor.

---

## C11: Virtual threads (Java 21)
**What:** JVM-managed threads, *mounted* on a small pool of carrier (platform) threads. When a virtual thread blocks on IO, it **unmounts** and its stack is saved on the heap, freeing the carrier. You can have millions of them.

**Pinning:** in JDK 21–23, blocking *inside `synchronized`* or in native frames keeps the carrier pinned. So prefer `ReentrantLock` in code that virtual threads run. (JDK 24 fixed pinning for `synchronized`.)

**Trade-offs:** they give no speedup for CPU-bound work. **Don't pool them** (they're cheap, so create one per task). Limit concurrent access to scarce resources with a `Semaphore`, not a pool size. Be careful with `ThreadLocal`-heavy code (millions of copies).

**Interview line:** *"For a thread-per-request IO-bound service, I'd use virtual threads and bound the downstream calls with a Semaphore. For CPU-bound matching I'd use a fixed pool of about Ncpu threads."*
**Build:** run 10,000 tasks that each `sleep(1s)` on a fixed pool of 200 vs `newVirtualThreadPerTaskExecutor()`, and compare the wall time.

---

## C12: Liveness & failure modes
- **Deadlock:** the four Coffman conditions are *mutual exclusion, hold-and-wait, no preemption, circular wait*. You break it by breaking one of them: **global lock ordering** (sort by id), **`tryLock` with timeout** and back off, or a single coarser lock. Detect it with `jstack` ("Found one Java-level deadlock") or `ThreadMXBean.findDeadlockedThreads()`.
- **Livelock:** threads keep reacting to each other and make no progress (e.g. two `tryLock` retries in lockstep). Fix it with **randomised backoff**.
- **Starvation:** unfair locks, a non-fair RW lock starving writers, low-priority threads.
- **Lost wakeup:** `notify` before `wait`, or `if` instead of `while`.
- **ThreadLocal leaks** in pools: threads are reused, so `remove()` in `finally`.
- **Swallowed exceptions** in executor tasks (see C9). Use `UncaughtExceptionHandler`, or wrap tasks.

**Build:** the classic transfer deadlock `transfer(a, b)` ‖ `transfer(b, a)`. Reproduce it, capture a `jstack` dump, and fix it with lock ordering by account id. Then fix it again with `tryLock` + backoff.

---

## C13: Design patterns for thread-safety (the LLD toolkit)
| Pattern | Idea | When | Cost |
|---|---|---|---|
| **Immutability** | `record`s, `final` fields, copy-on-change | Value objects, config, events | Allocation |
| **Thread confinement** | Only one thread touches the data | Per-request objects, event loops | Needs a hand-off mechanism |
| **Single-writer / partitioning** | One thread owns each partition (symbol, user shard) and others send it messages | Order books, per-user rate limiters, game rooms | Cross-partition ops get hard |
| **Lock striping** | N locks, `lock[hash(key) % N]` | Big maps with per-key invariants | Multi-key ops need ordered locking |
| **Per-entity lock / CAS** | Lock or CAS on the resource itself | Seats, spots, accounts | Multi-entity ops need ordering |
| **Copy-on-write** | Readers see snapshots | Listener lists, routing tables | Write cost O(n) |
| **Optimistic concurrency** | Read the version → compute → CAS/UPDATE WHERE version=v, retry | Low contention updates, the DB world | Retries under contention |
| **Pessimistic locking** | Lock first, then act | High contention, expensive retries | Throughput, deadlock risk |
| **Idempotency keys** | Dedupe retried requests by client key | Payments, bookings | Storage for keys + TTL |
| **Producer-consumer** | Decouple with a bounded queue | Async work, logging, notifications | Latency, backpressure design |

**Build:** a `StripedLockMap<K,V>` with 16 stripes and `update(key, fn)`, plus a multi-key `transfer(k1, k2)` using ordered stripe locking.

---

## C14: Beyond one JVM (the "now make it work on 10 servers" follow-up)
In-process locks mean nothing across instances. Know these answers:
| Need | Answer | Trade-off |
|---|---|---|
| Prevent double-booking in a DB | `UPDATE seat SET status='HELD', version=v+1 WHERE id=? AND version=v` (optimistic) **or** `SELECT … FOR UPDATE` (pessimistic) | Optimistic retries vs lock waits and deadlocks |
| Unique constraint as a lock | `INSERT booking(show_id, seat_id) UNIQUE` | Simple and strong, but only for "create once" |
| Distributed lock | Redis `SET key val NX PX ttl` + a safe release (Lua compare-and-delete), or ZooKeeper/etcd leases | A TTL expiring mid-work means **2 holders**, so use **fencing tokens** (a monotonically increasing token checked by the resource) |
| Distributed rate limit | Redis `INCR`+`EXPIRE` (fixed window) or a sorted-set sliding log in a Lua script | Network hop per request, and Redis becomes critical, so consider local token buckets + periodic sync |
| Exactly-once-ish processing | At-least-once delivery + **idempotent consumers** (dedupe table / idempotency key) | Storage and TTL for dedupe keys |
| Ordering per entity | Partition by key (Kafka partition = single writer per key) | Hot keys |

---

## Master trade-off table: "which tool?"
| Situation | First choice | Upgrade when… |
|---|---|---|
| One flag, one writer | `volatile boolean` | Never needs more |
| Counter, metrics | `LongAdder` | Need an exact atomic read-then-act → `AtomicLong` |
| Single-variable state transition | `AtomicReference.compareAndSet` | Multi-field invariant → lock or CAS on an immutable snapshot |
| Per-key update in a map | `ConcurrentHashMap.compute/merge` | Multi-key invariant → striped/ordered locks |
| Multi-step invariant on one object | `synchronized` on that object | Need timeout/fairness/conditions → `ReentrantLock` |
| Multiple resources at once | Ordered locking (sort by id) | Contention high → partition / single-writer |
| Read-mostly shared structure | Immutable snapshot + `volatile` swap (copy-on-write) | Large and mutated often → `StampedLock` optimistic read |
| Hand work between threads | Bounded `BlockingQueue` | Need priority/delay → `PriorityBlockingQueue` / `DelayQueue` |
| Limit concurrency to a resource | `Semaphore` | — |
| Run many IO-bound tasks | Virtual threads (+ Semaphore for downstream limits) | CPU-bound → fixed pool ≈ Ncpu |
| Async fan-out/fan-in | `CompletableFuture` on **your own** executor | — |
| Across processes/servers | DB optimistic locking / unique constraint | Need a mutex → lease-based lock + fencing token |

---

## Must-code-from-memory list (target times, blank file, no IDE)
| # | Build | Target |
|---|---|---|
| M1 | Bounded blocking queue (`ReentrantLock` + 2 Conditions) | 12 min |
| M2 | Thread-safe LRU (HashMap + DLL + one lock), then explain the striped version | 20 min |
| M3 | Token bucket per user (`CHM.compute`, lazy refill, injected `Clock`) | 15 min |
| M4 | Sliding-window-log rate limiter | 12 min |
| M5 | CAS state machine for a seat with hold expiry | 10 min |
| M6 | Producer-consumer with executor + poison pill shutdown | 12 min |
| M7 | Odd/even printer (`wait/notifyAll`) and the `Semaphore` version | 8 min |
| M8 | Deadlock-free `transfer(a, b)` (ordered locks) | 8 min |
| M9 | Delayed job scheduler (`DelayQueue` + worker) | 15 min |
| M10 | Semaphore-based object/connection pool with timeout | 10 min |
| M11 | Read-through cache with per-key loading (`computeIfAbsent` + TTL) | 12 min |
| M12 | Singleton three ways (holder, enum, DCL + volatile), and why you'd avoid it | 5 min |
