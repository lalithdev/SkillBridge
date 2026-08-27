# SkillBridge — Product Requirements Document (PRD)

**SIH Problem Statement:** SIH26044 — Portal for Academia–Industry Collaboration for Skill Mapping, Internships and Placement
**Product:** SkillBridge — Academia–Industry Skill Intelligence & Career Collaboration Platform
**Status:** Draft v0.1
**Source of truth:** The provided SkillBridge Project Overview document only. No requirements below are invented beyond what that document (plus the Must/Should/Later tier list supplied alongside it) supports; every place where the source is silent is called out explicitly rather than filled in.
**Target build constraint:** MVP achievable by a 6-person student team in 3 days (hackathon build).

---

## 1. Product Overview

SkillBridge is a web-based platform connecting **Students**, **Colleges/Placement Cells**, **Companies/Industry**, and a **Platform Admin** around one shared concept: comparing what a student's current skills *are* against what an opportunity *requires*.

The platform's core loop: students maintain a current-skills profile and discover internships/placements; companies define required skills and publish opportunities; the system compares the two and produces a match score, matched skills, and missing skills; students apply; companies rank and recruit; and the same underlying data is aggregated for colleges as industry-demand vs. student-skill-availability insights — closing an academia–industry feedback loop.

One-line definition (from source): *"SkillBridge connects what students have, what industry requires, and what colleges need to improve."*

---

## 2. Problem

Three sides of one problem, as stated in the source:

**Students** don't know: what skills companies want; which of their own skills match a given opportunity; which required skills they're missing; which internships/placements are actually relevant to them.

**Colleges** lack a centralized view of: what skills their students have; what skills companies are demanding; which skills are commonly missing; where training is needed; internship participation and placement outcomes; industry feedback.

**Companies** struggle to: find suitable student talent; filter large volumes of applicants; compare candidate skills against job requirements; shortlist relevant candidates; manage recruitment together with colleges.

---

## 3. Target Users

| Role | Who they are |
|---|---|
| **Student** | Individual learner maintaining a skill profile, discovering and applying to internships/placements |
| **Company / Industry** | Organization posting opportunities and required skills, reviewing and recruiting candidates |
| **College / Placement Cell** | Institution overseeing its students, viewing skill analytics, industry demand, and skill gaps |
| **Platform Admin** | Operates and moderates the platform: users, colleges, companies, skills taxonomy, opportunities |

---

## 4. Goals

- **G1.** Let students see, for any opportunity, a match score plus exactly which required skills they have and which they're missing.
- **G2.** Let companies filter for eligibility and rank applicants by skill match so they can shortlist efficiently.
- **G3.** Let colleges see, at population level, how student skill availability compares with industry skill demand, so they know where to focus training.
- **G4.** Turn recruitment/internship outcomes and company feedback into a continuous, aggregated signal that feeds back into college-level insight.
- **G5.** Ship a working MVP — the Must-Have tier only — within a 3-day, 6-person team build.

## 5. Non-Goals

- **Not** an internal assessment/testing engine — the source explicitly states no internal assessment engine is required; company-run assessments happen externally or as a future integration.
- **Not** a measure of skill proficiency — match %, and college-level "% of students having skill X," represent presence/coverage of a skill, not how good someone is at it (explicitly stated in the source in two places).
- **Not** a general-purpose job board or LMS.
- **Not** handling payroll, stipend disbursement, or any financial transaction.
- **Not** a native mobile app — the source describes a web-based platform.
- **Not** a microservices system — a modest, modular structure is appropriate for the timeline; nothing in the source requires distributed infrastructure.

---

## 6. User Personas

**The Student** — *"Find opportunities that match my current skills and understand what skills are missing for those opportunities."* Needs: a clear skills list, a ranked/filterable opportunity list, a per-opportunity match breakdown, and application status tracking.

**The Company** — *"Find students whose current skills match our requirements."* Needs: a way to define required skills and eligibility, a ranked/filtered candidate list, and a simple recruitment pipeline to move candidates through stages.

**The College / Placement Cell** — *"Understand what skills our students have and how those skills compare with current industry demand."* Needs: student/department visibility, an aggregated skills view, an industry-demand view, and a skill-gap view that points to where training is needed.

