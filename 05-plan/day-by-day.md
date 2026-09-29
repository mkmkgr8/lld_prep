# Day-by-Day Plan: 56 Days (Sat 26 Sep → Fri 20 Nov 2026)

**Budget:** Sat/Sun = 4h · Mon–Fri = 2h. Weeks run Sat→Fri.
**Legend:** `#n` = problem number in `04-problems/problem-bank.md` · `C1–C14` = modules in `03-concurrency/building-blocks.md` · `M1–M12` = must-code list (same file) · `PDF Pn` = Part n of `references/LLD_Complete_Reference.pdf`
**Code goes in:** problems → `solutions/<problem-slug>/` · concurrency builds → `solutions/conc/<Cn-name>/`
**Every day ends with the same step:** type `review <path>` → I score it → the row goes into `06-mocks/progress-log.md`.

**Two tracks run through all 8 weeks:**
- **Design track:** OOP → patterns → problems → machine coding → SDE-3 problems → mocks.
- **Concurrency track:** C1–C14, woven in from Day 2 so it's never one crammed week.

| Week | Dates | Design focus | Concurrency focus |
|---|---|---|---|
| 1 | 26 Sep–2 Oct | Java, OOP, SOLID, UML | C1 threads, C2 memory model |
| 2 | 3–9 Oct | Patterns I + first problems | C3 synchronized, C4 CAS/atomics |
| 3 | 10–16 Oct | Patterns II + core problems | C5 locks/AQS, C6 synchronizers, C7 concurrent collections |
| 4 | 17–23 Oct | Concurrency-heavy problems | C8 queues, C9 executors, C10 CompletableFuture |
| 5 | 24–30 Oct | 90-min machine coding | C11 virtual threads, C12 liveness, C13 patterns |
| 6 | 31 Oct–6 Nov | SDE-3 problems | C14 distributed follow-ups, M1–M12 speed |
| 7 | 7–13 Nov | Company targeting | Timed must-codes |
| 8 | 14–20 Nov | Mocks + polish | Rapid-fire Q&A |

---

## WEEK 1: Foundations (26 Sep – 2 Oct)
**Exit criteria:** SOLID with a violation + fix for each, without notes · parking lot rebuilt from memory · can explain happens-before and why `count++` races.

### Day 1: Sat 26 Sep (4h)
| Time | Do |
|---|---|
| 0:00–0:20 | Install JDK 21 (see `README.md`). Compile and run `solutions/_reference/parking-lot`. |
| 0:20–1:20 | `solutions/day01-warmup/Warmup.java`: `record Money`, enum with behaviour, sealed `Shape` + pattern switch, generic `InMemoryRepo<T>` with `Optional`. Blank editor. |
| 1:20–2:20 | Read `01-fundamentals/oop-solid.md` + PDF P1–P2. Then `teach SRP`, `teach OCP`. |
| 2:20–3:30 | Study `_reference/parking-lot` (DESIGN.md + every class). Close it and answer the 5 questions from today's brief in writing. |
| 3:30–4:00 | `review solutions/day01-warmup` → log. |

**Done when:** the warm-up compiles, and all 5 written answers are graded.

### Day 2: Sun 27 Sep (4h)
| Time | Do |
|---|---|
| 0:00–1:00 | **Rebuild Parking Lot from a blank file** (60-min timer) → `solutions/parking-lot/` |
| 1:00–1:30 | Diff against the reference. List every gap. |
| 1:30–2:30 | `teach LSP`, `teach ISP`, `teach DIP` (3 quiz questions each) |
| 2:30–3:30 | **C1:** read it. Build: the lost-update demo (2 threads × 1M `count++`) → `solutions/conc/C1-lost-update/`. Explain the result in 3 sentences. |
| 3:30–4:00 | Review + log |

### Day 3: Mon 28 Sep (2h)
- 0:00–0:45 · `01-fundamentals/uml-and-java-essentials.md`. Draw the parking-lot class diagram on paper with correct arrows.
- 0:45–1:45 · **C2 part 1** (visibility, `volatile`). Build: the stop-flag bug and its fix → `conc/C2-stop-flag/`
- 1:45–2:00 · Log.

### Day 4: Tue 29 Sep (2h)
- 0:00–1:15 · **Single-threaded LRU from scratch** (HashMap + your own doubly linked list, no `LinkedHashMap`) → `solutions/lru/v1/`
- 1:15–1:45 · Now use `LinkedHashMap(accessOrder=true)` + `removeEldestEntry`. Compare the two.
- 1:45–2:00 · Review.

