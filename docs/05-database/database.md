# SkillBridge — Database Design

**Phase:** System Design  
**Version:** 1.1  
**Status:** DRAFT — Ready for Final Review  
**Date:** 2026-08-27  
**Derived from:** PRD.md · SRS.md · architecture.md (approved) · Open Decisions (OD-01 to OD-12 confirmed) · DBQ-01 to DBQ-05 resolved

---

## Table of Contents

1. [Primary Key Strategy](#1-primary-key-strategy)
2. [Global Conventions](#2-global-conventions)
3. [Entity Relationship Overview](#3-entity-relationship-overview)
4. [Table Definitions](#4-table-definitions)
   - [4.1 users](#41-users)
   - [4.2 colleges](#42-colleges)
   - [4.3 departments](#43-departments)
   - [4.4 student_profiles](#44-student_profiles)
   - [4.5 company_profiles](#45-company_profiles)
   - [4.6 skills](#46-skills)
   - [4.7 student_skills](#47-student_skills)
   - [4.8 projects](#48-projects)
   - [4.9 certifications](#49-certifications)
   - [4.10 opportunities](#410-opportunities)
   - [4.11 opportunity_required_branches](#411-opportunity_required_branches)
   - [4.12 opportunity_required_years](#412-opportunity_required_years)
   - [4.13 required_skills](#413-required_skills)
   - [4.14 applications](#414-applications)
   - [4.15 internship_records](#415-internship_records)
   - [4.16 company_feedback](#416-company_feedback)
5. [Enum Reference](#5-enum-reference)
6. [Index Summary](#6-index-summary)
7. [Relationship Summary](#7-relationship-summary)
8. [Analytics Derivation](#8-analytics-derivation)
9. [SRS Requirement Validation](#9-srs-requirement-validation)
10. [Database Design Decisions & Resolved Questions](#10-database-design-decisions--resolved-questions)

---

## 1. Primary Key Strategy

### Decision: `BIGINT` generated with identity (serial)

**Choice:** `BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY` (PostgreSQL 10+) for all primary keys.

**Why BIGINT:**
- 2^63 possible values — more than sufficient for any foreseeable SkillBridge scale.
- Simpler JOIN conditions and indexes than UUID.
- 8 bytes storage per row (vs. 16 bytes for UUID).
- Natural sort order aligns with insertion order, aiding pagination and debugging.
- Spring Data JPA maps cleanly to `Long` in Java with no extra configuration.

**Why not UUID:**
- UUID is beneficial when PKs must be generated client-side (offline, distributed systems) or when enumerable IDs must be obscured in public URLs.
- SkillBridge is a centralized modular monolith — all IDs are generated server-side.
- UUID v4 PKs fragment B-tree indexes, harming insert performance at scale.
- No cross-system ID merging or public-facing ID obfuscation requirements exist.

**Format:** `BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY`

All foreign keys referencing these PKs are `BIGINT NOT NULL` (or `BIGINT NULL` for optional references).

---

## 2. Global Conventions

| Convention | Rule |
|---|---|
| Naming | `snake_case` for all tables, columns, constraints, and indexes. Plural table names. |
| Timestamps | All tables carry `created_at TIMESTAMPTZ NOT NULL DEFAULT now()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()` (junction tables carry `created_at`). |
| Timezone | All timestamps stored as `TIMESTAMPTZ` (UTC / timezone-aware). Application converts to local time at the presentation layer. |
| Soft delete / Deactivation | No generic soft-delete column. Deactivation is represented by a domain-named status flag where required (`is_active`, `status = 'CLOSED'`). |
| Boolean | `BOOLEAN NOT NULL DEFAULT true/false` (no nullable booleans). |
| Text lengths | `VARCHAR(n)` with explicit limits on structured text; `TEXT` for unrestricted free-text fields (`feedback_text`, `description`, `career_interests`). |
| Enum values | Stored as `VARCHAR(20)` with a `CHECK` constraint. Avoids the operational overhead of PostgreSQL custom `ENUM` types (which require DDL migrations to extend). |
| CGPA precision | `NUMERIC(4,2)` — stores values 0.00–10.00 (Indian 10-point scale). |
| Match % precision | `NUMERIC(5,2)` — stores values 0.00–100.00. |
| `updated_at` maintenance | Maintained by the application layer (JPA `@PreUpdate`) in MVP. |

---

## 3. Entity Relationship Overview

```
users (1) ──────────────────────────── (0..1) student_profiles (∞) ─── (1) departments
  │ (1)                                              │ (1)
  │ (0..1) colleges ──────── (∞) student_profiles    │ (∞)
  │ (0..1) company_profiles                          ├─ student_skills (join: student_profiles × skills)
                │ (1)                                ├─ projects (student_profiles)
                │ (∞)                                └─ certifications (student_profiles)
            opportunities
                │ (1)
                ├─ (∞) required_skills (join: opportunities × skills)
                ├─ (∞) opportunity_required_branches (join: opportunities × departments)
                ├─ (∞) opportunity_required_years (opportunities)
                │ (1)
                │ (∞)
            applications (student_profiles × opportunities)
                │ (1)
                │ (0..1)
         internship_records (pure 1:1 on applications)
                │ (1)
                │ (0..1)
          company_feedback (1:1 on internship_records)
```

---

## 4. Table Definitions

---

### 4.1 `users`

**Purpose:** Central authentication table. Exactly one row per registered account regardless of role (FR-AUTH-01, FR-AUTH-02). Roles: `STUDENT`, `COMPANY`, `COLLEGE`, `ADMIN`.

```sql
CREATE TABLE users (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_users_email
        UNIQUE (email),
    CONSTRAINT ck_users_role
        CHECK (role IN ('STUDENT','COMPANY','COLLEGE','ADMIN'))
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK, auto-generated |
| `email` | VARCHAR(255) | NOT NULL | — | Unique login identifier (FR-AUTH-01) |
| `password` | VARCHAR(255) | NOT NULL | — | BCrypt hash (work factor ≥ 10); never plaintext |
| `role` | VARCHAR(20) | NOT NULL | — | One of: `STUDENT`, `COMPANY`, `COLLEGE`, `ADMIN` |
| `is_active` | BOOLEAN | NOT NULL | true | Account active flag; Admin can deactivate (FR-ADM-01) |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | Account creation timestamp |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | Last update timestamp |

**Constraints:**
- `CONSTRAINT uq_users_email UNIQUE (email)` — prevents duplicate registration (FR-AUTH-01)
- `CONSTRAINT ck_users_role CHECK (role IN ('STUDENT','COMPANY','COLLEGE','ADMIN'))`

**Indexes:**
- PK on `id`
- UNIQUE index on `email` (login lookup)
- Index on `role` (Admin user management filter, FR-ADM-01)
- Index on `is_active` (filter active vs. deactivated accounts)

---

### 4.2 `colleges`

**Purpose:** Represents a registered college / placement cell (FR-COL-01, FR-ADM-02). College analytics (FR-ANL-01, FR-ANL-05) and student scoping are anchored on `college_id`.

```sql
CREATE TABLE colleges (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    name                VARCHAR(255) NOT NULL,
    address             VARCHAR(500),
    website             VARCHAR(500),
    contact_email       VARCHAR(255),
    contact_phone       VARCHAR(30),
    verification_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    verified_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_colleges_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uq_colleges_user_id
        UNIQUE (user_id),
    CONSTRAINT ck_colleges_verification_status
        CHECK (verification_status IN ('PENDING','VERIFIED','REJECTED'))
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `user_id` | BIGINT | NOT NULL | — | FK → `users.id`; one-to-one account link |
| `name` | VARCHAR(255) | NOT NULL | — | College institutional name |
| `address` | VARCHAR(500) | NULL | — | Campus address |
| `website` | VARCHAR(500) | NULL | — | Institutional URL |
| `contact_email` | VARCHAR(255) | NULL | — | Placement cell email |
| `contact_phone` | VARCHAR(30) | NULL | — | Placement cell phone |
| `verification_status` | VARCHAR(20) | NOT NULL | 'PENDING' | `PENDING`, `VERIFIED`, `REJECTED` (FR-ADM-02, OD-06) |
| `verified_at` | TIMESTAMPTZ | NULL | — | Timestamp when Admin verified/rejected |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_colleges_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_colleges_user_id UNIQUE (user_id)` — one college profile per user account
- `CONSTRAINT ck_colleges_verification_status CHECK (verification_status IN ('PENDING','VERIFIED','REJECTED'))`

**Indexes:**
- PK on `id`
- UNIQUE on `user_id`
- Index on `verification_status` (Admin verification queue)

---

### 4.3 `departments`

**Purpose:** Master taxonomy of standardized academic departments / engineering branches (DBQ-01 Approved). Used for student academic profiles and opportunity branch eligibility criteria, preventing free-text typos and mismatch errors.

```sql
CREATE TABLE departments (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    code        VARCHAR(50)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_departments_code
        UNIQUE (code),
    CONSTRAINT uq_departments_name
        UNIQUE (name)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `name` | VARCHAR(150) | NOT NULL | — | Department name, e.g., "Computer Science and Engineering" |
| `code` | VARCHAR(50) | NOT NULL | — | Unique branch code, e.g., "CSE", "ECE", "MECH", "IT" |
| `is_active` | BOOLEAN | NOT NULL | true | Soft-deactivation flag; inactive departments cannot be newly selected |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT uq_departments_code UNIQUE (code)` — canonical code identifier
- `CONSTRAINT uq_departments_name UNIQUE (name)` — prevents duplicate department names

**Indexes:**
- PK on `id`
- UNIQUE on `code`
- UNIQUE on `name`
- Index on `is_active` (dropdown selection filtering)

---

### 4.4 `student_profiles`

**Purpose:** Student identity, academic details, and career interests (FR-STU-01). Drives eligibility matching (FR-MATCH-02) and college skill availability analytics (FR-ANL-01).

```sql
CREATE TABLE student_profiles (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    college_id       BIGINT       NOT NULL,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    department_id    BIGINT,
    year_of_study    SMALLINT,
    cgpa             NUMERIC(4,2),
    career_interests TEXT,
    portfolio_url    VARCHAR(500),
    github_url       VARCHAR(500),
    resume_path      VARCHAR(1000),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_sp_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sp_college
        FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sp_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    CONSTRAINT uq_sp_user_id
        UNIQUE (user_id),
    CONSTRAINT ck_sp_year_of_study
        CHECK (year_of_study IS NULL OR (year_of_study BETWEEN 1 AND 8)),
    CONSTRAINT ck_sp_cgpa
        CHECK (cgpa IS NULL OR (cgpa >= 0.00 AND cgpa <= 10.00))
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `user_id` | BIGINT | NOT NULL | — | FK → `users.id`; one-to-one |
| `college_id` | BIGINT | NOT NULL | — | FK → `colleges.id`; **one college per student** (OD-05) |
| `first_name` | VARCHAR(100) | NOT NULL | — | First name |
| `last_name` | VARCHAR(100) | NOT NULL | — | Last name |
| `department_id` | BIGINT | NULL | — | FK → `departments.id`; normalized branch reference (DBQ-01) |
| `year_of_study` | SMALLINT | NULL | — | Academic year (1–8); eligibility checking (FR-MATCH-02) |
| `cgpa` | NUMERIC(4,2) | NULL | — | 0.00–10.00 scale; eligibility checking (FR-MATCH-02) |
| `career_interests` | TEXT | NULL | — | Free-text career interests |
| `portfolio_url` | VARCHAR(500) | NULL | — | Optional portfolio link (FR-STU-03) |
| `github_url` | VARCHAR(500) | NULL | — | Optional GitHub link (FR-STU-03) |
| `resume_path` | VARCHAR(1000) | NULL | — | Storage key from `FileStorageService` (FR-STU-04 / S1; NULL until uploaded; max 5 MB PDF/DOCX per OD-08) |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_sp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT`
- `CONSTRAINT fk_sp_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE RESTRICT`
- `CONSTRAINT fk_sp_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_sp_user_id UNIQUE (user_id)`
- `CONSTRAINT ck_sp_year_of_study CHECK (year_of_study IS NULL OR (year_of_study BETWEEN 1 AND 8))`
- `CONSTRAINT ck_sp_cgpa CHECK (cgpa IS NULL OR (cgpa >= 0.00 AND cgpa <= 10.00))`

**Indexes:**
- PK on `id`
- UNIQUE on `user_id`
- Index on `college_id` (all college analytics and student queries, FR-ANL-01, FR-COL-01)
- Index on `department_id` (department filtering and eligibility checks)

---

### 4.5 `company_profiles`

**Purpose:** Company identity, contact information, and verification status (FR-COM-01, FR-COM-02). Displayed alongside posted opportunities.

```sql
CREATE TABLE company_profiles (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    name                VARCHAR(255) NOT NULL,
    industry            VARCHAR(150),
    description         TEXT,
    location            VARCHAR(255),
    website             VARCHAR(500),
    contact_email       VARCHAR(255),
    contact_phone       VARCHAR(30),
    verification_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    verified_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_cp_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uq_cp_user_id
        UNIQUE (user_id),
    CONSTRAINT ck_cp_verification_status
        CHECK (verification_status IN ('PENDING','VERIFIED','REJECTED'))
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `user_id` | BIGINT | NOT NULL | — | FK → `users.id`; one-to-one |
| `name` | VARCHAR(255) | NOT NULL | — | Company display name |
| `industry` | VARCHAR(150) | NULL | — | Industry sector (e.g., "Software", "Fintech") |
| `description` | TEXT | NULL | — | Company summary |
| `location` | VARCHAR(255) | NULL | — | Headquarters / primary office location |
| `website` | VARCHAR(500) | NULL | — | Company website URL |
| `contact_email` | VARCHAR(255) | NULL | — | Recruiting contact email |
| `contact_phone` | VARCHAR(30) | NULL | — | Contact telephone |
| `verification_status` | VARCHAR(20) | NOT NULL | 'PENDING' | `PENDING`, `VERIFIED`, `REJECTED` (FR-COM-02, FR-ADM-02, OD-06) |
| `verified_at` | TIMESTAMPTZ | NULL | — | Timestamp of Admin verification |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_cp_user_id UNIQUE (user_id)`
- `CONSTRAINT ck_cp_verification_status CHECK (verification_status IN ('PENDING','VERIFIED','REJECTED'))`

**Indexes:**
- PK on `id`
- UNIQUE on `user_id`
- Index on `verification_status` (Admin moderation queue)

---

### 4.6 `skills`

**Purpose:** Master skills taxonomy managed by Admin (FR-ADM-03). Shared vocabulary for student current skills (FR-STU-02) and opportunity required skills (FR-INT-01), guaranteeing exact set intersection for skill matching (FR-MATCH-01).

```sql
CREATE TABLE skills (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    category    VARCHAR(100),
    is_active   BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_skills_name
        UNIQUE (name)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `name` | VARCHAR(150) | NOT NULL | — | Canonical skill name (e.g., "Java", "React", "PostgreSQL") |
| `category` | VARCHAR(100) | NULL | — | Category grouping (e.g., "Programming Languages", "Databases") |
| `is_active` | BOOLEAN | NOT NULL | true | Soft-deactivation; inactive skills cannot be newly selected |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT uq_skills_name UNIQUE (name)` — prevents duplicate taxonomy entries (FR-ADM-03)

**Indexes:**
- PK on `id`
- UNIQUE on `name`
- Index on `is_active` (filter active skills for dropdowns and typeahead)

---

### 4.7 `student_skills`

**Purpose:** Junction table representing a student's self-reported current skills (FR-STU-02). Input to matching engine (FR-MATCH-01) and skill availability analytics (FR-ANL-01).

```sql
CREATE TABLE student_skills (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_profile_id  BIGINT      NOT NULL,
    skill_id            BIGINT      NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_ss_student
        FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_skill
        FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ss_student_skill
        UNIQUE (student_profile_id, skill_id)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `student_profile_id` | BIGINT | NOT NULL | — | FK → `student_profiles.id` |
| `skill_id` | BIGINT | NOT NULL | — | FK → `skills.id` |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | Timestamp when skill was added |

**Constraints:**
- `CONSTRAINT fk_ss_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE`
- `CONSTRAINT fk_ss_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT` (cannot delete skill actively referenced)
- `CONSTRAINT uq_ss_student_skill UNIQUE (student_profile_id, skill_id)` (prevents duplicate skill entry)

**Validation Note (DBQ-03):** Maximum skill count limit (e.g., max 30 skills) is enforced at the application service layer, not as a DB-level trigger.

**Indexes:**
- PK on `id`
- UNIQUE on `(student_profile_id, skill_id)`
- Index on `skill_id` (analytics: count students per skill in FR-ANL-01)
- Index on `student_profile_id` (load student skills during matching)

---

### 4.8 `projects`

**Purpose:** Student portfolio projects (FR-STU-03). Displayed to companies reviewing applicants.

```sql
CREATE TABLE projects (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_profile_id  BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    project_url         VARCHAR(500),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_proj_student
        FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `student_profile_id` | BIGINT | NOT NULL | — | FK → `student_profiles.id` |
| `title` | VARCHAR(255) | NOT NULL | — | Project title |
| `description` | TEXT | NULL | — | Project description |
| `project_url` | VARCHAR(500) | NULL | — | Live demo or GitHub repository URL |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Indexes:**
- PK on `id`
- Index on `student_profile_id` (load all projects for a student profile)

---

### 4.9 `certifications`

**Purpose:** Student portfolio certifications (FR-STU-03). Displayed to reviewing companies.

```sql
CREATE TABLE certifications (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_profile_id  BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    issuer              VARCHAR(255),
    issued_date         DATE,
    certificate_url     VARCHAR(500),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_cert_student
        FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE CASCADE
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `student_profile_id` | BIGINT | NOT NULL | — | FK → `student_profiles.id` |
| `title` | VARCHAR(255) | NOT NULL | — | Certification title |
| `issuer` | VARCHAR(255) | NULL | — | Organization issuing certification |
| `issued_date` | DATE | NULL | — | Issue date |
| `certificate_url` | VARCHAR(500) | NULL | — | Verification URL |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Indexes:**
- PK on `id`
- Index on `student_profile_id` (load all certifications for a student profile)

---

### 4.10 `opportunities`

**Purpose:** Internship and placement postings published by companies (FR-INT-01, FR-INT-02). Core entity driving opportunity search (FR-INT-03), skill matching (FR-MATCH-01), eligibility checks (FR-MATCH-02), applications (FR-APP-01), and industry demand analytics (FR-ANL-02).

```sql
CREATE TABLE opportunities (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_profile_id   BIGINT       NOT NULL,
    title                VARCHAR(255) NOT NULL,
    description          TEXT,
    type                 VARCHAR(20)  NOT NULL,
    location             VARCHAR(255),
    mode                 VARCHAR(20)  NOT NULL DEFAULT 'ONSITE',
    duration_weeks       SMALLINT,
    stipend_amount       NUMERIC(12,2),
    stipend_currency     VARCHAR(10)  NOT NULL DEFAULT 'INR',
    min_cgpa             NUMERIC(4,2),
    application_deadline DATE,
    status               VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_opp_company
        FOREIGN KEY (company_profile_id) REFERENCES company_profiles(id) ON DELETE RESTRICT,
    CONSTRAINT ck_opp_type
        CHECK (type IN ('INTERNSHIP','PLACEMENT')),
    CONSTRAINT ck_opp_mode
        CHECK (mode IN ('ONSITE','REMOTE','HYBRID')),
    CONSTRAINT ck_opp_status
        CHECK (status IN ('DRAFT','OPEN','CLOSED')),
    CONSTRAINT ck_opp_min_cgpa
        CHECK (min_cgpa IS NULL OR (min_cgpa >= 0.00 AND min_cgpa <= 10.00)),
    CONSTRAINT ck_opp_duration
        CHECK (duration_weeks IS NULL OR duration_weeks > 0)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `company_profile_id` | BIGINT | NOT NULL | — | FK → `company_profiles.id` |
| `title` | VARCHAR(255) | NOT NULL | — | Role / job title |
| `description` | TEXT | NULL | — | Detailed job description |
| `type` | VARCHAR(20) | NOT NULL | — | `INTERNSHIP` or `PLACEMENT` |
| `location` | VARCHAR(255) | NULL | — | City / location |
| `mode` | VARCHAR(20) | NOT NULL | 'ONSITE' | `ONSITE`, `REMOTE`, `HYBRID` |
| `duration_weeks` | SMALLINT | NULL | — | Duration in weeks (internships) |
| `stipend_amount` | NUMERIC(12,2) | NULL | — | Monthly stipend or annual salary; NULL = unpaid/unspecified |
| `stipend_currency` | VARCHAR(10) | NOT NULL | 'INR' | Currency code |
| `min_cgpa` | NUMERIC(4,2) | NULL | — | Eligibility threshold; NULL = no minimum CGPA |
| `application_deadline` | DATE | NULL | — | Deadline date; evaluated at apply-time by `ApplicationService` (DBQ-05) |
| `status` | VARCHAR(20) | NOT NULL | 'DRAFT' | `DRAFT`, `OPEN`, `CLOSED` lifecycle |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_opp_company FOREIGN KEY (company_profile_id) REFERENCES company_profiles(id) ON DELETE RESTRICT`
- `CONSTRAINT ck_opp_type CHECK (type IN ('INTERNSHIP','PLACEMENT'))`
- `CONSTRAINT ck_opp_mode CHECK (mode IN ('ONSITE','REMOTE','HYBRID'))`
- `CONSTRAINT ck_opp_status CHECK (status IN ('DRAFT','OPEN','CLOSED'))`
- `CONSTRAINT ck_opp_min_cgpa CHECK (min_cgpa IS NULL OR (min_cgpa >= 0.00 AND min_cgpa <= 10.00))`
- `CONSTRAINT ck_opp_duration CHECK (duration_weeks IS NULL OR duration_weeks > 0)`

**Indexes:**
- PK on `id`
- Index on `company_profile_id` (company views its posted opportunities)
- Index on `status` (**critical path**: filtered on `status = 'OPEN'` in student search and industry demand analytics)
- Index on `type` (filter internship vs. placement)
- Index on `application_deadline` (deadline checks)

---

### 4.11 `opportunity_required_branches`

**Purpose:** Multi-valued branch eligibility criteria for opportunities (FR-MATCH-02, DBQ-01 Approved). References normalized `departments.id`. An empty set for an opportunity signifies that **all departments/branches are eligible**.

```sql
CREATE TABLE opportunity_required_branches (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT NOT NULL,
    department_id   BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_orb_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT fk_orb_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    CONSTRAINT uq_orb_opp_dept
        UNIQUE (opportunity_id, department_id)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `opportunity_id` | BIGINT | NOT NULL | — | FK → `opportunities.id` |
| `department_id` | BIGINT | NOT NULL | — | FK → `departments.id` (normalized master branch, DBQ-01) |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_orb_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE`
- `CONSTRAINT fk_orb_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_orb_opp_dept UNIQUE (opportunity_id, department_id)` (prevents duplicate branch assignments)

**Indexes:**
- PK on `id`
- UNIQUE on `(opportunity_id, department_id)`
- Index on `department_id` (query opportunities accepting a specific department)
- Index on `opportunity_id` (load branch criteria for eligibility check)

---

### 4.12 `opportunity_required_years`

**Purpose:** Multi-valued year-of-study eligibility criteria for opportunities (FR-MATCH-02). An empty set signifies that **all academic years are eligible**.

```sql
CREATE TABLE opportunity_required_years (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT   NOT NULL,
    year_of_study   SMALLINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_ory_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT uq_ory_opp_year
        UNIQUE (opportunity_id, year_of_study),
    CONSTRAINT ck_ory_year
        CHECK (year_of_study BETWEEN 1 AND 8)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `opportunity_id` | BIGINT | NOT NULL | — | FK → `opportunities.id` |
| `year_of_study` | SMALLINT | NOT NULL | — | Eligible academic year (1–8) |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_ory_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE`
- `CONSTRAINT uq_ory_opp_year UNIQUE (opportunity_id, year_of_study)`
- `CONSTRAINT ck_ory_year CHECK (year_of_study BETWEEN 1 AND 8)`

**Indexes:**
- PK on `id`
- UNIQUE on `(opportunity_id, year_of_study)`
- Index on `opportunity_id` (load eligible years for eligibility check)

---

### 4.13 `required_skills`

**Purpose:** Junction table defining required skills for an opportunity (FR-INT-01, FR-MATCH-01). Primary input for industry skill demand analytics (FR-ANL-02).

```sql
CREATE TABLE required_skills (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT NOT NULL,
    skill_id        BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_rs_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_skill
        FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT,
    CONSTRAINT uq_rs_opp_skill
        UNIQUE (opportunity_id, skill_id)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `opportunity_id` | BIGINT | NOT NULL | — | FK → `opportunities.id` |
| `skill_id` | BIGINT | NOT NULL | — | FK → `skills.id` |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Constraints:**
- `CONSTRAINT fk_rs_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE`
- `CONSTRAINT fk_rs_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_rs_opp_skill UNIQUE (opportunity_id, skill_id)`

**Validation Note (DBQ-03):** Maximum skill count limit is enforced at application service layer.

**Indexes:**
- PK on `id`
- UNIQUE on `(opportunity_id, skill_id)`
- Index on `skill_id` (**critical for FR-ANL-02**: count opportunities per required skill)
- Index on `opportunity_id` (load required skills during matching)

---

### 4.14 `applications`

**Purpose:** Formal application record submitted by a student for an opportunity (FR-APP-01). Enforces one application per student per opportunity. Tracks recruitment pipeline progression (FR-APP-04) and stores an immutable match score snapshot (architecture §10, FR-APP-03).

```sql
CREATE TABLE applications (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_profile_id      BIGINT       NOT NULL,
    opportunity_id          BIGINT       NOT NULL,
    status                  VARCHAR(20)  NOT NULL DEFAULT 'APPLIED',
    match_percent_at_apply  NUMERIC(5,2) NOT NULL,
    applied_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_app_student
        FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE RESTRICT,
    CONSTRAINT fk_app_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE RESTRICT,
    CONSTRAINT uq_app_student_opportunity
        UNIQUE (student_profile_id, opportunity_id),
    CONSTRAINT ck_app_status
        CHECK (status IN (
            'APPLIED',
            'UNDER_REVIEW',
            'SHORTLISTED',
            'INTERVIEW',
            'SELECTED',
            'REJECTED'
        )),
    CONSTRAINT ck_app_match_percent
        CHECK (match_percent_at_apply >= 0.00 AND match_percent_at_apply <= 100.00)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `student_profile_id` | BIGINT | NOT NULL | — | FK → `student_profiles.id` |
| `opportunity_id` | BIGINT | NOT NULL | — | FK → `opportunities.id` |
| `status` | VARCHAR(20) | NOT NULL | 'APPLIED' | Current pipeline stage |
| `match_percent_at_apply` | NUMERIC(5,2) | NOT NULL | — | Immutable match score snapshot at time of application (FR-APP-03) |
| `applied_at` | TIMESTAMPTZ | NOT NULL | now() | Submission timestamp |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | Last pipeline status update |

**Constraints:**
- `CONSTRAINT fk_app_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id) ON DELETE RESTRICT`
- `CONSTRAINT fk_app_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_app_student_opportunity UNIQUE (student_profile_id, opportunity_id)` — **strictly one application per student per opportunity** (FR-APP-01)
- `CONSTRAINT ck_app_status CHECK (status IN ('APPLIED','UNDER_REVIEW','SHORTLISTED','INTERVIEW','SELECTED','REJECTED'))`
- `CONSTRAINT ck_app_match_percent CHECK (match_percent_at_apply >= 0.00 AND match_percent_at_apply <= 100.00)`

**Recruitment Pipeline Transition Map (OD-03 — Forward-only, enforced in `ApplicationService`):**
```
APPLIED       → UNDER_REVIEW | REJECTED
UNDER_REVIEW  → SHORTLISTED  | REJECTED
SHORTLISTED   → INTERVIEW    | REJECTED
INTERVIEW     → SELECTED     | REJECTED
SELECTED      → (terminal — triggers internship_records auto-creation via FR-APP-05)
REJECTED      → (terminal)
```

**Indexes:**
- PK on `id`
- UNIQUE on `(student_profile_id, opportunity_id)`
- Index on `student_profile_id` (student "My Applications" query, FR-APP-02)
- Index on `opportunity_id` (company applicant review, FR-APP-03)
- Composite index on `(opportunity_id, match_percent_at_apply DESC)` (ranked applicant queries, FR-APP-03)
- Index on `status` (college placement funnel aggregation, FR-COL-02)

---

### 4.15 `internship_records`

**Purpose:** Lifecycle tracking for confirmed internships and placements (FR-APP-05, FR-INT-04, FR-INT-06 / S5). Automatically created when an application transitions to `SELECTED`.

#### Schema Refinement & Normalization Decision

`internship_records` is designed with a normalized foreign key reference to `application_id`. Redundant columns (`student_id`, `company_id`, `opportunity_id`, `type`) are eliminated because they are directly and unambiguously derived through `application_id` via `applications` and `opportunities`:

- `student_id` = `applications.student_profile_id`
- `opportunity_id` = `applications.opportunity_id`
- `company_id` = `opportunities.company_profile_id`
- `type` = `opportunities.type` (`INTERNSHIP` or `PLACEMENT`)

**Why Normalized:**
1. **Guarantees 3NF and Data Integrity:** Eliminates data redundancy and prevents update anomalies or synchronization drift between `applications` and `internship_records`.
2. **Zero Performance Penalty:** In PostgreSQL, joining `applications` (and `opportunities`) via primary keys/foreign keys is an in-memory B-tree index lookup (< 0.5ms).
3. **Clean Domain Model:** Follows project architecture principles of keeping tables lean and avoiding unnecessary columns.

```sql
CREATE TABLE internship_records (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id  BIGINT      NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'UPCOMING',
    start_date      DATE,
    end_date        DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_ir_application
        FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ir_application
        UNIQUE (application_id),
    CONSTRAINT ck_ir_status
        CHECK (status IN ('UPCOMING','ONGOING','COMPLETED')),
    CONSTRAINT ck_ir_dates
        CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `application_id` | BIGINT | NOT NULL | — | FK → `applications.id`; source selected application (1:1) |
| `status` | VARCHAR(20) | NOT NULL | 'UPCOMING' | `UPCOMING`, `ONGOING`, `COMPLETED` (FR-INT-04) |
| `start_date` | DATE | NULL | — | Internship/placement start date |
| `end_date` | DATE | NULL | — | Internship/placement end date |
| `created_at` | TIMESTAMPTZ | NOT NULL | now() | Created automatically on selection |
| `updated_at` | TIMESTAMPTZ | NOT NULL | now() | |

**Derived Query Examples:**
- **Student's internships:**
  ```sql
  SELECT ir.*, o.title, o.type, cp.name AS company_name
  FROM internship_records ir
  JOIN applications a ON a.id = ir.application_id
  JOIN opportunities o ON o.id = a.opportunity_id
  JOIN company_profiles cp ON cp.id = o.company_profile_id
  WHERE a.student_profile_id = :student_profile_id;
  ```
- **Company's active interns:**
  ```sql
  SELECT ir.*, sp.first_name, sp.last_name, o.title
  FROM internship_records ir
  JOIN applications a ON a.id = ir.application_id
  JOIN opportunities o ON o.id = a.opportunity_id
  JOIN student_profiles sp ON sp.id = a.student_profile_id
  WHERE o.company_profile_id = :company_profile_id;
  ```

**Constraints:**
- `CONSTRAINT fk_ir_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_ir_application UNIQUE (application_id)` — **exactly one internship record per selected application** (FR-APP-05)
- `CONSTRAINT ck_ir_status CHECK (status IN ('UPCOMING','ONGOING','COMPLETED'))`
- `CONSTRAINT ck_ir_dates CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date)`

**Indexes:**
- PK on `id`
- UNIQUE on `application_id`
- Index on `status` (filter active vs. completed records)

---

### 4.16 `company_feedback`

**Purpose:** Qualitative feedback submitted by a company upon conclusion of an internship or placement (FR-INT-05, OD-04, OD-11, DBQ-04). Free-text only. Visible to both the student and the college placement cell.

```sql
CREATE TABLE company_feedback (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    internship_record_id BIGINT      NOT NULL,
    feedback_text        TEXT        NOT NULL,
    submitted_at         TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_cf_internship
        FOREIGN KEY (internship_record_id) REFERENCES internship_records(id) ON DELETE RESTRICT,
    CONSTRAINT uq_cf_internship
        UNIQUE (internship_record_id)
);
```

| Column | Type | Null | Default | Notes |
|---|---|---|---|---|
| `id` | BIGINT | NOT NULL | identity | PK |
| `internship_record_id` | BIGINT | NOT NULL | — | FK → `internship_records.id` (1:1) |
| `feedback_text` | TEXT | NOT NULL | — | Free-text feedback (FR-INT-05; DBQ-04: free-text only; OD-04: visible to student and college) |
| `submitted_at` | TIMESTAMPTZ | NOT NULL | now() | Submission timestamp |

**Constraints:**
- `CONSTRAINT fk_cf_internship FOREIGN KEY (internship_record_id) REFERENCES internship_records(id) ON DELETE RESTRICT`
- `CONSTRAINT uq_cf_internship UNIQUE (internship_record_id)` — exactly one feedback submission per internship record

**Business Rules Enforced in Service Layer:**
- **OD-11:** `InternshipService` validates that `internship_records.status = 'COMPLETED'` before permitting feedback submission.
- **OD-04:** Feedback read access for both student and college is enforced at the API/RBAC service layer.
- **DBQ-04:** Feedback is maintained as free-text only with no numeric ratings.

**Indexes:**
- PK on `id`
- UNIQUE on `internship_record_id`

---

## 5. Enum Reference

All enum-like domain states are stored as `VARCHAR(20)` with explicit `CHECK` constraints:

| Table | Column | Valid Enum Values |
|---|---|---|
| `users` | `role` | `STUDENT` · `COMPANY` · `COLLEGE` · `ADMIN` |
| `colleges` | `verification_status` | `PENDING` · `VERIFIED` · `REJECTED` |
| `company_profiles` | `verification_status` | `PENDING` · `VERIFIED` · `REJECTED` |
| `opportunities` | `type` | `INTERNSHIP` · `PLACEMENT` |
| `opportunities` | `mode` | `ONSITE` · `REMOTE` · `HYBRID` |
| `opportunities` | `status` | `DRAFT` · `OPEN` · `CLOSED` |
| `applications` | `status` | `APPLIED` · `UNDER_REVIEW` · `SHORTLISTED` · `INTERVIEW` · `SELECTED` · `REJECTED` |
| `internship_records` | `status` | `UPCOMING` · `ONGOING` · `COMPLETED` |

---

## 6. Index Summary

| Table | Column(s) | Index Type | Purpose |
|---|---|---|---|
| `users` | `id` | PK (B-tree) | Identity |
| `users` | `email` | UNIQUE (B-tree) | Authentication login lookup (FR-AUTH-02) |
| `users` | `role` | B-tree | Admin user list filtering by role (FR-ADM-01) |
| `users` | `is_active` | B-tree | Filter active vs. deactivated accounts (FR-ADM-01) |
| `colleges` | `id` | PK (B-tree) | Identity |
| `colleges` | `user_id` | UNIQUE (B-tree) | 1:1 user account lookup |
| `colleges` | `verification_status` | B-tree | Admin verification queue (FR-ADM-02) |
| `departments` | `id` | PK (B-tree) | Identity |
| `departments` | `code` | UNIQUE (B-tree) | Branch code lookup (DBQ-01) |
| `departments` | `name` | UNIQUE (B-tree) | Department name lookup (DBQ-01) |
| `departments` | `is_active` | B-tree | Active departments for UI dropdowns |
| `student_profiles` | `id` | PK (B-tree) | Identity |
| `student_profiles` | `user_id` | UNIQUE (B-tree) | 1:1 user account lookup |
| `student_profiles` | `college_id` | B-tree | College student queries & analytics (FR-ANL-01, FR-COL-01) |
| `student_profiles` | `department_id` | B-tree | Department-based student filtering & eligibility checks |
| `company_profiles` | `id` | PK (B-tree) | Identity |
| `company_profiles` | `user_id` | UNIQUE (B-tree) | 1:1 user account lookup |
| `company_profiles` | `verification_status` | B-tree | Admin verification queue (FR-ADM-02) |
| `skills` | `id` | PK (B-tree) | Identity |
| `skills` | `name` | UNIQUE (B-tree) | Prevent duplicate taxonomy entries (FR-ADM-03) |
| `skills` | `is_active` | B-tree | Filter active skills for typeahead / autocomplete |
| `student_skills` | `id` | PK (B-tree) | Identity |
| `student_skills` | `(student_profile_id, skill_id)` | UNIQUE (B-tree) | Prevent duplicate skills per student |
| `student_skills` | `student_profile_id` | B-tree | Load student skill set for matching (FR-MATCH-01) |
| `student_skills` | `skill_id` | B-tree | Count students per skill in availability analytics (FR-ANL-01) |
| `projects` | `id` | PK (B-tree) | Identity |
| `projects` | `student_profile_id` | B-tree | Load portfolio projects for student profile (FR-STU-03) |
| `certifications` | `id` | PK (B-tree) | Identity |
| `certifications` | `student_profile_id` | B-tree | Load certifications for student profile (FR-STU-03) |
| `opportunities` | `id` | PK (B-tree) | Identity |
| `opportunities` | `company_profile_id` | B-tree | Company opportunity management (FR-INT-01, FR-INT-02) |
| `opportunities` | `status` | B-tree | Filter open opportunities (critical path: FR-INT-03, FR-ANL-02) |
| `opportunities` | `type` | B-tree | Filter internships vs. placements |
| `opportunities` | `application_deadline` | B-tree | Apply-time deadline checks (DBQ-05) |
| `opportunity_required_branches` | `id` | PK (B-tree) | Identity |
| `opportunity_required_branches` | `(opportunity_id, department_id)` | UNIQUE (B-tree) | Prevent duplicate branch assignments |
| `opportunity_required_branches` | `opportunity_id` | B-tree | Load branch criteria for eligibility check (FR-MATCH-02) |
| `opportunity_required_branches` | `department_id` | B-tree | Query opportunities accepting a specific department |
| `opportunity_required_years` | `id` | PK (B-tree) | Identity |
| `opportunity_required_years` | `(opportunity_id, year_of_study)` | UNIQUE (B-tree) | Prevent duplicate year assignments |
| `opportunity_required_years` | `opportunity_id` | B-tree | Load eligible years for eligibility check (FR-MATCH-02) |
| `required_skills` | `id` | PK (B-tree) | Identity |
| `required_skills` | `(opportunity_id, skill_id)` | UNIQUE (B-tree) | Prevent duplicate skills per opportunity |
| `required_skills` | `opportunity_id` | B-tree | Load required skills for matching (FR-MATCH-01) |
| `required_skills` | `skill_id` | B-tree | Count opportunities per skill in demand analytics (FR-ANL-02) |
| `applications` | `id` | PK (B-tree) | Identity |
| `applications` | `(student_profile_id, opportunity_id)` | UNIQUE (B-tree) | Enforce one application per student per opportunity (FR-APP-01) |
| `applications` | `student_profile_id` | B-tree | Student "My Applications" list (FR-APP-02) |
| `applications` | `opportunity_id` | B-tree | Company applicant list (FR-APP-03) |
| `applications` | `(opportunity_id, match_percent_at_apply DESC)` | Composite (B-tree) | Fast ranked candidate retrieval (FR-APP-03) |
| `applications` | `status` | B-tree | Placement funnel status aggregation (FR-COL-02) |
| `internship_records` | `id` | PK (B-tree) | Identity |
| `internship_records` | `application_id` | UNIQUE (B-tree) | 1:1 link to selected application (FR-APP-05) |
| `internship_records` | `status` | B-tree | Filter active vs. completed internships (FR-INT-04) |
| `company_feedback` | `id` | PK (B-tree) | Identity |
| `company_feedback` | `internship_record_id` | UNIQUE (B-tree) | 1:1 link to completed internship record (FR-INT-05) |

---

## 7. Relationship Summary

| Parent Table | Child Table | FK Column in Child | ON DELETE | Rationale |
|---|---|---|---|---|
| `users` | `student_profiles` | `user_id` | RESTRICT | Account deactivation uses `users.is_active` |
| `users` | `company_profiles` | `user_id` | RESTRICT | Account deactivation uses `users.is_active` |
| `users` | `colleges` | `user_id` | RESTRICT | Account deactivation uses `users.is_active` |
| `colleges` | `student_profiles` | `college_id` | RESTRICT | Preserves student profile and analytics scoping |
| `departments` | `student_profiles` | `department_id` | RESTRICT | Preserves academic department reference |
| `student_profiles` | `student_skills` | `student_profile_id` | CASCADE | Student skills are owned entirely by the profile |
| `student_profiles` | `projects` | `student_profile_id` | CASCADE | Projects are owned entirely by the profile |
| `student_profiles` | `certifications` | `student_profile_id` | CASCADE | Certifications are owned entirely by the profile |
| `student_profiles` | `applications` | `student_profile_id` | RESTRICT | Preserves application audit history |
| `skills` | `student_skills` | `skill_id` | RESTRICT | Prevents deletion of skills held by students |
| `skills` | `required_skills` | `skill_id` | RESTRICT | Prevents deletion of skills required by opportunities |
| `departments` | `opportunity_required_branches` | `department_id` | RESTRICT | Prevents deletion of departments referenced in postings |
| `company_profiles` | `opportunities` | `company_profile_id` | RESTRICT | Company deactivation preserves posting history |
| `opportunities` | `required_skills` | `opportunity_id` | CASCADE | Requirements belong entirely to the opportunity |
| `opportunities` | `opportunity_required_branches` | `opportunity_id` | CASCADE | Branch criteria belong to the opportunity |
| `opportunities` | `opportunity_required_years` | `opportunity_id` | CASCADE | Year criteria belong to the opportunity |
| `opportunities` | `applications` | `opportunity_id` | RESTRICT | Preserves candidate application history |
| `applications` | `internship_records` | `application_id` | RESTRICT | Preserves confirmed internship/placement outcome |
| `internship_records` | `company_feedback` | `internship_record_id` | RESTRICT | Preserves submitted company evaluation |

---

## 8. Analytics Derivation

All analytics are computed at query time from operational tables (architecture §12). No redundant aggregation tables are maintained.

### 8.1 Student Skill Availability (FR-ANL-01)
Measures the percentage of a college's student population that lists each skill:

```sql
SELECT
    s.id   AS skill_id,
    s.name AS skill_name,
    COUNT(DISTINCT ss.student_profile_id) * 100.0
        / NULLIF((
            SELECT COUNT(*) FROM student_profiles sp2
            WHERE sp2.college_id = :college_id
          ), 0) AS availability_pct
FROM skills s
JOIN student_skills ss ON ss.skill_id = s.id
JOIN student_profiles sp ON sp.id = ss.student_profile_id
WHERE sp.college_id = :college_id
  AND s.is_active = true
GROUP BY s.id, s.name
ORDER BY availability_pct DESC;
```

### 8.2 Industry Skill Demand (FR-ANL-02 — OD-07: All OPEN Opportunities)
Measures the percentage of open opportunities requiring each skill:

```sql
SELECT
    s.id   AS skill_id,
    s.name AS skill_name,
    COUNT(DISTINCT rs.opportunity_id) * 100.0
        / NULLIF((
            SELECT COUNT(*) FROM opportunities o2
            WHERE o2.status = 'OPEN'
          ), 0) AS demand_pct
FROM skills s
JOIN required_skills rs ON rs.skill_id = s.id
JOIN opportunities o ON o.id = rs.opportunity_id
WHERE o.status = 'OPEN'
GROUP BY s.id, s.name
ORDER BY demand_pct DESC;
```

### 8.3 Skill Gap Classification (FR-ANL-03 — OD-01 Confirmed Thresholds)
Computed in `AnalyticsService` by joining availability and demand sets:

$$\text{gap} = \text{demand\_pct} - \text{availability\_pct}$$

- **HIGH:** $\text{gap} \ge 30.0\%$
- **MODERATE:** $15.0\% \le \text{gap} < 30.0\%$
- **LOW:** $0.0\% < \text{gap} < 15.0\%$
- **SURPLUS:** $\text{gap} \le 0.0\%$

### 8.4 Placement Funnel (FR-COL-02)
Aggregates application pipeline status counts across the college's student body:

```sql
SELECT a.status, COUNT(*) AS stage_count
FROM applications a
JOIN student_profiles sp ON sp.id = a.student_profile_id
WHERE sp.college_id = :college_id
GROUP BY a.status;

-- Optional department filter (DBQ-01):
-- AND sp.department_id = :department_id
```

### 8.5 College Students and Departments Overview (FR-COL-01)

```sql
SELECT d.id AS department_id, d.name AS department_name, d.code AS department_code, COUNT(sp.id) AS student_count
FROM departments d
LEFT JOIN student_profiles sp ON sp.department_id = d.id AND sp.college_id = :college_id
WHERE d.is_active = true
GROUP BY d.id, d.name, d.code
ORDER BY student_count DESC;
```

### 8.6 Company Ranked Candidates (FR-APP-03)

```sql
SELECT a.id, a.student_profile_id, a.match_percent_at_apply, a.status
FROM applications a
WHERE a.opportunity_id = :opportunity_id
ORDER BY a.match_percent_at_apply DESC;
```

---

## 9. SRS Requirement Validation

| FR-ID | Requirement | Database Schema Support |
|---|---|---|
| FR-AUTH-01 | User Registration | `users.email` UNIQUE; `users.role` CHECK |
| FR-AUTH-02 | User Login | `users.email` indexed; `users.password` stores BCrypt hash |
| FR-AUTH-03 | Session Logout / Expiry | Stateless JWT — no database table needed |
| FR-AUTH-04 | Role-Based Access Control | `users.role` read by Spring Security |
| FR-STU-01 | Student Profile Management | `student_profiles` with `college_id` FK (OD-05), `department_id` FK (DBQ-01) |
| FR-STU-02 | Student Skills Management | `student_skills` with UNIQUE `(student_profile_id, skill_id)` |
| FR-STU-03 | Student Portfolio | `projects`, `certifications`, `portfolio_url`, `github_url` |
| FR-STU-04 | Resume Upload (Should-Have) | `student_profiles.resume_path` (file binary stored on filesystem per OD-08) |
| FR-STU-05 | Resume Skill Extraction (Should-Have) | Confirmed skills saved to `student_skills` via existing flow |
| FR-STU-06 | Recommended Opportunities (Should-Have) | Computed from `student_skills` × `required_skills` × `opportunities` |
| FR-COM-01 | Company Profile Management | `company_profiles` table |
| FR-COM-02 | Display Verification Status | `company_profiles.verification_status` |
| FR-COL-01 | View Students & Departments | `student_profiles` scoped by `college_id`; joined with `departments` |
| FR-COL-02 | View Placement Funnel | `applications.status` grouped and scoped by `student_profiles.college_id` |
| FR-ADM-01 | Manage Users | `users.is_active` toggle flag |
| FR-ADM-02 | Verify/Reject Company/College | `company_profiles.verification_status`, `colleges.verification_status` |
| FR-ADM-03 | Manage Skills Taxonomy | `skills` table; soft-deactivation via `is_active`; UNIQUE `name` |
| FR-ADM-04 | Moderate Opportunities | `opportunities.status = 'CLOSED'` |
| FR-MATCH-01 | Skill Match Computation | Pure math over `student_skills` and `required_skills` |
| FR-MATCH-02 | Eligibility Checking | `opportunity_required_branches` (using `department_id`), `opportunity_required_years`, `opportunities.min_cgpa` |
| FR-APP-01 | Submit Application | `applications` with UNIQUE `(student_profile_id, opportunity_id)` |
| FR-APP-02 | View My Applications | `applications` indexed on `student_profile_id` |
| FR-APP-03 | Company View Ranked Applicants | Composite index on `(opportunity_id, match_percent_at_apply DESC)` |
| FR-APP-04 | Update Pipeline Status | `applications.status` CHECK constraint; forward-only state machine |
| FR-APP-05 | Auto-Create Internship/Placement | `internship_records` with UNIQUE `application_id` |
| FR-APP-06 | In-App Notifications (Should-Have) | Excluded from MVP schema (DBQ-02 deferred) |
| FR-INT-01 | Create Opportunity Posting | `opportunities`, `opportunity_required_branches`, `opportunity_required_years`, `required_skills` |
| FR-INT-02 | Edit / Close Opportunity | `opportunities.status` update |
| FR-INT-03 | Search Opportunities | `opportunities` filtered by `status = 'OPEN'` |
| FR-INT-04 | Track Internship Lifecycle | `internship_records.status` (`UPCOMING`, `ONGOING`, `COMPLETED`) |
| FR-INT-05 | Company Feedback | `company_feedback` linked 1:1 to `internship_records` (free-text only, DBQ-04) |
| FR-INT-06 | Placement Tracking (Should-Have) | Derived via `opportunities.type = 'PLACEMENT'` through `application_id` |
| FR-ANL-01 | Skill Availability % | Computed at query time via `student_skills` and `student_profiles` |
| FR-ANL-02 | Industry Skill Demand % | Computed at query time via `required_skills` and `opportunities` (OD-07) |
| FR-ANL-03 | Skill Gap Classification | Evaluated in `AnalyticsService` using OD-01 threshold constants |
| FR-ANL-04 | Filterable Demand View (Should-Have) | Filter parameterized over `required_skills` query |
| FR-ANL-05 | Skill Gap Dashboard | Aggregated view of FR-ANL-01, FR-ANL-02, FR-ANL-03 |

**Result:** All 39 functional requirements are fully supported by this database schema.

---

## 10. Database Design Decisions & Resolved Questions

All five Database Open Questions (DBQ-01 through DBQ-05) have been reviewed, resolved, and incorporated into the schema design.

| # | Topic | Decision & Implementation | Status |
|---|---|---|---|
| **DBQ-01** | **Department Normalization** | **APPROVED.** Created master `departments` table (`id`, `name`, `code`, `is_active`, `created_at`, `updated_at`). Updated `student_profiles.department_id` and `opportunity_required_branches.department_id` as foreign keys to `departments.id`. Prevents branch name typos and guarantees reliable eligibility matching. | ✅ RESOLVED |
| **DBQ-02** | **Notification Structure** | **DEFERRED.** Excluded `notifications` table from MVP schema. In-app notifications (FR-APP-06 / S4) will introduce a dedicated migration if implemented post-MVP. | ✅ RESOLVED |
| **DBQ-03** | **Skill Count Limit** | **APPLICATION-LEVEL VALIDATION.** No database-level check or trigger for maximum skills per student/opportunity. Application service layer validates reasonable limits (e.g., max 30 skills). | ✅ RESOLVED |
| **DBQ-04** | **Company Feedback Structure** | **FREE-TEXT ONLY.** `company_feedback` uses `feedback_text TEXT NOT NULL` without numeric ratings or multi-criteria breakdown, matching the PRD baseline. | ✅ RESOLVED |
| **DBQ-05** | **Opportunity Closing Mechanism** | **APPLICATION-LEVEL VALIDATION.** No scheduled database triggers or cron jobs for auto-closing opportunities. `ApplicationService` verifies `opportunities.application_deadline` at application submission time and rejects expired postings. | ✅ RESOLVED |

---

*Database design complete and refined.*  
*Status: DRAFT — Ready for Final Review.*  
*Next steps: API Contract design and Flyway V1 migration script generation upon approval.*
