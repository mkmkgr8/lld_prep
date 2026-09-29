# LLD Prep — SDE-2/3, India, 8 Weeks, Java

**Goal:** Walk into any LLD / machine-coding round and produce a clean, extensible, thread-aware design in time, then handle the follow-ups well enough to earn the SDE-3 bar.

## Setup (one time)
```bash
brew install openjdk@21
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
java -version   # should say 21
```
IDE: IntelliJ IDEA Community. Also practise in a **plain editor with no autocomplete** for at least 30% of sessions, because some rounds use a shared doc.

## How to use this folder
1. Open Claude Code in this folder: `cd ~/lld-prep && claude`. Claude reads `CLAUDE.md` and knows your context.
2. Type `today` to get the day's task.
3. Practise problems with `mock <problem>` or `machine-coding <problem>`.
4. Every attempt is logged in `06-mocks/progress-log.md`. Weak areas get tracked automatically.

## The 5 things that decide SDE-2 vs SDE-3 in LLD
| Signal | SDE-2 bar | SDE-3 bar |
|---|---|---|
| Requirements | Asks sensible clarifying questions | **Drives** scope: proposes MVP vs later scope and names non-functional constraints |
| Modelling | Correct entities and relationships | Clean boundaries: entities vs services vs strategies; no god classes |
| Extensibility | Uses 1–2 patterns correctly | Chooses patterns **because** of a stated change axis and can explain what it would *not* abstract |
| Concurrency | Aware of races when asked | Identifies races unprompted and picks the right granularity (lock / CAS / partitioning) |
| Code | Compiles, readable | Runnable, tested edge cases, clean exceptions, immutability where it counts |

## Folder map
```
00-framework/     interview playbook, rubric, company formats   ← read first
01-fundamentals/  OOP, SOLID, UML, Java essentials
02-patterns/      patterns that actually get asked, with Java skeletons
03-concurrency/   building blocks C1–C14 (internals + trade-offs) + applying them in LLD
04-problems/      tiered problem bank + attempt template
05-plan/          day-by-day.md (56 days) + 8-week overview
06-mocks/         progress log + weak areas
solutions/        your code (one per problem) + _reference/ gold standard
```
