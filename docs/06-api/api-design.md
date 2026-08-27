# SkillBridge — REST API Design Specification

**Phase:** System Design  
**Version:** 1.2  
**Status:** APPROVED — Verified System Design Baseline  
**Date:** 2026-08-27  
**Derived from:** PRD.md · SRS.md · architecture.md (approved) · database.md (approved) · openapi.yaml  
**OpenAPI Specification File:** [`openapi.yaml`](file:///e:/LALITH%20PROJECTS/SIH%202026%20-%20PS044/SkillBridge/skillbridge/docs/06-api/openapi.yaml)

---

## Table of Contents

1. [API Architecture & Design Principles](#1-api-architecture--design-principles)
2. [API Versioning & Base Path](#2-api-versioning--base-path)
3. [Authentication Approach & Stateless Session Lifecycle](#3-authentication-approach--stateless-session-lifecycle)
4. [Authorization & Role-Based Access Control (RBAC)](#4-authorization--role-based-access-control-rbac)
5. [Endpoint Organization by Domain](#5-endpoint-organization-by-domain)
6. [Request & Response Conventions](#6-request--response-conventions)
7. [Pagination, Filtering & Sorting Conventions](#7-pagination-filtering--sorting-conventions)
8. [Error Handling & Validation Formats](#8-error-handling--validation-formats)
9. [HTTP Status Code Reference](#9-http-status-code-reference)
10. [Core Business Rules Enforced at API Boundary](#10-core-business-rules-enforced-at-api-boundary)
11. [Complete Endpoint Catalog](#11-complete-endpoint-catalog)
    - [11.1 Authentication Endpoints](#111-authentication-endpoints)
    - [11.2 Student Endpoints](#112-student-endpoints)
    - [11.3 Company Endpoints](#113-company-endpoints)
    - [11.4 College Endpoints](#114-college-endpoints)
    - [11.5 Admin Endpoints](#115-admin-endpoints)
    - [11.6 Skills & Departments Taxonomy Endpoints](#116-skills--departments-taxonomy-endpoints)
    - [11.7 Opportunity Endpoints](#117-opportunity-endpoints)
    - [11.8 Application Endpoints](#118-application-endpoints)
    - [11.9 Internship & Placement Endpoints](#119-internship--placement-endpoints)
    - [11.10 Feedback Endpoints](#1110-feedback-endpoints)
    - [11.11 Skill Matching Endpoints](#1111-skill-matching-endpoints)
    - [11.12 College Analytics Endpoints](#1112-college-analytics-endpoints)
12. [SRS Functional Requirements Traceability Matrix](#12-srs-functional-requirements-traceability-matrix)

---

## 1. API Architecture & Design Principles

The SkillBridge API is designed as a **clean, stateless, resource-oriented RESTful HTTP API** adhering to the following core tenets:

1. **Stateless Operations:** Every HTTP request carries complete context and authorization in the `Authorization: Bearer <token>` header. No server-side session state is retained.
2. **Resource-Oriented URI Structure:** Clean nouns represent resources (`/opportunities`, `/applications`, `/skills`, `/departments`), with hierarchical nesting used only when sub-resources are tightly bound to a parent (`/opportunities/{id}/applications`, `/internships/{id}/feedback`).
3. **HTTP Verb Semantics:** Strict adherence to standard HTTP methods:
   - `GET`: Safe, idempotent resource retrieval.
   - `POST`: Non-idempotent creation or state transitions.
   - `PUT`: Idempotent full resource replacement.
   - `PATCH`: Idempotent partial resource or status mutation.
   - `DELETE`: Idempotent resource removal or soft-deactivation.
4. **Strong DTO Encapsulation:** Database entities are never exposed across the controller boundary. All request inputs and response payloads map to explicit Java Data Transfer Objects (DTOs) with field-level Jakarta Bean Validation.
5. **Living Contract First:** The entire API surface is formally defined in OpenAPI 3.0.3 ([`openapi.yaml`](file:///e:/LALITH%20PROJECTS/SIH%202026%20-%20PS044/SkillBridge/skillbridge/docs/06-api/openapi.yaml)) and rendered via SpringDoc Swagger UI at `/api/swagger-ui.html`.

> [!NOTE]
> **Architectural Separation of Concerns:**
> In OpenAPI 3.0, the `BearerAuth` security scheme declares the HTTP transport-level **authentication requirement** (validating a signed, unexpired JWT token).
> Fine-grained **role permissions** (`STUDENT`, `COMPANY`, `COLLEGE`, `ADMIN`), **resource ownership rules** (e.g., verifying that a company only modifies its own opportunity postings), and **institutional data scoping** (e.g., colleges restricted to their own enrolled students) are enforced server-side by Spring Security URL rules (`SecurityConfig`) and method-level annotations (`@PreAuthorize` + `SecurityService`).

---

## 2. API Versioning & Base Path

All endpoints are versioned using URI path versioning:

```
Base URL: /api/v1
```

- **Protocol:** HTTPS only in production.
- **Content Type:** `application/json` (except multipart file upload endpoints using `multipart/form-data` and binary file download streams).
- **Character Encoding:** UTF-8.

---

## 3. Authentication Approach & Stateless Session Lifecycle

### 3.1 Stateless Bearer JWT

- Authentication is handled via JSON Web Tokens (JWT) signed with **HS256** using a high-entropy secret (`JWT_SECRET` environment variable).
- Tokens are valid for **24 hours** from issue (`JWT_EXPIRY_MS = 86400000`, confirmed in OD-09).
- Clients submit the token in the standard HTTP Authorization header:
  ```http
  Authorization: Bearer <jwt_token_string>
  ```

### 3.2 JWT Payload Structure

```json
{
  "sub": "101",
  "email": "student@university.edu",
  "role": "STUDENT",
  "collegeId": 12,
  "studentProfileId": 45,
  "iat": 1756310400,
  "exp": 1756396800
}
```

### 3.3 Client Token Storage & Stateless Logout

- In accordance with Architecture §6, the React SPA stores the token in **`sessionStorage`** or in-memory React `AuthContext` (avoiding persistent `localStorage` to mitigate long-term XSS token extraction).
- **Stateless Logout Semantics:** Because JWT is completely stateless and no distributed token blacklist (e.g. Redis) is used in MVP (per project rules), calling `POST /auth/logout` returns `204 No Content` as an acknowledgement. The client application is responsible for discarding the token from its local state and `sessionStorage`.
- On HTTP `401 Unauthorized`, the Axios response interceptor clears the client context and navigates to `/login`.

---

## 4. Authorization & Role-Based Access Control (RBAC)

Access control operates across three layers of defense:

1. **Frontend Route Guards (`ProtectedRoute`):** Client-side routing verification for smooth UX.
2. **Spring Security URL Authorization (`SecurityConfig`):** Coarse-grained URL role gating.
3. **Method-Level Pre-Authorization (`@PreAuthorize` + `SecurityService`):** Fine-grained data ownership and institutional scoping verification.

### 4.1 Role Permission Matrix

| Module / Action | Anonymous | Student | Company | College | Admin |
|---|:---:|:---:|:---:|:---:|:---:|
| `POST /auth/register` (Roles: `STUDENT`, `COMPANY`, `COLLEGE` only) | ✅ | ❌ | ❌ | ❌ | ❌ |
| `POST /auth/login` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `GET /auth/me`, `POST /auth/logout` | ❌ | ✅ | ✅ | ✅ | ✅ |
| Student Profile Read/Update | ❌ | ✅ (Own Profile) | ✅ (Applicant) | ✅ (Affiliated Student) | ✅ |
| Student Skills Management | ❌ | ✅ (Own Profile) | ❌ | ❌ | ❌ |
| Student Resume Upload/Download | ❌ | ✅ (Own Resume) | ✅ (Applied Candidate)| ❌ | ✅ |
| Company Profile Read/Update | ❌ | ✅ (Public View) | ✅ (Own Profile) | ✅ (Public View) | ✅ |
| College Profile Read/Update | ❌ | ❌ | ❌ | ✅ (Own Profile) | ✅ |
| College Student Roster | ❌ | ❌ | ❌ | ✅ (Scoped to College) | ✅ |
| Master Skills Taxonomy Read | ❌ | ✅ | ✅ | ✅ | ✅ |
| Master Skills Taxonomy Write | ❌ | ❌ | ❌ | ❌ | ✅ |
| Master Departments Read | ❌ | ✅ | ✅ | ✅ | ✅ |
| Master Departments Write | ❌ | ❌ | ❌ | ❌ | ✅ |
| Browse Opportunities | ❌ | ✅ (Annotated Match) | ✅ | ✅ | ✅ |
| Create / Edit Opportunity | ❌ | ❌ | ✅ (Own Postings) | ❌ | ❌ |
| Submit Application | ❌ | ✅ (Eligible Students)| ❌ | ❌ | ❌ |
| View My Applications | ❌ | ✅ (Own Applications) | ❌ | ❌ | ❌ |
| View Opportunity Candidates | ❌ | ❌ | ✅ (Own Opportunity) | ❌ | ✅ |
| Advance Application Stage | ❌ | ❌ | ✅ (Own Opportunity) | ❌ | ❌ |
| Internship Lifecycle Management | ❌ | ✅ (View Own) | ✅ (Manage Own Hires)| ✅ (View Affiliated) | ✅ |
| Submit Company Feedback | ❌ | ❌ | ✅ (Completed Hires) | ❌ | ❌ |
| View Feedback | ❌ | ✅ (Own Feedback) | ✅ (Own Submissions)| ✅ (Aggregated College)| ✅ |
| College Analytics & Skill Gap | ❌ | ❌ | ❌ | ✅ (Scoped to College)| ✅ |
| Admin User & Org Verification | ❌ | ❌ | ❌ | ❌ | ✅ |

### 4.2 Resource Ownership & Institutional Scoping Enforcements

- **Public Self-Registration:** Public registration (`POST /auth/register`) explicitly permits only `STUDENT`, `COMPANY`, and `COLLEGE` roles. Admin accounts cannot be self-registered and are provisioned directly.
- **Student Data Privacy & Scoping:** Students can only view and modify their own profile (`/students/profile`), skills, portfolio items, resume, and submitted applications (`/applications/my`, `/internships/my`). Student profiles and resumes are accessible to companies only if the student has actively applied to an opportunity owned by that company, to colleges only if the student is affiliated with that college (`student.college_id == auth.collegeId`), or to platform `ADMIN`.
- **Company Opportunity Ownership:** A company user can only update postings (`PUT /opportunities/{id}`, `PATCH /opportunities/{id}/status`) where `opportunities.company_profile_id == auth.companyProfileId`.
- **Company Candidate & Pipeline Scoping:** A company user can only view applicants (`GET /opportunities/{opportunityId}/applications`), download applicant resumes (`GET /students/{studentId}/resume`), and advance application stages (`PATCH /applications/{id}/status`) for opportunities created and owned by their company profile.
- **College Institutional Scoping:** All college endpoints (`/colleges/students`, `/colleges/departments`, `/colleges/feedback`, `/analytics/skills/availability`, `/analytics/skills/gap`, `/analytics/placement-funnel`) **strictly derive the college identity from the authenticated JWT token claim (`collegeId`)**. Callers cannot supply a foreign `collegeId` in request bodies or query parameters to inspect other institutions.
- **Admin System-Wide Access:** `ADMIN` role can execute platform-wide moderation, taxonomy management, user activation toggling, organization verification, and system auditing across all institutions (using optional `collegeId` query parameters where supported).

---

## 5. Endpoint Organization by Domain

Endpoints are structured into 12 coherent functional modules matching the backend modular monolith architecture:

```
/api/v1
  ├── /auth               # Authentication & Session
  ├── /students           # Student Profile, Skills, Portfolio, Resume
  ├── /companies          # Company Profiles
  ├── /colleges           # College Management & Student Roster
  ├── /admin              # Platform Moderation, Users, Verifications
  ├── /skills             # Master Skills Taxonomy
  ├── /departments        # Standardized Academic Engineering Branches
  ├── /opportunities      # Internship & Placement Postings
  ├── /applications       # Candidate Applications & Recruitment Pipeline
  ├── /internships        # Confirmed Outcome Lifecycle
  ├── /feedback           # Qualitative Company Evaluations
  ├── /matching           # Skill Match Calculation & Recommendations
  └── /analytics          # Institutional Skill Gap & Recruitment Funnel
```

---

## 6. Request & Response Conventions

### 6.1 Standard Request Headers

```http
Authorization: Bearer <token>
Content-Type: application/json
Accept: application/json
```

### 6.2 Standard Response Wrapper (Direct DTO or Paginated)

Single resource requests return the DTO directly:

```json
{
  "id": 101,
  "title": "Junior Backend Developer Intern",
  "type": "INTERNSHIP",
  "status": "OPEN",
  "matchPercent": 80.0,
  "isEligible": true
}
```

---

## 7. Pagination, Filtering & Sorting Conventions

### 7.1 Query Parameters

Standard pagination, filtering, and sorting query parameters apply to large collection endpoints (`GET /opportunities`, `GET /opportunities/company/my`, `GET /applications/my`, `GET /opportunities/{opportunityId}/applications`, `GET /internships/my`, `GET /internships/company/my`, `GET /colleges/students`, `GET /colleges/feedback`, `GET /admin/users`, `GET /matching/recommendations`):

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | `integer` | `0` | Zero-based page index. |
| `size` | `integer` | `10` | Page size limit (maximum: `100`). |
| `sort` | `string` | `created_at,desc` | Sort field and direction (`field,asc` or `field,desc`). |
| `search`| `string` | `null` | Case-insensitive keyword search query. |

> [!NOTE]
> Bounded master taxonomy catalogs (`/skills`, `/departments`), student profile sub-collections (`/students/profile/skills`, `/projects`, `/certifications`), administrative verification queues (`/admin/verifications`), college department breakdown (`/colleges/departments`), and analytics dashboards/metrics (`/analytics/skills/availability`, `/analytics/skills/demand`, `/analytics/skills/gap`, `/analytics/placement-funnel`) return direct array lists or specialized structured DTOs rather than paginated envelopes.

### 7.2 Standard Paginated Response Structure (`PageResponse<T>`)

```json
{
  "content": [
    { "id": 1, "title": "Opportunity A" },
    { "id": 2, "title": "Opportunity B" }
  ],
  "page": {
    "page": 0,
    "size": 10,
    "totalElements": 24,
    "totalPages": 3,
    "isFirst": true,
    "isLast": false
  }
}
```

---

## 8. Error Handling & Validation Formats

### 8.1 Unified Error Response Schema (`ErrorResponse`)

All error responses return a standardized RFC 7807-inspired JSON payload:

```json
{
  "timestamp": "2026-08-27T15:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "details": [
    {
      "field": "cgpa",
      "message": "must be between 0.0 and 10.0"
    },
    {
      "field": "requiredSkillIds",
      "message": "must contain at least 1 skill"
    }
  ],
  "path": "/api/v1/opportunities"
}
```

### 8.2 Security in Server Errors (500)

For uncaught server exceptions, the backend logs full stack traces internally at `ERROR` level, while returning a sanitized client response:

```json
{
  "timestamp": "2026-08-27T15:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "details": [],
  "path": "/api/v1/applications"
}
```

---

## 9. HTTP Status Code Reference

| Status Code | Meaning | Standard Application Trigger |
|---|---|---|
| **200 OK** | Success | Successful resource read, update, or calculation. |
| **201 Created** | Created | Successful entity creation (`POST /auth/register`, `POST /opportunities`, etc.). |
| **204 No Content** | Success (No Body) | Successful resource deletion or stateless logout acknowledgement. |
| **400 Bad Request** | Client Error | Jakarta validation failure, invalid state transition, or malformed payload. |
| **401 Unauthorized** | Unauthenticated | Missing, expired, or malformed JWT token. |
| **403 Forbidden** | Authorization Denied | Authenticated user lacks required role or resource ownership. |
| **404 Not Found** | Resource Missing | Targeted entity ID does not exist. |
| **409 Conflict** | State Conflict | Duplicate email registration, duplicate application, or unique taxonomy conflict. |
| **413 Payload Too Large** | File Cap Exceeded | Resume upload exceeds the 5 MB ceiling. |
| **415 Unsupported Media Type**| Invalid Format | Resume file is not a valid PDF or DOCX document. |
| **500 Internal Server Error** | System Failure | Unhandled server exception. |

---

## 10. Core Business Rules Enforced at API Boundary

1. **Skill Coverage Computation (FR-MATCH-01):**
   $$\text{match \%} = \frac{|\text{student\_skill\_ids} \cap \text{required\_skill\_ids}|}{|\text{required\_skill\_ids}|} \times 100$$
   - Measures presence/coverage, not proficiency.
   - Per SRS/database requirements, every opportunity posting must have at least one required skill (`requiredSkillIds.minItems: 1`).
2. **Independent Eligibility Evaluation (FR-MATCH-02, DBQ-01):**
   - Branch eligibility: Evaluated via normalized `departments.id`. If `requiredDepartmentIds` is empty, all departments are eligible.
   - Year eligibility: Evaluated via `yearOfStudy`. If `requiredYearsOfStudy` is empty, all years are eligible.
   - CGPA eligibility: `student.cgpa >= opportunity.min_cgpa`.
   - Ineligible students can view match breakdown, but cannot submit applications.
3. **Application Uniqueness & Snapshot (FR-APP-01, FR-APP-03):**
   - Enforces strictly 1 application per `(student_profile_id, opportunity_id)`. Duplicate attempts return `409 Conflict`.
   - Freezes immutable `matchPercentAtApply` on application creation.
4. **Forward-Only Recruitment Pipeline (FR-APP-04, OD-03):**
   ```
   APPLIED → UNDER_REVIEW → SHORTLISTED → INTERVIEW → SELECTED | REJECTED
   ```
   - Skipping stages or reversing stages is rejected with `400 Bad Request`.
5. **Atomic Application Selection & Outcome Creation (FR-APP-05):**
   - When an employer transitions an application to `SELECTED` via `PATCH /applications/{id}/status`, the system executes an atomic database transaction that updates the application status to `SELECTED` and automatically creates an associated `internship_records` entity inheriting the opportunity's type (`INTERNSHIP` or `PLACEMENT`), initial status (`UPCOMING`), student profile, and company profile.
   - Both operations succeed or fail together in a single transaction. No separate outcome creation endpoint is required or exposed.
6. **Company Verification & Opportunity Publishing Policy (FR-INT-01, FR-COM-02):**
   - Any registered company profile may create and publish opportunities.
   - The company's `verification_status` (`PENDING`, `VERIFIED`, `REJECTED`) is a public trust badge displayed alongside the company profile and opportunity listings, enabling students and placement cells to assess trust.
   - A `PENDING` verification status does not block a company from creating or publishing opportunities.
7. **Opportunity Creation & Publication Lifecycle (FR-INT-01, FR-INT-02):**
   - Opportunities are automatically published with `status: OPEN` upon creation (`POST /opportunities`).
   - The status is not client-supplied in `CreateOpportunityRequest` or `UpdateOpportunityRequest`.
   - Postings can be closed or reopened (`OPEN` ↔ `CLOSED`) via `PATCH /opportunities/{id}/status` by the creator company.
   - Platform Admin can moderate and close postings via `PATCH /admin/opportunities/{id}/status`.
   - There is no client-created `DRAFT` state in the public lifecycle.
8. **Placement Funnel Semantics (FR-COL-02):**
   - The placement funnel endpoint (`GET /analytics/placement-funnel`) returns **counts of applications currently residing in each active and terminal status**:
     - `applied`: Applications currently awaiting review (`example: 35`).
     - `underReview`: Applications currently undergoing review (`example: 25`).
     - `shortlisted`: Applications currently shortlisted (`example: 20`).
     - `interview`: Applications currently in interview stage (`example: 15`).
     - `selected`: Applications with confirmed selection outcomes (`example: 10`).
     - `rejected`: Applications marked rejected (`example: 15`).
     - `totalApplications`: Total applications submitted across all stages ($35 + 25 + 20 + 15 + 10 + 15 = 120$).
9. **Company Feedback Gating (FR-INT-05, OD-04, OD-11, DBQ-04):**
   - Requires `internship_records.status = 'COMPLETED'`.
   - Free-text only (`feedbackText TEXT NOT NULL`).
   - Accessible to both the candidate and their college placement cell.
10. **Skill Gap Severity Calculation (FR-ANL-03, OD-01):**
    $$\text{gap} = \text{demand\_pct} - \text{availability\_pct}$$
    - `HIGH`: $\text{gap} \ge 30\%$
    - `MODERATE`: $15\% \le \text{gap} < 30\%$
    - `LOW`: $0\% < \text{gap} < 15\%$
    - `SURPLUS`: $\text{gap} \le 0\%$
    - Industry demand scope: Aggregated across **all OPEN opportunities** platform-wide (OD-07).
11. **File Handling Rules (FR-STU-04 / S1, OD-08):**
    - Allowed MIME types: PDF (`application/pdf`) and DOCX (`application/vnd.openxmlformats-officedocument.wordprocessingml.document`).
    - Maximum file size: 5 MB (`5,242,880 bytes`).
    - Binary stored outside web root; accessible only via authenticated `/api/v1/students/{id}/resume`.

---

## 11. Complete Endpoint Catalog

### 11.1 Authentication Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `POST` | `/auth/register` | Register new user account & profile | Public | Any (`STUDENT`, `COMPANY`, `COLLEGE`) | `RegisterRequest` | `201 Created` | `400`, `409` |
| `POST` | `/auth/login` | Authenticate & obtain JWT | Public | Any | `LoginRequest` | `200 OK` | `400`, `401` |
| `GET` | `/auth/me` | Inspect current session context | Bearer | Any Authenticated Role | None | `200 OK` | `401` |
| `POST` | `/auth/logout` | Stateless logout acknowledgement | Bearer | Any Authenticated Role | None | `204 No Content` | `401` |

---

### 11.2 Student Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/students/profile` | View authenticated student profile | Bearer | `STUDENT` (Own) | None | `200 OK` | `401`, `403`, `404` |
| `PUT` | `/students/profile` | Update academic & personal details | Bearer | `STUDENT` (Own) | `UpdateStudentProfileRequest` | `200 OK` | `400`, `401`, `403` |
| `GET` | `/students/{id}/profile` | View profile by ID | Bearer | `COLLEGE` (Affiliated), `COMPANY` (Applicant), `ADMIN` | None | `200 OK` | `401`, `403`, `404` |
| `GET` | `/students/profile/skills` | List student's current skills | Bearer | `STUDENT` (Own) | None | `200 OK` | `401`, `403` |
| `POST` | `/students/profile/skills` | Add skill from taxonomy | Bearer | `STUDENT` (Own) | `AddStudentSkillRequest` | `201 Created` | `400`, `401`, `403`, `409` |
| `DELETE`| `/students/profile/skills/{skillId}` | Remove skill from profile | Bearer | `STUDENT` (Own) | None | `204 No Content` | `401`, `403`, `404` |
| `GET` | `/students/profile/projects` | List portfolio projects | Bearer | `STUDENT` (Own) | None | `200 OK` | `401`, `403` |
| `POST` | `/students/profile/projects` | Add portfolio project | Bearer | `STUDENT` (Own) | `ProjectRequest` | `201 Created` | `400`, `401`, `403` |
| `PUT` | `/students/profile/projects/{projectId}` | Update portfolio project | Bearer | `STUDENT` (Own) | `ProjectRequest` | `200 OK` | `400`, `401`, `403`, `404` |
| `DELETE`| `/students/profile/projects/{projectId}` | Delete portfolio project | Bearer | `STUDENT` (Own) | None | `204 No Content` | `401`, `403`, `404` |
| `GET` | `/students/profile/certifications` | List certifications | Bearer | `STUDENT` (Own) | None | `200 OK` | `401`, `403` |
| `POST` | `/students/profile/certifications` | Add certification | Bearer | `STUDENT` (Own) | `CertificationRequest` | `201 Created` | `400`, `401`, `403` |
| `PUT` | `/students/profile/certifications/{certificationId}` | Update certification | Bearer | `STUDENT` (Own) | `CertificationRequest` | `200 OK` | `400`, `401`, `403`, `404` |
| `DELETE`| `/students/profile/certifications/{certificationId}` | Delete certification | Bearer | `STUDENT` (Own) | None | `204 No Content` | `401`, `403`, `404` |
| `POST` | `/students/profile/resume` | Upload resume file (PDF/DOCX) | Bearer | `STUDENT` (Own) | `multipart/form-data` | `200 OK` | `400`, `401`, `403`, `413`, `415` |
| `DELETE`| `/students/profile/resume` | Delete uploaded resume | Bearer | `STUDENT` (Own) | None | `204 No Content` | `401`, `403`, `404` |
| `GET` | `/students/{studentId}/resume` | Download student resume | Bearer | `STUDENT` (Own), `COMPANY` (Applicant), `ADMIN` | None | `200 OK` (Stream) | `401`, `403`, `404` |

---

### 11.3 Company Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/companies/profile` | View company profile | Bearer | `COMPANY` (Own) | None | `200 OK` | `401`, `403` |
| `PUT` | `/companies/profile` | Update company profile | Bearer | `COMPANY` (Own) | `UpdateCompanyProfileRequest` | `200 OK` | `400`, `401`, `403` |
| `GET` | `/companies/{id}` | View public company profile | Bearer | Any Authenticated Role | None | `200 OK` | `401`, `404` |

---

### 11.4 College Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/colleges/profile` | View college profile | Bearer | `COLLEGE` (Own) | None | `200 OK` | `401`, `403` |
| `PUT` | `/colleges/profile` | Update college profile | Bearer | `COLLEGE` (Own) | `UpdateCollegeProfileRequest` | `200 OK` | `400`, `401`, `403` |
| `GET` | `/colleges/students` | List student roster (paginated/filtered) | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |
| `GET` | `/colleges/departments` | Department student enrollment counts | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |

---

### 11.5 Admin Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/admin/users` | List all platform accounts | Bearer | `ADMIN` only | None | `200 OK` | `401`, `403` |
| `PATCH` | `/admin/users/{id}/status` | Activate/deactivate user | Bearer | `ADMIN` only | `{ isActive: boolean }` | `200 OK` | `401`, `403`, `404` |
| `GET` | `/admin/verifications` | View pending organization queue | Bearer | `ADMIN` only | None | `200 OK` | `401`, `403` |
| `PATCH` | `/admin/verifications/{type}/{id}` | Approve/reject verification | Bearer | `ADMIN` only | `{ status: "VERIFIED" \| "REJECTED" }` | `200 OK` | `400`, `401`, `403`, `404` |
| `PATCH` | `/admin/opportunities/{id}/status` | Moderate/close opportunity | Bearer | `ADMIN` only | `{ status: "CLOSED" }` | `200 OK` | `400`, `401`, `403`, `404` |

---

### 11.6 Skills & Departments Taxonomy Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/skills` | Search master skills taxonomy | Bearer | Any Authenticated Role | None | `200 OK` | `401` |
| `POST` | `/skills` | Add master skill | Bearer | `ADMIN` only | `CreateSkillRequest` | `201 Created` | `400`, `401`, `403`, `409` |
| `PUT` | `/skills/{id}` | Update master skill | Bearer | `ADMIN` only | `UpdateSkillRequest` | `200 OK` | `400`, `401`, `403`, `404` |
| `DELETE`| `/skills/{id}` | Soft-deactivate skill | Bearer | `ADMIN` only | None | `204 No Content` | `401`, `403`, `404` |
| `GET` | `/departments` | List academic engineering departments | Bearer | Any Authenticated Role | None | `200 OK` | `401` |
| `POST` | `/departments` | Create academic department | Bearer | `ADMIN` only | `CreateDepartmentRequest` | `201 Created` | `400`, `401`, `403`, `409` |

---

### 11.7 Opportunity Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/opportunities` | Search open opportunities | Bearer | Any Authenticated Role | None | `200 OK` | `401` |
| `POST` | `/opportunities` | Post internship or placement | Bearer | `COMPANY` (Own Postings) | `CreateOpportunityRequest` | `201 Created` | `400`, `401`, `403` |
| `GET` | `/opportunities/company/my` | List company's own postings | Bearer | `COMPANY` (Own Postings) | None | `200 OK` | `401`, `403` |
| `GET` | `/opportunities/{id}` | View opportunity details & match | Bearer | Any Authenticated Role | None | `200 OK` | `401`, `404` |
| `PUT` | `/opportunities/{id}` | Update opportunity posting | Bearer | `COMPANY` (Posting Owner) | `UpdateOpportunityRequest` | `200 OK` | `400`, `401`, `403`, `404` |
| `PATCH` | `/opportunities/{id}/status` | Update opportunity publication status | Bearer | `COMPANY` (Posting Owner) | `{ status: "OPEN" \| "CLOSED" }` | `200 OK` | `400`, `401`, `403`, `404` |

---

### 11.8 Application Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `POST` | `/applications` | Submit application | Bearer | `STUDENT` (Eligible) | `SubmitApplicationRequest` | `201 Created` | `400`, `401`, `403`, `409` |
| `GET` | `/applications/my` | Student's submitted applications | Bearer | `STUDENT` (Own Applications) | None | `200 OK` | `401`, `403` |
| `GET` | `/applications/{id}` | View application details | Bearer | `STUDENT` (Applicant), `COMPANY` (Owner), `COLLEGE`, `ADMIN` | None | `200 OK` | `401`, `403`, `404` |
| `GET` | `/opportunities/{opportunityId}/applications` | Company view ranked applicants | Bearer | `COMPANY` (Opportunity Owner), `ADMIN` | None | `200 OK` | `401`, `403`, `404` |
| `PATCH` | `/applications/{id}/status` | Advance recruitment stage | Bearer | `COMPANY` (Opportunity Owner) | `{ status: ApplicationStatus }` | `200 OK` | `400`, `401`, `403`, `404` |

---

### 11.9 Internship & Placement Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/internships/my` | Student's confirmed outcomes | Bearer | `STUDENT` (Own Outcomes) | None | `200 OK` | `401`, `403` |
| `GET` | `/internships/company/my` | Company's confirmed interns | Bearer | `COMPANY` (Own Hires) | None | `200 OK` | `401`, `403` |
| `GET` | `/internships/{id}` | View outcome details | Bearer | `STUDENT` (Intern), `COMPANY` (Employer), `COLLEGE`, `ADMIN` | None | `200 OK` | `401`, `403`, `404` |
| `PATCH` | `/internships/{id}/status` | Update progress lifecycle | Bearer | `COMPANY` (Employer) | `{ status, startDate, endDate }` | `200 OK` | `400`, `401`, `403`, `404` |

---

### 11.10 Feedback Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `POST` | `/internships/{internshipId}/feedback` | Submit post-internship feedback | Bearer | `COMPANY` (Employer; Completed status) | `SubmitFeedbackRequest` | `201 Created` | `400`, `401`, `403`, `409` |
| `GET` | `/internships/{internshipId}/feedback` | View feedback for an outcome | Bearer | `STUDENT` (Intern), `COMPANY` (Employer), `COLLEGE`, `ADMIN` | None | `200 OK` | `401`, `403`, `404` |
| `GET` | `/colleges/feedback` | College aggregated feedback list | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |

---

### 11.11 Skill Matching Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/matching/opportunities/{opportunityId}` | Evaluate match & eligibility | Bearer | `STUDENT` only | None | `200 OK` | `401`, `403`, `404` |
| `GET` | `/matching/recommendations` | Opportunities ranked by match % (paginated) | Bearer | `STUDENT` only | None | `200 OK` | `401`, `403` |

---

### 11.12 College Analytics Endpoints

| Method | URI | Purpose | Auth | Role(s) | Request Body | Success | Error Codes |
|---|---|---|:---:|:---:|---|:---:|:---:|
| `GET` | `/analytics/skills/availability` | Compute student skill presence % | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |
| `GET` | `/analytics/skills/demand` | Compute industry skill demand % | Bearer | `COLLEGE`, `ADMIN` | None | `200 OK` | `401`, `403` |
| `GET` | `/analytics/skills/gap` | Skill gap analysis dashboard | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |
| `GET` | `/analytics/placement-funnel` | Aggregated recruitment funnel | Bearer | `COLLEGE` (Scoped to Own College), `ADMIN` | None | `200 OK` | `401`, `403` |

---

## 12. SRS Functional Requirements Traceability Matrix

The matrix below maps functional requirements from the approved SRS to the corresponding REST API contract. All Must-Have (MVP) and core Should-Have requirements are directly implemented across the API endpoints, while non-MVP or deferred capabilities (such as AI resume skill extraction per Architecture §12 and in-app notifications per Architecture §11) are explicitly designated as **Deferred**:

| SRS FR-ID | Requirement Title | Supported REST API Endpoint(s) | HTTP Method |
|---|---|---|:---:|
| **FR-AUTH-01** | User Registration | `/api/v1/auth/register` | `POST` |
| **FR-AUTH-02** | User Login | `/api/v1/auth/login` | `POST` |
| **FR-AUTH-03** | Session Logout / Expiry | `/api/v1/auth/logout` | `POST` |
| **FR-AUTH-04** | Role-Based Access Control | `/api/v1/auth/me` + All endpoint authorization guards | `GET` |
| **FR-STU-01** | Student Profile Management | `/api/v1/students/profile` | `GET`, `PUT` |
| **FR-STU-02** | Student Skills Management | `/api/v1/students/profile/skills`, `/api/v1/students/profile/skills/{skillId}` | `GET`, `POST`, `DELETE` |
| **FR-STU-03** | Student Portfolio Items | `/api/v1/students/profile/projects/**`, `/api/v1/students/profile/certifications/**` | `GET`, `POST`, `PUT`, `DELETE` |
| **FR-STU-04** | Resume Upload *(Should-Have)* | `/api/v1/students/profile/resume`, `/api/v1/students/{studentId}/resume` | `POST`, `GET`, `DELETE` |
| **FR-STU-05** | Resume Skill Extraction *(Should-Have)* | Deferred post-MVP (AI extraction deferred per Architecture §12; confirmed skills managed via `/api/v1/students/profile/skills`) | Deferred |
| **FR-STU-06** | Recommended Opportunities *(Should-Have)* | `/api/v1/matching/recommendations` | `GET` |
| **FR-COM-01** | Company Profile Management | `/api/v1/companies/profile`, `/api/v1/companies/{id}` | `GET`, `PUT` |
| **FR-COM-02** | Display Verification Status | `/api/v1/companies/{id}` (`verificationStatus` field) | `GET` |
| **FR-COL-01** | View Students & Departments | `/api/v1/colleges/students`, `/api/v1/colleges/departments` | `GET` |
| **FR-COL-02** | View Placement Funnel | `/api/v1/analytics/placement-funnel` | `GET` |
| **FR-ADM-01** | Manage User Accounts | `/api/v1/admin/users`, `/api/v1/admin/users/{id}/status` | `GET`, `PATCH` |
| **FR-ADM-02** | Verify/Reject Organizations | `/api/v1/admin/verifications`, `/api/v1/admin/verifications/{type}/{id}` | `GET`, `PATCH` |
| **FR-ADM-03** | Manage Skills Taxonomy | `/api/v1/skills`, `/api/v1/skills/{id}` | `GET`, `POST`, `PUT`, `DELETE` |
| **FR-ADM-04** | Moderate Opportunities | `/api/v1/admin/opportunities/{id}/status` | `PATCH` |
| **FR-MATCH-01**| Skill Match Computation | `/api/v1/opportunities/{id}`, `/api/v1/matching/opportunities/{id}` | `GET` |
| **FR-MATCH-02**| Eligibility Checking | Embedded in `/api/v1/opportunities`, `/api/v1/matching/opportunities/{id}` | `GET` |
| **FR-APP-01** | Submit Application | `/api/v1/applications` | `POST` |
| **FR-APP-02** | View Student Applications | `/api/v1/applications/my`, `/api/v1/applications/{id}` | `GET` |
| **FR-APP-03** | Ranked Candidates for Company | `/api/v1/opportunities/{opportunityId}/applications` | `GET` |
| **FR-APP-04** | Update Pipeline Status | `/api/v1/applications/{id}/status` | `PATCH` |
| **FR-APP-05** | Auto-Create Outcome on Selection | Executed on `/api/v1/applications/{id}/status` (`SELECTED`) | `PATCH` |
| **FR-APP-06** | In-App Notifications *(Should-Have)* | Deferred post-MVP per DBQ-02 / architecture | Deferred |
| **FR-INT-01** | Create Opportunity Posting | `/api/v1/opportunities` | `POST` |
| **FR-INT-02** | Edit / Close Opportunity | `/api/v1/opportunities/{id}`, `/api/v1/opportunities/{id}/status` | `PUT`, `PATCH` |
| **FR-INT-03** | Search / Browse Opportunities | `/api/v1/opportunities`, `/api/v1/opportunities/{id}` | `GET` |
| **FR-INT-04** | Track Internship Lifecycle | `/api/v1/internships/my`, `/api/v1/internships/company/my`, `/api/v1/internships/{id}/status` *(Core status lifecycle mapped; student progress logging deferred per Database Design §4.15)* | `GET`, `PATCH` |
| **FR-INT-05** | Company Feedback | `/api/v1/internships/{internshipId}/feedback`, `/api/v1/colleges/feedback` | `POST`, `GET` |
| **FR-INT-06** | Placement Tracking *(Should-Have)*| `/api/v1/internships/{id}` (`type = PLACEMENT`) | `GET` |
| **FR-ANL-01** | Student Skill Availability % | `/api/v1/analytics/skills/availability` | `GET` |
| **FR-ANL-02** | Industry Skill Demand % | `/api/v1/analytics/skills/demand` | `GET` |
| **FR-ANL-03** | Skill Gap Classification | `/api/v1/analytics/skills/gap` | `GET` |
| **FR-ANL-04** | Filterable Demand View *(Should-Have)* | `/api/v1/analytics/skills/demand?type=...` | `GET` |
| **FR-ANL-05** | Skill Gap Dashboard View | `/api/v1/analytics/skills/gap` | `GET` |

---

*SkillBridge REST API Design Specification Complete.*  
*Status: APPROVED — Verified System Design Baseline.*
