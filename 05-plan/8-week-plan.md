# 8-Week Plan (starting from rusty)

> Overview only. **The daily schedule to follow is `day-by-day.md`.**

**Budget:** ~2 h on weekdays, ~4 h on weekend days (≈ 18 h/week). **Started Sat 2026-09-26**, weeks run Sat→Fri, finishing Fri 2026-11-20.
**Rule:** every problem gets done **twice**. Attempt 1 is untimed and learning-focused. Attempt 2 is timed, 3–7 days later, from a blank file.

| Week | Theme | Weekday (2h) | Weekend (4h) | Exit criteria |
|---|---|---|---|---|
| **1** | Rebuild fundamentals | Java 17 refresher (records, enums, sealed, streams, collections) · `01-fundamentals/*` + PDF Complete Parts 1–2 · `teach` SOLID with Claude, one principle per day | Read `_reference/parking-lot` line by line. Rewrite it from memory. | Can explain each SOLID principle with a violation + fix in Java, without notes |
| **2** | Patterns I + first problems | Tier-1 patterns: Strategy, Factory, Observer, Singleton, Builder (`drill` each) + PDF Parts 3–4, 16 | #2 Vending Machine (State), #4 Tic-Tac-Toe, #6 Logger | Writes all Tier-1 pattern skeletons from memory in <2 min each |
| **3** | Patterns II + core problems | State, Chain, Decorator, Command, Composite + PDF Part 5 | #3 ATM, #7 LRU, #9 Splitwise | Avg rubric ≥ 2.5 |
| **4** | Concurrency | `03-concurrency/*` + PDF Part 6: blocking queue, token bucket, thread-safe LRU, producer-consumer, all from scratch · end of week: `drill pdf-bugs` | #10 BookMyShow (with holds), #12 Rate Limiter, #11 Elevator | Names the race **unprompted** in every problem |
| **5** | Machine coding mode | One 90-min `machine-coding` round every other day: #13, #14, #19, #20 | #15, #21 + re-attempt the weakest Tier-2 problem | Runnable code in 90 min, every time |
| **6** | SDE-3 depth | Tier-3: #23 KV store + txns, #22 scheduler, #26 rule engine, #35 circuit breaker | #24 Order book, #30 Snake (live requirement changes via `followups`) | Avg ≥ 3.2, R7 ≥ 3 |
| **7** | Company targeting | 2 problems per target group from the bank (see tags). Re-attempt every 🟨. | 2 full mocks: 1× design-45 (FAANG), 1× machine-coding-90 (Flipkart/PhonePe) | Avg ≥ 3.4 on 3 consecutive mocks |
| **8** | Mocks + polish | One mock daily, alternating modes. Review `weak-areas.md` and fix only those. | 2 mocks + a human mock (peer or paid) | 🏆 on ≥ 12 problems |

**PDF rule:** attempt a problem first, then read its PDF solution and critique it. The PDFs stop at the SDE-2 bar. Weeks 5–8 are where you go past them.

## Daily loop (2h)
1. **10 min** · flash review: patterns decision table + yesterday's gaps
2. **80–90 min** · problem attempt (mock or machine-coding)
3. **20 min** · Claude review → log to `06-mocks/progress-log.md` → write top 3 gaps

## Weekly checkpoint (Sunday, 15 min)
Ask Claude: *"weekly review"*. It reads the log, updates `weak-areas.md`, and adjusts next week.

## Minimum set if interviews get pulled forward
Parking Lot · Splitwise · BookMyShow · Elevator · Rate Limiter · LRU · Vending Machine · Food Delivery · KV store w/ TTL · Snake/Atlassian-style
