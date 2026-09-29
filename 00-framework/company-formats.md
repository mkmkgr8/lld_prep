# Company Round Formats (India, SDE-2/3)

> These are typical patterns. Formats shift, so **always ask your recruiter**: "Is the design round LLD, HLD, or machine coding? Do I need runnable code?"

## FAANG India
| Company | LLD shows up as | What they care about |
|---|---|---|
| **Amazon** (SDE-2) | Usually 1 LLD/OOD round (~60 min) in the loop, with Leadership Principle questions mixed into every round | Clean OOD, extensibility, working through a flow. Class design on a whiteboard or in a code editor, not necessarily runnable. |
| **Google** (L4/L5) | Rarely a separate "LLD" round. Design quality is judged **inside coding rounds** (API design, class structure). L5 adds system design (HLD). | Clean abstractions, well-shaped APIs, follow-up extensions to your code |
| **Microsoft** (SDE-2) | Often one design round that is LLD-heavy (sometimes mixed with HLD) | OOD fundamentals, patterns, clarity |
| **Meta / Apple** | Meta: mostly coding + product/system design. Apple: team-dependent, often OOD. | Depends on team, so ask |

## Indian product companies: machine coding
| Company | Format | Notes |
|---|---|---|
| **Flipkart** | Machine coding, ~90 min, then a review discussion | Runnable code, modularity, extensibility. Classic: booking systems, rule engines. |
| **Swiggy / Zomato** | Machine coding, 90–120 min | Delivery or ordering domain problems; concurrency discussion afterwards |
| **PhonePe** | Machine coding (known for being strict), then a design discussion | Strong emphasis on concurrency and clean code |
| **Razorpay / CRED / Meesho** | Machine coding or LLD | Payments/wallet/ledger domains show up |

## Global product companies (India offices)
| Company | Format | Notes |
|---|---|---|
| **Uber** | Machine coding / "coding + design" round | Runnable code, extensibility, concurrency. E.g. rate limiters, schedulers. |
| **Atlassian** | "Code design" round | Starts simple (e.g. rate limiter, snake game, tag cloud) and then **adds requirements live**. OCP tested hard. |
| **Salesforce** | LLD round | Classic OOD problems |

## Trading / fintech / infra
| Company | Format | Notes |
|---|---|---|
| **Goldman, DE Shaw, Rubrik, Nutanix-style** | LLD + heavy concurrency | Thread-safe caches, blocking queues, producer-consumer, rate limiters, order books. Know `java.util.concurrent` well. |

## What this means for prep
1. **Two modes** are needed: design discussion (FAANG) *and* runnable machine coding (Indian product cos + Uber).
2. Concurrency is **mandatory** for PhonePe, Uber and trading firms, and it's the main SDE-3 separator everywhere else.
3. Atlassian-style "requirements added live" is best trained by having Claude run `followups` sessions.
