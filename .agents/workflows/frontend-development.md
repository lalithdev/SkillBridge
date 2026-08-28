---
description: Step-by-step workflow for implementing frontend screens and features independently from the backend.
---

# SkillBridge — Frontend Development Workflow

## Purpose
Guide the frontend developer and assisting AI agents through the complete lifecycle of implementing, styling, testing, and integrating a frontend screen or feature in SkillBridge.

---

## 1. Independent Development Principle

> **Key Rule:** The frontend developer **DOES NOT need to wait for the backend to be completed**.

Frontend and backend tracks run concurrently during the hackathon:
- The frontend integrates directly against the **approved OpenAPI contract** (`docs/06-api/openapi.yaml`).
- If a backend endpoint is not yet live on the dev server, the frontend may temporarily utilize **isolated mock data** in the API service layer (e.g. `mockOpportunitiesData.js` or MSW handlers).
- **Mock Data Rules:**
  1. Must be strictly isolated in test/dev mock fixtures.
  2. Must match the exact DTO field names and types from `docs/06-api/openapi.yaml`.
  3. Must never alter or invent custom endpoint URLs.
  4. Must be cleanly swapped with live `apiClient` calls upon backend integration.

---

## 2. Step-by-Step Implementation Sequence

```
1. Read Rules & Skills ───► 2. Read PRD/SRS ───► 3. Read UX Flows & Screen Spec
                                                           │
┌──────────────────────────────────────────────────────────┘
▼
4. Check Design System & OpenAPI ───► 5. Build/Reuse Shared UI Components
                                                           │
┌──────────────────────────────────────────────────────────┘
▼
6. Implement Page Component ───► 7. Wire API / TanStack Query Hooks
                                                           │
┌──────────────────────────────────────────────────────────┘
▼
8. Handle 4 UI States ───► 9. Responsive & a11y Checks ───► 10. Test & Commit
```

1. **Read Project Rules & Skills:**
   - Inspect `.agents/rules/01-project.md`, `02-development.md`, `03-quality.md`, `04-architecture-guardrails.md`.
   - Activate skills: `frontend-development`, `ui-ux-design`, `react`, `shadcn-ui`, `responsive-design`, `accessibility`.
2. **Read PRD / SRS Requirements:**
   - Identify the functional requirements (`FR-STU-*`, `FR-COM-*`, `FR-COL-*`, `FR-ADM-*`, `FR-MATCH-*`, `FR-APP-*`, `FR-INT-*`, `FR-ANL-*`) relevant to the screen.
3. **Read UX User Flow:**
   - Consult `docs/03-ux/user-flows.md` for trigger actions, preconditions, and transition rules.
4. **Read Screen Specification:**
   - Consult `docs/03-ux/screen-specifications.md` for component layout, displayed data, and action controls.
5. **Read Design System Guidelines:**
   - Consult `docs/03-ux/design-system.md` for color tokens, badge styling, status matrices, and typography.
6. **Read OpenAPI Contract:**
   - Consult `docs/06-api/openapi.yaml` to identify exact request schemas, response DTOs, and HTTP status codes.
7. **Identify Components & API Calls:**
   - Check `src/components/ui/` for existing shadcn primitives.
   - Check `src/components/shared/` for reusable domain components (`SkillBadge`, `MatchScoreRing`, `StatusStepper`).
   - Define or locate domain API service functions in `src/api/`.
8. **Implement / Reuse UI Foundations:**
   - Construct missing domain feature components in `src/features/<domain>/`.
9. **Implement Route Page Component:**
   - Create or update the screen page file in `src/pages/<role>/`.
   - Register route in `src/routes/AppRoutes.jsx` with appropriate `<ProtectedRoute allowedRoles={[...]} />`.
10. **Implement API Integration with TanStack Query:**
    - Wrap API call in a custom hook (`useOpportunities`, `useApplications`, etc.) in `src/hooks/`.
    - Configure query keys, stale time, and mutation cache invalidation.
11. **Implement Deterministic 4-State Handling:**
    - Render skeleton shimmer on `isLoading`.
    - Render `<ErrorMessage onRetry={...} />` on `isError`.
    - Render `<EmptyState />` when data array is empty.
    - Render interactive data on `isSuccess`.
12. **Implement Responsive Adaptations:**
    - Ensure page adapts across Mobile (375px), Tablet (768px), and Desktop (1024px+).
    - Convert tables to stacked cards on mobile viewports.
13. **Verify Accessibility (a11y):**
    - Ensure keyboard navigability (`Tab`, `Escape`, `Enter`).
    - Verify form `<label>` associations, visible focus rings, and screen reader `aria-label`s.
14. **Run Verification & Tests:**
    - Run `npm run test` and `npm run build` to verify clean build without syntax/lint errors.
15. **Commit Focused Changes:**
    - Commit with conventional commit message (e.g. `feat(frontend): implement student skill management view`).

---

## 3. Strict Guardrails for Frontend Developers & AI Agents

The frontend developer and AI agent must **NEVER**:
- ❌ Change backend API paths or HTTP methods.
- ❌ Invent custom DTO fields not present in `docs/06-api/openapi.yaml`.
- ❌ Invent custom business rules or eligibility filters.
- ❌ Modify database, backend architecture, or PRD/SRS documents casually.
- ❌ Add product features outside approved MVP scope.
- ❌ Introduce another UI component library (MUI, Chakra, AntD).
- ❌ Introduce TypeScript (`.ts`, `.tsx`).

---

## 4. Checklists

### Checklist 1: Before Asking AI to Code a Screen
- [ ] Have I identified the Screen ID from `docs/03-ux/screen-specifications.md` (e.g. `SCR-STU-03`)?
- [ ] Have I identified the matching User Flow ID from `docs/03-ux/user-flows.md` (e.g. `Flow STU-02`)?
- [ ] Have I verified the API endpoints and response shapes in `docs/06-api/openapi.yaml`?
- [ ] Have I checked if shared components (`SkillBadge`, `MatchScoreRing`, `StatusStepper`) already exist?

### Checklist 2: Before Committing Code
- [ ] Does the screen strictly implement all 4 states (Loading, Success, Empty, Error)?
- [ ] Is the skill coverage disclaimer footnote present if match scores or availability % are shown?
- [ ] Are all form inputs controlled and mapped to server `fieldErrors`?
- [ ] Does the screen look clean and usable on mobile (375px) without horizontal scroll?
- [ ] Can the screen be navigated with the keyboard?
- [ ] Does `npm run build` compile without errors?
- [ ] Is the commit message formatted as `feat(frontend): ...` or `fix(frontend): ...`?
