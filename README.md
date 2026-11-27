# Capstone Project — Library Management System

> **Name:** Shubham Kuvad  
> **Roll No:** `BC2025052`  

---

A command-line **Library Management System** implemented in Java that manages library items, members, loans, overdue fines, sorted reports, and item reservations using simulated time.

## Requirements

- Java 16 or higher (JDK 17 / JDK 21 recommended)

## How to Run

Compile all Java source files:
```bash
javac src/*.java
```

Run interactively (one command per line from standard input):
```bash
java -cp src Main
```

Run with an input file and redirect output:
```bash
java -cp src Main < sample.txt > out.txt
```

## Features

- Add books, magazines, reference books, and library members (`STUDENT`, `STAFF`, `FACULTY`)
- Borrow and return items with automatic due-day and late-fine calculation
- Simulated day counter starting at day `0` (advanced only via `ADVANCE`)
- Case-insensitive title search and sorted reports (`SEARCH`, `LOANS`, `OVERDUE`, `INVENTORY`)
- Custom checked exception hierarchy caught centrally in the command loop (`ERROR <message>`)
- Reservation queue (`RESERVE`) with automatic issue on return, skipping members currently at their borrow limit

## Commands

Fields are separated by `|`. Reports with no matching rows print `NONE`.

| Command | Description | Output on Success |
|---|---|---|
| `ADD_BOOK\|id\|title\|author` | Add a book | `OK` |
| `ADD_MAGAZINE\|id\|title\|issue` | Add a magazine | `OK` |
| `ADD_REFERENCE\|id\|title\|author` | Add a reference book | `OK` |
| `ADD_MEMBER\|id\|name\|type` | Add a member (`STUDENT`, `STAFF`, `FACULTY`) | `OK` |
| `BORROW\|memberId\|itemId` | Lend an item to a member | `OK due on day <dueDay>` |
| `RETURN\|itemId` | Return an item and compute fine | `OK fine <amount>` (or `, issued to <memberId> due on day <dueDay>`) |
| `RESERVE\|memberId\|itemId` | Join an on-loan item's reservation queue | `OK reserved` |
| `ADVANCE\|days` | Move simulated time forward | `OK today is day <day>` |
| `SEARCH\|text` | Case-insensitive title search (sorted by title, then ID) | `id\|TYPE\|title` |
| `LOANS\|memberId` | Active loans of a member (sorted by due day, then item ID) | `itemId\|title\|dueDay` |
| `OVERDUE` | All overdue loans (most days late first, then item ID) | `itemId\|memberId\|daysLate\|fine` |
| `INVENTORY` | All items and status (sorted by item ID) | `itemId\|TYPE\|status` |

## Rules

### Items
| Type | Borrowable | Fine per Day Late |
|---|---|---|
| `Book` | Yes | ₹2 |
| `Magazine` | Yes | ₹1 |
| `ReferenceBook` | No | — |

### Members
| Type | Max Items at Once | Loan Period |
|---|---|---|
| `Student` | 3 | 14 days |
| `Staff` | 4 | 21 days |
| `Faculty` | 5 | 30 days |

### Borrowing, Fines & Reservations
- Time starts at day `0` and moves forward only via `ADVANCE`.
- `dueDay = borrowDay + loanPeriod`.
- `fine = daysLate * finePerDay` (`0` if returned on or before `dueDay`).
- An item can be on loan to only one member at a time.
- Only items currently on loan can be reserved; a member cannot reserve an item they currently hold or reserve the same item twice.
- On `RETURN`, a reserved item is automatically issued to the first queued member below their borrow limit (members at their limit are skipped and stay in the queue).

## Assumptions

- Every command in the input is well-formed: the right command name, the right number of fields, and valid numbers where numbers are expected.
- IDs used in `ADD_` commands are always unique; duplicate-ID handling is not required.
- Time is purely simulated starting at day `0`; the system clock is never used.

## Design

| Class / Interface | Kind | Role / Responsibility |
|---|---|---|
| `Main` | Class | Reads commands from `System.in`, formats sorted reports (`Stream` + `Comparator`), and catches `LibraryException` centrally |
| `Library` | Class | Coordinates items, members, active loans, reservation queues (`ArrayDeque`), and simulated `currentDay` |
| `Loan` | Class | Links a `Borrowable` item and a `Member`; stores `borrowDay` / `dueDay` and calculates overdue days and fines |
| `LibraryItem` | Abstract Class | Base class for all items (`Book`, `Magazine`, `ReferenceBook`) holding `id` and `title` |
| `Borrowable` | Interface | Implemented by borrowable items (`Book`, `Magazine`) to declare `getFinePerDay()` |
| `Member` | Abstract Class | Base class for all members (`Student`, `Staff`, `Faculty`) defining borrow limits and loan periods |
| `LibraryException` | Abstract Class | Base checked exception extended by all custom domain error classes |

## Project Structure

```text
.
├── src/
│   ├── Main.java                              # CLI entry point, command parser & report formatter
│   ├── Library.java                           # Core catalogue, loan, time & reservation manager
│   ├── Loan.java                              # Active loan record, due day & fine calculation
│   │
│   ├── LibraryItem.java                       # Abstract base class for all library items
│   ├── Borrowable.java                        # Interface for borrowable items
│   ├── Book.java                              # Borrowable book (₹2/day fine)
│   ├── Magazine.java                          # Borrowable magazine (₹1/day fine)
│   ├── ReferenceBook.java                     # Non-borrowable reference book
│   │
│   ├── Member.java                            # Abstract base class for all members
│   ├── Student.java                           # Student member (limit: 3, period: 14 days)
│   ├── Staff.java                             # Staff member (limit: 4, period: 21 days)
│   ├── Faculty.java                           # Faculty member (limit: 5, period: 30 days)
│   │
│   ├── LibraryException.java                  # Abstract base exception for all library errors
│   ├── InvalidInputException.java             # Malformed command, unknown type, or negative days
│   ├── ItemNotFoundException.java             # Unknown item ID
│   ├── MemberNotFoundException.java           # Unknown member ID
│   ├── ItemNotAvailableException.java         # Item already on loan or not borrowable
│   ├── BorrowLimitExceededException.java      # Member has reached maximum active loans
│   ├── ItemNotOnLoanException.java            # Returning an item that is not on loan
│   └── ReservationNotAllowedException.java    # Invalid reservation attempt
│
├── README.md                                  # Project documentation
└── UML.pdf                                    # Class diagram
```

## Example

**Input:**
```text
ADD_BOOK|B1|Introduction to Algorithms|Cormen
ADD_REFERENCE|R1|Mahabharata|Vyasa
ADD_MEMBER|S1|Ananya Rao|STUDENT
BORROW|S1|B1
BORROW|S1|R1
ADVANCE|20
OVERDUE
RETURN|B1
INVENTORY
```

**Output:**
```text
OK
OK
OK
OK due on day 14
ERROR R1 cannot be borrowed
OK today is day 20
B1|S1|6|12
OK fine 12
B1|BOOK|AVAILABLE
R1|REFERENCE|NOT_BORROWABLE
```
