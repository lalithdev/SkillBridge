---
description: Guide for developing a frontend feature or screen in SkillBridge.
---

# Frontend Feature Development

Follow the comprehensive frontend implementation workflow:
See `frontend-development.md` for full step-by-step instructions, checklists, and guardrails.

## Quick Sequence
1. Read relevant requirements in `docs/01-product/PRD.md` and `docs/02-requirements/SRS.md`.
2. Inspect UX flow in `docs/03-ux/user-flows.md` and screen spec in `docs/03-ux/screen-specifications.md`.
3. Check `docs/03-ux/design-system.md` for design tokens and components.
4. Verify backend contract in `docs/06-api/openapi.yaml`.
5. Implement feature components in `src/features/<domain>/` and page in `src/pages/<role>/`.
6. Integrate TanStack Query hook in `src/hooks/` and API client in `src/api/`.
7. Handle 4 UI states (Loading, Success, Empty, Error).
8. Ensure responsive layout (mobile-first) and accessibility (WCAG 2.1 AA).
9. Test and verify build (`npm run test`, `npm run build`).
10. Commit with conventional commit message (`feat(frontend): ...`).
