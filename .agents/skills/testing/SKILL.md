---
name: testing
description: Practical testing expectations across backend and frontend for SkillBridge.
---

# SkillBridge — Testing Skill

## Purpose
Ensure quality and verification across backend and frontend before code is committed or merged.

## Required Behavior
1. **Backend Testing:** Unit and slice tests with JUnit 5 and Mockito. Test core business rules (skill matching, eligibility, stage transitions).
2. **Frontend Testing:** Component and interaction tests with Vitest and React Testing Library. Test domain components and 4 UI states.
3. **Verification Before Completion:** Never mark a task complete without running `./mvnw test` or `npm run test` and ensuring 100% passing tests.
