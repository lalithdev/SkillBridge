# SkillBridge — UX User Flows Specification

**Phase:** System Design (UX / User Flows)  
**Version:** 1.0  
**Status:** APPROVED — UX Specification Baseline  
**Date:** 2026-08-28  
**Derived from:** PRD.md · SRS.md · architecture.md · database.md · openapi.yaml · api-design.md  
**Primary Actors:** Student · Company · College · Admin  

---

## Table of Contents

1. [Introduction & UX Design Principles](#1-introduction--ux-design-principles)
2. [Global Navigation & Role Routing Architecture](#2-global-navigation--role-routing-architecture)
3. [Authentication & Session Management Flows (Common)](#3-authentication--session-management-flows-common)
   - [Flow AUTH-01: User Registration](#flow-auth-01-user-registration)
   - [Flow AUTH-02: User Login & Role Dashboard Routing](#flow-auth-02-user-login--role-dashboard-routing)
   - [Flow AUTH-03: Session Validation & Access Control](#flow-auth-03-session-validation--access-control)
   - [Flow AUTH-04: User Logout](#flow-auth-04-user-logout)
4. [Student User Flows](#4-student-user-flows)
   - [Flow STU-01: Profile Setup & Academic Information](#flow-stu-01-profile-setup--academic-information)
   - [Flow STU-02: Current Skills Management](#flow-stu-02-current-skills-management)
   - [Flow STU-03: Portfolio Projects & Certifications](#flow-stu-03-portfolio-projects--certifications)
   - [Flow STU-04: Resume File Upload & Management](#flow-stu-04-resume-file-upload--management)
   - [Flow STU-05: Browse & Search Opportunities](#flow-stu-05-browse--search-opportunities)
   - [Flow STU-06: View Opportunity Details & Skill Match Breakdown](#flow-stu-06-view-opportunity-details--skill-match-breakdown)
   - [Flow STU-07: View Recommended Opportunities](#flow-stu-07-view-recommended-opportunities)
   - [Flow STU-08: Submit Application](#flow-stu-08-submit-application)
   - [Flow STU-09: Track Submitted Applications](#flow-stu-09-track-submitted-applications)
   - [Flow STU-10: Track Confirmed Internship & View Feedback](#flow-stu-10-track-confirmed-internship--view-feedback)
5. [Company User Flows](#5-company-user-flows)
   - [Flow COM-01: Company Profile Setup & Verification Badge](#flow-com-01-company-profile-setup--verification-badge)
   - [Flow COM-02: Create & Publish Opportunity Posting](#flow-com-02-create--publish-opportunity-posting)
   - [Flow COM-03: Manage Posted Opportunities](#flow-com-03-manage-posted-opportunities)
   - [Flow COM-04: Candidate Discovery & Ranked Applicant Review](#flow-com-04-candidate-discovery--ranked-applicant-review)
   - [Flow COM-05: Advance Candidate Recruitment Pipeline](#flow-com-05-advance-candidate-recruitment-pipeline)
   - [Flow COM-06: Manage Confirmed Interns & Submit Evaluation Feedback](#flow-com-06-manage-confirmed-interns--submit-evaluation-feedback)
6. [College / Placement Cell User Flows](#6-college--placement-cell-user-flows)
   - [Flow COL-01: College Profile Overview](#flow-col-01-college-profile-overview)
   - [Flow COL-02: Student Roster & Department Directory](#flow-col-02-student-roster--department-directory)
   - [Flow COL-03: Student Skill Availability Analytics](#flow-col-03-student-skill-availability-analytics)
   - [Flow COL-04: Industry Skill Demand Analytics](#flow-col-04-industry-skill-demand-analytics)
   - [Flow COL-05: Skill Gap Analysis Dashboard](#flow-col-05-skill-gap-analysis-dashboard)
   - [Flow COL-06: Placement Funnel Monitoring](#flow-col-06-placement-funnel-monitoring)
   - [Flow COL-07: Aggregated Company Feedback Review](#flow-col-07-aggregated-company-feedback-review)
7. [Platform Admin User Flows](#7-platform-admin-user-flows)
   - [Flow ADM-01: User Account Directory & Deactivation](#flow-adm-01-user-account-directory--deactivation)
   - [Flow ADM-02: Organization Verification Review Queue](#flow-adm-02-organization-verification-review-queue)
   - [Flow ADM-03: Master Skills Taxonomy Management](#flow-adm-03-master-skills-taxonomy-management)
   - [Flow ADM-04: Master Academic Departments Management](#flow-adm-04-master-academic-departments-management)
   - [Flow ADM-05: Opportunity Moderation & Force-Closing](#flow-adm-05-opportunity-moderation--force-closing)
8. [Cross-Flow State & Exception Matrix](#8-cross-flow-state--exception-matrix)

---

## 1. Introduction & UX Design Principles

This document defines the complete user flows for all four primary roles in **SkillBridge** (Student, Company, College Placement Cell, and Admin). It establishes the interaction contracts, navigation structures, system validations, and UX state behaviors (Loading, Success, Empty, and Error) governing the frontend application.

### UX Core Principles
1. **Role-Scoped Context:** Every authenticated screen strictly isolates navigation and data to the logged-in user's role and institutional affiliation.
2. **Deterministic UI State Handling:** Every data-fetching screen explicitly implements all four UI states:
   - **Loading:** Non-blocking skeleton loaders / `<LoadingSpinner />`.
   - **Success:** Rendered interactive data and actionable controls.
   - **Empty:** Informative `<EmptyState />` with contextual call-to-action buttons.
   - **Error:** Clear, non-technical `<ErrorMessage />` with a retry mechanism and field-level validation feedback.
3. **Explicit Skill Coverage Semantics:** All matching scores and availability percentages explicitly communicate **skill coverage/presence** (not verified proficiency) to prevent misinterpretation.
4. **Zero-Guesswork Form Feedback:** Input forms validate inline with clear constraints (character caps, allowed file types, numeric ranges) and map API validation errors directly to the offending fields.

---

## 2. Global Navigation & Role Routing Architecture

```
                                  ┌───────────────────────────┐
                                  │      Public Visitor       │
                                  └─────────────┬─────────────┘
                                                │
                                 ┌──────────────┴──────────────┐
                                 ▼                             ▼
                        ┌──────────────────┐          ┌──────────────────┐
                        │   /login Route   │          │  /register Route │
                        └────────┬─────────┘          └────────┬─────────┘
                                 │                             │
                                 └──────────────┬──────────────┘
                                                │ (JWT Issued)
                                                ▼
                                  ┌───────────────────────────┐
                                  │    AuthContext / Router   │
                                  └─────────────┬─────────────┘
                                                │
             ┌──────────────────┬───────────────┴───────────────┬──────────────────┐
             ▼                  ▼                               ▼                  ▼
     ┌───────────────┐  ┌───────────────┐               ┌───────────────┐  ┌───────────────┐
     │ Role: STUDENT │  │ Role: COMPANY │               │ Role: COLLEGE │  │ Role: ADMIN   │
     └───────┬───────┘  └───────┬───────┘               └───────┬───────┘  └───────┬───────┘
             │                  │                               │                  │
             ▼                  ▼                               ▼                  ▼
     /student/dashboard /company/dashboard              /college/dashboard /admin/dashboard
     ├── Profile & Skills├── Profile & Badge            ├── Students & Dept├── User Accounts
     ├── Browse Postings├── Post Opportunity           ├── Skill Gap Anal ├── Verifications
     ├── Recommendations├── Ranked Applicants          ├── Industry Demand├── Skill Taxonomy
     ├── My Applications├── Pipeline Stage             ├── Placement Funnl└── Moderation
     └── My Internships └── Confirmed Outcomes         └── Co. Feedback
```

---

## 3. Authentication & Session Management Flows (Common)

### Flow AUTH-01: User Registration
- **User Role:** Anonymous Visitor (`STUDENT`, `COMPANY`, or `COLLEGE`)
- **SRS Traceability:** `FR-AUTH-01`
- **API Endpoint:** `POST /api/v1/auth/register`
- **Entry Point:** Public Navbar → "Register" button (`/register`)
- **Preconditions:** User is unauthenticated.
- **User Action:**
  1. Select role tab: "Student", "Company", or "College" (Admin self-registration is forbidden).
  2. Enter primary credentials: Email and Password (min 8 characters per OD-10).
  3. Fill role-specific registration fields:
     - **Student:** First Name, Last Name, College selection (dropdown from active colleges), Department selection (dropdown from active departments), Year of Study (1–8), CGPA (0.00–10.00).
     - **Company:** Company Name, Industry, Location, Contact Email, Contact Phone, Website.
     - **College:** College Name, Address, Contact Email, Contact Phone, Website.
  4. Click "Create Account".
- **System Response & Processing:**
  - Client-side validation checks email format, password length (≥ 8), and numeric boundaries.
  - Submits registration payload to `POST /auth/register`.
  - Backend verifies email uniqueness, creates `users` entity and associated profile entity (`student_profiles`, `company_profiles`, or `colleges`) inside an atomic transaction.
- **Next Step:** Navigates to `/login` with a banner: *"Registration successful! Please log in with your credentials."*
- **Success State:** Registration completes; user is redirected to Login view.
- **Error States:**
  - `409 Conflict`: Email already exists → Inline alert on email field: *"An account with this email already exists."*
  - `400 Bad Request`: Validation failure → Highlight offending fields with server message (e.g., *"CGPA must be between 0.0 and 10.0"*).
- **Authorization & Restrictions:** Admin role cannot be registered via public registration.

---

### Flow AUTH-02: User Login & Role Dashboard Routing
- **User Role:** Any Registered User (`STUDENT`, `COMPANY`, `COLLEGE`, `ADMIN`)
- **SRS Traceability:** `FR-AUTH-02`, `FR-AUTH-04`
- **API Endpoint:** `POST /api/v1/auth/login`
- **Entry Point:** Public Navbar → "Login" button (`/login`)
- **Preconditions:** User has a registered account.
- **User Action:**
  1. Enter registered Email and Password.
  2. Click "Sign In".
- **System Response & Processing:**
  - Submits credentials to `POST /auth/login`.
  - Backend verifies BCrypt hash, checks `users.is_active = true`, generates signed 24h JWT containing `role`, `userId`, and institutional IDs (`collegeId`, `companyId`, `studentProfileId`).
  - Frontend receives `AuthResponse` (`token`, `role`, `userId`, etc.), writes token to `sessionStorage` and initializes `AuthContext`.
- **Next Step:** Deterministic routing based on `role`:
  - `STUDENT` → `/student/dashboard`
  - `COMPANY` → `/company/dashboard`
  - `COLLEGE` → `/college/dashboard`
  - `ADMIN` → `/admin/dashboard`
- **Success State:** Role dashboard renders with user-specific welcome banner and navigation sidebar.
- **Error States:**
  - `401 Unauthorized`: Invalid credentials or deactivated account → Alert banner: *"Invalid email or password."* or *"Account has been deactivated. Contact platform administrator."*
  - `400 Bad Request`: Malformed email/empty fields → Inline field validation errors.
- **Authorization & Restrictions:** Public endpoint.

---

### Flow AUTH-03: Session Validation & Access Control
- **User Role:** Any Authenticated User
- **SRS Traceability:** `FR-AUTH-03`, `FR-AUTH-04`
- **API Endpoint:** `GET /api/v1/auth/me` + Client Axios Interceptor
- **Entry Point:** Any protected URL route (e.g., `/student/*`, `/company/*`, `/college/*`, `/admin/*`)
- **Preconditions:** User has an active browser tab with token in `sessionStorage`.
- **User Action:** User navigates to or refreshes a protected route.
- **System Response & Processing:**
  - `ProtectedRoute` inspects `AuthContext`:
    1. If token is absent → Redirects to `/login?redirect={path}`.
    2. If token role does not match required route role → Redirects to `/unauthorized`.
  - Axios attaches `Authorization: Bearer <token>` on all requests.
  - If backend responds with `401 Unauthorized` (token expired after 24h):
    - Axios response interceptor clears `sessionStorage` and `AuthContext`.
    - Redirects user to `/login` with notification: *"Session expired. Please log in again."*
- **Success State:** Protected page content renders cleanly.
- **Error States:**
  - `403 Forbidden`: Cross-role route access attempt → Renders dedicated `/unauthorized` view with *"Access Denied: You do not have permission to view this page."* and a button to return to home dashboard.

---

### Flow AUTH-04: User Logout
- **User Role:** Any Authenticated User
- **SRS Traceability:** `FR-AUTH-03`
- **API Endpoint:** `POST /api/v1/auth/logout`
- **Entry Point:** Top Navigation Bar → User Avatar Menu → "Log Out"
- **Preconditions:** User is logged in.
- **User Action:** Click "Log Out".
- **System Response & Processing:**
  - Sends `POST /auth/logout` (stateless acknowledgement returning `204 No Content`).
  - Frontend immediately purges JWT from `sessionStorage` and resets `AuthContext`.
- **Next Step:** Navigates to `/login`.
- **Success State:** User is unauthenticated; navigation bar reverts to public state.

---

## 4. Student User Flows

### Flow STU-01: Profile Setup & Academic Information
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-STU-01`
- **API Endpoints:** `GET /api/v1/students/profile`, `PUT /api/v1/students/profile`
- **Entry Point:** Student Sidebar → "My Profile" (`/student/profile`)
- **Preconditions:** Student is authenticated.
- **User Action:**
  1. View current personal and academic profile.
  2. Click "Edit Profile".
  3. Modify editable fields: First Name, Last Name, Department (dropdown from active master `departments`), Year of Study (1–8), CGPA (0.00–10.00), Career Interests (free-text), Portfolio URL, GitHub URL.
  4. Note: College affiliation is fixed at registration (OD-05) and displayed as read-only.
  5. Click "Save Changes".
- **System Response & Processing:**
  - Frontend validates CGPA range (0.0–10.0) and URL formats.
  - Submits payload to `PUT /students/profile`.
  - Backend updates `student_profiles` record.
- **Next Step:** Profile screen re-renders in read-only mode with updated values.
- **Success State:** Toast notification: *"Profile updated successfully."*
- **Error States:**
  - `400 Bad Request`: Invalid CGPA or URL format → Inline field validation errors.
  - `403 Forbidden`: Attempting to modify another student's profile → Access denied alert.
- **Authorization & Restrictions:** Scoped strictly to the authenticated student (`@PreAuthorize` ownership).

---

### Flow STU-02: Current Skills Management
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-STU-02`, `FR-MATCH-01`
- **API Endpoints:** `GET /api/v1/students/profile/skills`, `POST /api/v1/students/profile/skills`, `DELETE /api/v1/students/profile/skills/{skillId}`, `GET /api/v1/skills`
- **Entry Point:** Student Sidebar → "My Skills" (`/student/skills`) or Profile Tab
- **Preconditions:** Student is logged in.
- **User Action:**
  1. View current list of tagged skills displayed as badges with categories.
  2. To Add a Skill:
     - Type in the autocomplete search input (queries `GET /skills?search=...`).
     - Select canonical skill from dropdown (e.g., "React", "PostgreSQL").
     - Click "Add Skill".
  3. To Remove a Skill:
     - Click the "×" icon on an existing skill badge.
     - Confirm removal in the confirmation dialog.
- **System Response & Processing:**
  - Add: Submits `POST /students/profile/skills` (`{ skillId }`). Backend inserts into `student_skills`.
  - Remove: Submits `DELETE /students/profile/skills/{skillId}`. Backend deletes row from `student_skills`.
  - System recalculates student's match score cache across active opportunities.
- **Next Step:** Skills badge list updates dynamically.
- **Success State:** Toast notification: *"Skill added"* / *"Skill removed"*. Match percentages on opportunity listings update immediately.
- **Empty State:** When 0 skills are added → Render `<EmptyState title="No skills added yet" description="Add your technical skills from the master catalog to start matching with internships." />`.
- **Error States:**
  - `409 Conflict`: Adding a skill already attached to the profile → Toast: *"You have already added this skill."*
  - `400 Bad Request`: Application-level cap reached (e.g., max 30 skills per DBQ-03) → Alert: *"Maximum skill limit reached."*
- **Authorization & Restrictions:** Students can only manage their own skill inventory.

---

### Flow STU-03: Portfolio Projects & Certifications
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-STU-03`
- **API Endpoints:** 
  - Projects: `GET /api/v1/students/profile/projects`, `POST /projects`, `PUT /projects/{id}`, `DELETE /projects/{id}`
  - Certifications: `GET /api/v1/students/profile/certifications`, `POST /certifications`, `PUT /certifications/{id}`, `DELETE /certifications/{id}`
- **Entry Point:** Student Profile → "Portfolio & Certifications" Tab (`/student/profile#portfolio`)
- **Preconditions:** Student is logged in.
- **User Action:**
  - **Add Project:** Click "Add Project" → Modal opens → Enter Title, Description, Project URL → Click "Save Project".
  - **Add Certification:** Click "Add Certification" → Modal opens → Enter Title, Issuer, Issue Date, Certificate URL → Click "Save Certification".
  - **Edit/Delete:** Click "Edit" or "Delete" icon on existing item cards.
- **System Response & Processing:**
  - Submits DTO to respective REST endpoint.
  - Backend persists in `projects` or `certifications` linked to `student_profiles.id`.
- **Next Step:** Modal closes; project/certification list refreshes.
- **Success State:** Toast notification: *"Portfolio item saved successfully."*
- **Empty State:** When no items exist → Render contextual empty state: *"No projects or certifications added. Showcase your practical work to prospective employers."*
- **Error States:** `400 Bad Request`: Missing title or invalid URL format → Field validation highlight.
- **Authorization & Restrictions:** Student owns project and certification records.

---

### Flow STU-04: Resume File Upload & Management
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-STU-04` (Should-Have / S1, OD-08)
- **API Endpoints:** `POST /api/v1/students/profile/resume`, `GET /api/v1/students/{studentId}/resume`, `DELETE /api/v1/students/profile/resume`
- **Entry Point:** Student Profile → "Resume" Section (`/student/profile#resume`)
- **Preconditions:** Student is logged in.
- **User Action:**
  1. Click "Upload Resume" or drag-and-drop file into the upload zone.
  2. Select file (PDF or DOCX, max 5 MB).
  3. Click "Upload".
  4. To View/Replace/Delete:
     - Click "Download Resume" to stream/view current file.
     - Click "Replace Resume" to upload a new version.
     - Click "Delete Resume" to remove existing file.
- **System Response & Processing:**
  - Client validates file extension and size (≤ 5,242,880 bytes).
  - Submits `multipart/form-data` to `POST /students/profile/resume`.
  - Backend verifies content MIME type, stores file via `FileStorageService`, and updates `student_profiles.resume_path`.
- **Next Step:** Upload dropzone replaces with resume card displaying filename, upload date, download link, and delete button.
- **Success State:** Toast: *"Resume uploaded successfully."*
- **Empty State:** Dropzone with helper text: *"Upload your resume in PDF or DOCX format (Max 5 MB)."*
- **Error States:**
  - `413 Payload Too Large`: File exceeds 5 MB → Alert: *"File size exceeds the 5 MB limit. Please compress your document."*
  - `415 Unsupported Media Type`: File is not PDF or DOCX → Alert: *"Invalid file type. Only PDF and DOCX documents are accepted."*
- **Authorization & Restrictions:** Students can only upload/delete their own resume. Companies can only download resumes for students who applied to their postings.

---

### Flow STU-05: Browse & Search Opportunities
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-INT-03`, `FR-MATCH-01`, `FR-MATCH-02`
- **API Endpoint:** `GET /api/v1/opportunities?search=...&type=...&mode=...&location=...&page=0&size=10`
- **Entry Point:** Student Sidebar → "Explore Opportunities" (`/student/opportunities`)
- **Preconditions:** Student is logged in and has profile populated.
- **User Action:**
  1. View list of open internship and placement postings.
  2. Apply search keywords (e.g., "Backend", "Full Stack").
  3. Filter by Type (`INTERNSHIP` / `PLACEMENT`), Mode (`ONSITE` / `REMOTE` / `HYBRID`), or Location.
  4. View key metadata on each opportunity card:
     - Role Title & Company Name (with Verification trust badge).
     - Type, Mode, Location, Duration, Stipend/Salary.
     - **Skill Match Score:** Visual badge (e.g., `80% Match (4/5 skills)`).
     - **Eligibility Tag:** Green `"Eligible"` or Amber `"Ineligible (CGPA/Branch)"`.
     - Application Deadline countdown.
- **System Response & Processing:**
  - Client sends paginated query params to `GET /opportunities`.
  - Backend dynamically computes match score and evaluates branch/year/CGPA eligibility for the requesting student on each record.
- **Next Step:** Click on an opportunity card to navigate to detail view (`/student/opportunities/{id}`).
- **Success State:** Paginated grid/list of opportunities rendered.
- **Empty State:** If no opportunities match query filters → Render `<EmptyState title="No opportunities found" description="Try adjusting your filters or search keywords." />`.
- **Error States:** `500 Server Error` → Render `<ErrorMessage message="Failed to load opportunities. Please retry." />`.

---

### Flow STU-06: View Opportunity Details & Skill Match Breakdown
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-INT-03`, `FR-MATCH-01`, `FR-MATCH-02`
- **API Endpoints:** `GET /api/v1/opportunities/{id}`, `GET /api/v1/matching/opportunities/{id}`
- **Entry Point:** Opportunity Card click → (`/student/opportunities/{id}`)
- **Preconditions:** Student is logged in; opportunity exists with `status = OPEN`.
- **User Action:**
  1. Review detailed job description, company details, location, duration, and stipend.
  2. Inspect the **Skill Match & Gap Breakdown Widget**:
     - **Match Percentage:** Visual progress ring (e.g., `75% Match`).
     - **Matched Skills:** Green badges indicating skills currently on student's profile (e.g., `[✓ Java]`, `[✓ SQL]`, `[✓ Git]`).
     - **Missing Skills:** Gray/Red outline badges indicating required skills the student lacks (e.g., `[+ Docker]`).
     - **Skill Coverage Disclaimer:** Label: *"Skill coverage represents required skill overlap, not verified proficiency."*
  3. Inspect **Eligibility Status Card**:
     - Displays required departments vs. student's department.
     - Displays required academic years vs. student's year.
     - Displays minimum CGPA vs. student's CGPA.
     - If ineligible, lists explicit failure reasons (e.g., *"Requires minimum CGPA 7.5 (Current: 7.1)"*).
  4. View "Apply Now" button state (Enabled if eligible and not applied; Disabled if ineligible or already applied).
- **System Response & Processing:**
  - Fetches posting details and computed match breakdown from `GET /matching/opportunities/{id}`.
- **Next Step:** Click "Apply Now" to trigger application modal (Flow STU-08).
- **Success State:** Comprehensive opportunity detail view rendered.
- **Error States:** `404 Not Found`: Opportunity does not exist or was deleted → Renders *"Opportunity not found"* with link to return to browse list.

---

### Flow STU-07: View Recommended Opportunities
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-STU-06` (Should-Have / S3)
- **API Endpoint:** `GET /api/v1/matching/recommendations?page=0&size=10`
- **Entry Point:** Student Dashboard → "Recommended For You" Widget (`/student/dashboard`)
- **Preconditions:** Student has at least 1 skill on their profile.
- **User Action:**
  1. View personalized list of open opportunities ordered strictly by descending Skill Match Percentage.
  2. Click "View All Recommendations" to open dedicated full-page recommendations view.
- **System Response & Processing:**
  - Backend retrieves open postings, evaluates match scores against student's skill set, filters out postings already applied to, and sorts descending by `matchPercent`.
- **Next Step:** Click opportunity card to view details (Flow STU-06).
- **Success State:** Ranked recommendation feed rendered.
- **Empty State:** If student has no skills → Banner: *"Add skills to your profile to receive personalized internship recommendations."* with button to `/student/skills`.

---

### Flow STU-08: Submit Application
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-APP-01`, `FR-APP-03`
- **API Endpoint:** `POST /api/v1/applications`
- **Entry Point:** Opportunity Detail View → "Apply Now" button (`/student/opportunities/{id}`)
- **Preconditions:** 
  - Opportunity `status = OPEN` and `application_deadline >= today`.
  - Student is verified eligible (FR-MATCH-02).
  - Student has not previously applied to this opportunity.
- **User Action:**
  1. Click "Apply Now".
  2. Confirmation modal appears summarizing:
     - Opportunity title and company name.
     - Student's current match score (e.g., `80% Match`).
     - Notice: *"Your current profile, skills, and resume will be submitted to the employer. Your match score will be snapshot at application time."*
  3. Click "Confirm & Submit Application".
- **System Response & Processing:**
  - Submits `{ opportunityId }` to `POST /applications`.
  - Backend verifies:
    1. Opportunity is OPEN and deadline has not passed (DBQ-05).
    2. Student meets branch, year, and CGPA eligibility criteria.
    3. No duplicate application exists for `(student_profile_id, opportunity_id)`.
  - Computes exact match score and creates `applications` record with initial status `APPLIED` and immutable `match_percent_at_apply` snapshot.
- **Next Step:** Modal closes; "Apply Now" button replaces with disabled badge: `[✓ Applied - Under Review]`.
- **Success State:** Toast notification: *"Application submitted successfully! You can track your status in My Applications."*
- **Error States:**
  - `409 Conflict`: Application already submitted → Alert: *"You have already submitted an application for this opportunity."*
  - `400 Bad Request` (Ineligible): *"You do not meet the eligibility requirements for this posting."*
  - `400 Bad Request` (Deadline passed): *"This opportunity has closed and is no longer accepting applications."*
- **Authorization & Restrictions:** Only students may submit applications.

---

### Flow STU-09: Track Submitted Applications
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-APP-02`, `FR-APP-04`
- **API Endpoint:** `GET /api/v1/applications/my?page=0&size=10`
- **Entry Point:** Student Sidebar → "My Applications" (`/student/applications`)
- **Preconditions:** Student is logged in.
- **User Action:**
  1. View list of all submitted applications.
  2. Inspect status stepper pipeline for each application:
     - `APPLIED` → `UNDER_REVIEW` → `SHORTLISTED` → `INTERVIEW` → `SELECTED` / `REJECTED`
  3. View applied date, company name, snapshot match percentage, and current status badge.
- **System Response & Processing:**
  - Fetches student's applications ordered by `applied_at DESC`.
- **Next Step:** If status reaches `SELECTED`, link appears: *"View Confirmed Internship Outcome"*.
- **Success State:** Application tracking table/cards rendered with active pipeline step highlighted.
- **Empty State:** When no applications exist → Render `<EmptyState title="No applications submitted" description="You haven't applied to any opportunities yet. Explore open postings to get started." />` with CTA button to `/student/opportunities`.

---

### Flow STU-10: Track Confirmed Internship & View Feedback
- **User Role:** `STUDENT`
- **SRS Traceability:** `FR-INT-04`, `FR-INT-05`, `FR-APP-05`
- **API Endpoints:** `GET /api/v1/internships/my`, `GET /api/v1/internships/{internshipId}/feedback`
- **Entry Point:** Student Sidebar → "My Internships" (`/student/internships`)
- **Preconditions:** At least one application has reached `SELECTED` status.
- **User Action:**
  1. View confirmed internship or placement records.
  2. Review role, company, type (`INTERNSHIP` / `PLACEMENT`), start date, end date, and lifecycle status (`UPCOMING`, `ONGOING`, `COMPLETED`).
  3. When status reaches `COMPLETED`:
     - View "Company Evaluation & Feedback" card.
     - Read qualitative free-text feedback submitted by the employer (OD-04, DBQ-04).
- **System Response & Processing:**
  - Fetches student's outcome records and associated feedback.
- **Next Step:** Student can reference feedback for career growth and profile refinement.
- **Success State:** Internship details and completed company feedback displayed.
- **Empty State:** If no selections yet → Empty state indicating no confirmed internships.

---

## 5. Company User Flows

### Flow COM-01: Company Profile Setup & Verification Badge
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-COM-01`, `FR-COM-02`
- **API Endpoints:** `GET /api/v1/companies/profile`, `PUT /api/v1/companies/profile`
- **Entry Point:** Company Sidebar → "Company Profile" (`/company/profile`)
- **Preconditions:** Company user is authenticated.
- **User Action:**
  1. View current company profile and verification status badge (`PENDING`, `VERIFIED`, or `REJECTED`).
  2. Click "Edit Profile".
  3. Update: Company Name, Industry sector, Description, Office Location, Website URL, Contact Email, Contact Phone.
  4. Click "Save Profile".
- **System Response & Processing:**
  - Validates required fields and URLs.
  - Submits `PUT /companies/profile`. Updates `company_profiles` entity.
- **Next Step:** Profile updates; verification badge remains clearly visible at the top.
- **Success State:** Toast: *"Company profile updated successfully."*
- **Error States:** `400 Bad Request`: Missing mandatory fields → Inline validation error.
- **Authorization & Restrictions:** Company can only edit its own profile. Verification status is modified solely by Admin (FR-ADM-02).

---

### Flow COM-02: Create & Publish Opportunity Posting
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-INT-01`, `FR-MATCH-01`, `FR-MATCH-02`
- **API Endpoints:** `POST /api/v1/opportunities`, `GET /api/v1/skills`, `GET /api/v1/departments`
- **Entry Point:** Company Sidebar → "Post Opportunity" (`/company/opportunities/create`)
- **Preconditions:** Company profile exists.
- **User Action:**
  1. Fill opportunity posting form:
     - **Role Title:** e.g., *"Junior Backend Developer Intern"*.
     - **Opportunity Type:** Radio button: `INTERNSHIP` or `PLACEMENT`.
     - **Work Mode:** Select: `ONSITE`, `REMOTE`, or `HYBRID`.
     - **Location:** City / Office location.
     - **Duration (Weeks):** Numeric (for internships).
     - **Stipend / Salary:** Amount and Currency (`INR`).
     - **Application Deadline:** Date picker (must be future date).
     - **Job Description:** Rich/Plain text role expectations.
  2. Define **Eligibility Criteria**:
     - **Eligible Branches:** Multi-select from master `departments` (Leave empty for "All Branches Eligible").
     - **Eligible Years of Study:** Multi-select checkboxes (1, 2, 3, 4, 5+; Leave empty for "All Years Eligible").
     - **Minimum CGPA:** Optional numeric threshold (0.00–10.00; Leave blank for "No Minimum CGPA").
  3. Define **Required Skills (Mandatory, Min 1)**:
     - Search and select skills from master taxonomy (e.g., `Java`, `Spring Boot`, `SQL`).
  4. Click "Publish Opportunity".
- **System Response & Processing:**
  - Validates `requiredSkillIds.length >= 1`, future deadline date, and valid CGPA.
  - Submits `POST /opportunities`.
  - Backend creates `opportunities` record with `status: OPEN`, inserts rows into `required_skills`, `opportunity_required_branches`, and `opportunity_required_years` in a single transaction.
- **Next Step:** Navigates to Manage Opportunities list (`/company/opportunities`).
- **Success State:** Toast: *"Opportunity published successfully and is now open for applications."*
- **Error States:**
  - `400 Bad Request`: Zero required skills selected → Inline error: *"At least one required skill must be selected."*
  - `400 Bad Request`: Application deadline in the past → Error: *"Deadline must be a future date."*
- **Authorization & Restrictions:** Company can only create opportunities under its own profile.

---

### Flow COM-03: Manage Posted Opportunities
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-INT-02`
- **API Endpoints:** `GET /api/v1/opportunities/company/my`, `PUT /api/v1/opportunities/{id}`, `PATCH /api/v1/opportunities/{id}/status`
- **Entry Point:** Company Sidebar → "Manage Opportunities" (`/company/opportunities`)
- **Preconditions:** Company is logged in.
- **User Action:**
  1. View table of all company postings with columns: Role Title, Type, Status (`OPEN` / `CLOSED`), Applicant Count, Deadline, Created Date.
  2. Actions per posting:
     - **View Applicants:** Click to open candidate review list (Flow COM-04).
     - **Edit Posting:** Update description, stipend, or criteria.
     - **Close / Reopen Posting:** Click toggle button (`OPEN` ↔ `CLOSED`).
- **System Response & Processing:**
  - Status toggle sends `PATCH /opportunities/{id}/status` (`{ status: "CLOSED" }` or `"OPEN"`).
  - Backend updates status. Closed postings immediately stop accepting new applications.
- **Next Step:** Status badge updates in real-time.
- **Success State:** Toast: *"Opportunity status updated to CLOSED."*
- **Empty State:** If company has no postings → Render `<EmptyState title="No opportunities posted yet" description="Post your first internship or placement opportunity to start receiving candidate applications." />` with CTA button to `/company/opportunities/create`.
- **Authorization & Restrictions:** Company can only view and mutate its own opportunities (`@PreAuthorize` ownership check).

---

### Flow COM-04: Candidate Discovery & Ranked Applicant Review
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-APP-03`, `FR-STU-04`
- **API Endpoints:** `GET /api/v1/opportunities/{opportunityId}/applications`, `GET /api/v1/students/{studentId}/profile`, `GET /api/v1/students/{studentId}/resume`
- **Entry Point:** Manage Opportunities → Click "View Applicants" on a posting (`/company/opportunities/{id}/applicants`)
- **Preconditions:** At least 1 candidate has applied.
- **User Action:**
  1. View applicant roster filtered strictly to **eligible candidates** and ordered by **Skill Match Percentage Descending** (e.g., 90%, 80%, 70%).
  2. Table columns: Candidate Name, Department, Year, CGPA, Match % Snapshot, Current Status, Actions.
  3. Click candidate row to open **Candidate Detail Drawer**:
     - Inspect candidate's academic info and self-reported skills.
     - Inspect portfolio projects and verified certifications.
     - Click "Download Resume" to stream the student's uploaded PDF/DOCX resume file.
- **System Response & Processing:**
  - Backend retrieves applicants using composite index `(opportunity_id, match_percent_at_apply DESC)`.
  - Resume download validates that candidate applied to this employer's posting before streaming file binary.
- **Next Step:** Advance candidate to next pipeline stage (Flow COM-05).
- **Success State:** Ranked candidate list rendered.
- **Empty State:** If 0 applications submitted → `<EmptyState title="No applicants yet" description="Applications submitted by eligible candidates will appear here ranked by skill match percentage." />`.
- **Authorization & Restrictions:** Company can only view applicants for postings it owns.

---

### Flow COM-05: Advance Candidate Recruitment Pipeline
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-APP-04`, `FR-APP-05`, `OD-03`
- **API Endpoint:** `PATCH /api/v1/applications/{id}/status`
- **Entry Point:** Candidate Detail Drawer or Table Action Menu (`/company/opportunities/{id}/applicants`)
- **Preconditions:** Application is in an active non-terminal state.
- **User Action:**
  1. Click "Change Status" dropdown on candidate record.
  2. Select next valid forward-only stage (OD-03):
     - If `APPLIED` → Select `UNDER_REVIEW` or `REJECTED`.
     - If `UNDER_REVIEW` → Select `SHORTLISTED` or `REJECTED`.
     - If `SHORTLISTED` → Select `INTERVIEW` or `REJECTED`.
     - If `INTERVIEW` → Select `SELECTED` or `REJECTED`.
  3. Confirm status change dialog: *"Are you sure you want to move [Candidate] to [Stage]?"*
- **System Response & Processing:**
  - Submits `{ status: "SHORTLISTED" }` to `PATCH /applications/{id}/status`.
  - Backend validates forward-only transition map.
  - **Special Case (`SELECTED`):** If new status is `SELECTED`, backend transaction updates application status and **automatically creates an `internship_records` entity** (inheriting type, student, company, and initial status `UPCOMING` per FR-APP-05).
- **Next Step:** Applicant table row status updates; student's tracker and college funnel update immediately.
- **Success State:** Toast: *"Candidate moved to SHORTLISTED."* / *"Candidate marked SELECTED. Internship record created."*
- **Error States:** `400 Bad Request`: Skipping or reversing stages (e.g., `APPLIED` → `SELECTED` directly) → Alert: *"Invalid status transition. Pipeline must progress forward one stage at a time."*
- **Authorization & Restrictions:** Company owns the parent opportunity.

---

### Flow COM-06: Manage Confirmed Interns & Submit Evaluation Feedback
- **User Role:** `COMPANY`
- **SRS Traceability:** `FR-INT-04`, `FR-INT-05`, `OD-04`, `OD-11`, `DBQ-04`
- **API Endpoints:** `GET /api/v1/internships/company/my`, `PATCH /api/v1/internships/{id}/status`, `POST /api/v1/internships/{internshipId}/feedback`
- **Entry Point:** Company Sidebar → "Interns & Outcomes" (`/company/internships`)
- **Preconditions:** At least 1 candidate was marked `SELECTED`.
- **User Action:**
  1. View confirmed hires table: Student Name, Role, Type (`INTERNSHIP` / `PLACEMENT`), Status (`UPCOMING`, `ONGOING`, `COMPLETED`), Dates, Feedback Status.
  2. Update Progress: Click "Update Status" to advance `UPCOMING` → `ONGOING` → `COMPLETED`. Optionally set Start Date and End Date.
  3. **Submit Feedback (Gated to `COMPLETED` status per OD-11):**
     - Click "Submit Feedback" button (Enabled only when status = `COMPLETED`).
     - Feedback modal opens with text area.
     - Enter qualitative evaluation text (e.g., *"Demonstrated excellent proficiency in Java and backend architecture..."*).
     - Click "Submit Evaluation".
- **System Response & Processing:**
  - Submits `{ feedbackText }` to `POST /internships/{internshipId}/feedback`.
  - Backend verifies `internship_records.status = 'COMPLETED'` and company ownership.
  - Inserts 1:1 `company_feedback` record.
- **Next Step:** Feedback card updates to *"Feedback Submitted"*. Feedback becomes visible to the student (Flow STU-10) and college (Flow COL-07).
- **Success State:** Toast: *"Company feedback submitted successfully."*
- **Error States:** `400 Bad Request`: Submitting feedback before status is `COMPLETED` → Alert: *"Feedback can only be submitted for completed internships."*
- **Authorization & Restrictions:** Company can only manage hires from its own opportunities.

---

## 6. College / Placement Cell User Flows

### Flow COL-01: College Profile Overview
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-COL-01`
- **API Endpoints:** `GET /api/v1/colleges/profile`, `PUT /api/v1/colleges/profile`
- **Entry Point:** College Sidebar → "College Profile" (`/college/profile`)
- **Preconditions:** College administrator is authenticated.
- **User Action:**
  1. View college institutional details, placement cell contact details, and Admin verification status (`PENDING`, `VERIFIED`, `REJECTED`).
  2. Click "Edit Profile" → Update Address, Website, Placement Email, Placement Phone → Click "Save".
- **System Response & Processing:**
  - Backend updates `colleges` record scoped to authenticated `collegeId` (from JWT).
- **Next Step:** Profile re-renders in read mode.
- **Success State:** Toast: *"College profile updated successfully."*
- **Authorization & Restrictions:** Scoped strictly to the logged-in college.

---

### Flow COL-02: Student Roster & Department Directory
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-COL-01`, `DBQ-01`
- **API Endpoints:** `GET /api/v1/colleges/students?departmentId=...&page=0&size=10`, `GET /api/v1/colleges/departments`, `GET /api/v1/students/{id}/profile`
- **Entry Point:** College Sidebar → "Students & Departments" (`/college/students`)
- **Preconditions:** College account is active.
- **User Action:**
  1. View Department Summary cards displaying total enrolled student counts per department.
  2. Filter student roster by Department dropdown or Search by student name.
  3. View student list: Name, Department, Year, CGPA, Skill Count, Application Count.
  4. Click a student row to open **Student Academic Profile Drawer** (read-only view of student's skills, portfolio projects, certifications).
- **System Response & Processing:**
  - Backend queries `student_profiles` enforcing `WHERE sp.college_id = :collegeId` (derived from token).
- **Next Step:** College placement coordinator inspects student preparedness.
- **Success State:** Paginated student roster rendered.
- **Empty State:** If no students onboarded yet → `<EmptyState title="No students registered" description="Students who register and select your college will appear here." />`.
- **Authorization & Restrictions:** College can ONLY view students affiliated with its own institution.

---

### Flow COL-03: Student Skill Availability Analytics
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-ANL-01`
- **API Endpoint:** `GET /api/v1/analytics/skills/availability`
- **Entry Point:** College Sidebar → "Skill Availability" (`/college/analytics/availability`)
- **Preconditions:** College has enrolled students with skills added.
- **User Action:**
  1. View institutional skill availability breakdown table & bar chart.
  2. Inspect per-skill presence: Skill Name, Category, Total Students Possessing Skill, **Availability %** (`(students with skill / total college students) * 100`).
  3. Sort by Availability % ascending/descending or filter by Skill Category.
  4. Observe disclaimer banner: *"Availability percentage measures self-reported skill presence across your student body, not tested proficiency."*
- **System Response & Processing:**
  - Backend executes real-time aggregation across `student_skills` and `student_profiles` scoped to `collegeId`.
- **Next Step:** Use availability insights to compare against industry demand (Flow COL-05).
- **Success State:** Skill availability chart and data table rendered.
- **Empty State:** If students haven't added skills → Empty state prompting student onboarding.

---

### Flow COL-04: Industry Skill Demand Analytics
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-ANL-02`, `FR-ANL-04`, `OD-07`
- **API Endpoint:** `GET /api/v1/analytics/skills/demand?type=...`
- **Entry Point:** College Sidebar → "Industry Demand" (`/college/analytics/demand`)
- **Preconditions:** Open opportunities exist on the platform.
- **User Action:**
  1. View platform-wide industry demand analytics table & chart.
  2. Filter by Opportunity Type (All Postings, `INTERNSHIP`, or `PLACEMENT`).
  3. Inspect per-skill demand metrics: Skill Name, Category, Total Requiring Postings, **Demand %** (`(open postings requiring skill / total open postings) * 100` per OD-07).
  4. Identify top demanded skills in the market (e.g., Python 65%, React 55%, AWS 40%).
- **System Response & Processing:**
  - Backend aggregates across `required_skills` for all postings with `status = 'OPEN'`.
- **Next Step:** Transition to Skill Gap Dashboard to evaluate curriculum alignment (Flow COL-05).
- **Success State:** Industry demand visualization rendered.
- **Empty State:** If no open postings exist → Informational message regarding current market data.

---

### Flow COL-05: Skill Gap Analysis Dashboard
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-ANL-03`, `FR-ANL-05`, `OD-01`
- **API Endpoint:** `GET /api/v1/analytics/skills/gap`
- **Entry Point:** College Sidebar → "Skill Gap Analysis" (`/college/analytics/skill-gap`)
- **Preconditions:** College availability and industry demand data exist.
- **User Action:**
  1. View institutional Skill Gap Dashboard combining Demand vs. Availability.
  2. Inspect the **Recharts Comparative Bar Chart** (Demand % vs. Availability % side-by-side per skill).
  3. Review the **Skill Gap Classification Table**:
     - Columns: Skill Name, Category, Industry Demand %, Student Availability %, Gap (Demand - Availability), **Gap Severity Badge**.
     - Severity Bands (OD-01 Confirmed):
       - 🔴 **HIGH GAP:** $\text{Gap} \ge 30\%$ (Immediate training/workshop recommendation).
       - 🟡 **MODERATE GAP:** $15\% \le \text{Gap} < 30\%$ (Curriculum elective focus).
       - 🟢 **LOW GAP:** $0\% < \text{Gap} < 15\%$ (Balanced supply).
       - 🔵 **SURPLUS:** $\text{Gap} \le 0\%$ (Student availability meets/exceeds demand).
  4. View actionable institutional training suggestions generated from HIGH gap skills.
- **System Response & Processing:**
  - Backend computes availability for the college, demand platform-wide, calculates `gap = demandPct - availabilityPct`, and applies OD-01 threshold classification.
- **Next Step:** Placement coordinators plan targeted student training bootcamps for high-gap skills (e.g., Docker, AWS).
- **Success State:** Interactive skill gap matrix and severity distribution rendered.
- **Empty State:** If insufficient data → Guidance on onboarding more students and skills.

---

### Flow COL-06: Placement Funnel Monitoring
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-COL-02`, `DBQ-01`
- **API Endpoint:** `GET /api/v1/analytics/placement-funnel?departmentId=...`
- **Entry Point:** College Sidebar → "Placement Funnel" (`/college/analytics/funnel`)
- **Preconditions:** College students have active applications.
- **User Action:**
  1. View recruitment pipeline funnel visualization representing total applications across active stages:
     - Applied → Under Review → Shortlisted → Interview → Selected → Rejected.
  2. Filter funnel metrics by Department dropdown (DBQ-01).
  3. Inspect conversion rates between stages (e.g., Shortlisted to Interview conversion %, Interview to Selected conversion %).
- **System Response & Processing:**
  - Backend aggregates `applications.status` counts joined with `student_profiles` where `college_id = :collegeId`.
- **Next Step:** Placement cell identifies recruitment bottlenecks and tracks student hiring success.
- **Success State:** Funnel chart and stage distribution breakdown rendered.
- **Empty State:** If 0 applications exist → Empty funnel state.

---

### Flow COL-07: Aggregated Company Feedback Review
- **User Role:** `COLLEGE`
- **SRS Traceability:** `FR-INT-05`, `OD-04`, `DBQ-04`
- **API Endpoint:** `GET /api/v1/colleges/feedback?page=0&size=10`
- **Entry Point:** College Sidebar → "Company Feedback" (`/college/feedback`)
- **Preconditions:** Employers have submitted feedback for completed internships/placements of college students.
- **User Action:**
  1. View aggregated qualitative feedback feed.
  2. Table/Cards display: Student Name, Department, Company Name, Opportunity Title, Completed Date, Evaluation Text.
  3. Filter feedback by Department or Search by Company.
  4. Identify qualitative industry feedback trends (e.g., communication skills, system design strengths, testing gaps).
- **System Response & Processing:**
  - Backend retrieves `company_feedback` records linked via `internship_records` → `applications` → `student_profiles` where `college_id = :collegeId`.
- **Next Step:** Feedback insights are reviewed in academic council meetings to improve practical curriculum.
- **Success State:** Feedback feed rendered.
- **Empty State:** If no feedback submitted yet → `<EmptyState title="No company feedback yet" description="Employer evaluations submitted upon completion of student internships will appear here." />`.

---

## 7. Platform Admin User Flows

### Flow ADM-01: User Account Directory & Deactivation
- **User Role:** `ADMIN`
- **SRS Traceability:** `FR-ADM-01`, `OD-12`
- **API Endpoints:** `GET /api/v1/admin/users?role=...&search=...&page=0&size=10`, `PATCH /api/v1/admin/users/{id}/status`
- **Entry Point:** Admin Sidebar → "User Accounts" (`/admin/users`)
- **Preconditions:** Admin is authenticated.
- **User Action:**
  1. Search and filter platform user accounts by Role (`STUDENT`, `COMPANY`, `COLLEGE`) and Status (`Active` / `Deactivated`).
  2. View user details: Email, Role, Created Date, Active Status.
  3. Toggle Account Status: Click "Deactivate" / "Activate" button.
  4. Confirm status change dialog.
- **System Response & Processing:**
  - Submits `PATCH /admin/users/{id}/status` (`{ isActive: false }`).
  - Backend updates `users.is_active`. Deactivated user is immediately blocked from logging in (Flow AUTH-02).
- **Next Step:** Table reflects updated account status.
- **Success State:** Toast: *"User account status updated."*
- **Error States:** `404 Not Found`: User ID not found.
- **Authorization & Restrictions:** Strictly restricted to `ADMIN` role.

---

### Flow ADM-02: Organization Verification Review Queue
- **User Role:** `ADMIN`
- **SRS Traceability:** `FR-ADM-02`, `OD-06`, `OD-12`
- **API Endpoints:** `GET /api/v1/admin/verifications`, `PATCH /api/v1/admin/verifications/{type}/{id}`
- **Entry Point:** Admin Sidebar → "Organization Verifications" (`/admin/verifications`)
- **Preconditions:** Companies or Colleges have registered with `PENDING` verification status.
- **User Action:**
  1. View pending verification queue divided into "Company Verifications" and "College Verifications".
  2. Inspect organization profile details: Name, Website, Official Email, Phone, Campus/Office Address.
  3. Actions per row:
     - Click **"Approve / Verify"** (sets status to `VERIFIED`).
     - Click **"Reject"** (sets status to `REJECTED`).
- **System Response & Processing:**
  - Submits `PATCH /admin/verifications/{type}/{id}` (`{ status: "VERIFIED" }`).
  - Backend updates `company_profiles.verification_status` or `colleges.verification_status` and records `verified_at = now()`.
- **Next Step:** Item clears from pending queue; organization's public profile displays verified badge.
- **Success State:** Toast: *"Organization verification status updated to VERIFIED."*
- **Empty State:** When no pending organizations exist → `<EmptyState title="Verification queue clean" description="No companies or colleges currently pending verification." />`.
- **Authorization & Restrictions:** Admin only.

---

### Flow ADM-03: Master Skills Taxonomy Management
- **User Role:** `ADMIN`
- **SRS Traceability:** `FR-ADM-03`, `OD-12`
- **API Endpoints:** `GET /api/v1/skills`, `POST /api/v1/skills`, `PUT /api/v1/skills/{id}`, `DELETE /api/v1/skills/{id}`
- **Entry Point:** Admin Sidebar → "Skills Taxonomy" (`/admin/skills`)
- **Preconditions:** Admin is logged in.
- **User Action:**
  1. View master taxonomy catalog with search by skill name and category filter.
  2. **Add Skill:** Click "Add Skill" → Enter unique Skill Name (e.g., *"FastAPI"*), select Category (e.g., *"Backend Frameworks"*) → Click "Create Skill".
  3. **Edit Skill:** Click "Edit" → Update Name or Category → Click "Save".
  4. **Soft-Deactivate Skill:** Click "Deactivate" → Inactive skill is retained for existing relations but hidden from future dropdown selections.
- **System Response & Processing:**
  - Validates skill name uniqueness (case-insensitive).
  - Submits to `POST /skills` or `PUT /skills/{id}`. Persists in `skills` table.
- **Next Step:** Skill immediately becomes available in student and company autocomplete fields.
- **Success State:** Toast: *"Skill created in master taxonomy."*
- **Error States:** `409 Conflict`: Skill name already exists in taxonomy → Alert: *"A skill with this name already exists."*
- **Authorization & Restrictions:** Admin write access only; all authenticated roles have read access.

---

### Flow ADM-04: Master Academic Departments Management
- **User Role:** `ADMIN`
- **SRS Traceability:** `DBQ-01`, `OD-12`
- **API Endpoints:** `GET /api/v1/departments`, `POST /api/v1/departments`
- **Entry Point:** Admin Sidebar → "Academic Departments" (`/admin/departments`)
- **Preconditions:** Admin is logged in.
- **User Action:**
  1. View standardized academic engineering branches table (e.g., CSE, ECE, MECH, IT, CIVIL).
  2. **Add Department:** Click "Add Department" → Enter Department Name (e.g., *"Data Science and Artificial Intelligence"*), Unique Branch Code (e.g., *"AI-DS"*) → Click "Create Department".
  3. Toggle Active status for existing departments.
- **System Response & Processing:**
  - Validates code and name uniqueness.
  - Submits `POST /departments`. Persists in `departments` master table.
- **Next Step:** New department appears in student profile and opportunity branch eligibility dropdowns.
- **Success State:** Toast: *"Academic department added to master list."*
- **Error States:** `409 Conflict`: Duplicate department code or name → Error highlight.
- **Authorization & Restrictions:** Admin write access only.

---

### Flow ADM-05: Opportunity Moderation & Force-Closing
- **User Role:** `ADMIN`
- **SRS Traceability:** `FR-ADM-04`, `OD-12`
- **API Endpoints:** `GET /api/v1/opportunities`, `PATCH /api/v1/admin/opportunities/{id}/status`
- **Entry Point:** Admin Sidebar → "Opportunity Moderation" (`/admin/opportunities`)
- **Preconditions:** Opportunities are published on the platform.
- **User Action:**
  1. Browse all platform-wide postings across companies.
  2. Search by keyword or filter by company.
  3. If an inappropriate or spam posting is identified: Click "Moderate / Deactivate".
  4. Confirm dialog: *"Deactivate posting: This will immediately close the posting to all students."*
- **System Response & Processing:**
  - Submits `PATCH /admin/opportunities/{id}/status` (`{ status: "CLOSED" }`).
  - Backend sets `opportunities.status = 'CLOSED'`.
- **Next Step:** Posting is immediately removed from student search results and recommendation feeds.
- **Success State:** Toast: *"Opportunity posting has been moderated and closed."*
- **Authorization & Restrictions:** Admin only.

---

## 8. Cross-Flow State & Exception Matrix

The table below summarizes standard UX component state behaviors across every major screen and workflow:

| Screen / Flow Domain | Loading State (`<LoadingSpinner />`) | Success State | Empty State (`<EmptyState />`) | Error State (`<ErrorMessage />`) |
|---|---|---|---|---|
| **Student Skills** (`Flow STU-02`) | Skeleton badge placeholders | Rendered skill badges with remove icons and search typeahead | *"No skills added yet. Search and tag your skills to unlock matches."* | Toast alert on duplicate or cap violation |
| **Opportunity Search** (`Flow STU-05`) | Skeleton card grid | Grid of opportunity cards with match % and eligibility badges | *"No opportunities match your filter criteria. Try resetting filters."* | Error banner with retry button |
| **Opportunity Detail** (`Flow STU-06`) | Full-page layout skeleton | Detailed description + Match breakdown ring + Matched/Missing badges | N/A (404 handled if deleted) | *"Opportunity not found or closed"* with back button |
| **Student Applications** (`Flow STU-09`) | Table row skeletons | Pipeline status steppers + Snapshot match % | *"You have not applied to any opportunities yet."* with Browse CTA | Alert banner on fetch failure |
| **Company Applicants** (`Flow COM-04`) | Ranked table row skeletons | Ranked candidate list sorted by `match_percent_at_apply DESC` | *"No eligible applicants have applied to this posting yet."* | Error banner with retry button |
| **Company Interns** (`Flow COM-06`) | Table skeletons | Confirmed intern roster + Feedback trigger modal | *"No confirmed interns found."* | Alert banner on fetch failure |
| **College Skill Gap** (`Flow COL-05`) | Dual-chart skeletons | Recharts Demand vs. Availability bars + Severity badges table | *"Insufficient data to compute skill gap. Onboard more students."* | Error banner with retry button |
| **Placement Funnel** (`Flow COL-06`) | Funnel chart skeleton | Visual stage progression bars + Department filters | *"No student applications in progress."* | Alert banner with retry button |
| **Admin Verifications** (`Flow ADM-02`) | Queue skeletons | Company and College pending review cards with Approve/Reject | *"Verification queue is empty. All organizations are reviewed."* | Toast alert on action failure |

---

*SkillBridge UX User Flows Specification Complete.*  
*Status: APPROVED — Ready for Screen Specifications & Frontend Implementation.*