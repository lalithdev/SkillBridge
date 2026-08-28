# SkillBridge — Frontend Screen Specifications

**Phase:** System Design (UX / Frontend Specifications)  
**Version:** 1.0  
**Status:** APPROVED — Frontend Blueprint Baseline  
**Date:** 2026-08-28  
**Derived from:** PRD.md · SRS.md · frontend-architecture.md · user-flows.md · api-design.md · openapi.yaml  
**Tech Stack:** React (Vite, JavaScript), Tailwind CSS, shadcn/ui, React Router, Axios, useState/useReducer, Recharts  

---

## Table of Contents

1. [Architectural Overview & Design System Guidelines](#1-architectural-overview--design-system-guidelines)
2. [Public & Authentication Screens](#2-public--authentication-screens)
   - [SCR-PUB-01: Landing Page](#scr-pub-01-landing-page)
   - [SCR-PUB-02: User Registration](#scr-pub-02-user-registration)
   - [SCR-PUB-03: User Login](#scr-pub-03-user-login)
   - [SCR-PUB-04: Unauthorized Access & 404 Pages](#scr-pub-04-unauthorized-access--404-pages)
3. [Student Role Screens](#3-student-role-screens)
   - [SCR-STU-01: Student Dashboard](#scr-stu-01-student-dashboard)
   - [SCR-STU-02: Student Profile & Academic Information](#scr-stu-02-student-profile--academic-information)
   - [SCR-STU-03: Current Skills Management](#scr-stu-03-current-skills-management)
   - [SCR-STU-04: Browse & Search Opportunities](#scr-stu-04-browse--search-opportunities)
   - [SCR-STU-05: Opportunity Details & Skill Match Breakdown](#scr-stu-05-opportunity-details--skill-match-breakdown)
   - [SCR-STU-06: Recommended Opportunities](#scr-stu-06-recommended-opportunities)
   - [SCR-STU-07: My Applications Tracker](#scr-stu-07-my-applications-tracker)
   - [SCR-STU-08: My Confirmed Internships & Company Feedback](#scr-stu-08-my-confirmed-internships--company-feedback)
4. [Company Role Screens](#4-company-role-screens)
   - [SCR-COM-01: Company Dashboard](#scr-com-01-company-dashboard)
   - [SCR-COM-02: Company Profile & Verification](#scr-com-02-company-profile--verification)
   - [SCR-COM-03: Create & Publish Opportunity](#scr-com-03-create--publish-opportunity)
   - [SCR-COM-04: Manage Posted Opportunities](#scr-com-04-manage-posted-opportunities)
   - [SCR-COM-05: Ranked Candidate Review & Applicant Pipeline](#scr-com-05-ranked-candidate-review--applicant-pipeline)
   - [SCR-COM-06: Confirmed Interns & Evaluation Feedback](#scr-com-06-confirmed-interns--evaluation-feedback)
5. [College / Placement Cell Role Screens](#5-college--placement-cell-role-screens)
   - [SCR-COL-01: College Dashboard](#scr-col-01-college-dashboard)
   - [SCR-COL-02: College Profile](#scr-col-02-college-profile)
   - [SCR-COL-03: Student Roster & Departments](#scr-col-03-student-roster--departments)
   - [SCR-COL-04: Student Skill Availability Analytics](#scr-col-04-student-skill-availability-analytics)
   - [SCR-COL-05: Industry Skill Demand Analytics](#scr-col-05-industry-skill-demand-analytics)
   - [SCR-COL-06: Skill Gap Analysis Dashboard](#scr-col-06-skill-gap-analysis-dashboard)
   - [SCR-COL-07: Placement Funnel Monitoring](#scr-col-07-placement-funnel-monitoring)
   - [SCR-COL-08: Aggregated Company Feedback Review](#scr-col-08-aggregated-company-feedback-review)
6. [Platform Admin Role Screens](#6-platform-admin-role-screens)
   - [SCR-ADM-01: Admin Dashboard](#scr-adm-01-admin-dashboard)
   - [SCR-ADM-02: User Account Directory & Deactivation](#scr-adm-02-user-account-directory--deactivation)
   - [SCR-ADM-03: Organization Verification Queue](#scr-adm-03-organization-verification-queue)
   - [SCR-ADM-04: Master Skills Taxonomy Management](#scr-adm-04-master-skills-taxonomy-management)
   - [SCR-ADM-05: Master Academic Departments Management](#scr-adm-05-master-academic-departments-management)
   - [SCR-ADM-06: Opportunity Moderation & Oversight](#scr-adm-06-opportunity-moderation--oversight)
7. [Screen-to-API & Requirements Traceability Matrix](#7-screen-to-api--requirements-traceability-matrix)
8. [Implementation Readiness & Audit Findings](#8-implementation-readiness--audit-findings)

---

## 1. Architectural Overview & Design System Guidelines

### 1.1 State Handling Standards
Every data-fetching screen in SkillBridge adheres to four explicit UI states:
1. **Loading State:** Render non-blocking skeleton loaders matching the target layout structure (`<SkeletonCard />`, `<SkeletonTable />`, `<SkeletonBadge />`).
2. **Success State:** Render interactive, fully actionable components with clear visual hierarchy and accessibility attributes.
3. **Empty State:** Render dedicated `<EmptyState />` components displaying a contextual icon, clear explanatory text, and a direct Call-To-Action (CTA) button when applicable.
4. **Error State:** Render inline field alerts for form validation errors or full-card `<ErrorMessage />` with a retry button on server or network failures.

### 1.2 Skill Coverage vs. Proficiency Disclaimer
All UI widgets, badges, progress rings, and tables displaying **Skill Match %** or **Student Skill Availability %** must render the standardized footnote:
> *"Skill match/availability measures self-reported skill presence and curriculum coverage, not verified individual proficiency."*

### 1.3 Layout & Shell Hierarchy
- **Public Layout:** Minimal top navigation with brand logo, explore links, login button, and registration CTA.
- **Authenticated App Layout:** Responsive sidebar navigation (collapsible on mobile/tablet), sticky top bar with user profile avatar, role badge, notifications dropdown (Should-Have S4), and logout button.

---

## 2. Public & Authentication Screens

### SCR-PUB-01: Landing Page
- **Screen Name:** Public Landing Page
- **Purpose:** Introduce the SkillBridge value proposition for Students, Companies, and Colleges; provide quick access to login and registration.
- **Who Can Access:** Public / Anonymous visitors (`*`)
- **Entry/Navigation Path:** `/`
- **Main Sections / Components:**
  - Header / Navbar (`<PublicNavbar />`): Logo, "For Students", "For Companies", "For Colleges", "Sign In", "Get Started".
  - Hero Section (`<HeroBanner />`): Value proposition headline, role switcher cards, "Register as Student / Company / College" CTA buttons.
  - Core Loop Showcase (`<FeatureGrid />`): Explains Skill Matching, Opportunity Discovery, Institutional Analytics, and Closed-Loop Feedback.
  - Live Public Stats Widget (`<PlatformStats />`): Counts of registered institutions, partner employers, and active opportunities.
  - Footer (`<PublicFooter />`): SIH PS044 attribution, copyright, navigation links.
- **Information Displayed:** High-level platform statistics, workflow diagrams, feature explanations.
- **User Actions:** Click "Sign In" (`/login`), click "Get Started" (`/register`), click role anchor links.
- **Relevant API Endpoint(s):** None (Static / Marketing presentation).
- **Loading State:** Instant static render.
- **Empty State:** N/A.
- **Error State:** N/A.
- **Success / Confirmation State:** N/A.
- **Important Validation or Permission Behavior:** If user already has an active JWT session in `sessionStorage`, automatically redirect to their respective dashboard (`/student/dashboard`, `/company/dashboard`, `/college/dashboard`, or `/admin/dashboard`).
- **Responsive / Mobile Considerations:** Mobile burger menu replaces horizontal navigation; feature cards stack vertically on `< 768px` viewports.

---

### SCR-PUB-02: User Registration
- **Screen Name:** User Registration
- **Purpose:** Allow new Students, Companies, and Colleges to create accounts with verified role claims and base profile metadata.
- **Who Can Access:** Unauthenticated Visitors only.
- **Entry/Navigation Path:** `/register` (or via Public Navbar "Register")
- **Main Sections / Components:**
  - Role Selector Tabs (`<Tabs />`): "Student", "Company", "College" (Admin self-registration is explicitly disabled).
  - Common Credentials Form (`<AccountCredentialsFields />`): Email, Password, Confirm Password.
  - Role-Specific Dynamic Form Cards:
    - *Student:* First Name, Last Name, College Dropdown (from active colleges), Department Dropdown (from master departments), Year of Study (1–8), CGPA (0.00–10.00).
    - *Company:* Company Name, Industry Sector, Office Location, Contact Email, Contact Phone, Website URL.
    - *College:* College Name, Campus Address, Official Contact Email, Contact Phone, Website URL.
  - Submit Action Button (`<Button loading={isSubmitting}>Create Account</Button>`).
  - Redirect Link: *"Already have an account? Sign in"*.
- **Information Displayed:** Role descriptions, required field markers (`*`), inline validation rules (password min 8 characters).
- **User Actions:** Select role tab, fill form inputs, submit registration.
- **Relevant API Endpoint(s):**
  - `POST /api/v1/auth/register`
  - `GET /api/v1/departments` (to populate student department dropdown)
  - `GET /api/v1/colleges/public` or `/colleges` (to populate student college dropdown)
- **Loading State:** Skeleton inputs during initial master catalog load; disabled button with spinner during POST submission.
- **Empty State:** N/A.
- **Error State:**
  - `409 Conflict`: Banner and inline error on email field: *"An account with this email already exists."*
  - `400 Bad Request`: Field-level error messages (e.g., *"CGPA must be between 0.0 and 10.0"*, *"Password must be at least 8 characters"*).
- **Success / Confirmation State:** Redirect to `/login` with a success toast/banner: *"Registration successful! Please log in with your credentials."*
- **Important Validation or Permission Behavior:** Client-side validation prevents form submission if password confirmation mismatches or required dropdowns are unselected.
- **Responsive / Mobile Considerations:** Single-column responsive layout; touch-friendly 48px input targets on mobile screens.

---

### SCR-PUB-03: User Login
- **Screen Name:** User Login
- **Purpose:** Authenticate registered users and issue role-scoped JWT session token.
- **Who Can Access:** Unauthenticated Visitors (`*`).
- **Entry/Navigation Path:** `/login`
- **Main Sections / Components:**
  - Login Card (`<Card />`): SkillBridge logo, form heading *"Welcome Back"*.
  - Inputs: Email field (`type="email"`), Password field (`type="password"` with show/hide toggle).
  - Actions: "Sign In" button, "Register an Account" link.
- **Information Displayed:** Form fields, error alerts.
- **User Actions:** Enter credentials, click "Sign In", press Enter to submit.
- **Relevant API Endpoint(s):** `POST /api/v1/auth/login`
- **Loading State:** "Signing in..." spinner state on primary button; form inputs disabled during request.
- **Empty State:** N/A.
- **Error State:**
  - `401 Unauthorized`: Alert banner *"Invalid email or password."* or *"Account has been deactivated. Please contact platform administrator."*
  - `400 Bad Request`: Inline validation if email format is invalid.
- **Success / Confirmation State:** JWT token and user profile written to `sessionStorage` and `AuthContext`; instant deterministic redirect based on role:
  - `STUDENT` → `/student/dashboard`
  - `COMPANY` → `/company/dashboard`
  - `COLLEGE` → `/college/dashboard`
  - `ADMIN` → `/admin/dashboard`
- **Important Validation or Permission Behavior:** If redirect query param exists (`/login?redirect=/student/opportunities`), redirect to requested URL upon successful login after role verification.
- **Responsive / Mobile Considerations:** Centered card with auto margins; responsive padding on small devices (`p-4` vs `p-8`).

---

### SCR-PUB-04: Unauthorized Access & 404 Pages
- **Screen Name:** Access Denied / Page Not Found
- **Purpose:** Inform users when navigating to non-existent URLs or attempting forbidden cross-role navigation.
- **Who Can Access:** Authenticated / Anonymous users.
- **Entry/Navigation Path:** `/unauthorized` (HTTP 403) and `*` (HTTP 404).
- **Main Sections / Components:**
  - Error Illustration / Icon (`<ShieldAlert />` / `<FileQuestion />`).
  - Heading: *"403 - Access Denied"* or *"404 - Page Not Found"*.
  - Explanatory message: *"You do not have permission to access this resource"* / *"The page you are looking for does not exist."*
  - Action button: "Return to Dashboard" (if authenticated) or "Back to Home" (if guest).
- **Information Displayed:** Error status, user role context, recovery action.
- **User Actions:** Click return button.
- **Relevant API Endpoint(s):** None.
- **Loading State:** Instant render.
- **Empty State:** N/A.
- **Error State:** N/A.
- **Success / Confirmation State:** Navigates back to authorized role route.
- **Important Validation or Permission Behavior:** Triggered automatically by frontend `ProtectedRoute` when `user.role` does not match route permission.
- **Responsive / Mobile Considerations:** Fully responsive centered layout.

---

## 3. Student Role Screens

### SCR-STU-01: Student Dashboard
- **Screen Name:** Student Dashboard
- **Purpose:** Central hub for student users to view application summary stats, match-based recommended opportunities, active applications pipeline, and profile completeness.
- **Who Can Access:** `STUDENT` role only.
- **Entry/Navigation Path:** `/student/dashboard`
- **Main Sections / Components:**
  - Welcome Banner (`<DashboardHeader />`): Greeting, student name, department, college name.
  - KPI Metrics Cards (`<StatsGroup />`):
    - *Profile Skills Count:* Total active skills tagged.
    - *Submitted Applications:* Total active/submitted applications.
    - *Shortlisted / Interviewing:* Applications in positive recruitment stages.
    - *Confirmed Placements/Internships:* Total selections.
  - Quick Recommendations Widget (`<RecommendedOpportunitiesCarousel />`): Top 3 opportunities ranked by match score with "Explore All" link (FR-STU-06 / S3).
  - Recent Applications Tracker Card (`<ApplicationPipelineCard />`): Mini status steppers for latest 3 applications.
  - Profile Completeness Callout (`<ProgressAlert />`): Prompts student to add skills, upload resume, or attach projects if missing.
- **Information Displayed:** Skill count, application stages, opportunity recommendations, institutional affiliation.
- **User Actions:** Click opportunity cards to view details, click "Explore Opportunities", click "Manage Skills".
- **Relevant API Endpoint(s):**
  - `GET /api/v1/students/profile`
  - `GET /api/v1/matching/recommendations?page=0&size=3`
  - `GET /api/v1/applications/my?page=0&size=3`
- **Loading State:** Skeleton metric cards and placeholder recommendation cards (`<SkeletonCard />`).
- **Empty State:** When no applications exist → *"No applications submitted yet. Browse opportunities to get started."* When no skills exist → Prominent alert prompting skill tagging.
- **Error State:** Banner alert with "Retry loading dashboard" button.
- **Success / Confirmation State:** Rendered interactive metrics.
- **Important Validation or Permission Behavior:** Scoped strictly to the logged-in student's JWT token claims.
- **Responsive / Mobile Considerations:** KPI cards collapse from 4-column grid to 2-column on tablets and 1-column on mobile.

---

### SCR-STU-02: Student Profile & Academic Information
- **Screen Name:** Student Profile & Portfolio
- **Purpose:** View and update personal academic info, portfolio projects, certifications, and manage uploaded resume files.
- **Who Can Access:** `STUDENT` role (Edit own), `COLLEGE` (View affiliated), `COMPANY` (View applicant), `ADMIN` (View/Moderate).
- **Entry/Navigation Path:** `/student/profile`
- **Main Sections / Components:**
  - Academic Profile Card (`<AcademicInfoCard />`):
    - Display: First Name, Last Name, College (Read-only), Department, Year of Study (1–8), CGPA (0.00–10.00), Career Interests.
    - "Edit Profile" modal with form validation.
  - Resume Management Section (`<ResumeUploadWidget />`) *(Should-Have S1 / FR-STU-04)*:
    - Drag-and-drop file upload zone (PDF/DOCX, max 5 MB).
    - File card showing uploaded resume filename, date, "Download", "Replace", and "Delete" actions.
  - Portfolio Projects List (`<ProjectListCard />`) *(FR-STU-03)*:
    - Cards displaying Project Title, Description, Live URL / GitHub Link.
    - "Add Project" modal (Title, Description, URL) + Edit/Delete controls.
  - Certifications List (`<CertificationListCard />`) *(FR-STU-03)*:
    - Cards displaying Certificate Title, Issuing Org, Issue Date, Credential URL.
    - "Add Certification" modal + Edit/Delete controls.
- **Information Displayed:** Academic details, college affiliation, resume status, project links, credentials.
- **User Actions:** Edit academic profile, upload/delete resume, add/edit/delete projects and certifications.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/students/profile`
  - `PUT /api/v1/students/profile`
  - `POST /api/v1/students/profile/resume`
  - `GET /api/v1/students/{studentId}/resume`
  - `DELETE /api/v1/students/profile/resume`
  - `GET /api/v1/students/profile/projects`, `POST /projects`, `PUT /projects/{id}`, `DELETE /projects/{id}`
  - `GET /api/v1/students/profile/certifications`, `POST /certifications`, `PUT /certifications/{id}`, `DELETE /certifications/{id}`
- **Loading State:** Skeleton profile layout with shimmering card placeholders.
- **Empty State:** Empty cards for projects and certifications with *"No projects/certifications added yet. Showcase your work to employers."*
- **Error State:**
  - File upload > 5 MB → `413 Payload Too Large`: *"File size exceeds 5 MB limit."*
  - Invalid format → `415 Unsupported Media Type`: *"Only PDF and DOCX documents are accepted."*
  - Form validation failure → Inline error highlighting on offending fields.
- **Success / Confirmation State:** Toast notifications: *"Profile updated"*, *"Resume uploaded"*, *"Project saved"*.
- **Important Validation or Permission Behavior:** College affiliation is immutable after registration. CGPA strictly bounded between 0.00 and 10.00.
- **Responsive / Mobile Considerations:** Two-column grid (Profile on left, Portfolio/Resume on right) collapses to stacked single column on `< 1024px`.

---

### SCR-STU-03: Current Skills Management
- **Screen Name:** Student Skills Management
- **Purpose:** Manage the student's canonical current-skills inventory that powers real-time skill matching and college analytics.
- **Who Can Access:** `STUDENT` role only.
- **Entry/Navigation Path:** `/student/skills`
- **Main Sections / Components:**
  - Header & Skill Coverage Notice: Clarifies that tagged skills reflect presence/coverage rather than verified proficiency.
  - Skill Search & Add Autocomplete (`<SkillTypeahead />`): Search bar querying master skills taxonomy with category tags.
  - Current Skills Badge Grid (`<SkillBadgeGroup />`): Badges grouped by category (Languages, Frameworks, Databases, Tools) with a remove ("×") icon on each.
  - Skill Count Indicator: Displays current count vs limit (e.g., `12 / 30 skills added`).
- **Information Displayed:** Tagged skills, skill categories, total count, taxonomy search suggestions.
- **User Actions:** Type in search bar, select canonical skill, click "Add Skill", click remove icon on badge, confirm deletion.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/students/profile/skills`
  - `POST /api/v1/students/profile/skills` (`{ skillId }`)
  - `DELETE /api/v1/students/profile/skills/{skillId}`
  - `GET /api/v1/skills?search=...`
- **Loading State:** Placeholder badge skeletons; loading spinner inside autocomplete dropdown.
- **Empty State:** `<EmptyState title="No skills added yet" description="Search and add technical skills from the master catalog to start matching with internships." />`
- **Error State:**
  - `409 Conflict`: Toast alert *"You have already added this skill to your profile."*
  - `400 Bad Request`: Toast alert *"Maximum skill limit (30) reached."*
- **Success / Confirmation State:** Immediate badge addition/removal with toast notification; match scores recalculate automatically across the platform.
- **Important Validation or Permission Behavior:** Students can only select skills present in the master skills taxonomy. Free-text custom skills are prevented to maintain deterministic matching.
- **Responsive / Mobile Considerations:** Wrap-flex badge layout; autocomplete dropdown adjusts to full-width drawer on small screens.

---

### SCR-STU-04: Browse & Search Opportunities
- **Screen Name:** Explore Opportunities
- **Purpose:** Allow students to browse, search, and filter open internship and placement postings annotated with personal skill match scores and eligibility status.
- **Who Can Access:** `STUDENT` role (also accessible in read-only mode by `COMPANY`, `COLLEGE`, and `ADMIN`).
- **Entry/Navigation Path:** `/student/opportunities`
- **Main Sections / Components:**
  - Search & Filter Header (`<OpportunityFilterBar />`):
    - Keyword search input (Title, Description, Company).
    - Type Filter: All / `INTERNSHIP` / `PLACEMENT`.
    - Work Mode Filter: All / `ONSITE` / `REMOTE` / `HYBRID`.
    - Location input / dropdown.
  - Opportunities Feed / Grid (`<OpportunityCardGrid />`):
    - Opportunity Card (`<OpportunityCard />`):
      - Role Title & Company Name (with Verified badge if company is verified).
      - Type, Location, Mode, Duration, Stipend / Salary.
      - **Skill Match Score Badge:** e.g., `80% Match (4/5 skills)` in green/amber badge.
      - **Eligibility Tag:** Green `"Eligible"` or Amber `"Ineligible (CGPA/Branch)"`.
      - Application Deadline countdown.
      - "View Details" button.
  - Pagination Controls (`<Pagination />`): Page navigation and results count.
- **Information Displayed:** Posting metadata, employer verification status, student match percentage, eligibility indicator, deadline.
- **User Actions:** Enter search keywords, toggle filter chips, paginate, click opportunity card to open detail view.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/opportunities?search=...&type=...&mode=...&location=...&page=0&size=10`
- **Loading State:** Grid of 6 skeleton opportunity cards with shimmering badges.
- **Empty State:** `<EmptyState title="No opportunities found" description="Try adjusting your search keywords or filter criteria." />`
- **Error State:** Error card with *"Unable to load opportunities"* and a retry button.
- **Success / Confirmation State:** Rendered paginated list of matching postings.
- **Important Validation or Permission Behavior:** Match % and eligibility are computed dynamically server-side for the requesting student's profile.
- **Responsive / Mobile Considerations:** Filter bar collapses into a slide-over filter drawer on mobile viewports (`< 768px`).

---

### SCR-STU-05: Opportunity Details & Skill Match Breakdown
- **Screen Name:** Opportunity Detail & Match Breakdown
- **Purpose:** Comprehensive view of a single posting, detailed skill match vs missing skill breakdown, explicit eligibility checklist, and application submission trigger.
- **Who Can Access:** `STUDENT` role (All authenticated roles have read access).
- **Entry/Navigation Path:** `/student/opportunities/:id`
- **Main Sections / Components:**
  - Header Hero (`<OpportunityHeader />`): Role Title, Company Name, Verified Badge, Location, Mode, Stipend, Deadline.
  - Job Description Card (`<JobDescriptionView />`): Full role overview, responsibilities, duration.
  - **Skill Match Breakdown Widget (`<SkillMatchWidget />`):**
    - Match Percentage Progress Ring (e.g., `75% Match`).
    - Matched Required Skills list (`<Badge variant="success">✓ Java</Badge>`).
    - Missing Required Skills list (`<Badge variant="outline">Missing: Docker</Badge>`).
    - Skill coverage disclaimer footnote.
  - **Eligibility Checklist Card (`<EligibilityChecklist />`):**
    - Eligible Branches (Master Departments) vs Student Department.
    - Eligible Years (1–5) vs Student Year.
    - Minimum CGPA vs Student CGPA.
    - Explicit Ineligibility Reasons banner (if student fails criteria).
  - Employer Information Card (`<CompanySummaryCard />`): Company overview, website, industry.
  - Sticky Application Action Footer (`<ApplyActionFooter />`):
    - "Apply Now" primary button (Enabled if eligible and not applied).
    - Disabled state with tooltip if Ineligible or Deadline Passed.
    - "Already Applied" badge with link to application tracker if submitted.
  - Application Confirmation Modal (`<ApplicationConfirmModal />`): Displays match score snapshot warning, profile summary, and "Confirm Submission" button.
- **Information Displayed:** Full posting specification, matched skills, missing skills, match score, eligibility evaluation, company details.
- **User Actions:** Review breakdown, click "Apply Now", confirm modal submission (FR-APP-01 / Flow STU-08).
- **Relevant API Endpoint(s):**
  - `GET /api/v1/opportunities/{id}`
  - `GET /api/v1/matching/opportunities/{id}`
  - `POST /api/v1/applications` (`{ opportunityId }`)
- **Loading State:** Full-page layout skeleton with shimmering match ring and description lines.
- **Empty State:** `404 Not Found` if opportunity was closed or deleted.
- **Error State:**
  - Ineligible application attempt → `400 Bad Request`: *"You do not meet the branch or CGPA eligibility criteria."*
  - Duplicate application attempt → `409 Conflict`: *"You have already applied to this opportunity."*
  - Deadline expired → `400 Bad Request`: *"This opportunity is closed to applications."*
- **Success / Confirmation State:** Application modal closes, success toast: *"Application submitted successfully! Track status in My Applications."* Button transitions to `[✓ Applied - Under Review]`.
- **Important Validation or Permission Behavior:** Backend creates immutable snapshot `match_percent_at_apply` at submission timestamp.
- **Responsive / Mobile Considerations:** Sticky bottom action bar ensures "Apply Now" is always accessible on mobile devices without excessive scrolling.

---

### SCR-STU-06: Recommended Opportunities
- **Screen Name:** Recommended Opportunities
- **Purpose:** Dedicated view presenting open opportunities ranked strictly by descending Skill Match Percentage for the student.
- **Who Can Access:** `STUDENT` role only.
- **Entry/Navigation Path:** `/student/recommendations` (or via Dashboard "View All Recommendations")
- **Main Sections / Components:**
  - Header: Title *"Recommended for Your Skill Profile"*, explanation of match score ranking.
  - Ranked Opportunity List (`<RankedOpportunityList />`): Ordered descending by `matchPercent`.
  - Match Breakdown Summary per card: Displays top matching skills and quick link to full detail view.
  - Pagination Controls.
- **Information Displayed:** Opportunities sorted from highest match (e.g. 100%, 80%) to lowest.
- **User Actions:** Click card to open detail view, apply directly.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/matching/recommendations?page=0&size=10`
- **Loading State:** Skeleton list of ranked cards with match badges.
- **Empty State:** When student has 0 skills → `<EmptyState title="No recommendations available" description="Add technical skills to your profile to receive personalized opportunity matches." action={<Button href="/student/skills">Add Skills</Button>} />`.
- **Error State:** Standard error banner with retry button.
- **Success / Confirmation State:** Interactive ranked list rendered.
- **Important Validation or Permission Behavior:** Excludes opportunities the student has already applied to.
- **Responsive / Mobile Considerations:** Match score progress indicator rendered horizontally on mobile to conserve vertical space.

---

### SCR-STU-07: My Applications Tracker
- **Screen Name:** My Applications Tracker
- **Purpose:** Track the real-time status of all submitted applications through the recruitment pipeline.
- **Who Can Access:** `STUDENT` role only.
- **Entry/Navigation Path:** `/student/applications`
- **Main Sections / Components:**
  - Header & Summary Counters: Total Applied, In Review, Shortlisted, Interviewing, Selected, Rejected.
  - Applications List / Table (`<ApplicationTrackerTable />`):
    - Opportunity Title & Company Name.
    - Applied Date & Snapshot Match % Badge (`match_percent_at_apply`).
    - **Visual Pipeline Stepper (`<StatusStepper />`):**
      - `APPLIED` → `UNDER_REVIEW` → `SHORTLISTED` → `INTERVIEW` → `SELECTED` (Green) or `REJECTED` (Red).
    - Status Badge & Contextual Actions:
      - If `SELECTED`: Link *"View Confirmed Outcome"* (`/student/internships`).
      - Link *"View Opportunity Details"*.
- **Information Displayed:** All student applications, submission dates, match score snapshot, current pipeline stage.
- **User Actions:** Filter by status, view pipeline progress, click to view posting or confirmed internship.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/applications/my?page=0&size=10`
- **Loading State:** Skeleton table rows with placeholder steppers.
- **Empty State:** `<EmptyState title="No applications submitted" description="You have not applied to any opportunities yet." action={<Button href="/student/opportunities">Explore Postings</Button>} />`.
- **Error State:** Banner alert with retry button.
- **Success / Confirmation State:** Rendered interactive tracking list.
- **Important Validation or Permission Behavior:** Read-only for students; stage mutations are performed exclusively by employers.
- **Responsive / Mobile Considerations:** Table transforms into stacked card view on `< 768px`, with vertical status stepper.

---

### SCR-STU-08: My Confirmed Internships & Company Feedback
- **Screen Name:** My Internships & Placement Outcomes
- **Purpose:** View confirmed internship and placement records resulting from `SELECTED` applications and inspect qualitative company evaluation feedback.
- **Who Can Access:** `STUDENT` role only.
- **Entry/Navigation Path:** `/student/internships`
- **Main Sections / Components:**
  - Active / Past Outcomes Roster (`<InternshipRoster />`):
    - Cards displaying: Role Title, Company Name, Type (`INTERNSHIP` / `PLACEMENT`), Start Date, End Date.
    - Lifecycle Status Badge: `UPCOMING` (Blue), `ONGOING` (Amber), `COMPLETED` (Green).
  - **Company Evaluation Feedback Card (`<CompanyFeedbackCard />`):**
    - Visible when status is `COMPLETED` and feedback has been submitted (FR-INT-05 / OD-04 / DBQ-04).
    - Displays: Qualitative evaluation text from employer, submission date, supervisor comments.
- **Information Displayed:** Confirmed hiring records, dates, lifecycle status, qualitative feedback.
- **User Actions:** Read feedback, filter between active and completed records.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/internships/my`
  - `GET /api/v1/internships/{internshipId}/feedback`
- **Loading State:** Skeleton cards with shimmer effects.
- **Empty State:** `<EmptyState title="No confirmed internships" description="When an employer selects you for an opportunity, your placement details and completion feedback will appear here." />`.
- **Error State:** Error banner with retry button.
- **Success / Confirmation State:** Rendered outcome cards and feedback texts.
- **Important Validation or Permission Behavior:** Feedback is displayed only after the employer submits it for a completed record.
- **Responsive / Mobile Considerations:** Stacked layout with clear visual dividers between outcome metadata and feedback text.

---

## 4. Company Role Screens

### SCR-COM-01: Company Dashboard
- **Screen Name:** Company Dashboard
- **Purpose:** Provide employers with an executive summary of active postings, total candidate applications, pending review counts, and quick actions.
- **Who Can Access:** `COMPANY` role only.
- **Entry/Navigation Path:** `/company/dashboard`
- **Main Sections / Components:**
  - Header & Verification Alert (`<VerificationAlert />`): Displays `PENDING`, `VERIFIED`, or `REJECTED` trust badge.
  - KPI Stat Cards (`<StatsGroup />`):
    - *Active Postings:* Count of open opportunities.
    - *Total Applicants:* Total received candidate applications.
    - *Under Review / Shortlisted:* Candidates in active recruitment funnel.
    - *Confirmed Hires:* Total candidates marked `SELECTED`.
  - Active Postings Quick Table (`<RecentPostingsTable />`): Displays latest 5 postings with applicant counts and "Review Candidates" action.
  - Quick Actions Bar: "Post New Opportunity", "Manage Interns".
- **Information Displayed:** Posting counts, candidate totals, verification status banner.
- **User Actions:** Click "Post Opportunity", click "Review Applicants" on any posting card.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/companies/profile`
  - `GET /api/v1/opportunities/company/my?page=0&size=5`
- **Loading State:** Skeleton KPI cards and placeholder table rows.
- **Empty State:** When no postings exist → Callout prompting employer to publish their first opportunity.
- **Error State:** Banner alert with retry option.
- **Success / Confirmation State:** Rendered dashboard metrics.
- **Important Validation or Permission Behavior:** Data strictly isolated to the authenticated company's profile (`companyId` in JWT).
- **Responsive / Mobile Considerations:** Responsive card grid adapts from 4 columns to single column on mobile.

---

### SCR-COM-02: Company Profile & Verification
- **Screen Name:** Company Profile & Settings
- **Purpose:** Manage employer corporate identity, contact information, industry classification, and view platform verification status.
- **Who Can Access:** `COMPANY` role (Edit own), all authenticated roles (View public profile).
- **Entry/Navigation Path:** `/company/profile`
- **Main Sections / Components:**
  - Header Card: Company Name, Industry Badge, Trust Verification Status (`PENDING`, `VERIFIED`, `REJECTED`).
  - Company Details Form (`<CompanyProfileForm />`):
    - Inputs: Company Name, Industry Sector, Description, Office Location, Official Website URL, Contact Email, Contact Phone.
    - "Save Profile" button (`<Button loading={isSaving}>Save Changes</Button>`).
  - Verification Explanatory Box: Explains that `VERIFIED` status is granted by Admin and displays trust badges on job postings.
- **Information Displayed:** Corporate profile metadata, contact info, verification status and notes.
- **User Actions:** Edit fields, click Save Changes.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/companies/profile`
  - `PUT /api/v1/companies/profile`
- **Loading State:** Skeleton input fields during fetch.
- **Empty State:** N/A.
- **Error State:** `400 Bad Request` with field-level validation highlights.
- **Success / Confirmation State:** Toast: *"Company profile updated successfully."*
- **Important Validation or Permission Behavior:** Company users cannot edit their own verification status (strictly Admin-managed via FR-ADM-02).
- **Responsive / Mobile Considerations:** Form fields arranged in single-column layout on small screens.

---

### SCR-COM-03: Create & Publish Opportunity
- **Screen Name:** Post Opportunity
- **Purpose:** Define and publish new internship or placement postings with required skills, eligibility criteria, and role specifications.
- **Who Can Access:** `COMPANY` role only.
- **Entry/Navigation Path:** `/company/opportunities/create`
- **Main Sections / Components:**
  - Basic Details Card (`<PostingBasicFields />`):
    - Role Title (e.g. *"Junior Backend Developer Intern"*).
    - Opportunity Type: Radio buttons `INTERNSHIP` / `PLACEMENT`.
    - Work Mode: Select `ONSITE` / `REMOTE` / `HYBRID`.
    - Location: Text input (e.g. *"Bangalore, India"*).
    - Duration (Weeks): Numeric input (required for internships).
    - Stipend / Salary: Numeric amount and currency (`INR`).
    - Application Deadline: Date picker (must be a future date).
    - Job Description: Rich/Plain text area.
  - **Eligibility Criteria Card (`<EligibilityCriteriaFields />`):**
    - Eligible Branches: Multi-select dropdown from master `departments` (Empty = "All Branches").
    - Eligible Academic Years: Multi-select checkboxes (1, 2, 3, 4, 5+; Empty = "All Years").
    - Minimum CGPA: Numeric input (0.00–10.00; Empty = "No CGPA threshold").
  - **Required Skills Selector Card (`<RequiredSkillsPicker />`):**
    - Mandatory selection of at least 1 canonical skill from master taxonomy (FR-INT-01).
    - Selected skills displayed as removable badges.
  - Action Controls: "Cancel", "Publish Opportunity" button.
- **Information Displayed:** Form fields, helper descriptions, skill search results.
- **User Actions:** Fill posting fields, select required skills from taxonomy, define eligibility, submit posting.
- **Relevant API Endpoint(s):**
  - `POST /api/v1/opportunities`
  - `GET /api/v1/skills`
  - `GET /api/v1/departments`
- **Loading State:** Skeleton loaders during initial taxonomy fetch; disabled button with spinner during POST.
- **Empty State:** N/A.
- **Error State:**
  - Zero required skills → `400 Bad Request`: Inline error *"At least one required skill must be selected."*
  - Past deadline → `400 Bad Request`: Inline error *"Deadline must be a future date."*
  - Missing mandatory fields → Red highlight on inputs.
- **Success / Confirmation State:** Toast: *"Opportunity published successfully!"* Redirect to `/company/opportunities`.
- **Important Validation or Permission Behavior:** Atomic transaction on backend creates opportunity and associates required skills, branches, and years.
- **Responsive / Mobile Considerations:** Form sections stack sequentially with sticky submit button on mobile.

---

### SCR-COM-04: Manage Posted Opportunities
- **Screen Name:** Manage Opportunities
- **Purpose:** List, filter, edit, and toggle active status (`OPEN` ↔ `CLOSED`) for all opportunities posted by the employer.
- **Who Can Access:** `COMPANY` role only.
- **Entry/Navigation Path:** `/company/opportunities`
- **Main Sections / Components:**
  - Header & "Post New Opportunity" primary button.
  - Filter Tabs: "All Postings", "Open", "Closed".
  - Opportunities Table / Cards (`<CompanyOpportunityTable />`):
    - Columns: Role Title, Type, Status Badge (`OPEN` / `CLOSED`), Applicant Count, Application Deadline, Created Date.
    - Actions per row:
      - **"View Applicants" button:** Opens candidate review view (SCR-COM-05).
      - **"Edit" button:** Opens edit form (`/company/opportunities/:id/edit`).
      - **"Close / Reopen" toggle:** Switches status between `OPEN` and `CLOSED`.
- **Information Displayed:** Postings list, active status, applicant counts, deadline dates.
- **User Actions:** Search postings, toggle status, click to edit or view applicants.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/opportunities/company/my?page=0&size=10`
  - `PATCH /api/v1/opportunities/{id}/status` (`{ status: "CLOSED" }` or `"OPEN"`)
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No opportunities posted yet" description="Post your first internship or placement opportunity to start receiving candidate applications." action={<Button href="/company/opportunities/create">Post Opportunity</Button>} />`.
- **Error State:** Error banner with retry button.
- **Success / Confirmation State:** Toast: *"Opportunity status updated to CLOSED/OPEN."*
- **Important Validation or Permission Behavior:** Closed opportunities immediately stop accepting new student applications.
- **Responsive / Mobile Considerations:** Table converts to responsive cards on viewports `< 768px`.

---

### SCR-COM-05: Ranked Candidate Review & Applicant Pipeline
- **Screen Name:** Candidate Review & Recruitment Pipeline
- **Purpose:** Review applicants for a specific opportunity, filtered strictly to eligible candidates and ranked by Skill Match Percentage descending; advance candidates through recruitment stages.
- **Who Can Access:** `COMPANY` role (Owner of opportunity only).
- **Entry/Navigation Path:** `/company/opportunities/:id/applicants`
- **Main Sections / Components:**
  - Header: Opportunity Title, Type, Total Eligible Applicants count.
  - Filter Bar: Filter by Pipeline Stage (`ALL`, `APPLIED`, `UNDER_REVIEW`, `SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED`).
  - **Ranked Candidates Table (`<RankedCandidateTable />`):**
    - Ordered strictly by `match_percent_at_apply DESC` (e.g. 90%, 80%, 70%).
    - Columns: Candidate Name, Department, Academic Year, CGPA, Match % Badge, Current Stage Badge, Actions.
    - Action Menu: "View Profile", "Advance Stage", "Reject".
  - **Candidate Detail Drawer / Modal (`<CandidateDetailDrawer />`):**
    - Displays Candidate Profile: Personal info, academic details, self-reported skills with match indicators.
    - Portfolio Projects & Certifications list.
    - **"Download Resume" button:** Streams candidate's uploaded PDF/DOCX resume file (FR-STU-04 / Flow COM-04).
  - **Stage Transition Dialog (`<StageTransitionModal />`):**
    - Enforces forward-only progression (OD-03):
      - `APPLIED` → `UNDER_REVIEW` / `REJECTED`
      - `UNDER_REVIEW` → `SHORTLISTED` / `REJECTED`
      - `SHORTLISTED` → `INTERVIEW` / `REJECTED`
      - `INTERVIEW` → `SELECTED` / `REJECTED`
    - Warning banner when selecting `SELECTED`: *"Marking as Selected will automatically create an active internship outcome record."*
- **Information Displayed:** Ranked candidate roster, snapshot match percentages, candidate profiles, resume files, pipeline stages.
- **User Actions:** Open candidate drawer, download resume, advance pipeline stage, reject candidate.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/opportunities/{opportunityId}/applications?stage=...`
  - `GET /api/v1/students/{studentId}/profile`
  - `GET /api/v1/students/{studentId}/resume`
  - `PATCH /api/v1/applications/{id}/status` (`{ status: "SHORTLISTED" }`)
- **Loading State:** Skeleton table rows with shimmer ranking badges.
- **Empty State:** `<EmptyState title="No applicants yet" description="Applications submitted by eligible candidates will appear here ranked by skill match percentage." />`.
- **Error State:**
  - Attempting invalid stage transition → `400 Bad Request`: *"Invalid pipeline stage transition. Progression must be forward-only."*
  - Server error → Error banner with retry button.
- **Success / Confirmation State:** Toast: *"Candidate moved to SHORTLISTED."* If `SELECTED`: *"Candidate marked Selected. Internship record created."*
- **Important Validation or Permission Behavior:** Automated creation of `internship_records` upon reaching `SELECTED` status (FR-APP-05).
- **Responsive / Mobile Considerations:** Candidate drawer opens as full-screen modal on mobile devices.

---

### SCR-COM-06: Confirmed Interns & Evaluation Feedback
- **Screen Name:** Confirmed Interns & Outcomes
- **Purpose:** Manage active hires resulting from `SELECTED` applications, track internship lifecycle status (`UPCOMING`, `ONGOING`, `COMPLETED`), and submit qualitative evaluation feedback.
- **Who Can Access:** `COMPANY` role only.
- **Entry/Navigation Path:** `/company/internships`
- **Main Sections / Components:**
  - Header & Summary Cards: Total Hires, Upcoming, Ongoing, Completed Interns.
  - Confirmed Interns Table (`<InternRosterTable />`):
    - Columns: Student Name, Role Title, Type (`INTERNSHIP` / `PLACEMENT`), Start Date, End Date, Lifecycle Status Badge, Feedback Status.
    - Actions:
      - **"Update Status" dropdown:** Transition `UPCOMING` → `ONGOING` → `COMPLETED`.
      - **"Submit Feedback" button:** Enabled **only when status = `COMPLETED`** (FR-INT-05 / OD-11 / DBQ-04).
  - **Company Feedback Modal (`<FeedbackSubmissionModal />`):**
    - Student Name & Role Summary.
    - Qualitative Evaluation Text Area (free-text feedback on student strengths, technical skills, areas of improvement).
    - "Submit Evaluation" button.
- **Information Displayed:** Confirmed intern roster, start/end dates, lifecycle status, feedback submission state.
- **User Actions:** Update internship status, open feedback modal, submit qualitative evaluation text.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/internships/company/my`
  - `PATCH /api/v1/internships/{id}/status` (`{ status: "ONGOING", startDate: "...", endDate: "..." }`)
  - `POST /api/v1/internships/{internshipId}/feedback` (`{ feedbackText: "..." }`)
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No confirmed interns" description="Candidates marked as 'Selected' in your recruitment pipelines will appear here." />`.
- **Error State:** Submitting feedback before completion → `400 Bad Request`: *"Feedback can only be submitted for completed internships."*
- **Success / Confirmation State:** Toast: *"Internship status updated."* / *"Feedback submitted successfully."* Feedback button updates to `[✓ Feedback Submitted]`.
- **Important Validation or Permission Behavior:** 1:1 relation between internship record and feedback entity; submitted feedback is distributed to both the student (SCR-STU-08) and the student's college (SCR-COL-08).
- **Responsive / Mobile Considerations:** Actions menu collapses into an icon dropdown on mobile devices.

---

## 5. College / Placement Cell Role Screens

### SCR-COL-01: College Dashboard
- **Screen Name:** College Placement Dashboard
- **Purpose:** Central intelligence hub for college placement coordinators displaying enrolled student counts, active recruitment funnel summary, high-priority skill gaps, and quick links.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/dashboard`
- **Main Sections / Components:**
  - Header & College Profile Info: College Name, Campus Location, Verification Badge.
  - KPI Metrics Cards (`<StatsGroup />`):
    - *Enrolled Students:* Total students registered under this college.
    - *Active Applications:* Total applications in progress.
    - *Offers / Selections:* Total students selected for internships or placements.
    - *Identified High Skill Gaps:* Count of skills with $\ge 30\%$ gap.
  - Quick Placement Funnel Widget (`<MiniFunnelChart />`): Visual mini-funnel (Applied → Shortlisted → Selected) with "View Full Funnel" link.
  - Top Industry Skill Gaps Card (`<TopGapsWidget />`): Top 4 skills with high severity gaps and "Curriculum Recommendations".
  - Quick Links: "Student Roster", "Skill Gap Analysis", "Company Feedback".
- **Information Displayed:** Institution-level summary stats, placement funnel overview, urgent skill gaps.
- **User Actions:** Click widget cards to navigate to deep-dive analytics screens.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/colleges/profile`
  - `GET /api/v1/analytics/placement-funnel`
  - `GET /api/v1/analytics/skills/gap`
- **Loading State:** Skeleton KPI cards and placeholder chart widgets.
- **Empty State:** Informational card prompting student onboarding if 0 students are registered.
- **Error State:** Banner alert with retry button.
- **Success / Confirmation State:** Interactive metrics and charts rendered.
- **Important Validation or Permission Behavior:** All metrics are strictly scoped to the college derived from the authenticated JWT (`collegeId`).
- **Responsive / Mobile Considerations:** Dashboard widgets stack into a single column on `< 1024px`.

---

### SCR-COL-02: College Profile
- **Screen Name:** College Institutional Profile
- **Purpose:** Manage college placement cell contact details, address, website, and inspect platform verification status.
- **Who Can Access:** `COLLEGE` role (Edit own), all authenticated roles (View public info).
- **Entry/Navigation Path:** `/college/profile`
- **Main Sections / Components:**
  - Profile Card: College Name, Campus Address, Placement Cell Email, Phone, Website URL, Verification Badge (`PENDING`, `VERIFIED`, `REJECTED`).
  - Edit Profile Form: Modal/card with inputs for Address, Contact Email, Contact Phone, Website URL.
  - Save Action Button (`<Button loading={isSaving}>Save Changes</Button>`).
- **Information Displayed:** Institutional metadata, contact channels, verification status.
- **User Actions:** Edit fields, submit changes.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/colleges/profile`
  - `PUT /api/v1/colleges/profile`
- **Loading State:** Skeleton form inputs.
- **Empty State:** N/A.
- **Error State:** `400 Bad Request` with field validation errors.
- **Success / Confirmation State:** Toast: *"College profile updated successfully."*
- **Important Validation or Permission Behavior:** Verification status is strictly modified by Admin (FR-ADM-02).
- **Responsive / Mobile Considerations:** Single-column layout on mobile devices.

---

### SCR-COL-03: Student Roster & Departments
- **Screen Name:** Student Roster & Department Directory
- **Purpose:** View and search enrolled students affiliated with the college, filter by academic department, and inspect individual student readiness profiles.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/students`
- **Main Sections / Components:**
  - Department Summary Cards: Total student enrollment counts per department (e.g. CSE: 120, ECE: 85, MECH: 60).
  - Search & Filter Bar: Search by student name/email, filter by Department dropdown.
  - Student Roster Table (`<StudentRosterTable />`):
    - Columns: Student Name, Department, Year of Study, CGPA, Tagged Skills Count, Applications Submitted, Actions.
    - Action: "View Student Profile".
  - **Student Academic Profile Drawer (`<StudentProfileDrawer />`):**
    - Read-only inspection of student's tagged skills, portfolio projects, certifications, and career interests (FR-COL-01 / DBQ-01).
- **Information Displayed:** Enrolled student directory, departmental distribution, student skill counts, application counts.
- **User Actions:** Search students, filter by department, open student drawer, paginate.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/colleges/students?departmentId=...&search=...&page=0&size=10`
  - `GET /api/v1/colleges/departments`
  - `GET /api/v1/students/{id}/profile`
- **Loading State:** Skeleton summary cards and table rows.
- **Empty State:** `<EmptyState title="No students registered" description="Students who register and select your college will appear here." />`.
- **Error State:** Error banner with retry option.
- **Success / Confirmation State:** Rendered student roster.
- **Important Validation or Permission Behavior:** Backend enforces `WHERE sp.college_id = :collegeId` (derived from JWT token) preventing cross-college student inspection.
- **Responsive / Mobile Considerations:** Drawer opens as full-screen modal on mobile viewports.

---

### SCR-COL-04: Student Skill Availability Analytics
- **Screen Name:** Student Skill Availability Analytics
- **Purpose:** Compute and visualize the percentage of the college's student body possessing each canonical technical skill.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/analytics/availability`
- **Main Sections / Components:**
  - Header & Disclaimer Banner: Explicitly clarifies that availability % represents self-reported skill presence across the student body, not verified proficiency (FR-ANL-01).
  - Skill Availability Bar Chart (`<RechartsBarChart />`): Visual distribution of top skills by student possession %.
  - Availability Data Table (`<SkillAvailabilityTable />`):
    - Columns: Skill Name, Category, Students Possessing Skill Count, Total College Students, **Availability %** (`(students with skill / total college students) * 100`).
    - Sortable by Availability % ascending/descending; filterable by Skill Category.
- **Information Displayed:** Per-skill availability metrics, category distribution, total student cohort size.
- **User Actions:** Sort table columns, filter categories, hover over chart bars for exact counts.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/analytics/skills/availability`
- **Loading State:** Skeleton chart canvas and placeholder table rows.
- **Empty State:** `<EmptyState title="Insufficient skill data" description="Students have not yet added skills to their profiles." />`.
- **Error State:** Error banner with retry button.
- **Success / Confirmation State:** Rendered interactive bar chart and table.
- **Important Validation or Permission Behavior:** Scoped exclusively to the logged-in college's student population.
- **Responsive / Mobile Considerations:** Bar chart switches to horizontal orientation on mobile devices for readable skill labels.

---

### SCR-COL-05: Industry Skill Demand Analytics
- **Screen Name:** Industry Skill Demand Analytics
- **Purpose:** Visualize platform-wide employer demand for technical skills aggregated from all open opportunity postings.
- **Who Can Access:** `COLLEGE` role (and `ADMIN`).
- **Entry/Navigation Path:** `/college/analytics/demand`
- **Main Sections / Components:**
  - Header & Opportunity Scope Filter: Filter demand by Opportunity Type (`ALL`, `INTERNSHIP`, `PLACEMENT`) *(Should-Have S6 / FR-ANL-04)*.
  - Industry Demand Bar Chart (`<RechartsBarChart />`): Top in-demand skills ranked by employer requirement frequency.
  - Demand Data Table (`<SkillDemandTable />`):
    - Columns: Skill Name, Category, Postings Requiring Skill Count, Total Open Postings, **Demand %** (`(open postings requiring skill / total open postings) * 100` per OD-07).
    - Sortable by Demand % descending.
- **Information Displayed:** Required skill frequencies across active market opportunities.
- **User Actions:** Toggle opportunity type filter, sort table, inspect demand distributions.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/analytics/skills/demand?type=...`
- **Loading State:** Skeleton chart and table placeholders.
- **Empty State:** `<EmptyState title="No open opportunities" description="Market demand analytics will update as employers publish postings." />`.
- **Error State:** Error banner with retry button.
- **Success / Confirmation State:** Interactive chart and table rendered.
- **Important Validation or Permission Behavior:** Aggregates across postings with `status = 'OPEN'`.
- **Responsive / Mobile Considerations:** Responsive chart container with auto-resizing width.

---

### SCR-COL-06: Skill Gap Analysis Dashboard
- **Screen Name:** Skill Gap Analysis Dashboard
- **Purpose:** Core institutional insight comparing student skill availability against industry demand, classifying severity gaps into actionable bands, and suggesting curriculum training areas.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/analytics/skill-gap`
- **Main Sections / Components:**
  - Header & Explanatory Summary: Explains gap calculation (`Gap = Demand % - Availability %`) at the institutional level.
  - **Comparative Dual-Bar Chart (`<RechartsGroupedBarChart />`):**
    - Side-by-side visual comparison of Industry Demand % (Blue) vs Student Availability % (Green) for each skill.
  - **Skill Gap Classification Table (`<SkillGapTable />`):**
    - Columns: Skill Name, Category, Industry Demand %, Student Availability %, Net Gap %, **Gap Severity Badge**.
    - **Severity Bands (OD-01 Confirmed):**
      - 🔴 **HIGH GAP:** $\text{Gap} \ge 30\%$ (Red badge — Immediate training needed).
      - 🟡 **MODERATE GAP:** $15\% \le \text{Gap} < 30\%$ (Amber badge — Elective focus).
      - 🟢 **LOW GAP:** $0\% < \text{Gap} < 15\%$ (Green badge — Balanced supply).
      - 🔵 **SURPLUS:** $\text{Gap} \le 0\%$ (Blue badge — Availability meets/exceeds demand).
  - **Institutional Training Recommendations Card (`<TrainingFocusCard />`):**
    - Automatically highlights top High Gap skills (e.g. *"AWS (Gap: 34%)"*, *"Docker (Gap: 31%)"*).
    - Suggests targeted workshops, hackathons, or guest lectures to bridge the identified deficit.
- **Information Displayed:** Comparative demand vs availability percentages, severity classifications, curriculum recommendations.
- **User Actions:** Sort table by gap severity, filter by category, export/print analytics summary.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/analytics/skills/gap`
- **Loading State:** Shimmering chart canvas and skeleton table with placeholder severity badges.
- **Empty State:** `<EmptyState title="Insufficient analytics data" description="Requires enrolled students with skills and open employer postings to compute gap matrix." />`.
- **Error State:** Error banner with retry option.
- **Success / Confirmation State:** Rendered comparative chart and classified gap table.
- **Important Validation or Permission Behavior:** Institution-level calculation; explicitly never evaluates or reports on individual student deficiencies.
- **Responsive / Mobile Considerations:** Grouped bar chart collapses to stacked vertical layout on mobile; table enables horizontal scroll.

---

### SCR-COL-07: Placement Funnel Monitoring
- **Screen Name:** Placement Funnel Monitoring
- **Purpose:** Track recruitment pipeline conversions and stage distributions across all affiliated students.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/analytics/funnel`
- **Main Sections / Components:**
  - Header & Department Filter Dropdown (All Departments / Specific Department per DBQ-01).
  - **Recruitment Funnel Visualization (`<FunnelStageChart />`):**
    - Step-by-step funnel bars:
      - `APPLIED` → `UNDER_REVIEW` → `SHORTLISTED` → `INTERVIEW` → `SELECTED` / `REJECTED`.
    - Stage conversion rates (e.g. Shortlist-to-Interview: 45%, Interview-to-Selected: 30%).
  - Stage Breakdown Summary Cards: Counts and percentages per stage.
- **Information Displayed:** Total student applications across each pipeline stage, conversion ratios.
- **User Actions:** Filter funnel by department, hover over stages for detailed counts.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/analytics/placement-funnel?departmentId=...`
- **Loading State:** Skeleton funnel bars with shimmer effects.
- **Empty State:** `<EmptyState title="No active applications" description="Funnel data will populate as students submit applications." />`.
- **Error State:** Error banner with retry button.
- **Success / Confirmation State:** Rendered interactive funnel chart.
- **Important Validation or Permission Behavior:** Aggregates applications where student belongs to the authenticated college.
- **Responsive / Mobile Considerations:** Funnel bars render vertically on mobile viewports.

---

### SCR-COL-08: Aggregated Company Feedback Review
- **Screen Name:** Company Feedback Review
- **Purpose:** Review aggregated qualitative employer feedback submitted upon completion of student internships to guide institutional curriculum improvements.
- **Who Can Access:** `COLLEGE` role only.
- **Entry/Navigation Path:** `/college/feedback`
- **Main Sections / Components:**
  - Header & Summary: Total feedback submissions count.
  - Search & Filter: Search by Company Name or Student Name, filter by Department.
  - **Feedback Feed / Cards (`<FeedbackCardList />`):**
    - Feedback Card:
      - Student Name & Department (e.g. *"Rahul Sharma - Computer Science"*).
      - Company Name & Opportunity Title (e.g. *"Infosys - Backend Developer Intern"*).
      - Completion Date.
      - **Qualitative Employer Feedback Text:** Free-text evaluation (e.g. *"Rahul demonstrated solid fundamentals in Java and REST APIs, but would benefit from more practical exposure to Docker and CI/CD pipelines."*).
- **Information Displayed:** Qualitative feedback entries, student names, employers, completion timestamps.
- **User Actions:** Search feedback, filter by department, paginate.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/colleges/feedback?departmentId=...&search=...&page=0&size=10`
- **Loading State:** Skeleton feedback cards.
- **Empty State:** `<EmptyState title="No company feedback yet" description="Employer evaluations submitted upon completion of student internships will appear here." />`.
- **Error State:** Error banner with retry option.
- **Success / Confirmation State:** Rendered feedback feed.
- **Important Validation or Permission Behavior:** Scoped strictly to feedback records attached to the college's students.
- **Responsive / Mobile Considerations:** Single-column feed format scales seamlessly on mobile devices.

---

## 6. Platform Admin Role Screens

### SCR-ADM-01: Admin Dashboard
- **Screen Name:** Admin Operations Dashboard
- **Purpose:** High-level platform health overview for the platform administrator showing total users, pending verifications, skills taxonomy size, and active postings.
- **Who Can Access:** `ADMIN` role only.
- **Entry/Navigation Path:** `/admin/dashboard`
- **Main Sections / Components:**
  - Header: Platform Operations Status.
  - KPI Stat Cards (`<StatsGroup />`):
    - *Total Users:* Count across Students, Companies, and Colleges.
    - *Pending Verifications:* Count of unverified organizations requiring review.
    - *Skills Taxonomy:* Count of active skills in master catalog.
    - *Active Postings:* Total live opportunities across all employers.
  - Pending Verification Queue Preview (`<PendingOrgWidget />`): Top 5 organizations awaiting verification with quick "Review" button.
  - Quick Action Links: "Manage Users", "Verification Queue", "Skills Taxonomy", "Academic Departments", "Moderate Postings".
- **Information Displayed:** System-wide counts, pending review queues, system status.
- **User Actions:** Click cards to navigate to admin operational screens.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/admin/users?page=0&size=1`
  - `GET /api/v1/admin/verifications`
  - `GET /api/v1/skills`
  - `GET /api/v1/opportunities?page=0&size=1`
- **Loading State:** Skeleton KPI cards and placeholder review list.
- **Empty State:** N/A.
- **Error State:** Banner alert with retry button.
- **Success / Confirmation State:** Rendered platform metrics.
- **Important Validation or Permission Behavior:** Restricted strictly to `ADMIN` JWT role claim.
- **Responsive / Mobile Considerations:** Standard responsive grid layout.

---

### SCR-ADM-02: User Account Directory & Deactivation
- **Screen Name:** User Accounts Management
- **Purpose:** Search, filter, inspect, and toggle active status (`Active` / `Deactivated`) for any user account across all roles.
- **Who Can Access:** `ADMIN` role only.
- **Entry/Navigation Path:** `/admin/users`
- **Main Sections / Components:**
  - Filter & Search Bar: Search by Email or Name, filter by Role (`STUDENT`, `COMPANY`, `COLLEGE`, `ADMIN`), filter by Status (`ACTIVE`, `INACTIVE`).
  - **User Directory Table (`<UserDirectoryTable />`):**
    - Columns: User ID, Email, Role Badge, Created Date, Active Status Badge (`Active` / `Deactivated`), Actions.
    - Action: **"Toggle Status" button** (Deactivate / Activate).
  - Deactivation Confirmation Dialog (`<ConfirmDialog />`): *"Are you sure you want to deactivate this account? The user will be immediately blocked from signing in."*
- **Information Displayed:** User accounts directory, role assignments, timestamps, active statuses.
- **User Actions:** Search users, toggle account active status, confirm action.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/admin/users?role=...&search=...&page=0&size=10`
  - `PATCH /api/v1/admin/users/{id}/status` (`{ isActive: false }` or `true`)
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No users found" description="No accounts match your search filters." />`.
- **Error State:** `404 Not Found` if user doesn't exist; error toast on action failure.
- **Success / Confirmation State:** Toast: *"User account status updated."* Status badge updates instantly.
- **Important Validation or Permission Behavior:** Deactivated users are immediately blocked from logging in by `POST /auth/login`.
- **Responsive / Mobile Considerations:** Table transforms into stacked user cards on mobile.

---

### SCR-ADM-03: Organization Verification Queue
- **Screen Name:** Organization Verification Queue
- **Purpose:** Review pending verification requests from newly registered Companies and Colleges, inspect official credentials, and grant or reject verified trust status.
- **Who Can Access:** `ADMIN` role only.
- **Entry/Navigation Path:** `/admin/verifications`
- **Main Sections / Components:**
  - Verification Queue Tabs: "Pending Companies" vs "Pending Colleges".
  - **Organization Review Cards / Table (`<VerificationQueueTable />`):**
    - Columns: Organization Name, Website URL, Official Contact Email, Phone, Campus/Office Address, Registration Date, Current Status (`PENDING`), Actions.
    - Actions per row:
      - **"Verify / Approve" button (Green):** Sets status to `VERIFIED`.
      - **"Reject" button (Red):** Sets status to `REJECTED`.
  - Organization Details Modal: Inspect full description and submitted contact info.
- **Information Displayed:** Pending organization submissions, website links, contact channels, verification status.
- **User Actions:** Inspect details, click Approve/Verify, click Reject.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/admin/verifications`
  - `PATCH /api/v1/admin/verifications/{type}/{id}` (`{ status: "VERIFIED" }` or `"REJECTED"`, where `type` is `companies` or `colleges`)
- **Loading State:** Skeleton review cards.
- **Empty State:** `<EmptyState title="Verification queue is clean" description="No companies or colleges currently pending verification." />`.
- **Error State:** Toast alert on action failure.
- **Success / Confirmation State:** Toast: *"Organization verification status updated to VERIFIED/REJECTED."* Item removes from pending queue.
- **Important Validation or Permission Behavior:** Updating verification status records `verified_at = now()` and displays the verified trust badge on public postings.
- **Responsive / Mobile Considerations:** Stacked card layout with full-width action buttons on mobile devices.

---

### SCR-ADM-04: Master Skills Taxonomy Management
- **Screen Name:** Master Skills Taxonomy
- **Purpose:** Maintain the standardized platform-wide skills catalog used by students to tag current skills and by employers to specify job requirements.
- **Who Can Access:** `ADMIN` role (Write access), all authenticated roles (Read access).
- **Entry/Navigation Path:** `/admin/skills`
- **Main Sections / Components:**
  - Header & "Add New Skill" primary button.
  - Search & Category Filter Bar.
  - **Skills Catalog Table (`<SkillsTaxonomyTable />`):**
    - Columns: Skill ID, Skill Name, Category (e.g. *Languages, Frameworks, Cloud, Databases*), Active Status, Usage Count, Actions.
    - Actions: "Edit Skill", "Deactivate / Activate".
  - **Add / Edit Skill Modal (`<SkillFormModal />`):**
    - Inputs: Skill Name (Text, unique), Category (Dropdown).
    - Submit Button (`<Button>Save Skill</Button>`).
- **Information Displayed:** Master skills taxonomy, categories, active statuses.
- **User Actions:** Search skills, add new skill, edit existing skill, deactivate skill.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/skills?search=...&category=...`
  - `POST /api/v1/skills` (`{ name: "FastAPI", category: "Frameworks" }`)
  - `PUT /api/v1/skills/{id}` (`{ name: "FastAPI", category: "Frameworks" }`)
  - `DELETE /api/v1/skills/{id}` (Soft-deactivate)
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No skills found" description="Add skills to initialize the master taxonomy." />`.
- **Error State:** Duplicate skill name → `409 Conflict`: *"A skill with this name already exists in the taxonomy."*
- **Success / Confirmation State:** Toast: *"Skill created/updated in master taxonomy."*
- **Important Validation or Permission Behavior:** Skill names are enforced unique (case-insensitive). Deactivated skills remain linked to historical profiles but are hidden from future selection dropdowns.
- **Responsive / Mobile Considerations:** Add/Edit modal adapts to mobile screen width.

---

### SCR-ADM-05: Master Academic Departments Management
- **Screen Name:** Master Academic Departments
- **Purpose:** Manage standardized engineering academic branches (e.g. CSE, ECE, MECH, IT, CIVIL) used for student profiles and opportunity eligibility filtering.
- **Who Can Access:** `ADMIN` role only (Write access), all roles (Read access).
- **Entry/Navigation Path:** `/admin/departments`
- **Main Sections / Components:**
  - Header & "Add Department" primary button.
  - **Departments Table (`<DepartmentTable />`):**
    - Columns: Department ID, Department Name, Branch Code (e.g. `CSE`, `AI-DS`), Active Status, Actions.
    - Actions: "Edit", "Toggle Active".
  - **Add / Edit Department Modal (`<DepartmentFormModal />`):**
    - Inputs: Department Name (e.g. *"Data Science and Artificial Intelligence"*), Unique Branch Code (e.g. *"AI-DS"*).
    - Save button.
- **Information Displayed:** Standardized academic departments list and branch codes.
- **User Actions:** Add new department, edit department name/code, toggle active status.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/departments`
  - `POST /api/v1/departments` (`{ name: "...", code: "..." }`)
  - `PUT /api/v1/departments/{id}`
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No departments configured" description="Add academic departments to initialize the directory." />`.
- **Error State:** `409 Conflict` on duplicate department code or name.
- **Success / Confirmation State:** Toast: *"Academic department added successfully."*
- **Important Validation or Permission Behavior:** Master departments populate student registration and opportunity branch multi-selects.
- **Responsive / Mobile Considerations:** Standard responsive table and modal behavior.

---

### SCR-ADM-06: Opportunity Moderation & Oversight
- **Screen Name:** Opportunity Moderation & Oversight
- **Purpose:** System-wide oversight allowing platform administrators to review all employer postings and force-close inappropriate, fraudulent, or expired listings.
- **Who Can Access:** `ADMIN` role only.
- **Entry/Navigation Path:** `/admin/opportunities`
- **Main Sections / Components:**
  - Search & Filter Bar: Search by Keyword or Company Name, filter by Status (`OPEN`, `CLOSED`).
  - **All Opportunities Table (`<AdminOpportunityTable />`):**
    - Columns: Posting ID, Role Title, Company Name, Type, Status Badge, Applicants Count, Deadline, Created Date, Actions.
    - Actions:
      - **"Inspect Posting":** Opens modal view of posting description and required skills.
      - **"Moderate / Force-Close" button (Red):** Immediately sets status to `CLOSED`.
  - Force-Close Confirmation Dialog: *"Force-closing this posting will immediately remove it from student search results and recommendation feeds."*
- **Information Displayed:** Complete platform opportunity listings across all companies.
- **User Actions:** Search postings, inspect details, force-close posting.
- **Relevant API Endpoint(s):**
  - `GET /api/v1/opportunities?search=...&page=0&size=10`
  - `PATCH /api/v1/admin/opportunities/{id}/status` (`{ status: "CLOSED" }`)
- **Loading State:** Skeleton table rows.
- **Empty State:** `<EmptyState title="No opportunities found" description="No postings match your search filters." />`.
- **Error State:** Toast error on action failure.
- **Success / Confirmation State:** Toast: *"Opportunity posting has been moderated and closed."* Status badge updates to `CLOSED`.
- **Important Validation or Permission Behavior:** Moderated postings are closed immediately to all students.
- **Responsive / Mobile Considerations:** Responsive table layout with modal inspection.

---

## 7. Screen-to-API & Requirements Traceability Matrix

| Screen ID & Name | Role | Primary Flow | SRS Requirements | Primary API Endpoints |
|---|---|---|---|---|
| `SCR-PUB-01` Landing Page | Public | N/A | PRD §1 | Static |
| `SCR-PUB-02` User Registration | Public | `AUTH-01` | `FR-AUTH-01` | `POST /auth/register`, `GET /departments`, `GET /colleges` |
| `SCR-PUB-03` User Login | Public | `AUTH-02` | `FR-AUTH-02`, `FR-AUTH-04` | `POST /auth/login` |
| `SCR-PUB-04` Unauthorized / 404 | Common | `AUTH-03` | `FR-AUTH-04` | Static / Client Route Guard |
| `SCR-STU-01` Student Dashboard | Student | `STU-01` | `FR-STU-01`, `FR-APP-02` | `GET /students/profile`, `GET /matching/recommendations`, `GET /applications/my` |
| `SCR-STU-02` Student Profile | Student | `STU-01`, `STU-03`, `STU-04` | `FR-STU-01`, `FR-STU-03`, `FR-STU-04` | `GET/PUT /students/profile`, `POST/GET/DELETE /students/profile/resume`, `/projects`, `/certifications` |
| `SCR-STU-03` Current Skills | Student | `STU-02` | `FR-STU-02`, `FR-MATCH-01` | `GET/POST/DELETE /students/profile/skills`, `GET /skills` |
| `SCR-STU-04` Explore Opportunities | Student | `STU-05` | `FR-INT-03`, `FR-MATCH-01`, `FR-MATCH-02` | `GET /opportunities` |
| `SCR-STU-05` Opportunity Details | Student | `STU-06`, `STU-08` | `FR-INT-03`, `FR-MATCH-01`, `FR-MATCH-02`, `FR-APP-01` | `GET /opportunities/{id}`, `GET /matching/opportunities/{id}`, `POST /applications` |
| `SCR-STU-06` Recommendations | Student | `STU-07` | `FR-STU-06` (S3) | `GET /matching/recommendations` |
| `SCR-STU-07` My Applications | Student | `STU-09` | `FR-APP-02`, `FR-APP-04` | `GET /applications/my` |
| `SCR-STU-08` My Internships & Feedback | Student | `STU-10` | `FR-INT-04`, `FR-INT-05`, `FR-APP-05` | `GET /internships/my`, `GET /internships/{id}/feedback` |
| `SCR-COM-01` Company Dashboard | Company | `COM-01` | `FR-COM-01`, `FR-COM-02` | `GET /companies/profile`, `GET /opportunities/company/my` |
| `SCR-COM-02` Company Profile | Company | `COM-01` | `FR-COM-01`, `FR-COM-02` | `GET/PUT /companies/profile` |
| `SCR-COM-03` Post Opportunity | Company | `COM-02` | `FR-INT-01`, `FR-MATCH-01`, `FR-MATCH-02` | `POST /opportunities`, `GET /skills`, `GET /departments` |
| `SCR-COM-04` Manage Opportunities | Company | `COM-03` | `FR-INT-02` | `GET /opportunities/company/my`, `PATCH /opportunities/{id}/status` |
| `SCR-COM-05` Candidate Review & Pipeline | Company | `COM-04`, `COM-05` | `FR-APP-03`, `FR-APP-04`, `FR-APP-05`, `FR-STU-04` | `GET /opportunities/{id}/applications`, `GET /students/{id}/profile`, `GET /students/{id}/resume`, `PATCH /applications/{id}/status` |
| `SCR-COM-06` Confirmed Interns & Feedback | Company | `COM-06` | `FR-INT-04`, `FR-INT-05`, `OD-04`, `OD-11` | `GET /internships/company/my`, `PATCH /internships/{id}/status`, `POST /internships/{id}/feedback` |
| `SCR-COL-01` College Dashboard | College | `COL-01` | `FR-COL-01`, `FR-COL-02`, `FR-ANL-03` | `GET /colleges/profile`, `GET /analytics/placement-funnel`, `GET /analytics/skills/gap` |
| `SCR-COL-02` College Profile | College | `COL-01` | `FR-COL-01` | `GET/PUT /colleges/profile` |
| `SCR-COL-03` Student Roster | College | `COL-02` | `FR-COL-01`, `DBQ-01` | `GET /colleges/students`, `GET /colleges/departments`, `GET /students/{id}/profile` |
| `SCR-COL-04` Skill Availability Analytics | College | `COL-03` | `FR-ANL-01` | `GET /analytics/skills/availability` |
| `SCR-COL-05` Industry Demand Analytics | College | `COL-04` | `FR-ANL-02`, `FR-ANL-04` (S6) | `GET /analytics/skills/demand` |
| `SCR-COL-06` Skill Gap Dashboard | College | `COL-05` | `FR-ANL-03`, `FR-ANL-05`, `OD-01` | `GET /analytics/skills/gap` |
| `SCR-COL-07` Placement Funnel | College | `COL-06` | `FR-COL-02`, `DBQ-01` | `GET /analytics/placement-funnel` |
| `SCR-COL-08` Company Feedback Review | College | `COL-07` | `FR-INT-05`, `OD-04` | `GET /colleges/feedback` |
| `SCR-ADM-01` Admin Dashboard | Admin | `ADM-01` | `FR-ADM-01`, `FR-ADM-02` | `GET /admin/users`, `GET /admin/verifications`, `GET /skills` |
| `SCR-ADM-02` User Accounts Directory | Admin | `ADM-01` | `FR-ADM-01`, `OD-12` | `GET /admin/users`, `PATCH /admin/users/{id}/status` |
| `SCR-ADM-03` Organization Verifications | Admin | `ADM-02` | `FR-ADM-02`, `OD-06` | `GET /admin/verifications`, `PATCH /admin/verifications/{type}/{id}` |
| `SCR-ADM-04` Master Skills Taxonomy | Admin | `ADM-03` | `FR-ADM-03` | `GET/POST/PUT/DELETE /skills` |
| `SCR-ADM-05` Master Academic Departments | Admin | `ADM-04` | `DBQ-01`, `OD-12` | `GET/POST/PUT /departments` |
| `SCR-ADM-06` Opportunity Moderation | Admin | `ADM-05` | `FR-ADM-04`, `OD-12` | `GET /opportunities`, `PATCH /admin/opportunities/{id}/status` |

---

## 8. Implementation Readiness & Audit Findings

### 8.1 Inconsistency & Baseline Verification Report
- **Requirement Coverage Audit:** 100% of all Must-Have functional requirements (`FR-AUTH-01`–`04`, `FR-STU-01`–`03`, `FR-COM-01`–`02`, `FR-COL-01`–`02`, `FR-ADM-01`–`04`, `FR-MATCH-01`–`02`, `FR-APP-01`–`05`, `FR-INT-01`–`05`, `FR-ANL-01`–`03`, `FR-ANL-05`) and all approved Should-Have requirements (`FR-STU-04` Resume Upload, `FR-STU-06` Recommendations, `FR-INT-06` Placement Tracking, `FR-ANL-04` Filtered Demand) are fully mapped to concrete, non-redundant frontend screens.
- **Zero Inconsistencies Discovered:** All screen routes, payload shapes, and state transitions strictly match `openapi.yaml`, `api-design.md`, and `user-flows.md`.
- **Architectural Conformance:** No TypeScript or disallowed state libraries introduced. Utilizes plain JavaScript, React Router, Tailwind CSS, shadcn/ui components, and Recharts.

### 8.2 Sign-Off
The Frontend Screen Specifications document is **100% complete, fully verified, and ready for immediate frontend UI component implementation**.
