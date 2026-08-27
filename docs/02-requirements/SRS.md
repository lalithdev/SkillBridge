# SkillBridge — Software Requirements Specification (SRS)

**Derived from:** The approved SkillBridge PRD (SIH26044) only.
**Scope rule:** Every requirement below traces to a feature already detailed in the PRD — Must-Have (F1–F13) or Should-Have (S1–S6). Later-tier items (AI recommendations, automated JD extraction, advanced analytics, email notifications, certificates, interview scheduling, skill assessments) are **not** specified here, consistent with the PRD treating them as out of scope for this build.
**Should-Have requirements are marked `*(Should-Have)*`** in their title; everything unmarked is Must-Have/MVP.
**Open items carried over from the PRD** (e.g., gap-severity thresholds, stage-progression rules) are noted as "TBD" in the relevant field rather than resolved here, per the PRD's own Open Questions.

---

## 1. Authentication

### FR-AUTH-01 — User Registration
- **Requirement:** The system shall allow a user to register an account under exactly one role (Student, Company, College, or Admin-invited).
- **Actor:** Student, Company, College, Admin
- **Preconditions:** No existing account is registered with the email provided.
- **Input:** Email, password, selected/assigned role, minimal role-specific registration fields.
- **Expected behavior:** System validates input, creates a new account with a single role, and stores credentials securely.
- **Output:** A newly created, unauthenticated account.
- **Error cases:** Email already registered; missing/invalid required fields; password policy violation (policy details not specified in PRD — TBD).
- **Acceptance criteria:** Registration with valid unique details succeeds; a duplicate-email attempt is rejected with a clear error; the created account has exactly one role.

### FR-AUTH-02 — User Login
- **Requirement:** The system shall allow a registered user to authenticate with email and password and receive a role-scoped session.
- **Actor:** Student, Company, College, Admin
- **Preconditions:** User has a registered account.
- **Input:** Email, password.
- **Expected behavior:** System validates credentials and, on success, issues a role-scoped session and routes the user to their role's dashboard.
- **Output:** Authenticated session; role-appropriate landing view.
- **Error cases:** Incorrect email/password; account not found; account deactivated by Admin.
- **Acceptance criteria:** Valid credentials produce a session and route to the correct dashboard; invalid credentials are rejected and no session is issued.

### FR-AUTH-03 — Session Logout / Expiry
- **Requirement:** The system shall allow a user to log out, and shall expire a session after a period of inactivity.
- **Actor:** Student, Company, College, Admin
- **Preconditions:** User has an active session.
- **Input:** Logout action, or elapsed idle time.
- **Expected behavior:** On logout or expiry, the session is invalidated; further requests require re-authentication.
- **Output:** Invalidated session; redirect to login.
- **Error cases:** Reuse of an expired/invalidated session token.
- **Acceptance criteria:** After logout, protected pages are unreachable without re-login.

### FR-AUTH-04 — Role-Based Access Control
- **Requirement:** The system shall restrict every screen and API action to the roles permitted to use it.
- **Actor:** System (enforces for all roles); Admin (manages accounts)
- **Preconditions:** User has an authenticated session with a role claim.
- **Input:** Requested screen/action; the user's role claim.
- **Expected behavior:** System checks the role claim against the permission required before granting access.
- **Output:** Access granted, or access denied.
- **Error cases:** Any cross-role access attempt (e.g., Student requesting a Company/College/Admin-only action).
- **Acceptance criteria:** No role can reach another role's screens or APIs; denied attempts return a clear "not authorized" response rather than failing silently.

---

## 2. Student