### Day 5: Wed 30 Sep (2h)
- 0:00–1:00 · **C2 part 2:** the happens-before rules, safe publication, `final` fields, why DCL needs `volatile`.
- 1:00–1:40 · Build **M12** (Singleton three ways) → `conc/M12-singleton/`. Write the paragraph "why I'd avoid Singleton for services".
- 1:40–2:00 · Quiz: `teach JMM`.

### Day 6: Thu 1 Oct (2h)
- 0:00–0:30 · Read `00-framework/interview-playbook.md` + `rubric.md`
- 0:30–1:45 · **#4 Tic-Tac-Toe** (N×N, O(1) win check), design round, **untimed** → `solutions/tic-tac-toe/`
- 1:45–2:00 · Review.

### Day 7: Fri 2 Oct (2h)
- 0:00–0:30 · LRU v1 again from a blank file (target 25 min)
- 0:30–1:30 · Re-read your week's gaps. Redo the two worst quiz topics.
- 1:30–2:00 · `weekly review`

---

## WEEK 2: Patterns I + monitors & CAS (3 – 9 Oct)
**Exit criteria:** Strategy, Factory, Builder, Observer, State and Decorator skeletons from memory in under 2 min each · Vending Machine timed at ≥ 2.5 · CAS state machine working.

### Day 8: Sat 3 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:00 | `02-patterns/patterns.md` Tier 1 + PDF P3. `drill strategy`, `drill factory`, `drill builder`. |
| 1:00–1:30 | `drill state` (+ the enum-FSM alternative) |
| 1:30–3:15 | **#2 Vending Machine**, untimed → `solutions/vending-machine/` |
| 3:15–4:00 | Read PDF P13. Critique their version against your rubric. Review + log. |

### Day 9: Sun 4 Oct (4h)
| Time | Do |
|---|---|
| 0:00–0:45 | `drill observer`, `drill decorator`, `drill singleton` (including the critique) |
| 0:45–2:15 | **#6 Logger framework** (levels as a Chain, sinks as a Strategy, async sink later) → `solutions/logger/` |
| 2:15–3:30 | **C3:** read it. Build **M7** (odd/even with `wait/notifyAll`) → `conc/C3-odd-even/` |
| 3:30–4:00 | Review + log |

### Day 10: Mon 5 Oct (2h)
- 0:00–1:30 · **C3 continued:** bounded buffer with `synchronized` + `wait/notifyAll` → `conc/C3-bounded-buffer/`. Test it with 3 producers and 3 consumers. Explain why it must be `while` and not `if`.
- 1:30–2:00 · Review.

### Day 11: Tue 6 Oct (2h)
- 0:00–0:40 · **C4:** CAS, the retry loop, ABA.
- 0:40–1:45 · Build **M5**: a seat CAS state machine `AVAILABLE→HELD(user, expiry)→BOOKED`, with an injected `Clock` for expiry → `conc/C4-seat-cas/`. Race 20 threads for one seat and assert exactly one wins.
- 1:45–2:00 · Review.

### Day 12: Wed 7 Oct (2h)
- 0:00–0:50 · **#4 Tic-Tac-Toe, timed 45** from a blank file
- 0:50–1:45 · `followups tic-tac-toe` (add Connect-4 rules, an undo move, an AI player)
- 1:45–2:00 · Log.

### Day 13: Thu 8 Oct (2h)
- 0:00–0:45 · **C4 part 2:** `LongAdder` vs `AtomicLong`, false sharing. Benchmark (8 threads × 10M increments, three ways) → `conc/C4-counters/`
- 0:45–1:45 · Speed drill: all six Tier-1 pattern skeletons, 2-min timer each
- 1:45–2:00 · Log.

### Day 14: Fri 9 Oct (2h)
- 0:00–1:00 · **#2 Vending Machine, timed 45**
- 1:00–1:30 · `followups vending-machine` (maintenance mode, change-making with denominations, concurrent buyers)
- 1:30–2:00 · `weekly review`

---

## WEEK 3: Patterns II + locks, synchronizers, collections (10 – 16 Oct)
**Exit criteria:** can explain how AQS and CHM work inside · Splitwise timed at ≥ 2.8 · LRU made thread-safe *correctly* (and can say why an RW lock is wrong for it).

### Day 15: Sat 10 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:00 | Tier-2 patterns: Chain, Command, Composite, Template, Adapter/Facade/Proxy + PDF P4–P5. `drill chain`, `drill command`. |
| 1:00–2:45 | **#3 ATM** (State for the session, Chain for the 2000→500→100 dispenser, compensating rollback) → `solutions/atm/` |
| 2:45–3:30 | Read PDF P14 and critique it (hint: the money type and missing denominations) |
| 3:30–4:00 | Review + log |

