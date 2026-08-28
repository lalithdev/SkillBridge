---
name: ui-ux-design
description: Enforce visual hierarchy, color tokens, typography, spacing, and interaction standards from the SkillBridge Design System.
---

# SkillBridge — UI/UX Design Skill

## Purpose
Ensure all frontend screens and components strictly adhere to the visual, typographic, color, and interaction standards defined in `docs/03-ux/design-system.md`.

## When to Use
Use this skill whenever styling components, designing layouts, configuring visual tokens, setting up dashboard widgets, or structuring user interaction patterns.

---

## Required Behavior

1. **Follow the Design System Baseline (`docs/03-ux/design-system.md`):**
   - **Font Family:** `Inter` (sans-serif) with tailored line-heights and letter-spacing (`-0.02em` on headings).
   - **Spacing Grid:** Strict 8pt grid (`p-2`, `p-4`, `p-6`, `p-8`, `gap-4`, `gap-6`).
   - **Border Radius:** `rounded-md` (6px) for inputs/buttons; `rounded-lg` (8px) for cards/tables; `rounded-full` for badges/avatars.
   - **Elevation:** Flat border-first aesthetic (`border border-slate-200 shadow-none`); `shadow-sm` for card hover; `shadow-lg` for modals/drawers.

2. **Semantic Color System Tokens:**
   - **Primary Brand:** Indigo (`bg-indigo-600`, `text-indigo-600`, `hover:bg-indigo-700`, `bg-indigo-50`).
   - **Neutrals (Slate):** Background `#FFFFFF`, Muted `#F8FAFC`, Border `#E2E8F0`, Text Primary `#0F172A`, Text Muted `#94A3B8`.
   - **Success / Match:** Emerald (`bg-emerald-600`, `bg-emerald-50`, `text-emerald-700`).
   - **Warning / Pending:** Amber (`bg-amber-500`, `bg-amber-50`, `text-amber-800`).
   - **Destructive / Error:** Rose (`bg-rose-600`, `bg-rose-50`, `text-rose-700`).
   - **Info / Neutral:** Sky/Blue (`bg-sky-600`, `bg-sky-50`, `text-sky-700`).

3. **Domain Status Visual Matrix:**
   - **Applications:**
     - `APPLIED` → Sky (`bg-sky-50 text-sky-700 border-sky-200`)
     - `UNDER_REVIEW` → Purple (`bg-purple-50 text-purple-700 border-purple-200`)
     - `SHORTLISTED` → Amber (`bg-amber-50 text-amber-700 border-amber-200`)
     - `INTERVIEW` → Indigo (`bg-indigo-50 text-indigo-700 border-indigo-200`)
     - `SELECTED` → Emerald (`bg-emerald-50 text-emerald-700 border-emerald-200`)
     - `REJECTED` → Rose (`bg-rose-50 text-rose-700 border-rose-200`)
   - **Verifications:**
     - `VERIFIED` → Emerald with `<CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />`
     - `PENDING` → Amber with `<Clock className="w-3.5 h-3.5 text-amber-500" />`
     - `REJECTED` → Rose with `<XCircle className="w-3.5 h-3.5 text-rose-500" />`
   - **Skill Gap Severity Bands (OD-01):**
     - **HIGH GAP ($\ge 30\%$):** Red badge (`bg-rose-50 text-rose-700 border-rose-200`)
     - **MODERATE GAP ($15\% - 30\%$):** Amber badge (`bg-amber-50 text-amber-700 border-amber-200`)
     - **LOW GAP ($0\% - 15\%$):** Green badge (`bg-emerald-50 text-emerald-700 border-emerald-200`)
     - **SURPLUS ($\le 0\%$):** Blue badge (`bg-blue-50 text-blue-700 border-blue-200`)

---

## Project-Specific Rules

- **No Arbitrary Color Values:** Always use Tailwind tokens mapped in the design system. Do NOT write inline arbitrary hex codes (`bg-[#123456]`).
- **Standardized Match Ring:** Render `<MatchScoreRing />` with $\ge 75\%$ green, $50-74\%$ amber, and $<50\%$ slate.
- **Skill Coverage Disclaimer:** Every screen displaying match % or availability % must include the standard disclaimer note.

---

## Do / Don't Guidance

### Do:
- Use consistent card paddings (`p-6` for content cards, `p-4` for compact list items).
- Pair icons with text labels in buttons and status badges.
- Use uppercase or title case for badge text (`font-semibold text-xs tracking-wider`).
- Maintain visual balance between KPI metric counters and detailed tables.

### Don't:
- **Do NOT introduce dark mode toggles or high-contrast themes not in design system.**
- **Do NOT use saturated or neon colors** that break institutional professional tone.
- **Do NOT create custom modal or drawer overlays** when shadcn `<Dialog>` and `<Sheet>` are standard.

---

## Definition of Good Implementation
A view satisfies UI/UX design standards when it is indistinguishable from the approved design system, maintains visual rhythm and hierarchy, renders sharp typography on all devices, and applies consistent status styling across all 30 screens.