### FR-STU-01 — Create/Edit Student Profile
- **Requirement:** The system shall let a Student create and edit personal information, education (college, department, year, CGPA), and career interests.
- **Actor:** Student
- **Preconditions:** Student is registered and logged in.
- **Input:** Personal info; college, department, year, CGPA; career interests.
- **Expected behavior:** System stores/updates the profile; department/year/CGPA become available to eligibility checks (FR-MATCH-02) and to the student's college (FR-COL-01).
- **Output:** Saved/updated profile record.
- **Error cases:** Missing required fields; invalid CGPA format; attempt to edit another student's profile.
- **Acceptance criteria:** Student can save department, year, CGPA, and career interests; the profile is visible to the student's college and to companies reviewing an application from that student.

### FR-STU-02 — Manage Current Skills
- **Requirement:** The system shall let a Student add and remove entries in their current-skills list.
- **Actor:** Student
- **Preconditions:** Student is logged in and has a profile.
- **Input:** Skill to add or remove.
- **Expected behavior:** The change is reflected immediately and used in the next match calculation (FR-MATCH-01).
- **Output:** Updated current-skills list.
- **Error cases:** Removing a skill not on the list; adding a duplicate skill.
- **Acceptance criteria:** A skill add/remove is reflected immediately in the profile and in any subsequent match calculation.

### FR-STU-03 — Manage Portfolio (Projects, Certifications, Portfolio Link)
- **Requirement:** The system shall let a Student attach projects, certifications, and a portfolio/GitHub link to their profile.
- **Actor:** Student
- **Preconditions:** Student is logged in and has a profile.
- **Input:** Project details, certification details, portfolio/GitHub URL.
- **Expected behavior:** System stores the items and displays them to companies reviewing that student.
- **Output:** Updated portfolio section on the profile.
- **Error cases:** Invalid URL format.
- **Acceptance criteria:** Added items are visible on the student's profile as seen by a reviewing company.

### FR-STU-04 — Upload Resume *(Should-Have)*
- **Requirement:** The system shall let a Student upload a resume file to their profile.
- **Actor:** Student
- **Preconditions:** Student is logged in.
- **Input:** Resume file (e.g., PDF).
- **Expected behavior:** System stores the file, links it to the profile, and allows replace/remove.
- **Output:** A stored, retrievable resume file.
- **Error cases:** Unsupported file type; file exceeds allowed size (limit not specified in PRD — TBD).
- **Acceptance criteria:** Student can upload, replace, and remove a resume; a reviewing company can view/download it.

### FR-STU-05 — Resume Skill Extraction & Confirmation *(Should-Have)*
- **Requirement:** The system shall use AI to suggest candidate skills from an uploaded resume and shall add none of them without explicit student confirmation.
- **Actor:** Student (confirms); System/AI (suggests)
- **Preconditions:** Student has uploaded a resume (FR-STU-04).
- **Input:** The uploaded resume file.
- **Expected behavior:** System extracts a candidate skill list and presents it as suggestions; student reviews and confirms/edits before anything is added.
- **Output:** A confirmed skill set added to the current-skills list (FR-STU-02).
- **Error cases:** Extraction yields no recognizable skills; student declines all suggestions.
- **Acceptance criteria:** No extracted skill reaches the profile without explicit student confirmation.

### FR-STU-06 — View Recommended Internships/Placements *(Should-Have)*
- **Requirement:** The system shall present a ranked "Recommended for you" list of open opportunities based on the student's current skill match.
- **Actor:** Student
- **Preconditions:** Student has at least one skill on profile; open opportunities exist.
- **Input:** None (system-generated).
- **Expected behavior:** System ranks open opportunities by the student's match % (FR-MATCH-01), descending.
- **Output:** An ordered recommended-opportunities list.
- **Error cases:** No open opportunities meet minimum relevance (empty state).
- **Acceptance criteria:** List is ordered by descending match % and updates when skills or available opportunities change.

---

## 3. Company

### FR-COM-01 — Create/Edit Company Profile
- **Requirement:** The system shall let a Company create and edit its name, industry, description, location, website, and contact information.
- **Actor:** Company
- **Preconditions:** Company is registered and logged in.
- **Input:** Company name, industry, description, location, website, contact info.
- **Expected behavior:** System stores/updates the profile and displays it alongside the company's opportunity listings.
- **Output:** Saved/updated company profile.
- **Error cases:** Missing required fields; attempt to edit another company's profile.
- **Acceptance criteria:** Company can save/update all fields; the profile appears on the company's opportunity listings.

