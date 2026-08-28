---
name: responsive-design
description: Mobile-first layout rules, responsive breakpoints, adaptive data tables, and mobile navigation patterns for SkillBridge.
---

# SkillBridge — Responsive Design Skill

## Purpose
Ensure all 30 SkillBridge frontend screens adapt seamlessly across mobile phones, tablets, laptops, and wide desktop displays without layout breaking, horizontal overflow, or clipped data.

## When to Use
Use this skill whenever structuring page layouts, building data tables, designing navigation headers, organizing KPI card grids, or testing mobile viewport compatibility.

---

## Required Behavior

1. **Tailwind Breakpoint Standards:**
   - **Mobile (`sm`):** `< 640px` (Stacked single-column layout, touch-friendly elements)
   - **Tablet (`md`):** `640px – 1023px` (2-column grids, condensed tables)
   - **Desktop (`lg`):** `1024px – 1279px` (Full persistent sidebar, 3/4-column grids, detailed tables)
   - **Wide Desktop (`xl`):** `\ge 1280px` (Max-width container `max-w-7xl mx-auto`)

2. **Mobile-First CSS Utility Approach:**
   - Write base styles for mobile, then layer responsive prefixes:
     `className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4"`
   - Avoid desktop-first fixed widths (`w-[1200px]`). Always use fluid widths (`w-full max-w-5xl`).

3. **Responsive Navigation Pattern (`AppShell`):**
   - **Desktop ($\ge 1024px$):** Fixed left sidebar (`w-64`), main content area offset (`ml-64`).
   - **Mobile / Tablet ($< 1024px$):** Left sidebar hidden; top navbar renders mobile hamburger icon triggering a full-height slide-over `<Sheet side="left">`.

---

## Project-Specific Rules

- **Adaptive Data Tables on Mobile ($< 768px$):**
  - Complex data tables (Applicant Roster, Student Roster, Opportunity Manager) must not force awkward horizontal page scrolling on phones.
  - On viewports `< 768px`, render either:
    1. A responsive card list layout with clear label-value pairs and full-width action buttons.
    2. An explicitly scrollable table container (`overflow-x-auto`) wrapped inside a card with boundary shadows.
- **Touch Hit Target Sizing:**
  - All interactive buttons, icon triggers, and dropdown items on mobile must have a minimum touch target size of **44x44px** (`min-h-[44px]`).
- **Responsive Chart Containers:**
  - Wrap all Recharts visualizations in `<ResponsiveContainer width="100%" height={300}>`.
  - Grouped bar charts on mobile must reduce tick label font sizes or switch to horizontal orientation for readable skill names.

---

## Do / Don't Guidance

### Do:
- Test layouts across standard viewport widths: 375px (Mobile), 768px (Tablet), 1024px (Small Desktop), 1440px (Wide Screen).
- Use `flex-wrap` on skill badge grids and filter chip containers.
- Use sticky bottom action bars (`fixed bottom-0 left-0 right-0 p-4 bg-white border-t`) on mobile for long forms like Opportunity Detail apply actions.

### Don't:
- **Do NOT allow viewport horizontal scrolling (`overflow-x-hidden` on body).**
- **Do NOT use fixed pixel heights on text containers** (`h-24`) that cause text truncation when font scales.
- **Do NOT hide critical action buttons (e.g. "Apply", "Submit") on mobile devices.**

---

## Definition of Good Implementation
A screen is responsively sound when:
1. It looks polished, legible, and balanced on a 375px mobile screen as well as a 1440px desktop display.
2. Form fields and buttons are easy to tap with fingers on mobile devices.
3. No components overlap, truncate unexpectedly, or break out of container boundaries.
