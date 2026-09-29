# Reference PDFs: How to Use Them

| File | Pages | Contents | Verdict |
|---|---|---|---|
| `LLD_Complete_Reference.pdf` | 47 | OOP, SOLID, 13 patterns, **concurrency deep dive**, 9 problems (Parking Lot, Rate Limiter, Splitwise, Elevator, BookMyShow, Snake & Ladder, **Vending Machine, ATM, Library**), interview protocol | **Primary.** Covers everything in Deep, plus 3 more problems and concurrency. |
| `LLD_Deep_Reference.pdf` | 57 | Same theory with more verbose explanations, Prototype, 6 problems, Rate Limiter with FixedWindow and response headers | **Secondary.** Read it only when an explanation in Complete is too terse. |

**Bar they target:** "SDE2, 7/10." That matches weeks 1–4 of the plan. Weeks 5–8 go further, into machine coding, SDE-3 follow-ups and Tier-3 problems, and the PDFs don't cover that.

## Where the PDFs fit in the 8-week plan
| Week | Read (Complete Reference) |
|---|---|
| 1 | Parts 1–2 (OOP, SOLID) |
| 2 | Parts 3–4 (creational, structural) + Part 16 (protocol) |
| 3 | Part 5 (behavioural) + Parts 13–14 (Vending, ATM) |
| 4 | Part 6 (concurrency) + Parts 8, 10, 11 (Rate Limiter, Elevator, BookMyShow) |
| 5 | Parts 7, 9, 12, 15 as timed re-attempts (Parking, Splitwise, Snake, Library) |

**Rule:** attempt the problem **before** reading its PDF solution. Then read it and critique it against `00-framework/rubric.md`.

## ⚠️ Errata: the PDF code has real bugs
They're listed in `pdf-errata.md`. **Don't open that file yet.** In Week 4, after the concurrency week, run `drill pdf-bugs` with Claude and try to find them yourself first. Spotting these bugs is exactly what earns SDE-3 marks in follow-ups.

## Where the PDFs disagree with this folder (and which to follow)
| Topic | PDF says | This folder says | Why |
|---|---|---|---|
| Singleton `ParkingLot` with DCL | Green flag | Mention it, but **inject one instance from `Main`** | Hidden global state kills testability, and `getInstance(floors, pricing)` silently ignores its args after the first call. At SDE-3, you're expected to critique Singleton, not reach for it. |
| Money as `double` | Used everywhere | `long` paise / `record Money` | Floating-point rounding errors in payment domains get called out at Razorpay, PhonePe and CRED |
| `LocalDateTime.now()` inside entities | Used | Inject `Clock` | Otherwise time-based logic (expiry, pricing) can't be tested |
| `synchronized` on the whole `tryAcquire` = "thread safe per client" | Claimed | It's a **global** lock | Correct but serialises every client. Per-client locking or CAS is the SDE-3 answer. |
| Company "bars" (Rubrik 8/10 etc.) | Stated as fact | Treat as anecdotal | These aren't verifiable. Confirm the round format with your recruiter. |
