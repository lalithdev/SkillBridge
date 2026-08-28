---
name: frontend-testing
description: Practical frontend testing expectations, component testing, interaction verification, and critical user-flow tests for SkillBridge.
---

# SkillBridge — Frontend Testing Skill

## Purpose
Define the practical testing standards, component verification rules, and critical user-flow test expectations for the SkillBridge React frontend without introducing unnecessary testing overhead.

## When to Use
Use this skill whenever writing frontend unit tests, testing component state rendering, verifying user interaction flows, or testing API integration error handling.

---

## Required Behavior

1. **Approved Testing Tools:**
   - **Test Runner:** Vitest (fast, native Vite integration)
   - **Component Testing:** React Testing Library (`@testing-library/react`, `@testing-library/user-event`)
   - **DOM Matchers:** `@testing-library/jest-dom`
   - **API Mocking:** Mock Service Worker (MSW) or Vitest spy/mock functions (`vi.spyOn`)

2. **Core Testing Priorities:**
   - **Priority 1: Reusable Domain Components** (`SkillBadge`, `MatchScoreRing`, `StatusStepper`, `EmptyState`, `ErrorMessage`).
   - **Priority 2: 4-State Rendering Verification** (Verify that screens correctly show Skeleton on loading, Error on 500, Empty on empty list, and Data on 200).
   - **Priority 3: Critical User Flows** (Login, Opportunity Filter & Search, Application Submit modal, Skill Add/Delete, Pipeline Stage Transition).
   - **Priority 4: Form Validations** (CGPA range bounds, password length, required field alerts).

---

## Project-Specific Rules

- **No Over-Testing of Implementation Details:** Test what the user sees and interacts with (rendered text, accessible roles, clicks), NOT internal React component state variables or private functions.
- **Mocking TanStack Query:** Wrap test components in a clean `QueryClientProvider` initialized with `retry: false` to ensure test failures fail immediately without timeouts:
  ```javascript
  const createTestQueryClient = () =>
    new QueryClient({
      defaultOptions: {
        queries: { retry: false, gcTime: 0 },
        mutations: { retry: false },
      },
    });
  ```
- **Mocking AuthContext:** Provide a mocked `AuthProvider` value containing sample user profiles (`role: 'STUDENT'`, `role: 'COMPANY'`, etc.) to test role-guarded routes.

---

## Do / Don't Guidance

### Do:
- Query elements by accessible role and text (`screen.getByRole('button', { name: /apply now/i })`, `screen.getByLabelText(/email/i)`).
- Verify that clicking "Apply Now" opens the confirmation dialog and snapshot notice.
- Test that missing required skills correctly render with dashed border styling.
- Test that API `400 Bad Request` validation errors render inline beneath offending inputs.

### Don't:
- **Do NOT introduce heavy end-to-end (E2E) frameworks (Cypress, Playwright) for basic MVP unit tests** unless explicitly requested.
- **Do NOT write snapshot tests that break on every minor CSS change.**
- **Do NOT test third-party library internals** (e.g. testing whether shadcn Radix UI handles focus trapping natively).
- **Do NOT ignore failing tests or disable test assertions.**

---

## Definition of Good Implementation
Frontend testing is complete when:
1. All critical domain components (`MatchScoreRing`, `SkillBadge`, `StatusStepper`) have unit test coverage.
2. The 4 UI states (Loading, Success, Empty, Error) are verified for major dashboard screens.
3. Tests run quickly via `npm run test` and pass 100% cleanly without warnings or unhandled rejections.
