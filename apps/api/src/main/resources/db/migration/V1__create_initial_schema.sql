-- =============================================================================
-- SkillBridge — Initial Schema Migration
-- Version: V1
-- Description: Create initial schema based on approved database design (16 tables)
-- Database: PostgreSQL 10+
-- Primary Key Strategy: BIGINT GENERATED ALWAYS AS IDENTITY
-- =============================================================================

-- =============================================================================
-- 1. USERS
-- =============================================================================
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
        CHECK (role IN ('STUDENT', 'COMPANY', 'COLLEGE', 'ADMIN'))
);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);

-- =============================================================================
-- 2. COLLEGES
-- =============================================================================
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
        CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

CREATE INDEX idx_colleges_verification_status ON colleges(verification_status);

-- =============================================================================
-- 3. DEPARTMENTS
-- =============================================================================
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

CREATE INDEX idx_departments_is_active ON departments(is_active);

-- =============================================================================
-- 4. STUDENT PROFILES
-- =============================================================================
CREATE TABLE student_profiles (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT        NOT NULL,
    college_id       BIGINT        NOT NULL,
    first_name       VARCHAR(100)  NOT NULL,
    last_name        VARCHAR(100)  NOT NULL,
    department_id    BIGINT,
    year_of_study    SMALLINT,
    cgpa             NUMERIC(4,2),
    career_interests TEXT,
    portfolio_url    VARCHAR(500),
    github_url       VARCHAR(500),
    resume_path      VARCHAR(1000),
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

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

CREATE INDEX idx_student_profiles_college_id ON student_profiles(college_id);
CREATE INDEX idx_student_profiles_department_id ON student_profiles(department_id);

-- =============================================================================
-- 5. COMPANY PROFILES
-- =============================================================================
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
        CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

CREATE INDEX idx_company_profiles_verification_status ON company_profiles(verification_status);

-- =============================================================================
-- 6. SKILLS
-- =============================================================================
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

CREATE INDEX idx_skills_is_active ON skills(is_active);

-- =============================================================================
-- 7. STUDENT SKILLS
-- =============================================================================
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

CREATE INDEX idx_student_skills_student_profile_id ON student_skills(student_profile_id);
CREATE INDEX idx_student_skills_skill_id ON student_skills(skill_id);

-- =============================================================================
-- 8. PROJECTS
-- =============================================================================
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

CREATE INDEX idx_projects_student_profile_id ON projects(student_profile_id);

-- =============================================================================
-- 9. CERTIFICATIONS
-- =============================================================================
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

CREATE INDEX idx_certifications_student_profile_id ON certifications(student_profile_id);

-- =============================================================================
-- 10. OPPORTUNITIES
-- =============================================================================
CREATE TABLE opportunities (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_profile_id   BIGINT        NOT NULL,
    title                VARCHAR(255)  NOT NULL,
    description          TEXT,
    type                 VARCHAR(20)   NOT NULL,
    location             VARCHAR(255),
    mode                 VARCHAR(20)   NOT NULL DEFAULT 'ONSITE',
    duration_weeks       SMALLINT,
    stipend_amount       NUMERIC(12,2),
    stipend_currency     VARCHAR(10)   NOT NULL DEFAULT 'INR',
    min_cgpa             NUMERIC(4,2),
    application_deadline DATE,
    status               VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT fk_opp_company
        FOREIGN KEY (company_profile_id) REFERENCES company_profiles(id) ON DELETE RESTRICT,
    CONSTRAINT ck_opp_type
        CHECK (type IN ('INTERNSHIP', 'PLACEMENT')),
    CONSTRAINT ck_opp_mode
        CHECK (mode IN ('ONSITE', 'REMOTE', 'HYBRID')),
    CONSTRAINT ck_opp_status
        CHECK (status IN ('DRAFT', 'OPEN', 'CLOSED')),
    CONSTRAINT ck_opp_min_cgpa
        CHECK (min_cgpa IS NULL OR (min_cgpa >= 0.00 AND min_cgpa <= 10.00)),
    CONSTRAINT ck_opp_duration
        CHECK (duration_weeks IS NULL OR duration_weeks > 0)
);

CREATE INDEX idx_opportunities_company_profile_id ON opportunities(company_profile_id);
CREATE INDEX idx_opportunities_status ON opportunities(status);
CREATE INDEX idx_opportunities_type ON opportunities(type);
CREATE INDEX idx_opportunities_application_deadline ON opportunities(application_deadline);

-- =============================================================================
-- 11. OPPORTUNITY REQUIRED BRANCHES
-- =============================================================================
CREATE TABLE opportunity_required_branches (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT      NOT NULL,
    department_id   BIGINT      NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_orb_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT fk_orb_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    CONSTRAINT uq_orb_opp_dept
        UNIQUE (opportunity_id, department_id)
);

CREATE INDEX idx_orb_opportunity_id ON opportunity_required_branches(opportunity_id);
CREATE INDEX idx_orb_department_id ON opportunity_required_branches(department_id);

-- =============================================================================
-- 12. OPPORTUNITY REQUIRED YEARS
-- =============================================================================
CREATE TABLE opportunity_required_years (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT      NOT NULL,
    year_of_study   SMALLINT    NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_ory_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT uq_ory_opp_year
        UNIQUE (opportunity_id, year_of_study),
    CONSTRAINT ck_ory_year
        CHECK (year_of_study BETWEEN 1 AND 8)
);

CREATE INDEX idx_ory_opportunity_id ON opportunity_required_years(opportunity_id);

-- =============================================================================
-- 13. REQUIRED SKILLS
-- =============================================================================
CREATE TABLE required_skills (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    opportunity_id  BIGINT      NOT NULL,
    skill_id        BIGINT      NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_rs_opportunity
        FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_skill
        FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT,
    CONSTRAINT uq_rs_opp_skill
        UNIQUE (opportunity_id, skill_id)
);

CREATE INDEX idx_required_skills_opportunity_id ON required_skills(opportunity_id);
CREATE INDEX idx_required_skills_skill_id ON required_skills(skill_id);

-- =============================================================================
-- 14. APPLICATIONS
-- =============================================================================
CREATE TABLE applications (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_profile_id      BIGINT        NOT NULL,
    opportunity_id          BIGINT        NOT NULL,
    status                  VARCHAR(20)   NOT NULL DEFAULT 'APPLIED',
    match_percent_at_apply  NUMERIC(5,2)  NOT NULL,
    applied_at              TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT now(),

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

CREATE INDEX idx_applications_student_profile_id ON applications(student_profile_id);
CREATE INDEX idx_applications_opportunity_id ON applications(opportunity_id);
CREATE INDEX idx_applications_opp_match_desc ON applications(opportunity_id, match_percent_at_apply DESC);
CREATE INDEX idx_applications_status ON applications(status);

-- =============================================================================
-- 15. INTERNSHIP RECORDS
-- =============================================================================
CREATE TABLE internship_records (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id  BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'UPCOMING',
    start_date      DATE,
    end_date        DATE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_ir_application
        FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ir_application
        UNIQUE (application_id),
    CONSTRAINT ck_ir_status
        CHECK (status IN ('UPCOMING', 'ONGOING', 'COMPLETED')),
    CONSTRAINT ck_ir_dates
        CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_internship_records_status ON internship_records(status);

-- =============================================================================
-- 16. COMPANY FEEDBACK
-- =============================================================================
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
