# SkillBridge — Frontend Design System

**Phase:** System Design (UX / Frontend Architecture)  
**Version:** 1.0  
**Status:** APPROVED — Design System Baseline  
**Date:** 2026-08-28  
**Derived from:** PRD.md · SRS.md · user-flows.md · screen-specifications.md · frontend-architecture.md  
**Target Stack:** React 18 (Vite, JavaScript), Tailwind CSS v3, shadcn/ui, Recharts, Lucide React Icons  

---

## Table of Contents

1. [Design Principles & Philosophy](#1-design-principles--philosophy)
2. [Color Palette & Semantic Tokens](#2-color-palette--semantic-tokens)
3. [Typography & Text Hierarchy](#3-typography--text-hierarchy)
4. [Spacing, Sizing & Layout Foundations](#4-spacing-sizing--layout-foundations)
5. [Core UI Component Standards (shadcn/ui + Tailwind)](#5-core-ui-component-standards-shadcnui--tailwind)
6. [Domain-Specific Data Visualizations & Indicators](#6-domain-specific-data-visualizations--indicators)
7. [Forms, Inputs & Validation Behavior](#7-forms-inputs--validation-behavior)
8. [UI State Handling & Feedback Architecture](#8-ui-state-handling--feedback-architecture)
9. [Responsive Breakpoints & Layout Adaptations](#9-responsive-breakpoints--layout-adaptations)
10. [Accessibility & Interaction Standards](#10-accessibility--interaction-standards)
11. [Component Directory & Implementation Blueprint](#11-component-directory--implementation-blueprint)

---

## 1. Design Principles & Philosophy

The SkillBridge design system is built to provide an institutional-grade, modern, and accessible interface for academia and industry collaboration.

```
       ┌────────────────────────────────────────────────────────┐
       │                Core Design Pillars                     │
       └────────────────────────────────────────────────────────┘
         │               │                │               │
  ┌──────▼──────┐ ┌──────▼──────┐  ┌──────▼──────┐ ┌──────▼──────┐
  │Professional │ │    Clean    │  │ Accessible  │ │ Determin-   │
  │  & Trust-   │ │  & Focused  │  │ & Inclusive │ │   istic     │
  │  Inspiring  │ │ (Low Noise) │  │  (WCAG AA)  │ │  (4 States) │
  └─────────────┘ └─────────────┘  └─────────────┘ └─────────────┘
```

1. **Professional & Trust-Inspiring:** Crisp typography, tailored elevation, structured data tables, and distinct institutional verification badges that build trust between colleges, students, and employers.
2. **Clean & Focused (Low Visual Noise):** High information density without clutter. Generous whitespace, scannable data layouts, and clear typographic hierarchy.
3. **Explicit Semantic Clarity:** Matching scores and availability percentages explicitly communicate **coverage/presence** rather than proficiency.
4. **Deterministic UI State Consistency:** Every view cleanly implements four distinct states: **Loading**, **Success**, **Empty**, and **Error**.
5. **Accessible & Responsive:** WCAG 2.1 AA compliant color contrasts, visible focus rings, full keyboard navigability, and responsive layouts.

---

## 2. Color Palette & Semantic Tokens

The color system uses Tailwind CSS utility classes mapped to CSS custom variables in HSL format for consistent theming and dark-mode readiness.

### 2.1 Neutral Base Palette (Slate Scale)

| Token Name | CSS Variable / Class | Hex Code | Primary Purpose |
|---|---|---|---|
| `background` | `hsl(0, 0%, 100%)` (`bg-background`) | `#FFFFFF` | Global page background |
| `surface` / `card` | `hsl(0, 0%, 100%)` (`bg-card`) | `#FFFFFF` | Container cards, modals, dropdowns |
| `surface-muted` | `hsl(210, 40%, 96.1%)` (`bg-slate-50`) | `#F8FAFC` | Table headers, secondary sidebars, input backgrounds |
| `border` | `hsl(214.3, 31.8%, 91.4%)` (`border-slate-200`)| `#E2E8F0` | Dividers, card borders, table row borders |
| `border-focus` | `hsl(221.2, 83.2%, 53.3%)` (`ring-primary`) | `#2563EB` | Active keyboard focus rings |
| `text-primary` | `hsl(222.2, 47.4%, 11.2%)` (`text-slate-900`)| `#0F172A` | Page headings, primary table data, high-emphasis text |
| `text-secondary`| `hsl(215.4, 16.3%, 46.9%)` (`text-slate-600`)| `#475569` | Subheadings, descriptions, body copy, timestamps |
| `text-muted` | `hsl(215, 16%, 57%)` (`text-slate-400`)| `#94A3B8` | Placeholder text, disabled labels, non-interactive icons |

### 2.2 Brand & Semantic Accent Tokens

```
  PRIMARY BRAND         SUCCESS / MATCH       WARNING / PENDING     DESTRUCTIVE / HIGH GAP
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐   ┌──────────────────────┐
│  Indigo 600     │   │  Emerald 600    │   │  Amber 500      │   │  Rose 600            │
│  #4F46E5        │   │  #059669        │   │  #D97706        │   │  #E11D48             │
│  Primary CTAs   │   │  Verified,      │   │  Under Review,  │   │  Rejected, Closed,   │
│  Active Links   │   │  Matched Skills │   │  Moderate Gap   │   │  High Gap (>=30%)    │
└─────────────────┘   └─────────────────┘   └─────────────────┘   └──────────────────────┘
```

| Semantic Role | Tailwind Class | Hex Value | Application Context |
|---|---|---|---|
| **Primary** | `bg-indigo-600`, `text-indigo-600` | `#4F46E5` | Main CTAs, active navigation items, brand accents |
| **Primary Hover** | `hover:bg-indigo-700` | `#4338CA` | Hover state for primary buttons |
| **Primary Light** | `bg-indigo-50`, `text-indigo-700` | `#EEF2FF` | Active sidebar item background, primary badge tint |
| **Success** | `bg-emerald-600`, `text-emerald-600` | `#059669` | `SELECTED` status, `VERIFIED` badge, Low Gap, Matched Skill |
| **Success Light** | `bg-emerald-50`, `text-emerald-700` | `#ECFDF5` | Success alert background, matched skill badge background |
| **Warning** | `bg-amber-500`, `text-amber-600` | `#D97706` | `PENDING` verification, Moderate Gap (15-30%), Ineligible alert |
| **Warning Light** | `bg-amber-50`, `text-amber-800` | `#FFFBEB` | Warning banner background, pending badge background |
| **Destructive / Error** | `bg-rose-600`, `text-rose-600` | `#E11D48` | `REJECTED` status, High Gap ($\ge 30\%$), Form validation error |
| **Destructive Light** | `bg-rose-50`, `text-rose-700` | `#FFF1F2` | Error banner background, rejected badge tint |
| **Info / Neutral** | `bg-sky-600`, `text-sky-600` | `#0284C7` | `APPLIED` status, Surplus Skill indicator ($\le 0\%$), tooltips |
| **Info Light** | `bg-sky-50`, `text-sky-700` | `#F0F9FF` | Informational callout background |

### 2.3 Domain Status Color Matrix

| Domain Status | Badge Background | Badge Text | Icon Color | Visual Meaning |
|---|---|---|---|---|
| **Application: `APPLIED`** | `bg-sky-50` | `text-sky-700 border-sky-200` | `text-sky-500` | Initial candidate submission |
| **Application: `UNDER_REVIEW`**| `bg-purple-50` | `text-purple-700 border-purple-200`| `text-purple-500`| Employer is inspecting profile |
| **Application: `SHORTLISTED`** | `bg-amber-50` | `text-amber-700 border-amber-200` | `text-amber-500` | Passed initial screening |
| **Application: `INTERVIEW`** | `bg-indigo-50` | `text-indigo-700 border-indigo-200`| `text-indigo-500`| Interview stage scheduled/active |
| **Application: `SELECTED`** | `bg-emerald-50`| `text-emerald-700 border-emerald-200`| `text-emerald-500`| Hired / Internship record created |
| **Application: `REJECTED`** | `bg-rose-50` | `text-rose-700 border-rose-200` | `text-rose-500` | Not selected for role |
| **Verification: `VERIFIED`** | `bg-emerald-50`| `text-emerald-700 border-emerald-200`| `text-emerald-600`| Organization confirmed by Admin |
| **Verification: `PENDING`** | `bg-amber-50` | `text-amber-700 border-amber-200` | `text-amber-500` | Awaiting Admin review |
| **Verification: `REJECTED`** | `bg-rose-50` | `text-rose-700 border-rose-200` | `text-rose-500` | Verification rejected |
| **Gap: High ($\ge 30\%$)** | `bg-rose-50` | `text-rose-700 border-rose-200` | `text-rose-600` | Immediate training needed |
| **Gap: Moderate ($15-30\%$)** | `bg-amber-50` | `text-amber-700 border-amber-200` | `text-amber-600` | Elective curriculum focus |
| **Gap: Low ($0-15\%$)** | `bg-emerald-50`| `text-emerald-700 border-emerald-200`| `text-emerald-600`| Balanced talent supply |
| **Gap: Surplus ($\le 0\%$)** | `bg-blue-50` | `text-blue-700 border-blue-200` | `text-blue-600` | Availability exceeds demand |

---

## 3. Typography & Text Hierarchy

SkillBridge uses **Inter** (fallback `system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`) for clarity across data tables, analytical charts, and form inputs.

```
  DISPLAY / HEADINGS             BODY & DATA COPY               LABELS & BADGES
┌────────────────────────────┐ ┌────────────────────────────┐ ┌────────────────────────────┐
│ Inter SemiBold / Bold      │ │ Inter Regular / Medium     │ │ Inter Medium / SemiBold    │
│ 18px – 30px                │ │ 14px – 16px                │ │ 12px – 13px                │
│ Letter Spacing: -0.02em    │ │ Line Height: 1.5           │ │ Uppercase or Title Case    │
└────────────────────────────┘ └────────────────────────────┘ └────────────────────────────┘
```

### 3.1 Type Scale & Utility Tokens

| Style Level | Element / Utility | Size / Line-Height | Weight | Tracking | Primary Usage |
|---|---|---|---|---|---|
| **Display H1** | `h1`, `text-3xl` | `30px` (`1.875rem`) / `36px` | `font-bold` (`700`) | `-0.025em` | Public landing hero headline |
| **Page H2** | `h2`, `text-2xl` | `24px` (`1.5rem`) / `32px` | `font-semibold` (`600`) | `-0.02em` | Main dashboard title, screen headers |
| **Section H3**| `h3`, `text-xl` | `20px` (`1.25rem`) / `28px` | `font-semibold` (`600`) | `-0.015em` | Card section titles, modal headers |
| **Card H4** | `h4`, `text-lg` | `18px` (`1.125rem`) / `24px` | `font-medium` (`500`) | `-0.01em` | Opportunity card titles, table group headers |
| **Body Large**| `p`, `text-base` | `16px` (`1.0rem`) / `24px` | `font-normal` (`400`) | `0` | Lead paragraphs, job descriptions |
| **Body Normal**| `p`, `text-sm` | `14px` (`0.875rem`) / `20px` | `font-normal` (`400`) | `0` | Default body copy, table cell content |
| **Body Medium**| `span`, `text-sm`| `14px` (`0.875rem`) / `20px` | `font-medium` (`500`) | `0` | Interactive table values, button text |
| **Label / Sub**| `label`, `text-xs`| `12px` (`0.75rem`) / `16px` | `font-medium` (`500`) | `+0.01em` | Form labels, input helper texts, footnotes |
| **Micro Tag** | `span`, `text-xs`| `11px` (`0.6875rem`) / `14px`| `font-semibold` (`600`) | `+0.02em` | Status badges, category pills, table headers |

---

## 4. Spacing, Sizing & Layout Foundations

### 4.1 Spacing Scale (8pt Grid System)
All margins, paddings, gaps, and component dimensions adhere strictly to Tailwind's 4px/8px standard scale:

- `space-1` = `4px` (`0.25rem`) — Micro spacing (badge padding, icon-to-text gap)
- `space-2` = `8px` (`0.5rem`) — Compact spacing (button icon gaps, chip spacing)
- `space-3` = `12px` (`0.75rem`) — Form field vertical gaps, badge internal padding
- `space-4` = `16px` (`1.0rem`) — Standard component internal padding, card padding
- `space-6` = `24px` (`1.5rem`) — Card group gaps, section padding, grid column gaps
- `space-8` = `32px` (`2.0rem`) — Major page section spacing, modal container padding
- `space-12` = `48px` (`3.0rem`) — Top-level dashboard vertical section margins

### 4.2 Border Radius System

| Token | Class | Value | Usage |
|---|---|---|---|
| **Small** | `rounded-sm` | `2px` | Checkboxes, table row focus indicators |
| **Medium** | `rounded-md` | `6px` | Inputs, select boxes, dropdown menus, buttons |
| **Large** | `rounded-lg` | `8px` | Standard cards, alert callouts, data table containers |
| **Extra Large** | `rounded-xl` | `12px` | Hero cards, analytics chart containers, modal dialogs |
| **Pill / Full** | `rounded-full` | `9999px` | Badges, skill tags, user profile avatars, match rings |

### 4.3 Elevation & Drop Shadows

- **Flat / Border-Only (Default):** `border border-slate-200 shadow-none` — Used for standard cards, data tables, and form containers to maintain a clean aesthetic.
- **Subtle Elevation:** `shadow-sm` (`box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05)`) — Opportunity cards on hover, active dropdown menus.
- **Modal / Overlay Elevation:** `shadow-lg` (`box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1)`) — Dialogs, popovers, and slide-over drawers.

---

## 5. Core UI Component Standards (shadcn/ui + Tailwind)

To maximize reliability and avoid redundant custom code, SkillBridge maps every UI pattern to standardized **shadcn/ui** components.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Component Taxonomy                              │
├──────────────────────────┬─────────────────────────────────────────────┤
│ Base shadcn/ui Component │ SkillBridge Feature Application             │
├──────────────────────────┼─────────────────────────────────────────────┤
│ `<Button />`             │ Primary submit, secondary actions, outline  │
│ `<Input />`, `<Textarea>`│ Profile forms, opportunity creation, search │
│ `<Select />`, `<Command>`│ Branch/year eligibility multi-selects       │
│ `<Card />`               │ Opportunity cards, analytics KPI widgets    │
│ `<Table />`              │ Applicant review, student rosters, gap data │
│ `<Badge />`              │ Status tags, verified trust pill, category  │
│ `<Dialog />`             │ Application confirm, feedback entry modal   │
│ `<Sheet />`              │ Candidate profile drawer, mobile sidebar    │
│ `<Tabs />`               │ Auth role selector, verification queue tabs │
│ `<Alert />`              │ Skill coverage footnote, ineligibility alert│
│ `<Skeleton />`           │ Non-blocking loading state placeholders     │
└──────────────────────────┴─────────────────────────────────────────────┘
```

### 5.1 Button Component (`<Button />`)

| Variant | Tailwind Styles | Usage |
|---|---|---|
| **Primary (Default)** | `bg-indigo-600 text-white hover:bg-indigo-700 shadow-sm font-medium` | "Apply Now", "Publish Opportunity", "Save Profile" |
| **Secondary** | `bg-slate-100 text-slate-900 hover:bg-slate-200 font-medium` | "Edit Profile", "Filter", secondary actions |
| **Outline** | `border border-slate-200 bg-white hover:bg-slate-50 text-slate-700` | "Cancel", "View Details", "Download Resume" |
| **Destructive** | `bg-rose-600 text-white hover:bg-rose-700 font-medium` | "Reject", "Deactivate Account", "Force-Close" |
| **Ghost** | `hover:bg-slate-100 text-slate-700 hover:text-slate-900` | Table row action menus, pagination controls |
| **Link** | `text-indigo-600 underline-offset-4 hover:underline p-0 h-auto` | Inline navigation links, "View All" triggers |

- **Loading State on Buttons:** When submitting, button renders `<Loader2 className="w-4 h-4 mr-2 animate-spin" />`, disables interaction (`pointer-events-none opacity-80`), and displays progressive text (e.g. *"Submitting..."*).

### 5.2 Badge Component (`<Badge />`)

- **Skill Badge (Current / Matched):** `bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full px-2.5 py-0.5 text-xs font-medium inline-flex items-center gap-1`
- **Skill Badge (Missing Required):** `bg-slate-50 text-slate-600 border border-dashed border-slate-300 rounded-full px-2.5 py-0.5 text-xs font-medium`
- **Verification Trust Badge:** `bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full px-2.5 py-0.5 text-xs font-semibold inline-flex items-center gap-1` (renders Lucide `<CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />`).

---

## 6. Domain-Specific Data Visualizations & Indicators

### 6.1 Skill Match Score Ring (`<MatchScoreRing />`)

The match score visually represents the percentage of required skills possessed by the student (`(matched / required) * 100` per PRD F8 / FR-MATCH-01).

```
   Match >= 75%              50% <= Match < 75%            Match < 50%
┌──────────────────┐        ┌──────────────────┐       ┌──────────────────┐
│   ╭────────╮     │        │   ╭────────╮     │       │   ╭────────╮     │
│  │   80%    │    │        │  │   60%    │    │       │  │   40%    │    │
│   ╰────────╯     │        │   ╰────────╯     │       │   ╰────────╯     │
│  Emerald-600     │        │  Amber-500       │       │  Slate-500       │
│  High Match      │        │  Moderate Match  │       │  Low Match       │
└──────────────────┘        └──────────────────┘       └──────────────────┘
```

- **Ring Color Thresholds:**
  - $\text{Score} \ge 75\%$: `stroke-emerald-600 text-emerald-600 bg-emerald-50/50`
  - $50\% \le \text{Score} < 75\%$: `stroke-amber-500 text-amber-600 bg-amber-50/50`
  - $\text{Score} < 50\%$: `stroke-slate-400 text-slate-600 bg-slate-50`
- **Mandatory Footnote:** Always renders subtitle: *"Matches X of Y required skills (Coverage only)"*.

### 6.2 Skill Gap Analysis Matrix Chart (Recharts)

The institutional Skill Gap view (SCR-COL-06 / Flow COL-05) renders a grouped bar chart comparing **Industry Demand %** against **Student Availability %**:

- **Industry Demand Bar:** Fill `#3B82F6` (Blue 500)
- **Student Availability Bar:** Fill `#10B981` (Emerald 500)
- **Chart Tooltip:** Renders custom React tooltip displaying exact counts (`Demand: 45 postings (60%)`, `Availability: 120 students (40%)`, `Net Gap: +20% (Moderate Gap)`).

---

## 7. Forms, Inputs & Validation Behavior

### 7.1 Field Layout & Required Field Indicator
- **Label Structure:** `<label className="text-xs font-medium text-slate-700 mb-1.5 block">Field Name <span className="text-rose-500">*</span></label>`
- **Helper Text:** `<p className="text-xs text-slate-500 mt-1">Guidance text or constraint</p>`

### 7.2 Field Interaction States

| Field State | Border & Ring Classes | Background | Icon / Indicator |
|---|---|---|---|
| **Default** | `border-slate-200 hover:border-slate-300` | `bg-white` | None |
| **Focus** | `border-indigo-600 ring-2 ring-indigo-600/20` | `bg-white` | None |
| **Error** | `border-rose-500 ring-2 ring-rose-500/20` | `bg-rose-50/20`| Alert circle icon on right |
| **Disabled** | `border-slate-200 bg-slate-50 cursor-not-allowed` | `bg-slate-50` | Muted text |

### 7.3 Inline Validation Message Standard
When validation fails (client-side or server `400 Bad Request`), the offending field renders:
```html
<p className="text-xs font-medium text-rose-600 mt-1.5 flex items-center gap-1">
  <AlertCircle className="w-3.5 h-3.5 flex-shrink-0" />
  <span>{errorMessage}</span>
</p>
```

---

## 8. UI State Handling & Feedback Architecture

Every screen strictly renders one of the four deterministically governed UI states:

```
                        ┌──────────────────────────────┐
                        │   Screen Component Mount     │
                        └──────────────┬───────────────┘
                                       │
                      ┌────────────────┴────────────────┐
                      ▼                                 ▼
             [ isPending: true ]               [ isError: true ]
                      │                                 │
            ┌─────────▼─────────┐             ┌─────────▼─────────┐
            │   Loading State   │             │   Error State     │
            │  Skeleton Shimmer │             │ Retryable Banner  │
            └───────────────────┘             └───────────────────┘
                      │
                      ▼
             [ isSuccess: true ]
                      │
         ┌────────────┴────────────┐
         ▼                         ▼
   [ data.length == 0 ]     [ data.length > 0 ]
         │                         │
┌────────▼────────┐       ┌────────▼────────┐
│   Empty State   │       │  Success State  │
│ Contextual CTA  │       │ Interactive UI  │
└─────────────────┘       └─────────────────┘
```

### 8.1 Loading State Standard (`<SkeletonCard />`, `<SkeletonTable />`)
- Skeleton placeholders match the exact dimensions and grid positions of rendered content.
- Shimmer animation: `animate-pulse bg-slate-100 rounded-md`.

### 8.2 Empty State Standard (`<EmptyState />`)
- Standardized container: `flex flex-col items-center justify-center p-8 text-center border border-dashed border-slate-200 rounded-xl bg-slate-50/50`.
- Contains:
  1. Icon wrapper: `w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-3`.
  2. Heading: `text-sm font-semibold text-slate-900 mb-1`.
  3. Description: `text-xs text-slate-500 max-w-sm mb-4`.
  4. Contextual CTA Button (e.g., *"Explore Postings"*, *"Add Skills"*, *"Post Opportunity"*).

### 8.3 Error State Standard (`<ErrorMessage />`)
- Standardized container: `p-4 border border-rose-200 bg-rose-50/60 rounded-lg flex items-start gap-3`.
- Renders:
  1. Icon: `<AlertTriangle className="w-5 h-5 text-rose-600 flex-shrink-0 mt-0.5" />`
  2. Title & Message: `text-sm font-medium text-rose-900` + `text-xs text-rose-700 mt-0.5`.
  3. Action: `<Button variant="outline" size="sm" onClick={onRetry}>Retry Request</Button>`.

---

## 9. Responsive Breakpoints & Layout Adaptations

SkillBridge targets standard responsive breakpoints using Tailwind conventions:

| Breakpoint | Viewport Width | Layout Behavior | Navigation Mode |
|---|---|---|---|
| **Mobile (`sm`)** | `< 640px` | Single-column stacked layouts, full-width cards | Collapsible mobile hamburger menu & bottom sheet |
| **Tablet (`md`)** | `640px – 1023px` | 2-column KPI grids, condensed data tables | Collapsible mini-sidebar (icons only) |
| **Desktop (`lg`)** | `1024px – 1279px`| 3-column / 4-column KPI grids, side-by-side drawers | Full persistent sidebar (`w-64`) |
| **Wide Desktop (`xl`)**| `\ge 1280px` | Max-width container (`max-w-7xl mx-auto`) | Full persistent sidebar + contextual right rail |

### 9.1 Data-Heavy Screen & Table Responsiveness
- **Desktop ($\ge 1024px$):** Standard structured `<table>` with sortable headers and action columns.
- **Mobile ($< 768px$):** Tables automatically transform into stacked `<Card />` components displaying key metadata in a vertical label-value pair format with a full-width bottom action trigger.

---

## 10. Accessibility & Interaction Standards

SkillBridge enforces **WCAG 2.1 Level AA** compliance across all screens:

1. **Color Contrast:** All body text meets at least **4.5:1** contrast ratio against backgrounds (`text-slate-900` on `bg-white` achieves **16.1:1**; `text-slate-600` achieves **5.8:1**).
2. **Keyboard Navigation:**
   - All interactive controls (buttons, links, form inputs, dialog triggers) are reachable via `Tab` / `Shift+Tab`.
   - Modals and drawers trap keyboard focus and dismiss on `Escape`.
   - Autocomplete dropdowns support `ArrowUp`, `ArrowDown`, and `Enter` selection.
3. **Visible Focus Rings:** Every interactive element has an explicit outline on keyboard focus:
   `focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-600 focus-visible:ring-offset-2`.
4. **ARIA & Semantic HTML:**
   - Native `<main>`, `<nav>`, `<header>`, `<aside>`, `<section>` landmark elements.
   - Screen-reader labels (`aria-label`) on all icon-only buttons (e.g. table delete icon, close modal button).
   - Form inputs paired with `<label htmlFor={id}>` and `aria-describedby` for validation errors.

---

## 11. Component Directory & Implementation Blueprint

```
apps/web/src/
├── components/
│   ├── ui/                       # Direct shadcn/ui components
│   │   ├── button.jsx
│   │   ├── input.jsx
│   │   ├── textarea.jsx
│   │   ├── select.jsx
│   │   ├── card.jsx
│   │   ├── table.jsx
│   │   ├── badge.jsx
│   │   ├── dialog.jsx
│   │   ├── sheet.jsx
│   │   ├── tabs.jsx
│   │   ├── alert.jsx
│   │   ├── skeleton.jsx
│   │   └── progress.jsx
│   │
│   ├── layout/                   # Structural layout components
│   │   ├── AppShell.jsx          # Shell with header and dynamic sidebar
│   │   ├── Sidebar.jsx           # Role-scoped navigation menu
│   │   ├── Navbar.jsx            # Top bar with user profile and role badge
│   │   └── PublicNavbar.jsx      # Marketing/auth public header
│   │
│   └── shared/                   # Domain-specific reusable components
│       ├── SkillBadge.jsx        # Canonical skill badge (matched/missing/category)
│       ├── MatchScoreRing.jsx    # Circular progress match indicator
│       ├── VerificationBadge.jsx # Trust badge for verified orgs
│       ├── StatusStepper.jsx     # Visual recruitment pipeline stage tracker
│       ├── EmptyState.jsx        # Standard empty state with icon and CTA
│       ├── ErrorMessage.jsx      # Standard retryable error callout
│       └── LoadingSpinner.jsx    # Non-blocking loading indicator
```

---

*SkillBridge Design System Specification Complete.*  
*Status: APPROVED — Ready for Frontend UI Component Implementation.*
