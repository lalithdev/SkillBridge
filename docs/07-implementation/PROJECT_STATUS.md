# SkillBridge — Project Status

## Current Phase

DEVELOPMENT READY

## Completed

- [x] Project definition
- [x] PRD — APPROVED
- [x] SRS — APPROVED
- [x] Agent rules configured
- [x] Agent skills configured
- [x] Agent workflows configured
- [x] Architecture — APPROVED
- [x] Open Decisions — ALL CONFIRMED (2026-08-27)
- [x] Database Design — APPROVED (DBQ-01 to DBQ-05 resolved)
- [x] API Contract — APPROVED
- [x] System Design Audit — APPROVED
- [x] UX / User Flows
- [x] Screen Specifications
- [x] Design System
- [x] Frontend Architecture
- [x] Architecture Guardrails
- [x] Git & Collaboration Workflow
- [x] Frontend Development Workflow
- [x] Frontend Agent Skills
- [x] Testing Strategy
- [x] Project Tracking

## Currently Working On

- [ ] Backend implementation
- [ ] Frontend implementation

## Development Strategy

SkillBridge development is split into two parallel tracks.

### Backend Track

**Owner:** Backend developer

```text
Project Setup
    ↓
Database / Flyway
    ↓
Spring Boot Foundation
    ↓
Authentication
    ↓
Student
    ↓
Company
    ↓
Matching
    ↓
Applications
    ↓
College
    ↓
Admin
Frontend Track

Owner: Frontend developer

UI Foundation
    ↓
Layout / App Shell
    ↓
Design System
    ↓
Authentication UI
    ↓
Student Foundation
    ↓
Company / College / Admin Screens
    ↓
API Integration

Both tracks work independently from the same approved project specifications.

The OpenAPI contract is the shared boundary between backend and frontend.

The frontend does not need to wait for backend implementation.

When backend endpoints are not yet available, the frontend may use isolated mock data that strictly follows the approved OpenAPI contract.

Integration Strategy

Backend and frontend will be developed independently and integrated during the hackathon.

Integration will be performed feature-by-feature rather than waiting for the entire backend or frontend to be completed.

Frontend mock implementations will be replaced with live backend API calls during integration.

Neither track should modify the other track's implementation files.

Shared project documentation and contracts remain the common source of truth.

Current Milestone

M2 — Development

Current Task

Begin parallel backend and frontend implementation.

Backend

Start with the manual / learning vertical slice and progressively implement the approved backend modules.

Frontend

Start with the UI foundation, layout, design system, authentication UI, and student foundation while consuming the approved UX specifications and OpenAPI contract.

Blockers

None.

Important Decisions
Product: SkillBridge
SIH Problem Statement: SIH26044
Primary roles: Student, Company, College, Admin
MVP = Must-Have features from approved SRS
AI is not required for core functionality
Architecture style: Modular Monolith (single Spring Boot JAR)
Authentication: Stateless JWT (24h expiry — OD-09)
Authorization: Spring Security — URL-level + @PreAuthorize ownership checks
Database: PostgreSQL with Flyway migrations
Primary key: BIGINT GENERATED ALWAYS AS IDENTITY (not UUID)
File storage: Local filesystem (MVP); resume_path stored in student_profiles; binary NOT in DB
File module: generic — no dependency on student module; student module owns resume concept
Skill matching: unweighted set intersection (MVP); weighted excluded (OD-02)
Pipeline: forward-only, no stage skipping or reversing (OD-03)
Skill gap thresholds: HIGH >= 30%, MODERATE >= 15%, LOW > 0%, SURPLUS <= 0% (OD-01)
Industry demand scope: all OPEN opportunities platform-wide (OD-07)
Resume: PDF and DOCX only, max 5 MB (OD-08)
Company feedback: visible to both Student and College; no internal flag (OD-04)
Student-college: one college per student; fixed at registration (OD-05)
Admin MVP scope: verification + deactivation + skill taxonomy CRUD + opportunity deactivation (OD-12)
Password policy: minimum 8 characters (OD-10)
Feedback: requires internship/placement COMPLETED status (OD-11)
Department normalization: departments master table referenced via department_id FK (DBQ-01)
Notifications table: deferred post-MVP (DBQ-02)
Skill count limit: validated at application level (DBQ-03)
Company feedback format: free-text only (DBQ-04)
Opportunity closing: apply-time validation in ApplicationService (DBQ-05)
Internship records: normalized 1:1 link to applications without redundant duplicate columns
Avoid unnecessary complexity
Development Constraints
Backend and frontend must strictly follow the approved project specifications.
Frontend must remain pure JavaScript; TypeScript is prohibited.
Frontend must strictly consume the approved OpenAPI endpoints and DTO shapes.
Backend must implement the approved database design and API contract.
No unapproved frameworks, libraries, architecture patterns, or product features.
Shared specification documents must not be casually modified from feature branches.
Any genuine contradiction in the specifications must be identified and escalated before changing the source of truth.
Never modify another developer's implementation branch or files unnecessarily.
Never commit secrets, credentials, environment files, or build artifacts.
Current Progress

Planning: 100%

Development: 0%

Integration: 0%

Notes

System design and development standards are complete.

The repository is now ready for parallel implementation.

The backend and frontend developers may work independently using their respective development branches.

The frontend developer may build the UI without waiting for the backend by following the approved UX specifications and OpenAPI contract.

The backend developer may implement the backend independently while preserving the approved API boundary.

Project status should be updated at meaningful development milestones rather than after every individual commit.

The final integration will combine both development tracks into the verified SkillBridge MVP.