# Problem Bank: 40 Problems, Tiered

Legend: **F** = FAANG · **P** = Indian product (machine coding) · **U** = Uber/Atlassian/Salesforce · **T** = trading/fintech
Status: ⬜ not started · 🟨 attempted · ✅ scored ≥3 avg · 🏆 scored ≥3.4 (SDE-3 bar)

## Tier 1: Foundations (Weeks 2–3). Learn the patterns here.
| # | Problem | Key patterns / focus | Asked at | Status |
|---|---|---|---|---|
| 1 | Parking Lot | Strategy (pricing, spot allocation), Factory, concurrency on spot | F P U | ⬜ |
| 2 | Vending Machine | **State** | F P | ⬜ |
| 3 | ATM | State + **Chain of Responsibility** (cash dispense) | F P | ⬜ |
| 4 | Tic-Tac-Toe / Connect-4 (N×N, O(1) win check) | Clean modelling, extensible rules | F U | ⬜ |
| 5 | Snake & Ladder | Modelling, dice strategy, multiplayer | P | ⬜ |
| 6 | Logger framework | **Chain of Responsibility**, Singleton debate, sinks (Strategy) | F U | ⬜ |
| 7 | LRU Cache (then LFU) | DLL + HashMap, eviction Strategy, thread-safety | F P U T | ⬜ |
| 8 | Library Management | Entities, search, fines | F | ⬜ |

## Tier 2: Core interview set (Weeks 3–5). The most frequently asked.
| # | Problem | Key patterns / focus | Asked at | Status |
|---|---|---|---|---|
| 9 | **Splitwise** (expense sharing + simplify debts) | Strategy (split types), balance graph | F P U | ⬜ |
| 10 | **BookMyShow / Movie ticket booking** | Seat hold with TTL, **concurrency**, Facade | F P U | ⬜ |
| 11 | **Elevator system** | State, Strategy (scheduling: SCAN/LOOK), Observer | F U | ⬜ |
| 12 | **Rate Limiter** (token bucket, sliding window) | Strategy, Clock injection, per-user concurrency | U P T F | ⬜ |
| 13 | **Food delivery (Swiggy/Zomato)** | Order state machine, rider assignment Strategy | P | ⬜ |
| 14 | **Cab booking (Uber/Ola)** | Matching Strategy, trip states, pricing (surge) | P U | ⬜ |
| 15 | Hotel booking / Meeting room scheduler | Interval overlap (TreeMap), concurrency | F P | ⬜ |
| 16 | Chess | Polymorphic move validation, game state | F | ⬜ |
| 17 | Notification service (email/SMS/push, retries, preferences) | Strategy, Observer, Decorator for retries | F P | ⬜ |
| 18 | Stack Overflow / Q&A (votes, reputation, tags) | Modelling, Observer for reputation | F | ⬜ |
| 19 | Online shopping cart + inventory + coupons | Strategy (discounts), Decorator/Chain for coupon rules, oversell race | P F | ⬜ |
| 20 | Digital wallet / Payments (PhonePe/Paytm-like) | Ledger, idempotency, concurrency on balance | P T | ⬜ |
| 21 | Pub-Sub / Kafka-lite (topics, partitions, consumer offsets) | Observer, BlockingQueue, concurrency | U P T | ⬜ |
| 22 | Task / Job scheduler (cron, delayed, recurring) | PriorityQueue/DelayQueue, Command, thread pool | U T F | ⬜ |

## Tier 3: SDE-3 differentiators (Weeks 5–7)
| # | Problem | Why it's hard | Asked at | Status |
|---|---|---|---|---|
| 23 | **In-memory key-value store with TTL + transactions** (begin/commit/rollback, nested) | Command/undo log, lazy vs active expiry | U T P | ⬜ |
| 24 | **Order book / Stock exchange matching engine** | Price-time priority with TreeMap + queues, single-writer | T | ⬜ |
| 25 | Thread-safe bounded blocking queue (from scratch) | Lock + Conditions | T U | ⬜ |
| 26 | Rule engine / Feature flags (evaluate targeting rules) | Composite + Interpreter-lite, extensibility | P U | ⬜ |
| 27 | File system (in-memory, `mkdir/ls/cat`, path resolution) | Composite, permissions | F U | ⬜ |
| 28 | Text editor with undo/redo | Command, memento | F U | ⬜ |
| 29 | Distributed-lock-lite / Lease manager | TTL, fencing tokens, concurrency | T | ⬜ |
| 30 | Snake game (Atlassian classic, requirements added live) | OCP under changing requirements | U | ⬜ |
| 31 | Tag cloud / Popular-content tracker (top-K in a sliding window) | Heaps + time buckets | U | ⬜ |
| 32 | Car rental / Vehicle rental (Zoomcar-like) | Availability intervals, pricing, concurrency | P | ⬜ |
| 33 | Inventory management with warehouses & order allocation | Allocation Strategy, consistency | P F | ⬜ |
| 34 | Auction system (live bidding) | Observer, concurrency on highest bid | P F | ⬜ |
| 35 | Circuit breaker + retry library | State (CLOSED/OPEN/HALF_OPEN), Clock | U T | ⬜ |
| 36 | Connection pool / Object pool | Semaphore, blocking borrow with timeout | T U | ⬜ |
| 37 | Cricbuzz / Live score (event-driven scoring) | Observer, State (innings) | P | ⬜ |
| 38 | Coupon / Offer engine (stacking rules, priorities) | Chain + Strategy, rule composition | P | ⬜ |
| 39 | Metrics collector / Logging aggregation (counters, histograms, flush) | Concurrency, batching | U T | ⬜ |
| 40 | Google Docs-lite (comments, versions, permissions), design-only | Modelling depth | F | ⬜ |

## Standard follow-ups to expect (practise on every problem)
1. "Two users do this at the same time. What breaks?"
2. "Add a new type / rule / channel. What classes change?" (Answer should be: none, only new ones.)
3. "Make it survive a restart." (repository interface → DB, versioning)
4. "Scale to 10M entities in memory." (data-structure choice, indices)
5. "How would you test this?" (Clock injection, interfaces for mocks)
6. "Payment/external call fails midway." (compensation, idempotency keys, state rollback)
