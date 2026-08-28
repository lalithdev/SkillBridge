---
trigger: always_on
---

# SkillBridge — Quality Rules

Every completed feature must:

1. Match the PRD.
2. Match the SRS.
3. Follow the architecture.
4. Follow the database design.
5. Follow the API contract.
6. Validate inputs.
7. Handle errors.
8. Enforce authorization.
9. Handle loading/empty/error states.
10. Have appropriate tests.
11. Pass build.
12. Pass lint/type checks.
13. Not break existing functionality.

## Before marking a task complete

Run appropriate:
- unit tests
- integration tests
- build
- lint
- type checking

## If something fails

Do not ignore the failure.

Investigate it.

Fix the root cause.

Run verification again.

## Scope Control

Do not refactor unrelated code during a feature task.

Do not add "nice to have" functionality unless requested.

## Completion

Never claim a task is complete merely because
the code was written.

A feature is not done until it satisfies requirements, acceptance criteria, tests, build, security and UI states.

It is complete only after verification.