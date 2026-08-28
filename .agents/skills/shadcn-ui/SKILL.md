---
name: shadcn-ui
description: Best practices for composing, styling, and customizing shadcn/ui components with Tailwind CSS in SkillBridge.
---

# SkillBridge — shadcn/ui Component Skill

## Purpose
Ensure proper usage, composition, and Tailwind CSS styling of **shadcn/ui** components across all SkillBridge screens while avoiding redundant custom component creation.

## When to Use
Use this skill whenever adding UI primitives, building forms, creating modals, constructing data tables, or styling interactive widgets.

---

## Required Behavior

1. **Standard shadcn/ui Primitives (`src/components/ui/`):**
   - **Buttons & Triggers:** `<Button>`, `<DropdownMenu>`, `<DropdownMenuTrigger>`, `<DropdownMenuContent>`
   - **Form Controls:** `<Input>`, `<Textarea>`, `<Select>`, `<SelectTrigger>`, `<SelectContent>`, `<SelectItem>`, `<Checkbox>`
   - **Modals & Overlays:** `<Dialog>`, `<DialogContent>`, `<DialogHeader>`, `<DialogTitle>`, `<DialogFooter>`, `<Sheet>`, `<SheetContent>`, `<SheetHeader>`
   - **Data Containers:** `<Card>`, `<CardHeader>`, `<CardTitle>`, `<CardContent>`, `<CardFooter>`, `<Table>`, `<TableHeader>`, `<TableBody>`, `<TableRow>`, `<TableCell>`, `<TableHead>`
   - **Feedback & Navigation:** `<Badge>`, `<Alert>`, `<AlertTitle>`, `<AlertDescription>`, `<Skeleton>`, `<Progress>`, `<Tabs>`, `<TabsList>`, `<TabsTrigger>`, `<TabsContent>`

2. **Customization via Tailwind Classes:**
   - Always customize shadcn/ui components using Tailwind utility classes passed via `className`.
   - Use the `cn(...)` utility (`clsx` + `tailwind-merge`) inside components to merge classes safely.
   - Maintain the semantic color tokens defined in `docs/03-ux/design-system.md`.

---

## Project-Specific Rules

- **Do Not Recreate Wheels:** If a UI pattern matches a shadcn primitive (e.g. modal dialog, slide-over drawer, dropdown, select box, badge), **always use the shadcn primitive from `src/components/ui/`**. Do not write ad-hoc raw HTML overlays or CSS popups.
- **Pure JavaScript shadcn Components:** All shadcn components must be pure `.jsx` without TypeScript type definitions (`React.FC`, interfaces, or generic parameters).
- **Accessible Radix Primitives:** Preserve the underlying Radix UI accessibility attributes (ARIA attributes, keyboard navigation, focus management) embedded in shadcn/ui.

---

## Do / Don't Guidance

### Do:
- Use `<Sheet side="right">` for candidate and student profile detail drawers (SCR-COM-05, SCR-COL-03).
- Use `<Dialog>` for confirmation prompts (Application Submit, Force-Close Opportunity, Feedback Submission).
- Use `<Skeleton>` to construct accurate placeholder layouts for loading states.
- Use `<Tabs>` for role selection on registration and queue filtering in Admin verification.

### Don't:
- **Do NOT install alternative component libraries (Material UI, Chakra UI, Ant Design, Mantine).**
- **Do NOT write raw `<div>` modals with manual `z-index` and backdrop clicks.**
- **Do NOT edit core Radix UI accessibility props** unless explicitly required for a custom ARIA label.
- **Do NOT strip Tailwind styles from shadcn primitives.**

---

## Definition of Good Implementation
shadcn/ui integration is successful when:
1. Every interactive primitive leverages the established `src/components/ui/` components.
2. Styling is consistent with the SkillBridge design system tokens.
3. Modals and dropdowns trap focus, open/close with smooth transitions, and dismiss cleanly on `Escape`.