### FR-COM-02 — Display Verification Status
- **Requirement:** The system shall display the company's verification status wherever its profile is shown.
- **Actor:** Company (subject); Admin (sets status, see FR-ADM-02)
- **Preconditions:** Company profile exists.
- **Input:** None (status is Admin-set).
- **Expected behavior:** Current verification status is shown on the profile and on its opportunity listings.
- **Output:** Visible verification status.
- **Error cases:** N/A (see FR-ADM-02 for setting errors).
- **Acceptance criteria:** Verified/unverified status is visible wherever the company profile appears.

---

## 4. College

### FR-COL-01 — View Students & Departments
- **Requirement:** The system shall let a College view its students and departments, with drill-down into an individual student.
- **Actor:** College
- **Preconditions:** College account is logged in; students are associated with the college.
- **Input:** Optional search/filter by department.
- **Expected behavior:** System returns student/department counts and allows opening an individual student's profile.
- **Output:** Students/departments view with drill-down.
- **Error cases:** No students onboarded yet (empty state).
- **Acceptance criteria:** College sees student and department counts and can open an individual student's profile.

### FR-COL-02 — View Placement Funnel
- **Requirement:** The system shall let a College view aggregated recruitment-pipeline statistics (applications, shortlists, interviews, offers, selected) across its own students.
- **Actor:** College
- **Preconditions:** At least one of the college's students has an application in progress.
- **Input:** Optional filter (e.g., by department).
- **Expected behavior:** System aggregates status counts from FR-APP-02/FR-APP-04 across the college's students.
- **Output:** Placement funnel view with per-stage counts.
- **Error cases:** No applications yet (empty state).
- **Acceptance criteria:** Funnel counts accurately reflect current recruitment stages for the college's students.

---

## 5. Admin

### FR-ADM-01 — Manage Users
- **Requirement:** The system shall let an Admin view, search, and deactivate any user account.
- **Actor:** Admin
- **Preconditions:** Admin is logged in.
- **Input:** Search query; deactivate action on a target account.
- **Expected behavior:** Admin can locate any account and toggle it active/inactive; a deactivated account can no longer log in.
- **Output:** Updated account status.
- **Error cases:** Deactivating an already-deactivated account; account not found.
- **Acceptance criteria:** Admin can view/search all users, colleges, and companies, and deactivate any account.

### FR-ADM-02 — Verify/Reject Company or College
- **Requirement:** The system shall let an Admin approve or reject the verification status of a Company or College profile.
- **Actor:** Admin
- **Preconditions:** A Company or College profile is pending verification.
- **Input:** Approve/reject action on a target profile.
- **Expected behavior:** System updates the profile's verification status (feeds FR-COM-02).
- **Output:** Updated verification status.
- **Error cases:** Verifying a profile missing required fields.
- **Acceptance criteria:** Admin can verify or reject a company/college; the resulting status is reflected on that profile.

### FR-ADM-03 — Manage Skills Taxonomy
- **Requirement:** The system shall let an Admin add, edit, and remove entries in the master skills list used platform-wide.
- **Actor:** Admin
- **Preconditions:** Admin is logged in.
- **Input:** Skill name to add/edit/remove.
- **Expected behavior:** System updates the master skills list; changes propagate wherever skills are selected (student skills, required skills).
- **Output:** Updated master skills taxonomy.
- **Error cases:** Adding a duplicate skill name; removing a skill currently in use (behavior not specified in PRD — TBD).
- **Acceptance criteria:** Admin can add, edit, and remove a skill from the master taxonomy.