**The Admin** — operates the platform: keeps the skills taxonomy clean and consistent (matching depends on it), verifies organizations, and monitors overall health. *(The source lists Admin's feature set but does not narrate an Admin persona or journey in the same depth as the other three — see Open Questions.)*

---

## 7. Core User Journeys

**Student journey**
1. Register and create a profile (education, department, year, CGPA, career interests).
2. Add current skills (e.g., Java, SQL, Git, React).
3. Browse/search internships and placements.
4. For each opportunity, see matched skills, missing skills, and match %.
5. Apply to relevant opportunities.
6. Track application status through the recruitment pipeline.
7. If selected, track the resulting internship/placement.
8. Receive company feedback at the end of the internship/placement.

**Company journey**
1. Register and create a company profile.
2. Post an opportunity (role, eligibility, required skills, location, duration, stipend/salary, deadline).
3. Receive applications; system filters by eligibility (branch/year/CGPA) first.
4. View candidates ranked by skill match %.
5. Inspect a candidate's profile, projects, certifications, resume.
6. Move candidates through Applied → Under Review → Shortlisted → Interview → Selected/Rejected.
7. Provide feedback after an internship/placement concludes.

**College journey**
1. View students and departments.
2. View aggregated student skill availability.
3. View industry skill demand (aggregated from relevant opportunities).
4. View the resulting skill-gap analysis and suggested training focus areas.
5. Monitor internship and placement participation and outcomes for their students.
6. View aggregated company feedback.

**Admin journey** *(scope as listed in the source; workflow detail not specified there)*
1. Manage user, college, and company accounts (including verification).
2. Manage the master skills list/taxonomy.
3. Manage/moderate opportunities.
4. View platform-wide reports.

---

## 8. Features

Features are grouped by priority tier (see Section 9 for the full tier table). Each **Must-Have** feature below includes purpose, actor, inputs, outputs, workflow, and acceptance criteria. **Should-Have** features include a lighter version of the same. **Later/If-time-permits** features are intentionally *not* detailed here — per the brief, they should not be where planning starts — and are described only in Section 14 (Future Scope).

### 🔴 Must Have

**F1 — Authentication**
- *Purpose:* Let Students, Companies, Colleges, and Admins securely create accounts and log in.
- *Actor:* All roles.
- *Inputs:* Email, password, role/registration details.
- *Outputs:* An authenticated session scoped to the user's role.
- *Workflow:* Register → account created → log in → routed to the role's dashboard.
- *Acceptance criteria:*
  - A user can register, log in, and log out.
  - Each account has exactly one role.
  - Invalid credentials are rejected with a clear error.
  - A session persists until logout or expiry.

**F2 — Role-Based Access Control**
- *Purpose:* Ensure each role only sees and can act on what it's permitted to.
- *Actor:* All roles (enforced by the system); Admin (manages roles).
- *Inputs:* Authenticated session + role claim.
- *Outputs:* Permitted (or denied) access to a given screen/action.
- *Workflow:* Every request checks the role claim before granting access.
- *Acceptance criteria:*
  - A Student cannot access Company/College/Admin screens or APIs, and vice versa.
  - Unauthorized access attempts are blocked.

**F3 — Student Profile**
- *Purpose:* Capture the student's identity and academic info, used for eligibility checks and college analytics.
- *Actor:* Student (edits); College, Company, Admin (view per permissions).
- *Inputs:* Personal info; education (college, department, year, CGPA); career interests.
- *Outputs:* A profile record used by eligibility filtering and college analytics.
- *Workflow:* Student completes profile after registration; can edit any time.
- *Acceptance criteria:*
  - Student can create/edit department, year, CGPA, and career interests.
  - Profile is visible to the student's own college and to companies reviewing an application.

**F4 — Skills (student skills management + supporting portfolio)**
- *Purpose:* Maintain the current-skills list that drives all matching, plus supporting evidence (projects, certifications, resume link, GitHub/portfolio).
- *Actor:* Student.
- *Inputs:* Skill add/remove actions; optional project, certification, resume, portfolio link.
- *Outputs:* The current skill list read by the Skill Matching Engine (F8); supporting evidence visible to companies.
- *Workflow:* Student adds/removes skills at any time; optionally attaches portfolio items.
- *Acceptance criteria:*
  - Adding/removing a skill updates the profile immediately and is reflected in match calculations.
  - Portfolio items (if added) are visible to companies viewing the student.

**F5 — Company Profile**
- *Purpose:* Give companies an identity used on opportunity listings and trusted by students/colleges.
- *Actor:* Company.
- *Inputs:* Company name, industry, description, location, website, contact info.
- *Outputs:* A profile shown alongside the company's opportunities.
- *Workflow:* Company registers, fills profile; profile carries a verification status field.
- *Acceptance criteria:*
  - Company can create/edit its profile.
  - Profile fields display on the company's opportunity listings.
  - Verified/unverified status is visible.

**F6 — Internship/Placement Posting**
- *Purpose:* Let companies define an opportunity and its requirements as the basis for filtering and matching.
- *Actor:* Company.
- *Inputs:* Role, description, eligibility (branch, CGPA, year), required skills, location, duration, stipend/salary, application deadline.
- *Outputs:* A published opportunity, visible to eligible students, feeding the Matching Engine.
- *Workflow:* Draft → define required skills & eligibility → publish → edit/close as needed.
- *Acceptance criteria:*
  - A published opportunity displays its required skills and eligibility criteria.
  - A closed/expired opportunity stops accepting new applications.

**F7 — Internship/Placement Search**
- *Purpose:* Let students browse, search, and filter opportunities.
- *Actor:* Student.
- *Inputs:* Keywords, filters (e.g., location, mode, skill, type).
- *Outputs:* A filtered opportunity list, each showing eligibility and skill-match % for that student.
- *Workflow:* Open Internships/Placements → search/filter → open an opportunity's detail view.
- *Acceptance criteria:*
  - Search and filters return relevant, correctly-scoped results.
  - Each listing shows the logged-in student's eligibility and match %.

**F8 — Skill Matching (the core engine)**
- *Purpose:* Compare a student's current skills to an opportunity's required skills to produce matched skills, missing skills, and a match score.
- *Actor:* System — triggered from student browsing/applying and from company applicant review.
- *Inputs:* Student's current skill set; opportunity's required skill set.
- *Outputs:* Matched skills, missing skills, match % (= matched ÷ required × 100), eligibility flag.
- *Workflow:* Pull both skill sets → compute intersection (matched) and difference (missing) → compute % → display to student and/or company.
- *Acceptance criteria:*
  - Match % correctly equals matched-skill-count ÷ required-skill-count × 100.
  - Matched/missing lists are accurate.
  - The UI/labeling makes clear this is skill *coverage*, not proficiency (per source).
  - *Note:* the source describes optional per-skill **weighted** matching as "a more advanced version." Recommend treating unweighted matching as the MVP baseline and weighted matching as Should-have/Later — see Open Questions.

**F9 — Application**
- *Purpose:* Let a student formally apply to an opportunity.
- *Actor:* Student.
- *Inputs:* Student, opportunity.
- *Outputs:* An application record with initial status "Applied."
- *Workflow:* Student reviews match/eligibility → applies → application appears in the company's applicant list and the student's tracker.
- *Acceptance criteria:*
  - Exactly one application is created per student per opportunity (no duplicates).
  - Company's applicant list and the student's "My Applications" both update immediately.

**F10 — Application Status (Recruitment Pipeline)**
- *Purpose:* Track a candidate through Applied → Under Review → Shortlisted → Interview → Selected/Rejected.
- *Actor:* Company (updates status); Student (views); College (views aggregated).
- *Inputs:* Company's stage-update action.
- *Outputs:* Current status per application.
- *Workflow:* Company views candidates ranked by match % → moves a candidate through stages → student sees the update.
- *Acceptance criteria:*
  - Every application has a current, visible status.
  - A Selected outcome on an internship-type opportunity produces an internship record for tracking.

**F11 — College Dashboard**
- *Purpose:* Give the Placement Cell one place to see students, departments, and placement activity.
- *Actor:* College.
- *Inputs:* Aggregated data from students, opportunities, applications.
- *Outputs:* Dashboard views — students, departments, and a placement funnel (applications, shortlists, interviews, offers, selected).
- *Workflow:* College logs in → views summary/aggregate screens → can drill into a student or department.
- *Acceptance criteria:*
  - College can see student and department counts.
  - College can see the placement funnel across their own students.

**F12 — Basic Skill-Gap Analysis**
- *Purpose:* Compare student skill availability (% of that college's students listing a skill) against industry demand (% of relevant opportunities requiring it) and flag gaps.
- *Actor:* College.
- *Inputs:* Aggregated student-skill data for that college; aggregated required-skill data from relevant opportunities.
- *Outputs:* A per-skill table of demand % vs. availability %, with a gap classification.
- *Workflow:* Aggregate availability → aggregate demand → compute gap per skill → display.
- *Acceptance criteria:*
  - Table shows, at minimum, skill name, demand %, and availability %.
  - A documented (even if simple) rule classifies gap severity.
  - Explicitly institution-level — not a claim about any individual student (per source).

**F13 — Admin Management**
- *Purpose:* Platform oversight — users, colleges, companies, skills taxonomy, opportunities, reports.
- *Actor:* Admin.
- *Inputs:* Admin actions (approve/suspend, verify, edit taxonomy, remove listing).
- *Outputs:* A clean, consistent skills taxonomy and verified organizations.
- *Workflow:* Review pending verifications → maintain skills list → moderate problem accounts/listings.
- *Acceptance criteria:*
  - Admin can view/search all users, colleges, companies.
  - Admin can verify/reject a company or college.
  - Admin can add/edit/remove a skill from the master taxonomy.
  - Admin can deactivate a user or an opportunity.

### 🟡 Should Have

**S1 — Resume Upload** — Student attaches a resume file to their profile. *Workflow:* upload → stored & linked to profile → viewable/downloadable by companies. *Acceptance:* upload/replace/remove supported; visible on the applicant view.

**S2 — Resume → Skill Extraction** — AI suggests candidate skills from an uploaded resume; nothing is added without the student confirming. *Acceptance:* extracted skills are suggestions only until confirmed (per source).

**S3 — Recommended Internships** — System ranks open opportunities by the student's match % into a "Recommended for you" list. *Acceptance:* list is ordered by descending match % and updates as skills/opportunities change.

**S4 — Notifications (in-app)** — Alerts for status changes (application update, new matching opportunity). *Acceptance:* unread indicator; notification links to the relevant record. *(Scope assumption: in-app only — see Open Questions on how this differs from "Email Notifications" in the Later tier.)*

**S5 — Placement Tracking** — Full-time placements follow the same application → pipeline → outcome flow as internships, recorded as placement records. *Acceptance:* College can view placement-specific stats (offers, selected count) distinct from internship stats.

**S6 — Industry Skill-Demand Analytics** — A richer, filterable view of required-skill frequency (beyond the flat table in F12), e.g., filterable by time range or opportunity type. *Acceptance:* at least one filter dimension beyond the MVP's flat snapshot.

### 🟢 Later / If Time Permits

Not detailed at this stage (see Section 14): AI recommendations, automated job-description skill extraction, advanced analytics, email notifications, certificates, interview scheduling, skill assessments.

---

## 9. Feature Priorities

| Tier | Features |
|---|---|
| 🔴 **Must Have** | Authentication · Role-based access · Student profile · Skills · Company profile · Internship posting · Internship search · Skill matching · Application · Application status · College dashboard · Basic skill-gap analysis · Admin management |
| 🟡 **Should Have** | Resume upload · Resume → skill extraction · Recommended internships · Notifications (in-app) · Placement tracking · Industry skill-demand analytics |
| 🟢 **Later / If time permits** | AI recommendations · Automated job-description skill extraction · Advanced analytics · Email notifications · Certificates · Interview scheduling · Skill assessments |

The 3-day build should sequence strictly top-down: **Must Have is the entire MVP scope.** Should-Have items are only attempted once every Must-Have feature is working end-to-end. Later-tier items are not started during the 3-day build.

---

## 10. Business Rules

- **Match score:** `match % = (matched required skills ÷ total required skills) × 100`. This represents required-skill *coverage*, not proficiency in any individual skill.
- **Weighted matching (optional, non-MVP):** a company may optionally assign importance weights to required skills; the weighted score sums the weights of matched skills. Recommended as Should-Have/Later, not MVP (see Open Questions).
- **Eligibility filtering runs before skill matching:** branch, year, and CGPA thresholds (as set on the opportunity) determine eligibility; only eligible students are matched/ranked for a company.
- **Recruitment pipeline stages:** Applied → Under Review → Shortlisted → Interview → Selected/Rejected. The company updates status at each stage. (Whether stages can be skipped or reversed is not specified in the source — see Open Questions.)
- **Internship status lifecycle:** Upcoming → Ongoing → Completed.
- **Student skill availability (college view):** `% = (students listing the skill ÷ total students) × 100` — self-reported presence, not verified proficiency.
- **Industry demand (college view):** `% = (relevant opportunities requiring the skill ÷ total relevant opportunities) × 100`.
- **Skill gap:** derived from comparing demand % to availability % per skill, classified into severity bands (e.g., High/Moderate). Exact numeric thresholds for each band are not specified in the source — see Open Questions.
- **No internal assessment engine:** any company-run assessment happens externally or via a future integration.
- **Company feedback:** per the source, feedback is both delivered to the student (student "receives company feedback") *and* aggregated for the college's insight view.

---

## 11. Success Metrics

The source document does not specify success metrics; the following are proposed and should be validated with stakeholders/judges before being treated as fixed targets (see Open Questions):

- Number of students, companies, and colleges onboarded.
- Student profile completion rate.
- % of opportunities posted with required skills fully defined.
- Average/median skill-match % at time of application.
- Applications submitted per opportunity.
- Conversion rate from Applied → Selected.
- Number of distinct skill-gap insights surfaced to a college.
- Time from application to first status update (company responsiveness).

---

## 12. Constraints

- **Team/timeline:** 6-person student team, 3-day build window.
- **AI is optional, not core:** the skill-matching calculation, eligibility filtering, and all core workflows must function without any AI component; AI (resume extraction, JD extraction, recommendations) is strictly additive.
- **No internal assessment engine** is to be built.
- **Web-based only** — no native mobile app in scope.
- **Confirmed technology stack for the project:** React + Vite + JavaScript on the frontend; Tailwind CSS, shadcn/ui, React Router, Axios, useState/useReducer, and Recharts for UI/data concerns; Spring Boot + Java on the backend with Spring Security + JWT, Spring Data JPA / Hibernate, PostgreSQL, REST APIs, OpenAPI / Swagger, and Jakarta Bean Validation.

---

## 13. Assumptions

- Skills are self-reported by students by default; resume-extraction (Should-Have) only assists entry and still requires student confirmation.
- A student belongs to a single college at a time (not addressed explicitly in the source — flagged as an assumption, not a confirmed rule).
- Each opportunity has one eligibility rule set (branch/year/CGPA), not per-department variants.
- Company/college "verification status" exists as a field, but the verification *process* (who verifies, how) is not defined in the source; assumed to be an Admin manual-review action pending confirmation.
- Company feedback can be free-text and/or structured (the source shows both example formats); free-text support is assumed as the MVP baseline, with structured categories as an enhancement.

---

## 14. Future Scope

Later-tier items, intentionally undetailed for this 3-day MVP build:

- **AI recommendations** — a more advanced, personalized version of opportunity recommendations beyond the simple match-% ranking in S3.
- **Automated job-description skill extraction** — auto-parsing a pasted job description into required skills (companies confirm before they're applied), versus manual entry in the MVP.
- **Advanced analytics** — deeper/trend-based analytics beyond the flat industry-demand and skill-gap tables.
- **Email notifications** — an external notification channel, in addition to the in-app notifications in S4.
- **Certificates.**
- **Interview scheduling.**
- **Skill assessments** — any internal assessment/testing engine remains explicitly out of scope per Section 5 (Non-Goals); if built later, it would live here as an integration, not a core matching dependency.

---

## MVP Feature List

Authentication · Role-based access control · Student profile · Skills (student skills + portfolio) · Company profile · Internship/placement posting · Internship/placement search · Skill matching (unweighted) · Application · Application status / recruitment pipeline · College dashboard · Basic skill-gap analysis · Admin management.

## Features Explicitly Excluded from MVP

**Should-Have (attempt only after MVP is fully working):** Resume upload · Resume → skill extraction · Recommended internships · Notifications (in-app) · Placement tracking · Industry skill-demand analytics.

**Later (not started in this build cycle):** AI recommendations · Automated job-description skill extraction · Advanced analytics · Email notifications · Certificates · Interview scheduling · Skill assessments.

## Open Questions Requiring Human Decision

1. What numeric thresholds define a skill gap as High vs. Moderate vs. Low? The source only gives illustrative examples (e.g., AWS at 58% demand / 24% availability shown as High; React at 64%/45% shown as Moderate) without a stated rule.
2. This item is resolved: the team has confirmed the final stack as React + Vite + JavaScript, Tailwind CSS, shadcn/ui, React Router, Axios, useState/useReducer, Recharts, Spring Boot + Java, Spring Security + JWT, Spring Data JPA / Hibernate, PostgreSQL, REST, OpenAPI / Swagger, and Jakarta Bean Validation.
3. Is weighted skill matching in scope for the MVP "Skill matching" Must-Have item, or is it Should-Have/Later? The source calls it "a more advanced version" of the core match.
4. What distinguishes Should-Have "Notifications" from Later-tier "Email notifications" — is the Should-Have scope strictly in-app only?
5. What is the company/college verification process, and who performs it — Admin manual review, or something automated?
6. Can a company move an application backward or skip stages in the recruitment pipeline, or is progression strictly linear and forward-only?
7. Is company feedback always shown to the student, or can a company mark feedback as college-only/internal?
8. Can a student be associated with more than one college, or is college affiliation fixed at registration?
9. Beyond branch/year/CGPA, are there other custom eligibility fields a company might need per opportunity?
10. Does Admin management need full CRUD over every entity in the 3-day MVP, or only verification/moderation actions, given the timeline?
