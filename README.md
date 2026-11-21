# Library Management System

A Java course project to design and build a command-driven college library management system.

## Current milestone

**Milestone 1 — Design**

This repository currently contains the UML class diagram and design documentation. The Java implementation has not started yet.

## Design overview

The design models:

- **Library items:** an abstract `LibraryItem` superclass with `Book`, `Magazine`, and `ReferenceBook` subclasses.
- **Borrowing capability:** a `Borrowable` interface implemented by `Book` and `Magazine`.
- **Members:** an abstract `Member` superclass with `Student`, `Staff`, and `Faculty` subclasses. Each member type defines its own borrowing limit and loan period.
- **Loans:** a separate `Loan` class that records the item, member, borrowing day, and due day, and provides overdue and fine calculations.
- **Library coordination:** `Library` maintains the catalogue, members, active loans, and simulated current day.
- **Exceptions:** custom exceptions represent domain failures. Additional input validation is planned for malformed commands.

## Optional reservations

The design also accommodates the optional reservation feature from the project brief. Reservations are maintained in a per-item FIFO queue. When an item is returned, it can be issued to the first eligible member in the queue; members at their borrowing limit are skipped and remain queued.

## UML diagram

See [UML.pdf](UML.pdf) for the Milestone 1 class diagram.

## Project brief

The project follows the course-provided **Capstone Project — Library Management System** specification. The implementation will be added in subsequent milestones.

## Status

- [x] Milestone 1: Design and UML class diagram
- [ ] Milestone 2: Core Java implementation
- [ ] Milestone 3: Reports, testing, and final submission
- [ ] Optional: Reservation implementation