### FR-ADM-04 — Manage/Moderate Opportunities
- **Requirement:** The system shall let an Admin deactivate or remove a posted opportunity.
- **Actor:** Admin
- **Preconditions:** The target opportunity exists.
- **Input:** Deactivate/remove action.
- **Expected behavior:** System removes the opportunity from student-facing search and closes it to new applications.
- **Output:** Opportunity deactivated/removed.
- **Error cases:** Opportunity already closed.
- **Acceptance criteria:** A deactivated opportunity no longer appears in student search results.

---

## 6. Skill Matching

### FR-MATCH-01 — Compute Skill Match (Matched / Missing / Score)
- **Requirement:** The system shall compare a student's current-skills set against an opportunity's required-skills set and produce matched skills, missing skills, and a match percentage.
- **Actor:** System (invoked from Student browsing/applying and Company applicant review)
- **Preconditions:** A student profile with a current-skills set exists; an opportunity with a required-skills set exists.
- **Input:** Student's current-skills set; opportunity's required-skills set.
- **Expected behavior:** System computes the intersection (matched) and difference (missing), then computes `match % = matched count ÷ required count × 100`.
- **Output:** Matched-skills list, missing-skills list, match percentage.
- **Error cases:** Opportunity has zero required skills (undefined percentage); student has zero current skills (0% match).
- **Acceptance criteria:** Match % always equals matched-count ÷ required-count × 100; matched/missing lists are exact set operations; labeling makes clear this is skill coverage, not proficiency. Weighted matching is explicitly **not** part of this requirement (see PRD Open Questions).

### FR-MATCH-02 — Eligibility Check
- **Requirement:** The system shall determine a student's eligibility for an opportunity based on the opportunity's branch, year, and CGPA requirements, ahead of or alongside the skill-match calculation.
- **Actor:** System (invoked during Student search/browsing and Company applicant review)
- **Preconditions:** Opportunity has defined eligibility criteria; student profile has department, year, and CGPA populated.
- **Input:** Student's department, year, CGPA; opportunity's branch, year, CGPA requirements.
- **Expected behavior:** System flags the student eligible or ineligible for the opportunity.
- **Output:** Eligibility flag.
- **Error cases:** Student profile missing department/year/CGPA (cannot evaluate).
- **Acceptance criteria:** An ineligible student is clearly marked on the opportunity view; company applicant ranking (FR-APP-03) includes only eligible candidates.

---

## 7. Applications

### FR-APP-01 — Submit Application
- **Requirement:** The system shall let an eligible Student submit an application to a specific opportunity.
- **Actor:** Student
- **Preconditions:** Student is eligible for the opportunity (FR-MATCH-02); opportunity is open.
- **Input:** Student, target opportunity.
- **Expected behavior:** System creates an application record with initial status "Applied," visible to the company and the student's tracker.
- **Output:** A new application record.
- **Error cases:** Duplicate application to the same opportunity; opportunity closed/expired; student ineligible.
- **Acceptance criteria:** Exactly one application exists per student per opportunity; the company's applicant list and the student's "My Applications" both update immediately.

### FR-APP-02 — View My Applications (Student)
- **Requirement:** The system shall let a Student view the list and current status of all their submitted applications.
- **Actor:** Student
- **Preconditions:** Student has at least one application.
- **Input:** None.
- **Expected behavior:** System returns all of the student's applications with current pipeline status.
- **Output:** Application list with status per entry.
- **Error cases:** No applications yet (empty state).
- **Acceptance criteria:** Every submitted application appears with its current status.

### FR-APP-03 — View Candidates Ranked by Skill Match (Company)
- **Requirement:** The system shall let a Company view applicants to its opportunity, filtered to eligible candidates and ranked by skill-match percentage.
- **Actor:** Company
- **Preconditions:** At least one eligible student has applied.
- **Input:** Target opportunity.
- **Expected behavior:** System filters applicants by eligibility (FR-MATCH-02) and orders the remainder by descending match % (FR-MATCH-01).
- **Output:** Ranked, eligibility-filtered applicant list.
- **Error cases:** No eligible applicants yet.
- **Acceptance criteria:** Only eligible candidates appear; ordering is strictly descending by match %.