### Day 16: Sun 11 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:15 | **C5:** AQS, `ReentrantLock` (fair/non-fair, tryLock, `Condition`), RWLock rules, StampedLock |
| 1:15–2:00 | **LRU v2, thread-safe:** one `ReentrantLock` around get and put → `solutions/lru/v2/`. Write *why* a read lock on `get` is a bug. |
| 2:00–3:30 | **#9 Splitwise**, untimed (split Strategy, `long` paise, remainder handling, simplify-debts heaps) → `solutions/splitwise/` |
| 3:30–4:00 | Review + log |

### Day 17: Mon 12 Oct (2h)
- 0:00–1:30 · Build **M1**: a bounded blocking queue with `ReentrantLock` + `notFull`/`notEmpty` → `conc/C5-blocking-queue/`. Test it with a `CountDownLatch` start gate.
- 1:30–2:00 · Compare it with your Day 10 `synchronized` version. What does the second `Condition` buy you?

### Day 18: Tue 13 Oct (2h)
- 0:00–1:30 · Read-heavy config cache three ways (`synchronized`, RWLock, StampedLock optimistic read) + a rough benchmark → `conc/C5-config-cache/`
- 1:30–2:00 · Write the decision rule in your own words: when does each one win?

### Day 19: Wed 14 Oct (2h)
- 0:00–0:45 · **C7:** CHM internals (CAS on empty bin, `synchronized` on bin head, treeify, cooperative resize), CopyOnWrite, SkipListMap
- 0:45–1:45 · Word-frequency counter three ways → `conc/C7-word-count/`
- 1:45–2:00 · Explain why `containsKey`+`put` is broken and `merge` isn't.

### Day 20: Thu 15 Oct (2h)
- 0:00–0:30 · **C6:** Semaphore, CountDownLatch, CyclicBarrier
- 0:30–1:45 · Build **M10**: a connection pool with `borrow(timeout)` → `conc/C6-conn-pool/`. Hammer it with 50 threads.
- 1:45–2:00 · Log.

### Day 21: Fri 16 Oct (2h)
- 0:00–1:00 · **#9 Splitwise, timed 45**
- 1:00–1:30 · `followups splitwise` (currencies, concurrent expense adds, group settlement)
- 1:30–2:00 · `weekly review`

---

## WEEK 4: Concurrency-heavy problems + queues, executors, async (17 – 23 Oct)
**Exit criteria:** names the race **unprompted** in every problem · BookMyShow with holds working · can explain the ThreadPoolExecutor order of operations and its traps · PDF bug hunt ≥ 9/13.

### Day 22: Sat 17 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:00 | **C8:** queue comparison + backpressure. Build **M6** (producer-consumer + poison pill) → `conc/C8-prod-cons/` |
| 1:00–3:15 | **#10 BookMyShow** with seat holds (TTL, CAS per seat, multi-seat ordered or all-or-nothing, expiry sweeper) → `solutions/bookmyshow/` |
| 3:15–3:45 | Read PDF P11 and critique it (hint: `confirmBooking` with `allMatch`) |
| 3:45–4:00 | Log |

### Day 23: Sun 18 Oct (4h)
| Time | Do |
|---|---|
| 0:00–2:00 | **#12 Rate Limiter:** a Strategy with token bucket + sliding log + sliding counter, a per-user `CHM.compute`, and an injected `Clock`. Add a concurrency test with 100 threads that asserts exactly N allowed. → `solutions/rate-limiter/` |
| 2:00–2:45 | **C14** (rate-limit + lock rows): "make it work across 10 servers". Write your answer. |
| 2:45–3:30 | Read PDF P8 and critique it (global `synchronized`) |
| 3:30–4:00 | Review + log |

### Day 24: Mon 19 Oct (2h)
- 0:00–0:45 · **C9:** the `ThreadPoolExecutor` order of operations, factory traps, rejection policies, sizing, shutdown
- 0:45–1:45 · Bounded executor experiment `(4, 8, ArrayBlockingQueue(100), CallerRunsPolicy)` → `conc/C9-executor/`. Log which thread runs each task.
- 1:45–2:00 · Explain why `max` is ignored with an unbounded queue.

### Day 25: Tue 20 Oct (2h)
- 0:00–1:45 · Build **M9**: a delayed job scheduler with `DelayQueue` + a worker pool + cancel → `conc/C9-scheduler/`
- 1:45–2:00 · Log.

