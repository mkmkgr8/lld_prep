# Scoring Rubric (used for every attempt)

Score each dimension **1–4**. 1 = missing/wrong, 2 = partial, 3 = solid (SDE-2 bar), 4 = exceptional (SDE-3 bar).

| # | Dimension | 3 looks like | 4 looks like |
|---|---|---|---|
| R1 | **Requirements & scoping** | Asks relevant questions, confirms MVP | Drives scope, names NFRs and explicit out-of-scope, prioritises |
| R2 | **Object modelling** | Right entities, correct relationships | Crisp responsibilities, no god class, value objects and immutability used where they fit |
| R3 | **SOLID & abstractions** | Interfaces where needed, low coupling | Every abstraction justified by a change axis; no over-engineering |
| R4 | **Patterns** | 1–2 relevant patterns applied correctly | Patterns picked through trade-off reasoning; can name the alternative and why it was rejected |
| R5 | **Code quality** | Compiles, readable names, custom exceptions | Idiomatic Java (records, enums with behaviour, Optional, streams where they help), fails fast |
| R6 | **Concurrency** | Identifies the race when prompted, fixes it | Spots it unprompted, picks the right granularity, explains the trade-offs |
| R7 | **Extensibility follow-ups** | Handles a new requirement with small changes | New requirement fits in without touching existing classes (OCP shown live) |
| R8 | **Communication & time** | Finishes the core in time, thinks aloud | Structured, sets the agenda, leaves 10+ min for follow-ups |

**Signal mapping (avg score)**
- < 2.0 → No hire
- 2.0–2.7 → Lean hire at SDE-2
- 2.8–3.3 → Hire SDE-2 / Lean SDE-3
- 3.4+ with R6 and R7 ≥ 3 → **Hire SDE-3**

**Automatic downgrades**
- Code doesn't run in a machine-coding round → cap at 2.
- No clarifying questions → R1 = 1.
- One class does everything → R2 ≤ 2.