### FR-APP-04 — Update Application Status (Recruitment Pipeline)
- **Requirement:** The system shall let a Company move an application through Applied → Under Review → Shortlisted → Interview → Selected/Rejected.
- **Actor:** Company
- **Preconditions:** Application exists and belongs to the company's opportunity.
- **Input:** Application, target stage.
- **Expected behavior:** System updates the current stage and surfaces it to the student (FR-APP-02) and college (FR-COL-02).
- **Output:** Updated application status.
- **Error cases:** Company attempting to update an application on an opportunity it doesn't own. Whether progression must be strictly forward-only is not specified in the PRD — **TBD**.
- **Acceptance criteria:** Every status change is immediately reflected on the student's tracker and the college's aggregated funnel.

### FR-APP-05 — Auto-Create Internship/Placement Record on Selection
- **Requirement:** The system shall automatically create an internship or placement record when an application reaches "Selected" status.
- **Actor:** System (triggered by FR-APP-04)
- **Preconditions:** Application status updated to "Selected."
- **Input:** The selected application; the opportunity's type (internship/placement).
- **Expected behavior:** System creates the corresponding internship (FR-INT-04) or placement (FR-INT-06) record, linked to student and company.
- **Output:** A new internship/placement record.
- **Error cases:** Opportunity type undefined at selection time.
- **Acceptance criteria:** Every "Selected" outcome produces exactly one corresponding internship/placement record.

### FR-APP-06 — In-App Notification on Status Change *(Should-Have)*
- **Requirement:** The system shall generate an in-app notification when an application's status changes, or when a new opportunity closely matches a student's skills.
- **Actor:** Student, Company, College (recipients)
- **Preconditions:** A triggering event occurs.
- **Input:** The triggering event.
- **Expected behavior:** System creates an in-app notification linked to the relevant record, marked unread until viewed.
- **Output:** An in-app notification.
- **Error cases:** Triggering event with no valid recipient (e.g., deactivated account).
- **Acceptance criteria:** Recipient sees an unread indicator; opening the notification navigates to the relevant record. Scope is in-app only — an email channel is Later-tier and out of scope for this requirement.

---

## 8. Internship / Placement

### FR-INT-01 — Create Opportunity Posting
- **Requirement:** The system shall let a Company create an internship or placement posting with role, description, eligibility (branch, CGPA, year), required skills, location, duration, stipend/salary, and application deadline.
- **Actor:** Company
- **Preconditions:** Company is logged in and has a profile.
- **Input:** All listed posting fields.
- **Expected behavior:** System stores the posting and, once published, makes it available to the matching engine and student search.
- **Output:** A created opportunity record.
- **Error cases:** Missing required fields (e.g., no required skills defined).
- **Acceptance criteria:** A published opportunity displays its required skills and eligibility to students.

### FR-INT-02 — Edit/Close Opportunity Posting
- **Requirement:** The system shall let a Company edit an existing posting or close it to further applications.
- **Actor:** Company
- **Preconditions:** Opportunity exists and belongs to the company.
- **Input:** Updated fields, or a close action.
- **Expected behavior:** System applies the edit, or marks the posting closed.
- **Output:** Updated or closed opportunity record.
- **Error cases:** Editing/closing an opportunity not owned by the company.
- **Acceptance criteria:** A closed/expired posting no longer accepts new applications (see FR-APP-01 error case).

### FR-INT-03 — Search/Filter Internships & Placements
- **Requirement:** The system shall let a Student search and filter open opportunities (e.g., keyword, location, mode, skill, type).
- **Actor:** Student
- **Preconditions:** At least one open opportunity exists.
- **Input:** Search keywords, filter selections.
- **Expected behavior:** System returns matching open opportunities, each annotated with eligibility and match % (FR-MATCH-01, FR-MATCH-02).
- **Output:** Filtered opportunity list.
- **Error cases:** No results match the given filters (empty state).
- **Acceptance criteria:** Results are correctly scoped to open opportunities; each shows the current student's eligibility and match %.