### Day 26: Wed 21 Oct (2h)
- 0:00–1:45 · **#11 Elevator**, untimed (State per elevator, dispatch Strategy, LOOK vs SCAN, request queue per elevator) → `solutions/elevator/`
- 1:45–2:00 · Log.

### Day 27: Thu 22 Oct (2h)
- 0:00–0:30 · **C10:** CompletableFuture composition, the common-pool trap, work stealing
- 0:30–1:45 · Price aggregator (fan-out to 3, timeout, fallback, own executor) → `conc/C10-aggregator/`
- 1:45–2:00 · Log.

### Day 28: Fri 23 Oct (2h)
- 0:00–1:15 · **`drill pdf-bugs`**: I show the snippets and you find the bugs. Target ≥ 9 of 13.
- 1:15–1:30 · Only now open `references/pdf-errata.md`.
- 1:30–2:00 · `weekly review`

---

## WEEK 5: Machine-coding mode + virtual threads, liveness, patterns (24 – 30 Oct)
**Exit criteria:** runnable code with a demo `Main` inside 90 min, every time · deadlock reproduced and fixed two ways.

### Day 29: Sat 24 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:30 | **`machine-coding` #13 Food delivery** (Swiggy style) → `solutions/food-delivery/` |
| 1:30–2:15 | Review against the machine-coding evaluation order |
| 2:15–3:30 | **C12:** build the transfer deadlock → `jstack` → fix with ordered locks (**M8**) → fix with `tryLock` + backoff → `conc/C12-deadlock/` |
| 3:30–4:00 | Log |

### Day 30: Sun 25 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:30 | **`machine-coding` #14 Cab booking** (matching Strategy, trip states, surge) → `solutions/cab-booking/` |
| 1:30–2:15 | Review |
| 2:15–3:15 | **#10 BookMyShow, timed 45** from a blank file |
| 3:15–4:00 | Log |

### Day 31: Mon 26 Oct (2h)
- 0:00–0:30 · **C11:** virtual threads, pinning, when not to use them
- 0:30–1:30 · 10k sleeping tasks: fixed pool vs virtual threads → `conc/C11-vthreads/`. Add a `Semaphore(50)` to limit "downstream" calls.
- 1:30–2:00 · Write your one-paragraph answer: "platform vs virtual threads for service X".

### Day 32: Tue 27 Oct (2h)
- 0:00–1:30 · **`machine-coding` #19 Cart + inventory + coupons** (oversell race, coupon Chain) → `solutions/cart/`
- 1:30–2:00 · Review.

### Day 33: Wed 28 Oct (2h)
- 0:00–0:30 · **C13:** confinement, single-writer, striping, optimistic vs pessimistic, idempotency
- 0:30–1:45 · `StripedLockMap` + ordered multi-key `transfer` → `conc/C13-striped/`
- 1:45–2:00 · Log.

### Day 34: Thu 29 Oct (2h)
- 0:00–1:30 · **`machine-coding` #20 Digital wallet** (ledger entries, idempotency keys, concurrent debits) → `solutions/wallet/`
- 1:30–2:00 · Review.

### Day 35: Fri 30 Oct (2h)
- 0:00–0:50 · **#12 Rate Limiter, timed 45**
- 0:50–1:30 · **M3 + M4** speed run (target times)
- 1:30–2:00 · `weekly review`

---

## WEEK 6: SDE-3 depth (31 Oct – 6 Nov)
**Exit criteria:** average ≥ 3.2 and R7 (extensibility) ≥ 3 · every must-code within 1.5× its target time.

### Day 36: Sat 31 Oct (4h)
| Time | Do |
|---|---|
| 0:00–1:30 | **`machine-coding` #23 KV store with TTL + nested transactions** (Command/undo log, lazy vs active expiry) → `solutions/kv-store/` |
| 1:30–2:15 | Review |
| 2:15–3:30 | **C14** in full: DB optimistic/pessimistic, unique-constraint locks, leases + fencing tokens. `teach fencing-tokens`. |
| 3:30–4:00 | Log |

### Day 37: Sun 1 Nov (4h)
| Time | Do |
|---|---|
| 0:00–2:00 | **#24 Order book / matching engine** (price-time priority: `TreeMap<price, Deque<Order>>`, one single-writer thread per symbol, partial fills, cancel) → `solutions/order-book/` |
| 2:00–2:30 | Review |
| 2:30–3:30 | Speed: **M1** (12 min), **M2** (20 min), **M5** (10 min) from blank files |
| 3:30–4:00 | Log |

