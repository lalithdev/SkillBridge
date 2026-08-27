# SkillBridge — Technical Architecture

**Phase:** System Design
**Version:** 1.0
**Status:** Draft — Awaiting Review
**Date:** 2026-08-27
**Source of Truth:** PRD.md · SRS.md · rules/02-development.md (tech stack is locked)

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Architecture Style](#2-architecture-style)
3. [Frontend Architecture](#3-frontend-architecture)
4. [Backend Architecture](#4-backend-architecture)
5. [Module Boundaries](#5-module-boundaries)
6. [Authentication](#6-authentication)
7. [Authorization / RBAC](#7-authorization--rbac)
8. [API Communication](#8-api-communication)
9. [Database Boundary](#9-database-boundary)
10. [Skill Matching Module](#10-skill-matching-module)
11. [Application / Recruitment Module](#11-application--recruitment-module)
12. [College Analytics Module](#12-college-analytics-module)
13. [File / Resume Handling](#13-file--resume-handling)
14. [Validation](#14-validation)
15. [Error Handling](#15-error-handling)
16. [Security](#16-security)
17. [Logging](#17-logging)
18. [Testing Approach](#18-testing-approach)
19. [Configuration / Environment Strategy](#19-configuration--environment-strategy)
20. [Deployment Approach](#20-deployment-approach)
21. [Scalability Considerations](#21-scalability-considerations)
22. [Architectural Decisions](#22-architectural-decisions)
23. [Risks and Trade-offs](#23-risks-and-trade-offs)
24. [Open Decisions](#24-open-decisions)
25. [Appendix A — PRD / SRS Validation](#appendix-a--prd--srs-validation)

---

## 1. System Overview

SkillBridge is a web-based Academia–Industry collaboration platform. It connects four actors —
**Student**, **Company**, **College**, and **Admin** — around one shared concept:
comparing what a student *currently has* against what an opportunity *requires*.

### Core Product Loop (from PRD §1)

```
Student Skills
  → Company Requirements
  → Skill Matching (intersection / difference / score)
  → Matched / Missing Skills
  → Opportunity (browse, apply)
  → Application (pipeline: Applied → … → Selected / Rejected)
  → Recruitment Outcome (internship / placement record)
  → Company Feedback
  → College Skill-Gap Analysis
  → (feeds back into training focus)
```

### System Boundary

| Layer | Technology | Runs On |
|---|---|---|
| Frontend SPA | React + Vite (JavaScript) | Browser |
| Backend API | Spring Boot (Java) | Application Server |
| Persistence | PostgreSQL | Database Server |
| File Storage | Local filesystem (MVP) | Application Server |

### High-Level Topology

```
Browser
└── React + Vite SPA
      │  HTTPS / REST (JSON)
      ▼
Application Server (JVM)
└── Spring Boot Modular Monolith
      │  JDBC
      ▼
PostgreSQL Database
```

---

## 2. Architecture Style

### Decision: Modular Monolith

**What:**
A single deployable Spring Boot application, organized internally into well-separated
domain modules. Each module owns its domain logic and exposes only intentional interfaces
to other modules. The React SPA is a separate build artifact served as static files.

**Why:**
- The PRD explicitly states: *"a modest, modular structure is appropriate for the timeline;
  nothing in the source requires distributed infrastructure."*
- A 6-person team coordinating on a single codebase avoids the operational overhead of
  inter-service networking, independent deployments, and distributed tracing.
- Module boundaries inside the monolith still enforce discipline and can be extracted
  to services later if the project grows beyond SIH scope.

**Alternative Considered:** Microservices (separate Spring Boot apps per domain).

**Why Rejected:** Service discovery, API gateways, distributed transactions, separate CI
pipelines, and inter-service authentication represent excessive overhead for this team
and timeline. Explicitly ruled out by PRD §5 and rules/02-development.md.

### Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│  Browser                                                             │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  React + Vite SPA (JavaScript)                                 │  │
│  │  React Router · Axios · shadcn/ui · Tailwind CSS · Recharts   │  │
│  │  useState / useReducer · AuthContext                           │  │
│  └──────────────────────────┬─────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────-┘
                              │  HTTPS / REST (JSON)
                              ▼
┌──────────────────────────────────────────────────────────────────────┐
│  Application Server (JVM)                                            │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  Spring Boot Modular Monolith                                  │  │
│  │                                                                │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐          │  │
│  │  │  auth   │  │ student │  │ company │  │ college │          │  │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘          │  │
│  │  ┌─────────┐  ┌─────────┐  ┌──────────┐  ┌────────┐          │  │
│  │  │  skill  │  │opprtny  │  │ matching │  │  app  │          │  │
│  │  └─────────┘  └─────────┘  └──────────┘  └────────┘          │  │
│  │  ┌──────────┐  ┌─────────┐  ┌────────┐                        │  │
│  │  │internshp │  │analytic │  │ admin  │                        │  │
│  │  └──────────┘  └─────────┘  └────────┘                        │  │
│  │  ┌─────────┐  ┌──────────────────────────────────────────┐    │  │
│  │  │  file   │  │            common/                        │    │  │
│  │  └─────────┘  │  GlobalExceptionHandler · SecurityConfig  │    │  │
│  │               │  CorsConfig · SwaggerConfig               │    │  │
│  │               └──────────────────────────────────────────┘    │  │
│  │                                                                │  │
│  │  Spring Security (JWT) · Spring Data JPA / Hibernate          │  │
│  │  Jakarta Bean Validation · OpenAPI / Swagger                  │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
                              │  JDBC
                              ▼
┌──────────────────────────────────────────────────────────────────────┐
│  PostgreSQL                                                          │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 3. Frontend Architecture

### Technology (Locked — rules/02-development.md)

| Concern | Tool |
|---|---|
| Framework | React 18 (JavaScript — no TypeScript) |
| Build Tool | Vite |
| Routing | React Router v6 |
| HTTP | Axios |
| UI Components | shadcn/ui |
| Styling | Tailwind CSS |
| Charts | Recharts |
| State | useState / useReducer (no Redux) |

### Folder Structure

```
src/
├── api/                  # Axios instance + per-module API call functions
│   ├── axios.js          # Base config, request/response interceptors
│   ├── auth.js
│   ├── student.js
│   ├── company.js
│   ├── college.js
│   ├── opportunity.js
│   ├── application.js
│   ├── matching.js
│   ├── analytics.js
│   └── admin.js
│
├── components/           # Reusable, domain-agnostic UI components
│   ├── ui/               # shadcn/ui re-exports and wrappers
│   ├── layout/           # AppShell, Sidebar, Navbar, Footer
│   ├── SkillBadge.jsx
│   ├── MatchScore.jsx
│   ├── StatusBadge.jsx
│   ├── LoadingSpinner.jsx
│   ├── EmptyState.jsx
│   └── ErrorMessage.jsx
│
├── pages/                # Route-level page components (one file per screen)
│   ├── auth/             # Login, Register
│   ├── student/          # Dashboard, Profile, Skills, Browse, OpportunityDetail, MyApplications
│   ├── company/          # Dashboard, Profile, PostOpportunity, ManageOpportunities, Applicants
│   ├── college/          # Dashboard, Students, SkillGap, PlacementFunnel
│   └── admin/            # Dashboard, Users, Companies, Colleges, Skills, Opportunities
│
├── hooks/                # Custom React hooks (useAuth, useOpportunities, …)
├── context/              # AuthContext — stores { user, role, token, collegeId, companyId }
├── utils/                # Formatting helpers, constants
├── router/               # Route definitions, ProtectedRoute component
└── main.jsx              # App entry point
```

### State Management Strategy

**Local state (`useState`):** Form values, UI toggles, loading/error flags per component.

**Lifted state (`useReducer` + Context):** Auth state (user, role, token, collegeId, companyId)
lives in `AuthContext`, passed via React Context to all descendants.
No global state library (Redux is forbidden per rules/02-development.md).

This is appropriate because SkillBridge has no deeply nested cross-cutting state beyond auth.

### Routing and Access Control

React Router v6 is used for all client-side routing.

A `ProtectedRoute` wrapper component:
1. Reads the current user from `AuthContext`.
2. Redirects to `/login` if not authenticated.
3. Redirects to `/unauthorized` if the user's role does not match the required role.

```
/                   → redirects to role-appropriate dashboard
/login
/register
/student/*          → ProtectedRoute(role=STUDENT)
/company/*          → ProtectedRoute(role=COMPANY)
/college/*          → ProtectedRoute(role=COLLEGE)
/admin/*            → ProtectedRoute(role=ADMIN)
/unauthorized
```

This is a UX guard only. The backend is the authoritative access control layer.

### API Layer

All server calls go through `src/api/axios.js`:
- `baseURL` set from `VITE_API_BASE_URL` environment variable.
- **Request interceptor:** attaches `Authorization: Bearer <token>` header on every request.
- **Response interceptor:** handles 401 globally (clears `AuthContext`, redirects to `/login`).

Each domain module (e.g., `student.js`) exports named async functions that call specific
endpoints. Pages call these functions, keeping them free of raw URL strings.

### UI State Handling (Mandatory per rules/02-development.md)

Every data-fetching component must handle all four states:

| State | Rendered Element |
|---|---|
| Loading | `<LoadingSpinner />` |
| Success (data present) | Rendered content |
| Empty (data = []) | `<EmptyState message="…" />` |
| Error | `<ErrorMessage message="…" onRetry={…} />` |

---

## 4. Backend Architecture

### Technology (Locked — rules/02-development.md)

| Concern | Tool |
|---|---|
| Framework | Spring Boot 3 (Java 21) |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| API Docs | OpenAPI / Swagger (springdoc-openapi) |
| Database Driver | PostgreSQL JDBC |

### Layered Architecture (Mandatory per rules/02-development.md)

```
HTTP Request
  → Controller   (HTTP layer only: deserialize, validate, delegate, serialize)
  → Service      (ALL business logic; calls Repository and other Services)
  → Repository   (data access via Spring Data JPA)
  → Entity       (JPA-managed domain object — never returned through the API)
  → DTO          (crosses the API boundary; mapped in Service layer)
```

Rules:
- Controllers **must not** contain business logic.
- Services **must not** return JPA entities to controllers — always map to DTOs.
- Entities are data holders; no business logic lives in entity classes.

### Internal Package Structure

Each module follows the same sub-package layout: `controller / service / repository / entity / dto`.

```
com.skillbridge/
├── auth/
│   ├── controller/    AuthController
│   ├── service/       AuthService, AuthServiceImpl
│   └── dto/           LoginRequest, RegisterRequest, AuthResponse
│
├── user/              (shared User entity + repository — used by auth and other modules)
│   ├── entity/        User
│   └── repository/    UserRepository
│
├── student/
│   ├── controller/    StudentController
│   ├── service/       StudentService, StudentServiceImpl
│   ├── repository/    StudentProfileRepository, StudentSkillRepository, PortfolioItemRepository
│   ├── entity/        StudentProfile, StudentSkill, Project, Certification
│   └── dto/           StudentProfileDto, SkillDto, PortfolioItemDto, …
│
├── company/
│   ├── controller/    CompanyController
│   ├── service/       CompanyService, CompanyServiceImpl
│   ├── repository/    CompanyProfileRepository
│   ├── entity/        CompanyProfile
│   └── dto/           CompanyProfileDto, VerificationStatusDto, …
│
├── college/
│   ├── controller/    CollegeController
│   ├── service/       CollegeService, CollegeServiceImpl
│   ├── repository/    CollegeRepository
│   ├── entity/        College
│   └── dto/           CollegeDto, …
│
├── skill/             (master skills taxonomy — shared reference data)
│   ├── controller/    SkillController   (admin-facing CRUD)
│   ├── service/       SkillService, SkillServiceImpl
│   ├── repository/    SkillRepository
│   ├── entity/        Skill
│   └── dto/           SkillDto
│
├── opportunity/
│   ├── controller/    OpportunityController
│   ├── service/       OpportunityService, OpportunityServiceImpl
│   ├── repository/    OpportunityRepository, RequiredSkillRepository
│   ├── entity/        Opportunity, RequiredSkill
│   └── dto/           OpportunityDto, CreateOpportunityRequest,
│                      OpportunitySearchRequest, OpportunityListItemDto, …
│
├── matching/
│   ├── service/       MatchingService, MatchingServiceImpl
│   └── dto/           MatchResultDto, EligibilityResultDto
│       (no repository — pure computation service; no database writes)
│
├── application/
│   ├── controller/    ApplicationController
│   ├── service/       ApplicationService, ApplicationServiceImpl
│   ├── repository/    ApplicationRepository
│   ├── entity/        Application
│   └── dto/           ApplicationDto, SubmitApplicationRequest,
│                      StatusUpdateRequest, ApplicantRankDto, …
│
├── internship/
│   ├── controller/    InternshipController
│   ├── service/       InternshipService, InternshipServiceImpl
│   ├── repository/    InternshipRepository, FeedbackRepository
│   ├── entity/        InternshipRecord, CompanyFeedback
│   └── dto/           InternshipDto, FeedbackDto, …
│
├── analytics/
│   ├── controller/    AnalyticsController
│   ├── service/       AnalyticsService, AnalyticsServiceImpl
│   └── dto/           SkillAvailabilityDto, SkillDemandDto, SkillGapDto,
│                      SkillGapDashboardDto, …
│
├── admin/
│   ├── controller/    AdminController
│   ├── service/       AdminService, AdminServiceImpl
│   └── dto/           AdminUserDto, VerificationRequest, …
│
├── file/
│   ├── controller/    FileController
│   ├── service/       FileStorageService (interface), LocalFileStorageServiceImpl
│   └── dto/           FileUploadResponse
│
└── common/
    ├── exception/     GlobalExceptionHandler, AppException, ErrorResponse
    ├── config/        SecurityConfig, CorsConfig, SwaggerConfig
    └── util/          DateUtils, StringUtils
```

---

## 5. Module Boundaries

Modules communicate through **service interfaces only**. No module may directly access
another module's `repository` package or `entity` package. Cross-module data is exchanged
via DTOs returned from the other module's service.

### Allowed Inter-Module Dependencies

```
auth         → user                     (authenticate User entity)
student      → user, college, skill,    (profile links to college; skills from master list;
               file                     student calls FileStorageService to store/delete resume)
company      → user, skill              (profile; required skills from master list)
opportunity  → company, skill           (posting linked to company; required skills)
matching     → student, opportunity     (reads skill sets; pure computation — no writes)
application  → student, opportunity,    (apply; invoke matching for score snapshot;
               matching, internship     trigger internship record on selection)
internship   → application, student,    (record created from selected application)
               company
analytics    → student, opportunity,    (aggregates existing data; no writes)
               application, internship,
               college
admin        → user, company, college,  (moderation and verification)
               opportunity, skill
file         → (none)                   (generic storage service; no domain dependency)
```

### File / Student Boundary Clarification

> **`file` module is generic.** `FileStorageService` stores, loads, and deletes any file
> given a caller-supplied path key. It has no knowledge of students, resumes, or any
> other domain concept.
>
> **`student` module owns the concept of "resume".** `StudentService` calls
> `FileStorageService` to store or delete a resume file, then persists the returned
> storage key in `StudentProfile.resumePath`. The `student` module depends on `file`,
> not the reverse.

### Boundary Enforcement Rule

> **No module may call another module's `Repository` directly.**
> All cross-module data access must go through the target module's `Service` interface.

This keeps each module independently changeable without cascading breakage across the codebase.

---

## 6. Authentication

### Mechanism: Stateless JWT

**What:**
JSON Web Tokens (JWT) issued at login, sent as `Authorization: Bearer <token>` on every
subsequent request. The server validates the token signature without server-side session state.

**Why:**
- Spring Security has first-class JWT support.
- Stateless design eliminates the need for a session store.
- Standard pattern for Spring Boot + React SPA applications.
- Avoids Redis or any additional infrastructure dependency.

**Alternative Considered:** Server-side sessions (Spring Session, HTTP session).

**Why Rejected:** Requires sticky sessions or a distributed session store (e.g., Redis,
which is explicitly excluded from the tech stack per rules/02-development.md).

### Authentication Flow

```
POST /api/v1/auth/register
  Input:  { email, password, role, role-specific fields }
  Output: 201 Created (no token; account created, not yet logged in)

POST /api/v1/auth/login
  Input:  { email, password }
  Output: { token, role, userId, [collegeId | companyId] }

Every subsequent protected request:
  Header: Authorization: Bearer <token>
  Backend: validates signature, reads claims, grants or denies access
```

### Token Storage (Frontend)

The JWT is stored in **`sessionStorage`** or in-memory inside `AuthContext`.

> **Not `localStorage`.** `localStorage` persists across tabs and browser restarts and is
> accessible to XSS scripts. `sessionStorage` is cleared when the tab is closed,
> reducing the exposure window.

### JWT Claims

```json
{
  "sub":        "<userId>",
  "role":       "STUDENT | COMPANY | COLLEGE | ADMIN",
  "collegeId":  "<id>",   // for STUDENT and COLLEGE roles
  "companyId":  "<id>",   // for COMPANY role
  "iat":        "<epoch>",
  "exp":        "<epoch>"
}
```

The `role`, `collegeId`, and `companyId` claims are read directly from the token on the
backend, eliminating extra database lookups for authorization context on most requests.

### Token Lifecycle

| Event | Behaviour |
|---|---|
| Login | Token issued with configured expiry (see Open Decisions OD-09) |
| Every request | Interceptor validates signature and `exp` claim |
| Token expired | Backend returns 401; Axios interceptor redirects to `/login` |
| Logout | Frontend clears token from context/storage; token is not server-side invalidated |

The inability to server-side invalidate a JWT is a known trade-off — see Section 23 R-01.

---

## 7. Authorization / RBAC

### Roles

| Role | JWT value |
|---|---|
| Student | `STUDENT` |
| Company | `COMPANY` |
| College | `COLLEGE` |
| Admin | `ADMIN` |

Each user account has exactly one role (FR-AUTH-01). The role is fixed at registration
and cannot be changed by the user.

### Backend RBAC — URL Level (SecurityConfig)

```
/api/v1/auth/**             → permitAll
/api/v1/admin/**            → hasRole(ADMIN)
/api/v1/student/**          → hasRole(STUDENT)
/api/v1/company/**          → hasRole(COMPANY)
/api/v1/college/**          → hasRole(COLLEGE)
/api/v1/opportunities/**    → authenticated  (method-level rules below)
/api/v1/applications/**     → authenticated  (method-level rules below)
/api/v1/analytics/**        → hasAnyRole(COLLEGE, ADMIN)
/api/v1/files/**            → authenticated
/api/v1/skills              → authenticated  (read); hasRole(ADMIN) for write
```

### Backend RBAC — Method Level (`@PreAuthorize`)

Used for fine-grained ownership checks beyond role:

```java
// Company can only edit its own opportunity
@PreAuthorize("hasRole('COMPANY') and @securityService.isOpportunityOwner(#id, authentication)")

// Company can only update applications on its own opportunity
@PreAuthorize("hasRole('COMPANY') and @securityService.isApplicationOwner(#id, authentication)")

// College can only view its own students and analytics
// (collegeId extracted from JWT claim — no extra DB lookup needed)

// Student can only edit their own profile
@PreAuthorize("hasRole('STUDENT') and @securityService.isProfileOwner(#id, authentication)")
```

A dedicated `SecurityService` bean handles these ownership checks to keep
authorization logic out of controllers.

### Frontend RBAC

`ProtectedRoute` enforces role at the routing level for UX.
The backend is the authoritative enforcement layer — frontend checks are defence-in-depth only.

---

## 8. API Communication

### Style: REST over HTTPS

**What:** HTTP REST API with JSON bodies. Spring MVC on the backend.
Axios on the frontend. All routes versioned under `/api/v1/`.

**Why:** REST is simple, stateless, and well-understood. Spring MVC and Axios have
excellent tooling support. Sufficient for all SkillBridge data access patterns.

**Alternative Considered:** GraphQL.

**Why Rejected:** Not permitted by rules/02-development.md. Adds a separate query language,
schema management, and resolver complexity without commensurate benefit for this domain.

### URL Convention

```
/api/v1/{domain}/{resource}
/api/v1/{domain}/{resource}/{id}
/api/v1/{domain}/{resource}/{id}/{sub-resource}
```

Examples:
```
GET    /api/v1/opportunities                         list / search
POST   /api/v1/opportunities                         create
GET    /api/v1/opportunities/{id}                    get by id
PUT    /api/v1/opportunities/{id}                    full update
PATCH  /api/v1/opportunities/{id}/status             partial update
DELETE /api/v1/opportunities/{id}                    deactivate / delete
GET    /api/v1/opportunities/{id}/applicants         nested resource
POST   /api/v1/applications                          submit application
PATCH  /api/v1/applications/{id}/status              advance pipeline stage
GET    /api/v1/analytics/skill-gap                   college analytics
POST   /api/v1/files/resume                          upload resume
GET    /api/v1/files/resume/{studentId}              download resume
```

### HTTP Status Code Conventions

| Scenario | Status Code |
|---|---|
| Success with data | 200 OK |
| Created | 201 Created |
| Success, no body | 204 No Content |
| Validation error | 400 Bad Request |
| Not authenticated | 401 Unauthorized |
| Authenticated, not authorized | 403 Forbidden |
| Resource not found | 404 Not Found |
| Duplicate / conflict | 409 Conflict |
| Server error | 500 Internal Server Error |

### API Documentation

springdoc-openapi generates an OpenAPI 3.0 specification automatically from Spring MVC
annotations. Available at:
- **Spec:** `GET /api/docs`
- **UI:** `GET /api/swagger-ui.html`

All endpoints must be annotated with `@Operation`, `@ApiResponse`, and `@Parameter`.
The OpenAPI spec serves as the shared contract between the frontend and backend teams.

### CORS Policy

`CorsConfig.java` restricts CORS to the known frontend origin only, set via environment
variable `FRONTEND_ORIGIN`. No wildcard `*` origin is permitted in production.

---

## 9. Database Boundary

### Technology: PostgreSQL

**What:** A single PostgreSQL database with a relational schema, managed exclusively via
migration scripts.

**Why:** Relational model fits the SkillBridge domain well — entities (students, skills,
opportunities, applications) have clear FK relationships, and relational integrity constraints
(unique on applications, FK cascades) are valuable. PostgreSQL is locked in the tech stack.

**Alternative Considered:** MySQL.

**Why Rejected:** PostgreSQL is specified in the locked technology stack. PostgreSQL also
has superior support for advanced query features that may be useful for analytics.

### Database Access Rules

1. **No raw SQL in business logic.** All database access goes through Spring Data JPA repositories.
2. **No entity returned from service to controller.** Services map to DTOs before returning.
3. **No business logic in entity classes.** Entities are data holders only.
4. **No `ddl-auto=update` in production.** Schema is managed exclusively by Flyway.

### Schema Migrations: Flyway

**What:** Versioned SQL migration scripts applied automatically on application startup.

**Why:**
- A team of 6 working on a shared schema needs a conflict-free way to evolve the database.
- Flyway creates a complete, reproducible schema history.
- Prevents "works on my machine" schema drift between team members.

**Alternative Considered:** Hibernate `ddl-auto=update`.

**Why Rejected:** Dangerous in any production-like environment — can miss constraints,
ignore renames, and does not create a schema history. Not appropriate for any deployment
beyond local prototyping.

**Migration naming convention:**
```
V1__create_initial_schema.sql
V2__add_internship_status.sql
V3__add_resume_path_to_student.sql
```

### Connection Pool

Spring Boot's HikariCP (default) manages the connection pool.
`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are externalized as environment variables.
Pool sizing uses HikariCP defaults for MVP; tunable as needed.

---

## 10. Skill Matching Module

### Purpose

Implements FR-MATCH-01 (skill match computation) and FR-MATCH-02 (eligibility check).
This is the core algorithmic module of SkillBridge.

### Design

`MatchingService` is a **pure computation service with no database writes and no owned
repository**. It receives input data and returns a result. Other modules call it; it
calls other modules' services to fetch data.

```
MatchingService.computeMatch(studentSkillIds: Set<Long>, requiredSkillIds: Set<Long>)
  → MatchResultDto {
       matchedSkills:    List<SkillDto>,
       missingSkills:    List<SkillDto>,
       matchPercent:     double,         // matched.size / required.size * 100
       requiredCount:    int
     }

MatchingService.checkEligibility(studentProfile: StudentProfileDto, opportunity: OpportunityDto)
  → EligibilityResultDto {
       eligible:         boolean,
       failureReasons:   List<String>   // e.g., "CGPA below minimum"
     }
```

### Skill Match Algorithm (Unweighted — MVP baseline per FR-MATCH-01)

```
matched     = studentSkillIds ∩ requiredSkillIds
missing     = requiredSkillIds \ studentSkillIds
matchPercent = (|matched| / |requiredSkillIds|) × 100

Edge case:
  if |requiredSkillIds| == 0 → matchPercent = 0 (display "No required skills defined")
```

Per PRD F8 and FR-MATCH-01: this is skill *coverage*, not proficiency.
The UI must label match % clearly as coverage.

Weighted matching is explicitly **not** part of the MVP (FR-MATCH-01 acceptance criteria;
PRD Open Question #3). It is recorded as Open Decision OD-02.

### Eligibility Algorithm (FR-MATCH-02)

```
eligible = true
failureReasons = []

if opportunity.requiredBranches is not empty
  and student.department not in opportunity.requiredBranches:
    eligible = false
    failureReasons += "Branch not eligible"

if opportunity.requiredYears is not empty
  and student.year not in opportunity.requiredYears:
    eligible = false
    failureReasons += "Year not eligible"

if opportunity.minCGPA > 0
  and student.cgpa < opportunity.minCGPA:
    eligible = false
    failureReasons += "CGPA below minimum (required: X.X)"
```

Per FR-MATCH-02: eligibility is evaluated independently. An ineligible student
can still see their match score but is clearly marked ineligible on the UI.

### Invocation Points

| Trigger | Caller | Purpose |
|---|---|---|
| Student browses opportunities | OpportunityService → MatchingService | Show match % + eligibility flag per listing |
| Student opens opportunity detail | OpportunityService → MatchingService | Show full matched/missing skill breakdown |
| Student submits application | ApplicationService → MatchingService | Snapshot match % onto the application record |
| Company views applicant list | ApplicationService → MatchingService | Rank applicants by match % (descending) |

### Match Score Snapshot

When an application is submitted, the match % at that moment is stored on the
`Application` entity (`matchPercentAtApply`). This ensures the company sees the
candidate's match score at the time of application, not a potentially changed later value.

---

## 11. Application / Recruitment Module

### Purpose

Implements FR-APP-01 through FR-APP-05, and FR-APP-06 (Should-Have notification).

### Application Entity (Conceptual)

```
Application {
  id, student, opportunity, status,
  matchPercentAtApply,
  appliedAt, updatedAt
}
```

### Recruitment Pipeline State Machine

```
APPLIED
  → UNDER_REVIEW
      → SHORTLISTED
            → INTERVIEW
                  → SELECTED   → triggers InternshipRecord / PlacementRecord creation
                  → REJECTED
            → REJECTED
      → REJECTED
  → REJECTED
```

Whether stages may be skipped or reversed is **TBD** (PRD Open Question #6 / OD-03).
The backend validates the target stage against an allowed-transitions map, which can
be updated once OD-03 is resolved.

### Application Business Rules

- **One application per student per opportunity** — enforced by a unique database constraint
  on `(student_id, opportunity_id)`. Duplicate attempts return 409 (FR-APP-01).
- **Application creation fails** if the opportunity is closed or the deadline has passed
  (FR-APP-01 error cases).
- **Company can only update applications** on opportunities it owns — `@PreAuthorize`
  ownership check (FR-APP-04).
- **On status → SELECTED:** `ApplicationService` invokes `InternshipService.createRecord(application)`
  to auto-create the internship or placement record (FR-APP-05).
- **Candidate ranking:** `GET /api/v1/opportunities/{id}/applicants` returns only eligible
  applicants, sorted by `matchPercentAtApply` descending (FR-APP-03).

### Internship / Placement Record (FR-APP-05, FR-INT-04)

Auto-created when an application reaches `SELECTED`. The record type (INTERNSHIP / PLACEMENT)
is derived from the opportunity's `type` field.

```
InternshipRecord {
  id, student, company, opportunity,
  type:   INTERNSHIP | PLACEMENT,
  status: UPCOMING | ONGOING | COMPLETED,
  startDate, endDate,
  feedback (FK to CompanyFeedback)
}

CompanyFeedback {
  id, internshipRecord, feedbackText,
  submittedAt
}
```

Company feedback (FR-INT-05) is captured on the record when the internship/placement
reaches `COMPLETED` status.

---

## 12. College Analytics Module

### Purpose

Implements FR-ANL-01 through FR-ANL-05, providing the College with skill availability,
industry demand, skill-gap analysis, and the placement funnel.

### Computation Approach

Analytics are **computed at query time** from existing operational data.
No separate aggregation tables or batch jobs are maintained for the MVP.

#### Skill Availability per College (FR-ANL-01)

```sql
-- % of the college's students who list each skill
SELECT s.id, s.name,
  COUNT(DISTINCT ss.student_id) * 100.0 / (SELECT COUNT(*) FROM student_profiles WHERE college_id = :cid)
    AS availability_pct
FROM skills s
JOIN student_skills ss ON ss.skill_id = s.id
JOIN student_profiles sp ON sp.id = ss.student_id
WHERE sp.college_id = :cid
GROUP BY s.id, s.name
```

#### Industry Skill Demand (FR-ANL-02)

```sql
-- % of OPEN opportunities that require each skill (OD-07: CONFIRMED — all OPEN opportunities)
SELECT s.id, s.name,
  COUNT(DISTINCT rs.opportunity_id) * 100.0
    / (SELECT COUNT(*) FROM opportunities WHERE status = 'OPEN')
    AS demand_pct
FROM skills s
JOIN required_skills rs ON rs.skill_id = s.id
JOIN opportunities o ON o.id = rs.opportunity_id
WHERE o.status = 'OPEN'
GROUP BY s.id, s.name
```

#### Skill Gap Classification (FR-ANL-03)

```
gap = demand_pct - availability_pct

Classification (OD-01: CONFIRMED):
  HIGH:     gap >= 30%
  MODERATE: gap >= 15% and < 30%
  LOW:      gap >  0% and < 15%
  SURPLUS:  gap <= 0%
```

The classification rule is confirmed and must be documented in the codebase constants
and surfaced as a visible legend in the college analytics UI.

#### Placement Funnel (FR-COL-02)

Aggregates `Application.status` counts across all students associated with the college.
Grouped by pipeline stage (APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW, SELECTED, REJECTED).
Optional filter by department.

### Analytics Authorization

`AnalyticsController` is restricted to `COLLEGE` and `ADMIN` roles.
A College user can only view analytics for their own college — the `collegeId` is
extracted from the JWT claim, not a user-supplied parameter.

### Performance Note

At MVP scale (hundreds of students, tens of opportunities), query-time aggregation is acceptable.
If data volume grows, analytics can be cached or moved to scheduled pre-computation.
See Section 21 for scalability notes.

---

## 13. File / Resume Handling

### Scope

Resume upload is **Should-Have** (FR-STU-04 / S1). The architecture is designed so that
this feature can be added without structural changes to the system.

### Design

```
FileStorageService (interface)              ← generic; no domain knowledge
  + store(MultipartFile file, String subDir) → String storedKey
  + load(String storedKey) → Resource
  + delete(String storedKey)

LocalFileStorageServiceImpl (MVP implementation)
  - Stores files under UPLOAD_DIR/{subDir}/{uuid}_{sanitisedOriginalName}
  - Returns the storedKey (relative path) — the caller persists this, not the module

StudentService (in student module)          ← owns the "resume" concept
  - Calls FileStorageService.store(file, "resumes") → storedKey
  - Persists storedKey in StudentProfile.resumePath
  - Calls FileStorageService.delete(resumePath) on remove
```

The `FileStorageService` interface is the **extension point**. `LocalFileStorageServiceImpl`
can be replaced with a cloud storage implementation (e.g., S3) without changing callers.

**Why local filesystem for MVP:** Zero external service dependency, fast to implement,
acceptable for demo/pilot scale.

**Why cloud storage was deferred:** Adds AWS SDK, IAM credential management, and network
dependency. The `FileStorageService` interface keeps this swap low-cost when needed.

### File Security

- **Type validation:** MIME type detected from file content (not just extension).
  Allowed types: **PDF and DOCX only** (OD-08: confirmed).
- **Size cap:** Max **5 MB** enforced by Spring (`spring.servlet.multipart.max-file-size`).
  (OD-08: confirmed).
- **Storage location:** Outside the web root — files are not directly web-accessible.
- **Filenames:** UUID-based names in storage to prevent path traversal and collisions.
- **Download access:** `GET /api/v1/files/resume/{studentId}` is authenticated.
  Access permitted only to: the student themselves, a company reviewing their application, Admin.

---

## 14. Validation

### Backend: Jakarta Bean Validation

`@Valid` is applied on all request DTO parameters at the controller layer.
Invalid requests produce **400 Bad Request** with a structured field-level error body.

Annotations used as appropriate:
`@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Email`, `@Pattern`, `@DecimalMin`, `@DecimalMax`

Business-rule validation (e.g., duplicate application, closed opportunity, unauthorized owner
access) is performed in the service layer and throws a typed `AppException` with an
appropriate status code.

### Frontend: Client-Side Validation

Client-side validation provides immediate feedback before network calls.
It is **not** a substitute for server validation — the backend always validates independently.

Required validations before submit:
- Required fields non-empty.
- Email format.
- Password minimum length (see OD-10).
- CGPA numeric, in range 0.0–10.0.
- URL format for portfolio / website fields.

Server 400 responses with field-level errors are displayed alongside the relevant form fields.

---

## 15. Error Handling

### Backend: Centralized Global Exception Handler

`GlobalExceptionHandler` (`@RestControllerAdvice`) intercepts all uncaught exceptions
and maps them to a consistent `ErrorResponse` DTO:

```json
{
  "timestamp": "2026-08-27T15:30:00Z",
  "status":    400,
  "error":     "Bad Request",
  "message":   "Validation failed",
  "details": [
    { "field": "cgpa", "message": "must be between 0.0 and 10.0" }
  ],
  "path": "/api/v1/students/profile"
}
```

Exception mapping:

| Exception | HTTP Status |
|---|---|
| `MethodArgumentNotValidException` | 400 Bad Request |
| `AppException(NOT_FOUND)` | 404 Not Found |
| `AppException(CONFLICT)` | 409 Conflict |
| `AppException(FORBIDDEN)` | 403 Forbidden |
| `AppException(BAD_REQUEST)` | 400 Bad Request |
| `AccessDeniedException` (Spring Security) | 403 Forbidden |
| `AuthenticationException` (Spring Security) | 401 Unauthorized |
| All others (unhandled) | 500 Internal Server Error |

**500 responses:** The internal stack trace is logged server-side (ERROR level) but never
returned to the client. The client receives only: `"An internal error occurred. Please try again."`.

### Frontend: Error Propagation

Axios response interceptor:
- `401` → clear `AuthContext`, redirect to `/login`.
- `403` → redirect to `/unauthorized`.
- All others → rethrow for the calling component to handle.

Each page component wraps data fetching in try/catch and sets an `error` state,
rendering `<ErrorMessage />` when the error state is populated.

---

## 16. Security

### Credential Security

- Passwords hashed with **BCrypt** (Spring Security default; work factor ≥ 10).
- JWT signed with **HS256** using a random secret key stored in environment variable `JWT_SECRET`.
  Never hardcoded in source code (rules/02-development.md).
- JWT expiry controlled via `JWT_EXPIRY_MS` environment variable.

### Input Security

- All inputs validated with Jakarta Bean Validation before processing.
- Spring Data JPA parameterizes all queries — no SQL injection vectors.
- Free-text fields (feedback text, descriptions) are stored as-is and HTML-escaped on output
  to prevent stored XSS. (`HtmlUtils.htmlEscape()` or equivalent at render time.)

### Authorization Security

- Every protected endpoint requires a valid, non-expired JWT.
- Role enforcement at URL level (`SecurityConfig`) and method level (`@PreAuthorize`).
- Ownership checks on all mutation endpoints.
- A College user can only see data scoped to their own `collegeId` (from JWT claim).
- A Company user can only modify resources they own.

### CORS

- CORS restricted to the known frontend origin (`FRONTEND_ORIGIN` env var).
- No wildcard `*` origin permitted in production.

### File Upload Security

- MIME type validated from file content (not extension only).
- File size capped at application level.
- Files stored outside web root; served only via authenticated controller endpoint.
- Stored filenames are UUID-based to prevent path traversal.

### HTTPS

All production traffic is over HTTPS. The backend runs behind Nginx which terminates TLS.
The backend JAR itself may serve HTTP to Nginx on localhost.

### HTTP Security Headers (Spring Security defaults + custom)

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Strict-Transport-Security` (when HTTPS is active)

### What Is Explicitly Not Done (per tech stack and PRD constraints)

- No OAuth / third-party login (not in tech stack).
- No Redis for JWT token blacklisting (not in tech stack). See R-01 in Section 23.
- No internal assessment engine (PRD §5).

---

## 17. Logging

### Backend: SLF4J + Logback

Spring Boot's default logging stack (SLF4J with Logback).

**Log levels by environment:**

| Environment | Root Level |
|---|---|
| Development | DEBUG |
| Production | INFO |

**What is always logged (INFO or above):**

- Application startup and shutdown.
- Incoming requests: HTTP method, path, response status, duration
  (via a request/response filter — no request bodies in production).
- Authentication events: login success/failure (with user ID; never passwords or tokens).
- JWT validation failures (401 events) — with reason (expired, invalid signature).
- Business-rule violations caught in service layer.
- Application pipeline stage transitions: application ID, student ID, old status, new status.
- File upload/download events: student ID, file size (no content).

**What is logged at ERROR level (always):**

- All unhandled exceptions caught by `GlobalExceptionHandler`, with full stack trace.

**What is NEVER logged:**

- Passwords (clear or hashed).
- JWT token values.
- Resume file binary content.
- Full request bodies containing PII in production.

**Log format:**

- **Development:** Plain text (human-readable).
- **Production:** Structured JSON (Logback JSON encoder) for easier log aggregation.

### Frontend: Development Only

- `console.error()` for caught errors during development.
- No sensitive data logged to the browser console.
- No frontend error tracking service added for MVP.

---

## 18. Testing Approach

### Backend Testing

| Layer | Type | Tooling |
|---|---|---|
| Service layer | Unit tests | JUnit 5 + Mockito |
| Repository layer | JPA slice tests | `@DataJpaTest` + H2 (or Testcontainers) |
| Controller layer | MVC slice tests | `@WebMvcTest` + MockMvc |
| Full stack flows | Integration tests | `@SpringBootTest` + Testcontainers (PostgreSQL) |

**Priority test targets (MVP must be tested):**

1. `MatchingService` — match algorithm with known inputs and expected outputs
   (FR-MATCH-01 acceptance criteria). This is the mathematical core; it must be correct.
2. `AnalyticsService` — skill availability / demand / gap percentage calculations.
3. `ApplicationService` — duplicate application prevention; status transition rules.
4. `AuthService` — registration with duplicate email; login with invalid credentials.
5. `OpportunityService` — eligibility filter applied correctly to applicant list.

**Why Testcontainers over H2 for integration tests:**
H2 does not support all PostgreSQL-specific SQL features or constraints.
Testcontainers spins a real PostgreSQL container for meaningful integration coverage.
H2 is acceptable for fast repository slice tests where PostgreSQL-specific features are not exercised.

### Build Gate (Mandatory before any merge)

```
mvn test             # all backend tests must pass
mvn package          # backend build must succeed
npm run build        # frontend production build must succeed with no errors
```

### Frontend Testing

Manual testing is the MVP baseline given the 3-day timeline.
Component structure is kept simple and stateless enough that manual coverage is feasible.
Vitest (Vite's built-in test runner) can be added post-MVP for utility function testing.

---

## 19. Configuration / Environment Strategy

### Principle: No Hardcoded Secrets

All secrets and environment-specific values are externalized to environment variables.
No credential, key, or connection string appears in committed source code.
(Mandatory per rules/02-development.md.)

### Backend Configuration Files

```
src/main/resources/
├── application.properties          # Shared defaults (committed — no secrets)
├── application-dev.properties      # Dev overrides (committed — no secrets)
└── application-prod.properties     # Prod structure (committed — all values from env vars)
```

Active profile set by environment variable `SPRING_PROFILES_ACTIVE`.

### Required Backend Environment Variables

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`dev` / `prod`) |
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing key (min 256-bit random string) |
| `JWT_EXPIRY_MS` | Token lifetime in milliseconds (see OD-09) |
| `UPLOAD_DIR` | Absolute path for file upload directory |
| `FRONTEND_ORIGIN` | Allowed CORS origin for the frontend |
| `MAX_FILE_SIZE` | Maximum upload size (see OD-08) |

### Frontend Configuration (Vite)

```
.env                  # Shared defaults (committed)
.env.local            # Local developer overrides (gitignored)
.env.production       # Production values (gitignored; set in deployment)
```

| Variable | Purpose |
|---|---|
| `VITE_API_BASE_URL` | Backend API base URL |

### Git Strategy for Secrets

- `.env.local`, `*.local`, files containing real credentials → **gitignored**.
- `.env.example` (backend) and `.env.example` (frontend) are **committed** with all required
  variable names and placeholder values.
- `README.md` documents the full environment setup steps for new team members.

---

## 20. Deployment Approach

### MVP Deployment: Single Server

Appropriate for a 6-person student team, SIH demo, and the "no unnecessary complexity" rule.

```
Single VM / VPS
│
├── Nginx (reverse proxy, HTTPS termination, static file serving)
│    ├── Serves React dist/ from /var/www/skillbridge/
│    └── Proxies /api/* → http://localhost:8080
│
├── Spring Boot JAR  (port 8080, managed by systemd)
│    └── java -jar skillbridge.jar
│
└── PostgreSQL  (port 5432, local)
     └── skillbridge_db
```

### Nginx Configuration (Concept)

```nginx
server {
  listen 443 ssl;
  server_name skillbridge.example.com;

  root /var/www/skillbridge;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;   # SPA fallback for React Router
  }

  location /api/ {
    proxy_pass http://localhost:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### Build and Deploy Steps

**Backend:**
```bash
mvn clean package                  # produces target/skillbridge.jar
scp skillbridge.jar user@server:/opt/skillbridge/
ssh user@server "systemctl restart skillbridge"
```

**Frontend:**
```bash
npm run build                      # produces dist/
scp -r dist/* user@server:/var/www/skillbridge/
```

### Process Management

A `systemd` unit manages the Spring Boot process with automatic restart on crash and
startup on reboot.

### Schema on Startup

Flyway migrations run automatically on Spring Boot startup. The database schema is always
up to date before the application begins accepting requests.

### Optional: Docker Compose

Docker Compose (listed as optional in rules/02-development.md) provides a portable
alternative to the manual setup above. If adopted:

```yaml
services:
  db:         # PostgreSQL
  backend:    # Spring Boot JAR
  frontend:   # Nginx serving React build
```

Docker Compose is not mandated for MVP. It can be adopted once the team is comfortable.

---

## 21. Scalability Considerations

### Current Target Scale

- Hundreds to low thousands of students (SIH demo or single college pilot).
- Single server is appropriate.
- No distributed caching, message queues, or background workers are needed.

### Identified Bottlenecks and Mitigations

| Concern | MVP Approach | Future Mitigation |
|---|---|---|
| Analytics queries | Query-time aggregation | Scheduled pre-computation or PostgreSQL materialized views |
| Match computation | Per-request computation (MatchingService) | Cache per (student, opportunity) pair; invalidate on skill/opportunity change |
| File storage | Local filesystem | Replace `LocalFileStorageServiceImpl` with S3 implementation (interface already decoupled) |
| Database connections | HikariCP defaults | Tune pool size; add read replicas for analytics |
| Concurrent users | Single JVM | Horizontal scaling is possible — stateless JWT auth means any instance can serve any request |

### Architectural Choices That Enable Future Scaling

1. **Stateless JWT** — any backend instance can validate any request without shared session state.
2. **`FileStorageService` interface** — cloud storage implementation can replace local without callers changing.
3. **Modular boundaries** — individual modules can be extracted to separate services if justified by scale,
   without needing to change their external API contracts.
4. **No in-memory shared request state** — thread-safe by design.

---

## 22. Architectural Decisions

Each decision is documented in ADR format: **What / Why / Alternative / Why Alternative Was Rejected**.

---

### ADR-01 — Modular Monolith over Microservices

| | |
|---|---|
| **What** | Single Spring Boot application with internal domain module separation |
| **Why** | 6-person team, 3-day MVP, PRD explicitly rules out distributed infrastructure |
| **Alternative** | Microservices (independent Spring Boot apps per domain) |
| **Rejected because** | Service discovery, distributed tracing, inter-service auth, separate CI/CD pipelines, distributed data consistency — excessive overhead for this team and timeline |

---

### ADR-02 — Stateless JWT over Server-Side Sessions

| | |
|---|---|
| **What** | JWT issued at login; validated on every request; no server-side session store |
| **Why** | No Redis or session DB needed; fits REST + SPA pattern; scales horizontally |
| **Alternative** | Spring Session with HTTP sessions (in-memory or DB-backed) |
| **Rejected because** | Requires sticky sessions or a distributed session store (Redis is excluded from the tech stack) |

---

### ADR-03 — PostgreSQL (Relational) over Any NoSQL Database

| | |
|---|---|
| **What** | Single PostgreSQL relational database |
| **Why** | Relational model fits SkillBridge perfectly; FK constraints and unique constraints are valuable; locked in tech stack |
| **Alternative** | MongoDB or similar document store |
| **Rejected because** | Not in tech stack; relational integrity (unique application constraint, FK cascades) is a core requirement |

---

### ADR-04 — useState / useReducer over Redux

| | |
|---|---|
| **What** | React's built-in state management; AuthContext via React Context |
| **Why** | No deeply nested cross-cutting state beyond auth; rules/02-development.md explicitly forbids Redux |
| **Alternative** | Redux Toolkit |
| **Rejected because** | Forbidden by project rules; overkill for the complexity level of this SPA |

---

### ADR-05 — Local Filesystem for File Storage (MVP)

| | |
|---|---|
| **What** | Resume files stored on the application server filesystem; path stored in DB |
| **Why** | Zero external dependency; fast to implement; acceptable for demo/pilot scale |
| **Alternative** | Amazon S3 or equivalent cloud object storage |
| **Deferred because** | Adds AWS SDK, IAM credentials, and network dependency. `FileStorageService` interface is decoupled so the swap costs minimal effort later |

---

### ADR-06 — Match Computation at Query Time

| | |
|---|---|
| **What** | Match % computed on demand by `MatchingService`; result not persisted (except the snapshot on application submit) |
| **Why** | Simple; correct; no background jobs or event listeners needed for MVP scale |
| **Alternative** | Pre-compute and cache match scores in a table; update via event/trigger on skill or opportunity change |
| **Deferred because** | Premature optimization; adds event-driven complexity (listeners, cache invalidation strategy); revisit if browse-page performance degrades at scale |

---

### ADR-07 — OpenAPI / Swagger for API Documentation

| | |
|---|---|
| **What** | springdoc-openapi auto-generates the API contract from Spring MVC annotations |
| **Why** | Living documentation; always in sync with the code; shared contract between frontend and backend teams; locked in tech stack |
| **Alternative** | Manual Postman collection only |
| **Rejected because** | Manual docs go stale; auto-generation from code is always accurate |

---

### ADR-08 — Flyway for Database Migrations

| | |
|---|---|
| **What** | Versioned SQL migration scripts applied on startup by Flyway |
| **Why** | Team collaboration requires conflict-free schema evolution; creates a complete schema history |
| **Alternative** | Hibernate `ddl-auto=update` |
| **Rejected because** | `ddl-auto=update` is dangerous in production (misses renames, drops constraints, leaves no history); not appropriate for any production-like environment |

---

### ADR-09 — Recharts for Data Visualization

| | |
|---|---|
| **What** | Recharts renders the college analytics charts (skill-gap bar chart, placement funnel) |
| **Why** | Locked in tech stack; React-native; integrates with shadcn/ui and Tailwind |
| **Alternative** | D3.js, Chart.js, Victory |
| **Rejected because** | Not in tech stack; Recharts is already specified |

---

## 23. Risks and Trade-offs

| # | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| R-01 | **JWT not server-side invalidatable** — logout does not immediately revoke a token; a stolen token is valid until expiry | Medium | Medium | Token expiry set to 24h (OD-09: confirmed). Token blacklisting (Redis) is excluded from the stack; accepted trade-off for MVP. |
| R-02 | **Analytics query performance at scale** — real-time aggregation over large student/opportunity sets may slow | Low (MVP scale) | Medium | Acceptable at MVP scale. Add caching or materialized views if query time becomes unacceptable. |
| R-03 | **Local file storage — data loss on server failure** — resumes not automatically backed up | Medium | Medium | Backup strategy (scheduled rsync to external storage) required before production. Resume upload is Should-Have scope anyway. |
| R-04 | **Single-server — no high availability** — server outage = full downtime | Medium | High (for live demo) | Acceptable for SIH. Move to containerized / HA deployment for any production use. |
| R-05 | **Skill taxonomy inconsistency** — if master skills list is poorly managed, student skills and required skills may not intersect (false low match scores) | Medium | High | Admin taxonomy management (FR-ADM-03) is Must-Have. Student skill input must use the master list (typeahead/autocomplete), not free-form text. |
| R-06 | **Cross-module coupling creep** — without enforcement, modules will start calling each other's repositories directly | Medium | Medium | Architecture document is the reference. Code review must enforce the boundary rule in Section 5. |
| R-07 | **Match score snapshot drift confusion** — company sees a different live score vs. the stored snapshot on the application | Low | Low | Document clearly in UI: "Match % at time of application." Both live and snapshot scores can be shown side by side. |

---

## 24. Open Decisions

**Status: ALL CONFIRMED — 2026-08-27.**

All open decisions have been resolved by the team. The confirmed values are binding
for database design, API contract, and implementation.

> **Rule:** Per project rule 01-project.md — if the PRD/SRS does not specify a requirement,
> it is recorded here. Nothing below is invented as a requirement.

| # | Question | PRD/SRS Reference | Confirmed Decision | Status |
|---|---|---|---|---|
| OD-01 | What numeric thresholds define High / Moderate / Low skill gap severity? | PRD §10, FR-ANL-03 | HIGH ≥ 30% · MODERATE ≥ 15% · LOW > 0% · SURPLUS ≤ 0% | ✅ CONFIRMED |
| OD-02 | Is weighted skill matching in MVP scope or Should-Have/Later? | PRD F8, Open Q #3, FR-MATCH-01 | Weighted matching **excluded from MVP**. Unweighted skill coverage only. | ✅ CONFIRMED |
| OD-03 | Can a company skip or reverse recruitment pipeline stages? | PRD §10, FR-APP-04, Open Q #6 | **Forward-only**. No stage skipping or reversing in MVP. Backend validates against an allowed-forward-transitions map. | ✅ CONFIRMED |
| OD-04 | Is company feedback visible to the student, or can it be marked internal/college-only? | PRD §10, Open Q #7 | Feedback is visible to **both the Student and the College**. No internal-only flag in MVP. | ✅ CONFIRMED |
| OD-05 | Can a student be associated with more than one college? | PRD §13 (assumption) | **One college per student.** College affiliation is fixed at registration. Admin may reassign if needed. | ✅ CONFIRMED |
| OD-06 | What is the verification workflow? Admin toggle only, or a multi-step process? | PRD §13 (assumption), Open Q #5 | **Simple Approve / Reject toggle.** Admin views a pending list and clicks Approve or Reject. No multi-step workflow. | ✅ CONFIRMED |
| OD-07 | Which opportunities count as "relevant" for the industry demand calculation (FR-ANL-02)? | FR-ANL-02 (TBD in SRS) | **All OPEN opportunities** platform-wide are counted. | ✅ CONFIRMED |
| OD-08 | What file types and maximum size are allowed for resume upload? | FR-STU-04 (TBD in SRS) | **PDF and DOCX only. Maximum 5 MB.** MIME type validated from file content, not extension. | ✅ CONFIRMED |
| OD-09 | What is the JWT token expiry duration? | Not specified in PRD/SRS | **24 hours.** Configured via `JWT_EXPIRY_MS` environment variable. | ✅ CONFIRMED |
| OD-10 | What is the password policy (minimum length, complexity)? | FR-AUTH-01 (TBD in SRS) | **Minimum 8 characters.** No special character requirement for MVP. | ✅ CONFIRMED |
| OD-11 | Can a company submit feedback before an internship record reaches Completed status? | FR-INT-05 (TBD in SRS) | **Feedback requires COMPLETED status.** Enforced in `InternshipService` — feedback endpoint rejects requests if status ≠ COMPLETED. | ✅ CONFIRMED |
| OD-12 | Does Admin need full CRUD over every entity in the MVP, or only verification + moderation actions? | PRD Open Q #10 | **Scoped to:** verification (Approve/Reject company and college) · user deactivation · skill taxonomy CRUD · opportunity deactivation. No full CRUD over all entities. | ✅ CONFIRMED |

---

## Appendix A — PRD / SRS Validation

The table below confirms that every Must-Have and Should-Have feature in the PRD has
a corresponding architectural provision in this document. Nothing in this architecture
has been invented beyond what the PRD and SRS specify.

| PRD Feature | SRS FR-IDs | Architectural Coverage in This Document |
|---|---|---|
| F1 Authentication | FR-AUTH-01, FR-AUTH-02, FR-AUTH-03 | §6 Authentication |
| F2 Role-Based Access | FR-AUTH-04 | §7 Authorization / RBAC |
| F3 Student Profile | FR-STU-01 | §4 Backend (student module), §5 |
| F4 Skills / Portfolio | FR-STU-02, FR-STU-03 | §4 Backend (student module) |
| F5 Company Profile | FR-COM-01, FR-COM-02 | §4 Backend (company module) |
| F6 Internship Posting | FR-INT-01, FR-INT-02 | §4 Backend (opportunity module) |
| F7 Internship Search | FR-INT-03 | §4 Backend (opportunity module), §10 |
| F8 Skill Matching | FR-MATCH-01 | §10 Skill Matching Module |
| F9 Application | FR-APP-01 | §11 Application / Recruitment Module |
| F10 Application Status / Pipeline | FR-APP-02, FR-APP-03, FR-APP-04, FR-APP-05 | §11 (state machine, ranking, auto-create record) |
| F11 College Dashboard | FR-COL-01, FR-COL-02 | §12 College Analytics Module (funnel) |
| F12 Basic Skill-Gap Analysis | FR-ANL-01, FR-ANL-02, FR-ANL-03, FR-ANL-05 | §12 College Analytics Module |
| F13 Admin Management | FR-ADM-01, FR-ADM-02, FR-ADM-03, FR-ADM-04 | §4 Backend (admin module), §7 RBAC |
| S1 Resume Upload | FR-STU-04 | §13 File / Resume Handling |
| S2 Resume → Skill Extraction | FR-STU-05 | AI feature — not architecturally designed (Later tier) |
| S3 Recommended Internships | FR-STU-06 | §10 (opportunities ranked by match %; sorted list) |
| S4 In-App Notifications | FR-APP-06 | Not detailed (Should-Have; add in-app notification entity when implemented) |
| S5 Placement Tracking | FR-INT-06 | §11 (InternshipRecord type field: INTERNSHIP / PLACEMENT) |
| S6 Industry Demand Analytics | FR-ANL-04 | §12 (filterable demand view — additional filter dimension on FR-ANL-02) |
| Eligibility filtering (Business Rule) | FR-MATCH-02 | §10 Skill Matching Module (eligibility algorithm) |
| Company feedback (Business Rule) | FR-INT-05 | §11 (CompanyFeedback on InternshipRecord) |

---

*Architecture document complete and **APPROVED**.*
*All Open Decisions confirmed — 2026-08-27.*
*Next step: Database design / ERD.*