### FR-INT-04 — Track Internship Record
- **Requirement:** The system shall track an internship record through Upcoming → Ongoing → Completed.
- **Actor:** Company (updates status); Student, College (view)
- **Preconditions:** Internship record exists (created via FR-APP-05).
- **Input:** Status update action; optional student progress update.
- **Expected behavior:** System stores and displays the current internship status and progress updates.
- **Output:** Current internship status and progress log.
- **Error cases:** Status update on a non-existent or already-Completed internship.
- **Acceptance criteria:** Internship status accurately reflects Upcoming/Ongoing/Completed at any point, visible to student, company, and college.

### FR-INT-05 — Capture Company Feedback
- **Requirement:** The system shall let a Company submit feedback on a student following an internship or placement, visible to the student and available to the college's aggregated view.
- **Actor:** Company (submits); Student (views own); College (views aggregated)
- **Preconditions:** Internship/placement record exists and has reached Completed status (or a recruitment outcome has been recorded).
- **Input:** Feedback text (free-text at minimum).
- **Expected behavior:** System stores the feedback against the record, shows it to the student, and includes it in the college's aggregated feedback view.
- **Output:** Stored feedback entry.
- **Error cases:** Feedback submitted before the record has concluded (ordering not explicitly constrained in PRD — TBD).
- **Acceptance criteria:** Submitted feedback appears on the student's record and contributes to the college's aggregated feedback view.

### FR-INT-06 — Placement Tracking *(Should-Have)*
- **Requirement:** The system shall track full-time placement records separately from internship records, through the same recruitment pipeline, with placement-specific statistics for the college.
- **Actor:** Student, Company, College
- **Preconditions:** A placement-type opportunity's application reached "Selected" (FR-APP-05).
- **Input:** None beyond the triggering selection.
- **Expected behavior:** System creates and tracks a placement record distinct from internship records; college can view placement-specific stats (offers, selected count).
- **Output:** Placement record; college-facing placement statistics.
- **Error cases:** N/A beyond those covered in FR-APP-05.
- **Acceptance criteria:** Placement counts (offers, selected) are viewable separately from internship counts.

---

## 9. Analytics

### FR-ANL-01 — Compute Student Skill Availability
- **Requirement:** The system shall compute, per skill, the percentage of a college's students who list that skill.
- **Actor:** System (for College view)
- **Preconditions:** The college has at least one student with skills recorded.
- **Input:** The college's student population and their current-skills sets.
- **Expected behavior:** System computes `% = (students listing the skill ÷ total students) × 100` for each skill present.
- **Output:** A per-skill availability percentage table.
- **Error cases:** No students with any skills recorded (empty state).
- **Acceptance criteria:** Percentages are calculated correctly and clearly labeled as self-reported presence, not proficiency.

### FR-ANL-02 — Compute Industry Skill Demand
- **Requirement:** The system shall compute, per skill, the percentage of relevant opportunities that require that skill.
- **Actor:** System (for College view)
- **Preconditions:** At least one relevant opportunity exists.
- **Input:** The set of relevant opportunities and their required-skills sets.
- **Expected behavior:** System computes `% = (opportunities requiring the skill ÷ total relevant opportunities) × 100` per skill.
- **Output:** A per-skill demand percentage table.
- **Error cases:** No relevant opportunities exist (empty state); the scope of "relevant" is not defined in the PRD — **TBD**.
- **Acceptance criteria:** Percentages are calculated correctly per skill across the relevant opportunity set.

