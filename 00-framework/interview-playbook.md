# LLD Interview Playbook

Run the same sequence every time. Interviewers score **process** as much as the final design.

## A. 45–60 min design round (Amazon, Microsoft, Google, Salesforce)

| Min | Step | What you say / do | Common mistakes |
|---|---|---|---|
| 0–5 | **1. Clarify** | Ask about actors, core use cases, what's out of scope, scale (in-memory? how many entities?), concurrency (multiple users at once?). **Write the list down.** | Designing before you know the scope |
| 5–8 | **2. Lock scope** | "For the MVP I'll support A, B, C. D and E I'll keep extensible but won't implement. OK?" | Trying to build everything |
| 8–15 | **3. Entities & relationships** | Nouns become classes; verbs become methods on services. Draw a quick class diagram (boxes and arrows). Mark composition vs association. | Putting business logic inside entities, or anaemic god services |
| 15–20 | **4. Identify change axes** | "Pricing will vary, so Strategy. Notification channels vary, so Observer/Strategy. Vehicle types vary, so polymorphism or an enum with behaviour." | Adding patterns for decoration |
| 20–40 | **5. Code the core** | Interfaces first, then the main service, then 1–2 concrete implementations. Skip getters and setters (say so). | Writing boilerplate, running out of time |
| 40–50 | **6. Walk through a flow** | Trace one use case end to end through your classes. Mention edge cases. | Not showing that the code actually works |
| 50–60 | **7. Follow-ups** | Concurrency, extensibility, persistence, scale. | Getting defensive, or redesigning from scratch |

## B. 90–120 min machine coding round (Flipkart, Swiggy, PhonePe, Razorpay, CRED, Meesho, Uber)

The rules are different: **working code beats a pretty design.**

| Min | Step |
|---|---|
| 0–10 | Read the problem **twice**. List the mandatory features and the bonus features. Ask the evaluator anything that is ambiguous. |
| 10–20 | Sketch the packages: `model/`, `service/`, `repository/` (in-memory maps), `strategy/`, `exception/`, `Main.java`. |
| 20–70 | Build **mandatory features end to end first**, in thin vertical slices. Run `Main` after every slice. |
| 70–85 | Add the bonus features and edge-case handling (invalid input, not found, duplicates). |
| 85–90 | Clean up names, remove dead code, and make sure `Main` demos every requirement with readable output. |

**Evaluation (typical):** runs without crashing > covers requirements > modularity/extensibility > code readability > tests/edge cases > concurrency handling (bonus, often asked in the review discussion).

**Hard rules**
- No frameworks (no Spring). Plain Java with in-memory `Map`s behind repository interfaces.
- One public class per file and sensible packages.
- Custom exceptions (`BookingNotFoundException`), not `RuntimeException("error")`.
- Don't read from stdin unless asked. A hardcoded demo in `Main` is standard.
- **Commit mentally at 60 min:** if a mandatory feature isn't working by then, cut scope elsewhere.

## C. Clarifying-question checklist (memorise this)
1. **Actors** — who uses it? (user, admin, system, external service)
2. **Core flows** — the top 3 things users do.
3. **Scale** — how many entities? Is everything in memory or does it need persistence?
4. **Concurrency** — can two actors touch the same resource at the same time?
5. **Variability** — which parts will change? (pricing, rules, types, channels)
6. **Failure** — what happens if payment fails, a slot disappears, or input is invalid?
7. **Out of scope** — auth, UI, persistence, distributed deployment (usually out).

## D. Phrases that signal SDE-3
- "I'm putting this behind an interface because **X will change**. I'm deliberately *not* abstracting Y because it's stable."
- "The race here is between check-availability and book. I'll make that atomic by…"
- "The lock is per-slot rather than global, so unrelated bookings don't contend."
- "If this had to go to a DB, the repository interface stays the same. Only the impl changes, and I'd use optimistic locking with a version column."
- "Trade-off: an enum is simpler, but adding a type means modifying it. With polymorphism it's open for extension. Given the requirement says new types are added often, I'll go polymorphic."