### Day 38: Mon 2 Nov (2h)
- 0:00–1:40 · **#22 Job scheduler** in full (one-off / delayed / recurring, retries with backoff, cancel, worker pool) → `solutions/job-scheduler/`
- 1:40–2:00 · Review.

### Day 39: Tue 3 Nov (2h)
- 0:00–1:00 · **#26 Rule engine / feature flags**, design-45 (Composite AND/OR/NOT + predicates)
- 1:00–1:45 · `followups rule-engine`
- 1:45–2:00 · Log.

### Day 40: Wed 4 Nov (2h)
- 0:00–1:30 · **#35 Circuit breaker + retry** (State CLOSED/OPEN/HALF_OPEN, CAS transitions, `Clock`, sliding failure window) → `solutions/circuit-breaker/`
- 1:30–2:00 · Review.

### Day 41: Thu 5 Nov (2h)
- 0:00–1:40 · **#21 Pub-sub / Kafka-lite** (topics, partitions, per-group offsets, key-based partitioning, consumer poll) → `solutions/pubsub/`
- 1:40–2:00 · Review.

### Day 42: Fri 6 Nov (2h)
- 0:00–1:40 · **#30 Snake game**, Atlassian style: I add 3 requirements live using `followups`
- 1:40–2:00 · `weekly review`

---

## WEEK 7: Company targeting (7 – 13 Nov)
**Exit criteria:** average ≥ 3.4 on 3 consecutive mocks.

### Day 43: Sat 7 Nov (4h)
- 0:00–1:00 · **FAANG `mock` design-45:** #18 Stack Overflow
- 1:00–1:30 · Debrief
- 1:30–3:00 · **Flipkart `machine-coding`:** #15 Meeting-room scheduler
- 3:00–4:00 · Debrief + log

### Day 44: Sun 8 Nov (4h)
- 0:00–1:00 · **Atlassian:** #31 Tag cloud / top-K in a sliding window, with live requirement changes
- 1:00–1:30 · **Uber:** #12 Rate limiter in 30 min flat
- 1:30–2:30 · **Trading:** M2 thread-safe LRU + M10 pool, timed, then a grilling from `followups` on internals (CHM, AQS, false sharing)
- 2:30–3:30 · **PhonePe:** #20 Wallet re-attempt in 60 min
- 3:30–4:00 · Log

### Day 45: Mon 9 Nov (2h)
- **#27 File system**, design-45 (Composite, path resolution, permissions) + debrief

### Day 46: Tue 10 Nov (2h)
- **#28 Text editor undo/redo**, design-45 (Command + memento) + debrief

### Day 47: Wed 11 Nov (2h)
- **#34 Auction system**, machine-coding 90 (concurrent bids on the highest bid, Observer, auction end timer) + a quick review

### Day 48: Thu 12 Nov (2h)
- **#38 Coupon/offer engine**, design-45 (Chain + Strategy, stacking rules) + debrief

### Day 49: Fri 13 Nov (2h)
- Re-attempt your **worst 🟨 problem**, timed
- `weekly review`

---

## WEEK 8: Mocks + polish (14 – 20 Nov)
**Exit criteria:** 🏆 on ≥ 12 problems · every M1–M12 at or under its target time · no open item in `weak-areas.md` older than 7 days.

| Day | Date | Hrs | Do |
|---|---|---|---|
| 50 | Sat 14 Nov | 4h | 2 full mocks on random problems, one design-45 and one machine-coding-90. I pick without telling you in advance. Debrief both. |
| 51 | Sun 15 Nov | 4h | **Human mock** (peer or paid platform), 1.5h · **Concurrency rapid-fire:** every "Interview Qs" in C1–C14, 60 min, answered out loud · Fix the gaps. |
| 52 | Mon 16 Nov | 2h | `mock` random design-45 + debrief |
| 53 | Tue 17 Nov | 2h | `weak-areas.md` only: drill the top 2 |
| 54 | Wed 18 Nov | 2h | `machine-coding` random 90 + a quick review |
| 55 | Thu 19 Nov | 2h | **Must-code speed run:** M1, M2, M3, M5, M6, M8, all against target times |
| 56 | Fri 20 Nov | 2h | Final readiness review: I read the whole log and give a verdict per company group, plus a maintenance plan (3 mocks/week) until interviews |

---

## If you fall behind
- **Missed a weekday:** skip that day's *theory*, keep its *build*, and move on. Never shift the whole calendar.
- **Missed a weekend:** drop the untimed attempt, keep the timed one.
- **An interview date appears:** tell me the company and date. I'll re-cut the remaining days using the problem bank tags.
