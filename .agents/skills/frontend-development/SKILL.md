---
name: frontend-development
description: Master frontend implementation standards, project architecture, API integration, and coding conventions for SkillBridge.
---

# SkillBridge — Frontend Development Skill

## Purpose
Enforce the overall frontend implementation standards, folder structure, API integration, state management, and coding discipline for the SkillBridge web application.

## When to Use
Use this skill whenever creating, modifying, organizing, or reviewing React frontend code, API service layers, hooks, or pages in SkillBridge.

---

## Required Behavior

1. **Approved Tech Stack Only:**
   - **Framework:** React 18 (Vite)
   - **Language:** Pure JavaScript (ES2022+). **Do NOT introduce TypeScript (`.ts`, `.tsx`).**
   - **Styling:** Tailwind CSS + shadcn/ui
   - **Routing:** React Router v6
   - **HTTP Client:** Axios
   - **Server State:** TanStack Query v5
   - **Local State:** `useState`, `useReducer`, React `AuthContext`
   - **Charts:** Recharts
   - **Icons:** Lucide React
2. **Directory & File Organization:**
   Follow the approved structure in `docs/03-ux/frontend-architecture.md`:
   - `src/api/` — Domain API client functions mapped to OpenAPI endpoints.
   - `src/components/ui/` — Direct shadcn/ui primitives.
   - `src/components/layout/` — `AppShell`, `Sidebar`, `Navbar`, `PublicNavbar`.
   - `src/components/shared/` — `SkillBadge`, `MatchScoreRing`, `VerificationBadge`, `StatusStepper`, `EmptyState`, `ErrorMessage`, `LoadingSpinner`.
   - `src/features/` — Domain-specific feature components (forms, tables, cards, modals).
   - `src/hooks/` — Custom React & TanStack Query hooks (`useOpportunities`, `useApplications`, etc.).
   - `src/pages/` — Route-level screen components matching `docs/03-ux/screen-specifications.md`.
   - `src/routes/` — `AppRoutes.jsx` and `ProtectedRoute.jsx`.
   - `src/context/` — `AuthContext.jsx` for JWT and session management.
   - `src/utils/` — Pure formatters, validators, and constants.
3. **Deterministic 4-State UI Handling:**
   Every data-fetching screen and component must explicitly implement:
   - **Loading State:** Shimmer skeleton (`<SkeletonCard />`, `<SkeletonTable />`).
   - **Success State:** Interactive rendered data.
   - **Empty State:** `<EmptyState title="..." description="..." action={<Button .../>} />`.
   - **Error State:** `<ErrorMessage message="..." onRetry={refetch} />`.

---

## Project-Specific Rules

- **Strict API Contract Conformance:** All API calls must strictly consume endpoints and DTO shapes defined in `docs/06-api/openapi.yaml`. Never invent mock endpoints or modify backend route paths.
- **Stateless Bearer JWT:** Store JWT in `sessionStorage` and `AuthContext`. Attach `Authorization: Bearer <token>` via Axios interceptor. On `401 Unauthorized`, purge token and navigate to `/login`.
- **Server State Separation:** Never duplicate server data into local `useState` unless specifically editing a form. Use TanStack Query hooks for all server data.
- **Skill Coverage Semantics:** Always include the mandatory skill coverage footnote wherever match scores or availability percentages are displayed:
  > *"Skill match/availability measures self-reported skill presence and curriculum coverage, not verified individual proficiency."*

---

## Do / Don't Guidance

### Do:
- Place API calls in `src/api/<domain>Api.js` and wrap them in custom TanStack Query hooks in `src/hooks/`.
- Use DTO field names matching backend JSON (`matchPercent`, `opportunityId`, `studentProfileId`).
- Use controlled form inputs with local `useState` and map API `fieldErrors` directly to inputs.
- Reuse shared components (`SkillBadge`, `MatchScoreRing`, `StatusStepper`) across screens.

### Don't:
- **Do NOT install Redux, MobX, Recoil, or Zustand.**
- **Do NOT introduce TypeScript.**
- **Do NOT write inline fetch calls or bypass `src/api/client.js`.**
- **Do NOT invent frontend-only features** not present in PRD, SRS, or Screen Specifications.
- **Do NOT store sensitive credentials or JWT secrets in `.env` files.**

---

## Definition of Good Implementation
A feature is well-implemented when:
1. It matches the corresponding screen specification in `docs/03-ux/screen-specifications.md`.
2. It interacts cleanly with backend REST APIs via OpenAPI-compliant DTOs.
3. It handles loading, error, empty, and success states deterministically.
4. It passes lint, build (`npm run build`), and component tests without console warnings.
