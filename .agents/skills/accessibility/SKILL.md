---
name: accessibility
description: WCAG 2.1 AA accessibility guidelines, keyboard navigation, focus management, semantic HTML, and ARIA standards for SkillBridge.
---

# SkillBridge — Accessibility (a11y) Skill

## Purpose
Ensure the SkillBridge web application conforms to **WCAG 2.1 Level AA** accessibility standards, providing an inclusive experience for students, employers, and college administrators across all assistive technologies and input devices.

## When to Use
Use this skill whenever constructing forms, writing markup, managing keyboard focus, choosing color combinations, creating dialogs, or handling screen reader announcements.

---

## Required Behavior

1. **Semantic HTML Landmarks:**
   - Use proper HTML5 landmark elements: `<main>`, `<nav>`, `<header>`, `<aside>`, `<section>`, `<footer>`.
   - Never replace native semantic elements with generic `<div>` wrappers when a semantic tag exists (e.g. use `<button>` for actions, `<a>` or `<Link>` for navigation).
   - Use a single `<h1>` per page, followed by a hierarchical `<h2>`, `<h3>`, `<h4>` structure.

2. **Keyboard Navigability & Focus Management:**
   - Every interactive control must be reachable and operable using `Tab` and `Shift+Tab`.
   - Modals (`<Dialog>`) and slide-over sheets (`<Sheet>`) must trap keyboard focus while open and restore focus to the triggering element upon closure.
   - Modals and dropdowns must close upon pressing the `Escape` key.
   - Autocomplete dropdowns must support keyboard arrow navigation (`ArrowUp`, `ArrowDown`) and `Enter` selection.

3. **Visible Focus Indicators:**
   - All interactive elements must exhibit an unambiguous, high-contrast focus indicator:
     `focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-600 focus-visible:ring-offset-2`
   - Never disable outline or ring styles on keyboard focus (`outline: none` without replacement is forbidden).

4. **Accessible Forms & Input Labels:**
   - Every input, select, and textarea must be explicitly associated with a `<label>` via `htmlFor` and `id`.
   - Validation errors must be linked to the offending input using `aria-describedby="field-error-id"`.
   - Mark required inputs with `aria-required="true"`.

---

## Project-Specific Rules

- **Icon-Only Buttons Must Have Screen-Reader Labels:**
  - Any button containing only an icon (e.g. table delete icon, modal close "×", copy link button) must include an accessible label:
    ```jsx
    <Button variant="ghost" size="sm" aria-label="Delete skill React">
      <X className="w-4 h-4" />
    </Button>
    ```
- **Color Contrast Compliance:**
  - Maintain a minimum contrast ratio of **4.5:1** for standard text and **3.0:1** for large headings and UI component borders against their backgrounds.
  - Never convey state purely through color; always pair color with text or an icon (e.g. badge displays both green background and checkmark icon with text `"VERIFIED"`).

---

## Do / Don't Guidance

### Do:
- Use `aria-live="polite"` on status updates or search results counters so screen readers announce dynamic changes.
- Provide descriptive alternative text (`alt="..."`) on all static images and company logos.
- Test tab order across multi-column forms and modal dialogs.

### Don't:
- **Do NOT use `tabindex` greater than 0** (disrupts natural tab order).
- **Do NOT trap keyboard focus** anywhere other than open modal dialogs.
- **Do NOT use non-interactive elements (`<div>`, `<span>`) for click handlers** without `role="button"` and keyboard event listeners.

---

## Definition of Good Implementation
A view is accessible when:
1. It can be fully navigated and operated using only a keyboard.
2. Form errors and modal transitions are announced clearly by screen readers.
3. It passes automated a11y checks (axe / Lighthouse Accessibility score $\ge 95$) with zero contrast or missing label violations.