### FR-ANL-03 — Skill Gap Classification
- **Requirement:** The system shall classify the gap between industry demand and student availability for each skill into a severity band (e.g., High/Moderate/Low).
- **Actor:** System (for College view)
- **Preconditions:** FR-ANL-01 and FR-ANL-02 have both been computed for the skill.
- **Input:** Demand % and availability % for the skill.
- **Expected behavior:** System applies a documented classification rule to produce a severity label.
- **Output:** A per-skill gap classification alongside its demand % and availability %.
- **Error cases:** Missing demand or availability data for a skill.
- **Acceptance criteria:** Every skill with both data points receives a classification; the rule/thresholds are documented (exact thresholds are an open PRD decision); output is explicitly institution-level, not a statement about any individual student.

### FR-ANL-04 — Industry Skill-Demand Analytics with Filters *(Should-Have)*
- **Requirement:** The system shall let a College view industry-demand data filtered by at least one dimension (e.g., time range, opportunity type) beyond the flat FR-ANL-02 snapshot.
- **Actor:** College
- **Preconditions:** FR-ANL-02 data exists.
- **Input:** Selected filter.
- **Expected behavior:** System recomputes/subselects the demand view per the chosen filter.
- **Output:** A filtered demand view.
- **Error cases:** Filter selection returns no matching opportunities (empty state).
- **Acceptance criteria:** At least one working filter dimension is available beyond the MVP's flat snapshot.

### FR-ANL-05 — View Basic Skill-Gap Analysis Dashboard
- **Requirement:** The system shall present the College with a single dashboard combining per-skill demand %, availability %, and gap classification (FR-ANL-01, FR-ANL-02, FR-ANL-03).
- **Actor:** College
- **Preconditions:** At least one skill has both demand and availability data.
- **Input:** None (aggregated system view).
- **Expected behavior:** System displays a table with, at minimum, skill name, demand %, availability %, and gap classification.
- **Output:** The skill-gap analysis table.
- **Error cases:** No data yet available (empty state).
- **Acceptance criteria:** Table content matches FR-ANL-01/02/03 outputs and is presented at the institution level, per the PRD's Basic Skill-Gap Analysis feature.

---

## Traceability to PRD Features

| PRD Feature | FR-IDs |
|---|---|
| F1 Authentication | FR-AUTH-01, FR-AUTH-02, FR-AUTH-03 |
| F2 Role-Based Access | FR-AUTH-04 |
| F3 Student Profile | FR-STU-01 |
| F4 Skills / Portfolio | FR-STU-02, FR-STU-03 |
| F5 Company Profile | FR-COM-01, FR-COM-02 |
| F6 Internship Posting | FR-INT-01, FR-INT-02 |
| F7 Internship Search | FR-INT-03 |
| F8 Skill Matching | FR-MATCH-01 |
| F9 Application | FR-APP-01 |
| F10 Application Status | FR-APP-02, FR-APP-03, FR-APP-04, FR-APP-05 |
| F11 College Dashboard | FR-COL-01, FR-COL-02 |
| F12 Basic Skill-Gap Analysis | FR-ANL-01, FR-ANL-02, FR-ANL-03, FR-ANL-05 |
| F13 Admin Management | FR-ADM-01, FR-ADM-02, FR-ADM-03, FR-ADM-04 |
| S1 Resume Upload | FR-STU-04 |
| S2 Resume → Skill Extraction | FR-STU-05 |
| S3 Recommended Internships | FR-STU-06 |
| S4 Notifications | FR-APP-06 |
| S5 Placement Tracking | FR-INT-06 |
| S6 Industry Skill-Demand Analytics | FR-ANL-04 |
| *(Business Rule, not a separate PRD feature #)* Eligibility filtering | FR-MATCH-02 |
| *(Business Rule, not a separate PRD feature #)* Company feedback | FR-INT-05 |

**Note:** FR-MATCH-02 (eligibility check) and FR-INT-05 (company feedback) are drawn from the PRD's Business Rules and journey descriptions rather than from a dedicated F#/S# feature entry — flagging this so the mapping stays traceable and nothing here is presented as invented.
