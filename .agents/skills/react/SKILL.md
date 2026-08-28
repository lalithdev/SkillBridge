---
name: react
description: React 18 component patterns, hooks, lifecycle, React Router, TanStack Query integration, and state management rules for SkillBridge.
---

# SkillBridge — React Development Skill

## Purpose
Guide the construction of React 18 functional components, custom hooks, route guards, and state management in the SkillBridge frontend.

## When to Use
Use this skill whenever creating or refactoring React components, writing hooks, handling component state, setting up routes, or integrating queries and mutations.

---

## Required Behavior

1. **React 18 Functional Components:**
   - Write modern functional components using pure JavaScript (no TypeScript).
   - Keep components focused, single-responsibility, and composable.
   - Use default or named exports consistently matching the component filename (`export default function OpportunityCard(...)`).
2. **Hook Discipline:**
   - Encapsulate all server state fetching and mutations in custom hooks under `src/hooks/` (e.g. `useOpportunities.js`, `useApplications.js`).
   - Prefix custom hooks with `use`.
   - Never call hooks conditionally or inside loops.
3. **Routing Architecture (React Router v6):**
   - Place all routes in `src/routes/AppRoutes.jsx`.
   - Guard role-scoped routes using `<ProtectedRoute allowedRoles={['STUDENT']} />`.
   - Use `useNavigate()` and `<Link to="...">` for client-side transitions.
4. **Server vs. Local State Separation:**
   - Server data: **TanStack Query** (`useQuery`, `useMutation`, `useQueryClient`).
   - Form inputs and modal toggles: **`useState`**.
   - Complex multi-step filtering/sorting: **`useReducer`**.
   - Global authentication: **`AuthContext`** (`useAuth`).

---

## Project-Specific Rules

- **Pure JavaScript Only:** All files must have `.jsx` or `.js` extension. Never write TypeScript syntax (`interface`, `type`, `as string`, `<T>`).
- **No Class Components:** Use functional components with standard React hooks.
- **Cache Invalidation on Mutation:** Every successful mutation must invalidate corresponding query keys (e.g., submitting an application invalidates `queryKeys.applications.my()`).
- **Clean Component Cleanup:** Always clean up event listeners, timers, or abort controllers in `useEffect` return functions.

---

## Do / Don't Guidance

### Do:
- Destructure props cleanly with sensible default values (`function SkillBadge({ name, category, onRemove = null })`).
- Memoize expensive analytics aggregations with `useMemo` where appropriate.
- Separate page containers (in `src/pages/`) from presentation components (in `src/features/` and `src/components/`).
- Use React Error Boundaries to catch unhandled rendering exceptions gracefully.

### Don't:
- **Do NOT introduce global Redux or Zustand stores.**
- **Do NOT copy server query data into local state** (anti-pattern: `const [list, setList] = useState(query.data)`).
- **Do NOT perform direct DOM manipulations** (`document.getElementById`). Use React `useRef` when DOM access is required.
- **Do NOT make raw Axios calls inside components.** Use custom hooks wrapping `src/api/*`.

---

## Definition of Good Implementation
A React component is well-architected when:
1. It is declarative, pure, and easy to read.
2. It relies on TanStack Query for server state and local hooks for UI state.
3. It triggers no redundant re-renders and produces zero React console warnings.
4. It isolates business logic in reusable hooks.
