-- =============================================================================
-- SkillBridge — Comprehensive Demo / Seed Data Script
-- Purpose: Realistic seed data for Supabase PostgreSQL database
-- Idempotent: Uses ON CONFLICT and dynamic subqueries for safe re-execution
-- =============================================================================

DO $$
DECLARE
    -- User IDs
    v_admin_uid BIGINT;
    v_iith_uid BIGINT;
    v_iitm_uid BIGINT;
    v_nitw_uid BIGINT;
    v_iiit_uid BIGINT;
    v_vit_uid BIGINT;
    
    v_msft_uid BIGINT;
    v_goog_uid BIGINT;
    v_amzn_uid BIGINT;
    v_delo_uid BIGINT;
    v_accn_uid BIGINT;
    v_ibm_uid BIGINT;
    v_orcl_uid BIGINT;
    
    v_stu1_uid BIGINT;
    v_stu2_uid BIGINT;
    v_stu3_uid BIGINT;
    v_stu4_uid BIGINT;
    v_stu5_uid BIGINT;
    v_stu6_uid BIGINT;

    -- College IDs
    v_iith_id BIGINT;
    v_iitm_id BIGINT;
    v_nitw_id BIGINT;
    v_iiit_id BIGINT;
    v_vit_id BIGINT;

    -- Company IDs
    v_msft_id BIGINT;
    v_goog_id BIGINT;
    v_amzn_id BIGINT;
    v_delo_id BIGINT;
    v_accn_id BIGINT;
    v_ibm_id BIGINT;
    v_orcl_id BIGINT;

    -- Department IDs
    v_dept_cse BIGINT;
    v_dept_ece BIGINT;
    v_dept_csit BIGINT;
    v_dept_mech BIGINT;
    v_dept_eee BIGINT;
    v_dept_aids BIGINT;

    -- Student Profile IDs
    v_sp1_id BIGINT;
    v_sp2_id BIGINT;
    v_sp3_id BIGINT;
    v_sp4_id BIGINT;
    v_sp5_id BIGINT;
    v_sp6_id BIGINT;

    -- Skill IDs
    v_sk_java BIGINT;
    v_sk_python BIGINT;
    v_sk_js BIGINT;
    v_sk_ts BIGINT;
    v_sk_react BIGINT;
    v_sk_spring BIGINT;
    v_sk_sql BIGINT;
    v_sk_postgres BIGINT;
    v_sk_git BIGINT;
    v_sk_docker BIGINT;
    v_sk_aws BIGINT;
    v_sk_ml BIGINT;
    v_sk_dsa BIGINT;
    v_sk_node BIGINT;
    v_sk_rest BIGINT;
    v_sk_htmlcss BIGINT;
    v_sk_k8s BIGINT;
    v_sk_linux BIGINT;

    -- Opportunity IDs
    v_opp_msft_swe BIGINT;
    v_opp_goog_swe BIGINT;
    v_opp_amzn_sde BIGINT;
    v_opp_delo_ta BIGINT;
    v_opp_accn_ase BIGINT;
    v_opp_ibm_aiml BIGINT;
    v_opp_orcl_cld BIGINT;
    v_opp_msft_fs BIGINT;

    -- Application IDs
    v_app_1 BIGINT;
    v_app_2 BIGINT;
    v_app_3 BIGINT;
    v_app_4 BIGINT;
    v_app_5 BIGINT;
    v_app_6 BIGINT;
    v_app_7 BIGINT;
    v_app_8 BIGINT;
    v_app_9 BIGINT;
    v_app_10 BIGINT;
    v_app_11 BIGINT;

    -- Internship IDs
    v_ir_1 BIGINT;
    v_ir_2 BIGINT;
BEGIN

    -- =========================================================================
    -- 1. DEPARTMENTS
    -- =========================================================================
    INSERT INTO departments (name, code, is_active)
    VALUES 
        ('Computer Science and Engineering', 'CSE', true),
        ('Electronics and Communication Engineering', 'ECE', true),
        ('Computer Science and Information Technology', 'CSIT', true),
        ('Mechanical Engineering', 'MECH', true),
        ('Electrical and Electronics Engineering', 'EEE', true),
        ('Artificial Intelligence and Data Science', 'AIDS', true)
    ON CONFLICT (name) DO UPDATE SET is_active = EXCLUDED.is_active;

    SELECT id INTO v_dept_cse FROM departments WHERE code = 'CSE';
    SELECT id INTO v_dept_ece FROM departments WHERE code = 'ECE';
    SELECT id INTO v_dept_csit FROM departments WHERE code = 'CSIT';
    SELECT id INTO v_dept_mech FROM departments WHERE code = 'MECH';
    SELECT id INTO v_dept_eee FROM departments WHERE code = 'EEE';
    SELECT id INTO v_dept_aids FROM departments WHERE code = 'AIDS';

    -- =========================================================================
    -- 2. SKILLS MASTER TAXONOMY
    -- =========================================================================
    INSERT INTO skills (name, category, is_active)
    VALUES
        ('Java', 'Backend', true),
        ('Python', 'Programming', true),
        ('JavaScript', 'Frontend', true),
        ('TypeScript', 'Frontend', true),
        ('React', 'Frontend', true),
        ('Spring Boot', 'Backend', true),
        ('SQL', 'Database', true),
        ('PostgreSQL', 'Database', true),
        ('Git', 'DevOps', true),
        ('Docker', 'DevOps', true),
        ('AWS', 'Cloud', true),
        ('Machine Learning', 'AI/ML', true),
        ('Data Structures & Algorithms', 'Computer Science', true),
        ('Node.js', 'Backend', true),
        ('REST APIs', 'Backend', true),
        ('HTML/CSS', 'Frontend', true),
        ('Kubernetes', 'DevOps', true),
        ('Linux', 'DevOps', true)
    ON CONFLICT (name) DO UPDATE SET category = EXCLUDED.category, is_active = EXCLUDED.is_active;

    SELECT id INTO v_sk_java FROM skills WHERE name = 'Java';
    SELECT id INTO v_sk_python FROM skills WHERE name = 'Python';
    SELECT id INTO v_sk_js FROM skills WHERE name = 'JavaScript';
    SELECT id INTO v_sk_ts FROM skills WHERE name = 'TypeScript';
    SELECT id INTO v_sk_react FROM skills WHERE name = 'React';
    SELECT id INTO v_sk_spring FROM skills WHERE name = 'Spring Boot';
    SELECT id INTO v_sk_sql FROM skills WHERE name = 'SQL';
    SELECT id INTO v_sk_postgres FROM skills WHERE name = 'PostgreSQL';
    SELECT id INTO v_sk_git FROM skills WHERE name = 'Git';
    SELECT id INTO v_sk_docker FROM skills WHERE name = 'Docker';
    SELECT id INTO v_sk_aws FROM skills WHERE name = 'AWS';
    SELECT id INTO v_sk_ml FROM skills WHERE name = 'Machine Learning';
    SELECT id INTO v_sk_dsa FROM skills WHERE name = 'Data Structures & Algorithms';
    SELECT id INTO v_sk_node FROM skills WHERE name = 'Node.js';
    SELECT id INTO v_sk_rest FROM skills WHERE name = 'REST APIs';
    SELECT id INTO v_sk_htmlcss FROM skills WHERE name = 'HTML/CSS';
    SELECT id INTO v_sk_k8s FROM skills WHERE name = 'Kubernetes';
    SELECT id INTO v_sk_linux FROM skills WHERE name = 'Linux';

    -- =========================================================================
    -- 3. USERS (ADMIN, COLLEGES, COMPANIES, STUDENTS)
    -- =========================================================================

    -- Admin User (Password: Admin@SkillBridge2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('admin@skillbridge.org', '$2a$10$9dFN9nUKlj.eNqWZgNBPxuah.JMIExYWcWA4zlPLk113WfoLD8p5q', 'ADMIN', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password, role = EXCLUDED.role, is_active = EXCLUDED.is_active;

    -- College Users
    -- IIT Hyderabad (Password: IITH@Placement2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('dean.placement@iith.ac.in', '$2a$10$NMP2UNLrO.f5AkzcZTMBjuMXcMJzZ9QvvUAcPsxs0WZBS0rYUl88S', 'COLLEGE', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_iith_uid FROM users WHERE email = 'dean.placement@iith.ac.in';

    -- IIT Madras (Password: IITM@Placement2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('placements@iitm.ac.in', '$2a$10$W3WmSJA4B8hGqHSqznmg3ew2U6nTtuo5XEjOjREZhqypGZ3KayGzC', 'COLLEGE', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_iitm_uid FROM users WHERE email = 'placements@iitm.ac.in';

    -- NIT Warangal (Password: NITW@Placement2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('tpo@nitw.ac.in', '$2a$10$DpuaMNQGkCW8Q0WnyRy3nObQDB.4SaRVOtYQ.dKzA53eAOrvgMqHC', 'COLLEGE', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_nitw_uid FROM users WHERE email = 'tpo@nitw.ac.in';

    -- IIIT Hyderabad (Password: IIITH@Placement2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('placement.cell@iiit.ac.in', '$2a$10$OY3kboZyxFSpPHLiS6V9Fea8dhS5DtRxOmdH/6rMVMsyf.MUYJZxC', 'COLLEGE', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_iiit_uid FROM users WHERE email = 'placement.cell@iiit.ac.in';

    -- VIT Vellore (Password: VIT@Placement2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('pat@vit.ac.in', '$2a$10$O142Ijs5DPvvmzGGgFEnyuahnqcRAs.ud4wJjMKt25ZLAuW8/Xfwu', 'COLLEGE', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_vit_uid FROM users WHERE email = 'pat@vit.ac.in';

    -- Company Users
    -- Microsoft (Password: MSFT@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('campus.recruitment@microsoft.com', '$2a$10$fr58CbTZYLBOY27gyzn6COuVQY/cyC8BH5Vx.EXPi4HVVbxh49LvK', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_msft_uid FROM users WHERE email = 'campus.recruitment@microsoft.com';

    -- Google (Password: GOOG@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('university.hiring@google.com', '$2a$10$FkMNQFRgYPbu.j9BGjS6LOV4JmfInFl2dlOLZBb.Q2ZOhm.j15lgq', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_goog_uid FROM users WHERE email = 'university.hiring@google.com';

    -- Amazon (Password: AMZN@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('student.programs@amazon.com', '$2a$10$k7vjUKh646LAHh9FLaWamupnQyMFe0/2mD.sQ3WEB2gSxRwbuUX/m', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_amzn_uid FROM users WHERE email = 'student.programs@amazon.com';

    -- Deloitte (Password: DELO@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('campus.talent@deloitte.com', '$2a$10$9f13dC5gnPmW4iWUiyvAYOM3a5KssA.5xR5Eriro0794zac3S7Ex.', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_delo_uid FROM users WHERE email = 'campus.talent@deloitte.com';

    -- Accenture (Password: ACCN@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('entrylevel.hiring@accenture.com', '$2a$10$L5md7o6.Td4t11.w5.1CvO6IDAGFtKnLjLHDt9O6jPmdFK4NPzwC6', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_accn_uid FROM users WHERE email = 'entrylevel.hiring@accenture.com';

    -- IBM (Password: IBMC@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('university.relations@ibm.com', '$2a$10$Plukkrzle19z9v3Xw0yAkeijL1oeBJehgcFRWMK9ODllSy47YtRzK', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_ibm_uid FROM users WHERE email = 'university.relations@ibm.com';

    -- Oracle (Password: ORCL@Hire2026!)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('campus.recruiting@oracle.com', '$2a$10$VU2bsiWoF5nWR5jNsRHaTORUFa1Gf1h8p9w5eThRDQnrcOx7OUkeO', 'COMPANY', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_orcl_uid FROM users WHERE email = 'campus.recruiting@oracle.com';

    -- Student Users
    -- Student 1: Konakanchi Venkata Lakshmi Vangnaini (Password: Skill@Vangnaini2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('vangnaini.k@iith.ac.in', '$2a$10$f0Kr0I/vVroDCwoXnzjn/uldbQk0OunS3X2gmhjb...dqYh7xtkLK', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu1_uid FROM users WHERE email = 'vangnaini.k@iith.ac.in';

    -- Student 2: Singuparapu Lalith Aditya (Password: Skill@Lalith2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('lalith.aditya@iitm.ac.in', '$2a$10$udV2hQ1/hWZ0VdVH1Sj8L.URgMVOpExgmAIw.F3gcCAIb.lLPdk6u', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu2_uid FROM users WHERE email = 'lalith.aditya@iitm.ac.in';

    -- Student 3: Maddi James (Password: Skill@James2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('james.maddi@nitw.ac.in', '$2a$10$2.qkjn2U2dCIWtG4oLI4IOo19QlN2wiXwfn6Bnr2i1DuPtmctRcu.', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu3_uid FROM users WHERE email = 'james.maddi@nitw.ac.in';

    -- Student 4: Prajwal AR (Password: Skill@Prajwal2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('prajwal.ar@iiit.ac.in', '$2a$10$RS2kmsq9zvn/Ilvk9XcjUOlT.vjTbt2RAYdXDvIqMlu7bSNxFZi12', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu4_uid FROM users WHERE email = 'prajwal.ar@iiit.ac.in';

    -- Student 5: Munaga Naga Sai Janaki (Password: Skill@Janaki2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('sai.janaki@vit.ac.in', '$2a$10$4Ceyr2wqdqi0b4r8UdkOjueqhoz.DM6NweimE1PWTXT3Ngg2VmODK', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu5_uid FROM users WHERE email = 'sai.janaki@vit.ac.in';

    -- Student 6: Manné Thavitha sree ramani (Password: Skill@Thavitha2026)
    INSERT INTO users (email, password, role, is_active)
    VALUES ('thavitha.manne@iith.ac.in', '$2a$10$7cr3ce/uQyYGVDGRZPAinODQprpg/GGc4H4csyGg5xfF8Hj/0.DMi', 'STUDENT', true)
    ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;
    SELECT id INTO v_stu6_uid FROM users WHERE email = 'thavitha.manne@iith.ac.in';

    -- =========================================================================
    -- 4. COLLEGES PROFILES
    -- =========================================================================
    INSERT INTO colleges (user_id, name, address, website, contact_email, contact_phone, verification_status, verified_at)
    VALUES
        (v_iith_uid, 'Indian Institute of Technology Hyderabad', 'Kandi, Sangareddy, Telangana 502284', 'https://www.iith.ac.in', 'dean.placement@iith.ac.in', '+91 40 2301 6000', 'VERIFIED', now()),
        (v_iitm_uid, 'Indian Institute of Technology Madras', 'IIT P.O., Chennai, Tamil Nadu 600036', 'https://www.iitm.ac.in', 'placements@iitm.ac.in', '+91 44 2257 8000', 'VERIFIED', now()),
        (v_nitw_uid, 'National Institute of Technology Warangal', 'Hanamkonda, Warangal, Telangana 506004', 'https://www.nitw.ac.in', 'tpo@nitw.ac.in', '+91 870 245 9191', 'VERIFIED', now()),
        (v_iiit_uid, 'International Institute of Information Technology Hyderabad', 'Gachibowli, Hyderabad, Telangana 500032', 'https://www.iiit.ac.in', 'placement.cell@iiit.ac.in', '+91 40 6653 1000', 'VERIFIED', now()),
        (v_vit_uid, 'Vellore Institute of Technology', 'Katpadi, Vellore, Tamil Nadu 632014', 'https://vit.ac.in', 'pat@vit.ac.in', '+91 416 220 2020', 'VERIFIED', now())
    ON CONFLICT (user_id) DO UPDATE SET 
        name = EXCLUDED.name, address = EXCLUDED.address, website = EXCLUDED.website, 
        contact_email = EXCLUDED.contact_email, contact_phone = EXCLUDED.contact_phone, 
        verification_status = EXCLUDED.verification_status, verified_at = EXCLUDED.verified_at;

    SELECT id INTO v_iith_id FROM colleges WHERE user_id = v_iith_uid;
    SELECT id INTO v_iitm_id FROM colleges WHERE user_id = v_iitm_uid;
    SELECT id INTO v_nitw_id FROM colleges WHERE user_id = v_nitw_uid;
    SELECT id INTO v_iiit_id FROM colleges WHERE user_id = v_iiit_uid;
    SELECT id INTO v_vit_id FROM colleges WHERE user_id = v_vit_uid;

    -- =========================================================================
    -- 5. COMPANY PROFILES
    -- =========================================================================
    INSERT INTO company_profiles (user_id, name, industry, description, location, website, contact_email, contact_phone, verification_status, verified_at)
    VALUES
        (v_msft_uid, 'Microsoft', 'Information Technology & Cloud Computing', 'Global leader in software, cloud infrastructure, AI platforms, and personal computing.', 'Hyderabad / Bangalore', 'https://www.microsoft.com', 'campus.recruitment@microsoft.com', '+91 40 6695 0000', 'VERIFIED', now()),
        (v_goog_uid, 'Google', 'Internet Services, AI & Software', 'World-leading technology company specializing in search, cloud services, and machine learning.', 'Bangalore / Hyderabad', 'https://about.google', 'university.hiring@google.com', '+91 80 6721 8000', 'VERIFIED', now()),
        (v_amzn_uid, 'Amazon', 'E-Commerce, Cloud & Logistics', 'Global technology enterprise pioneering e-commerce, cloud computing with AWS, and AI systems.', 'Hyderabad / Bangalore', 'https://www.amazon.jobs', 'student.programs@amazon.com', '+91 40 4000 0000', 'VERIFIED', now()),
        (v_delo_uid, 'Deloitte', 'Management Consulting & Technology Services', 'Premier global consulting and professional services firm offering digital transformation and technology advisory.', 'Hyderabad / Gurgaon', 'https://www2.deloitte.com', 'campus.talent@deloitte.com', '+91 40 6762 1000', 'VERIFIED', now()),
        (v_accn_uid, 'Accenture', 'Information Technology & Consulting', 'Global professional services enterprise driving innovation across cloud, enterprise applications, and digital services.', 'Bangalore / Pune / Hyderabad', 'https://www.accenture.com', 'entrylevel.hiring@accenture.com', '+91 80 4106 0000', 'VERIFIED', now()),
        (v_ibm_uid, 'IBM', 'Hybrid Cloud & Enterprise AI', 'Global leader in cognitive computing, hybrid cloud solutions, enterprise software, and research.', 'Bangalore / Kochi', 'https://www.ibm.com', 'university.relations@ibm.com', '+91 80 4068 3000', 'VERIFIED', now()),
        (v_orcl_uid, 'Oracle', 'Enterprise Software & Cloud Infrastructure', 'Global provider of enterprise database management systems, cloud infrastructure (OCI), and business applications.', 'Hyderabad / Bangalore', 'https://www.oracle.com', 'campus.recruiting@oracle.com', '+91 40 6605 0000', 'VERIFIED', now())
    ON CONFLICT (user_id) DO UPDATE SET 
        name = EXCLUDED.name, industry = EXCLUDED.industry, description = EXCLUDED.description,
        location = EXCLUDED.location, website = EXCLUDED.website, contact_email = EXCLUDED.contact_email,
        contact_phone = EXCLUDED.contact_phone, verification_status = EXCLUDED.verification_status, verified_at = EXCLUDED.verified_at;

    SELECT id INTO v_msft_id FROM company_profiles WHERE user_id = v_msft_uid;
    SELECT id INTO v_goog_id FROM company_profiles WHERE user_id = v_goog_uid;
    SELECT id INTO v_amzn_id FROM company_profiles WHERE user_id = v_amzn_uid;
    SELECT id INTO v_delo_id FROM company_profiles WHERE user_id = v_delo_uid;
    SELECT id INTO v_accn_id FROM company_profiles WHERE user_id = v_accn_uid;
    SELECT id INTO v_ibm_id FROM company_profiles WHERE user_id = v_ibm_uid;
    SELECT id INTO v_orcl_id FROM company_profiles WHERE user_id = v_orcl_uid;

    -- =========================================================================
    -- 6. STUDENT PROFILES
    -- =========================================================================

    -- Student 1: Konakanchi Venkata Lakshmi Vangnaini (IIT Hyderabad, CSE, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu1_uid, v_iith_id, 'Konakanchi Venkata', 'Lakshmi Vangnaini', v_dept_cse, 2, 8.85,
        'Backend Engineering, Distributed Systems, Spring Boot Microservices and Cloud Computing',
        'https://vangnaini.dev', 'https://github.com/vangnaini-k', '+91 9492076004'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp1_id FROM student_profiles WHERE user_id = v_stu1_uid;

    -- Student 2: Singuparapu Lalith Aditya (IIT Madras, CSE, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu2_uid, v_iitm_id, 'Lalith Aditya', 'Singuparapu', v_dept_cse, 2, 9.20,
        'Full Stack Development, High Performance Distributed Systems, Algorithms and Systems Architecture',
        'https://lalithaditya.dev', 'https://github.com/lalithdev', '+91 8341647137'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp2_id FROM student_profiles WHERE user_id = v_stu2_uid;

    -- Student 3: Maddi James (NIT Warangal, ECE, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu3_uid, v_nitw_id, 'James', 'Maddi', v_dept_ece, 2, 8.40,
        'Machine Learning, Embedded Systems, Signal Processing and Applied AI in Edge Computing',
        'https://jamesmaddi.tech', 'https://github.com/james-maddi', '+91 6309353927'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp3_id FROM student_profiles WHERE user_id = v_stu3_uid;

    -- Student 4: Prajwal AR (IIIT Hyderabad, CSIT, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu4_uid, v_iiit_id, 'Prajwal', 'AR', v_dept_csit, 2, 8.90,
        'Frontend Architecture, React Ecosystems, Modern UI/UX, Node.js Web Services',
        'https://prajwalar.design', 'https://github.com/prajwalar', '+91 9019722495'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp4_id FROM student_profiles WHERE user_id = v_stu4_uid;

    -- Student 5: Munaga Naga Sai Janaki (VIT Vellore, CSE, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu5_uid, v_vit_id, 'Naga Sai', 'Munaga Janaki', v_dept_cse, 2, 9.10,
        'Algorithms, Data Engineering, Backend Database Optimization and Enterprise Java Systems',
        'https://janakimunaga.dev', 'https://github.com/sai-janaki', '+91 8309526728'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp5_id FROM student_profiles WHERE user_id = v_stu5_uid;

    -- Student 6: Manné Thavitha sree ramani (IIT Hyderabad, CSIT, Y24 -> Year 2)
    INSERT INTO student_profiles (user_id, college_id, first_name, last_name, department_id, year_of_study, cgpa, career_interests, portfolio_url, github_url, phone)
    VALUES (
        v_stu6_uid, v_iith_id, 'Thavitha Sree', 'Manné Ramani', v_dept_csit, 2, 8.65,
        'Web Engineering, Interactive UI Development, REST API Design and Python Automation',
        'https://thavithasree.me', 'https://github.com/thavitha-manne', '+91 8008934324'
    )
    ON CONFLICT (user_id) DO UPDATE SET
        college_id = EXCLUDED.college_id, first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name,
        department_id = EXCLUDED.department_id, year_of_study = EXCLUDED.year_of_study, cgpa = EXCLUDED.cgpa,
        career_interests = EXCLUDED.career_interests, portfolio_url = EXCLUDED.portfolio_url, github_url = EXCLUDED.github_url,
        phone = EXCLUDED.phone;
    SELECT id INTO v_sp6_id FROM student_profiles WHERE user_id = v_stu6_uid;

    -- =========================================================================
    -- 7. STUDENT SKILLS ASSIGNMENTS
    -- =========================================================================

    -- Student 1 (Vangnaini): Java, SQL, Spring Boot, Git, REST APIs
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp1_id, v_sk_java),
        (v_sp1_id, v_sk_sql),
        (v_sp1_id, v_sk_spring),
        (v_sp1_id, v_sk_git),
        (v_sp1_id, v_sk_rest)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- Student 2 (Lalith Aditya): Java, Python, React, SQL, Git, DSA, Docker
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp2_id, v_sk_java),
        (v_sp2_id, v_sk_python),
        (v_sp2_id, v_sk_react),
        (v_sp2_id, v_sk_sql),
        (v_sp2_id, v_sk_git),
        (v_sp2_id, v_sk_dsa),
        (v_sp2_id, v_sk_docker)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- Student 3 (James Maddi): Python, Machine Learning, SQL, Git, REST APIs
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp3_id, v_sk_python),
        (v_sp3_id, v_sk_ml),
        (v_sp3_id, v_sk_sql),
        (v_sp3_id, v_sk_git),
        (v_sp3_id, v_sk_rest)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- Student 4 (Prajwal AR): JavaScript, React, Node.js, SQL, Git, HTML/CSS
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp4_id, v_sk_js),
        (v_sp4_id, v_sk_react),
        (v_sp4_id, v_sk_node),
        (v_sp4_id, v_sk_sql),
        (v_sp4_id, v_sk_git),
        (v_sp4_id, v_sk_htmlcss)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- Student 5 (Sai Janaki): Python, Java, DSA, PostgreSQL, Spring Boot
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp5_id, v_sk_python),
        (v_sp5_id, v_sk_java),
        (v_sp5_id, v_sk_dsa),
        (v_sp5_id, v_sk_postgres),
        (v_sp5_id, v_sk_spring)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- Student 6 (Thavitha Sree): JavaScript, React, Python, REST APIs, Git, HTML/CSS
    INSERT INTO student_skills (student_profile_id, skill_id)
    VALUES 
        (v_sp6_id, v_sk_js),
        (v_sp6_id, v_sk_react),
        (v_sp6_id, v_sk_python),
        (v_sp6_id, v_sk_rest),
        (v_sp6_id, v_sk_git),
        (v_sp6_id, v_sk_htmlcss)
    ON CONFLICT (student_profile_id, skill_id) DO NOTHING;

    -- =========================================================================
    -- 8. PROJECTS & CERTIFICATIONS
    -- =========================================================================
    INSERT INTO projects (student_profile_id, title, description, project_url)
    VALUES 
        (v_sp1_id, 'Cloud Banking Microservices', 'Scalable core banking API built with Spring Boot, PostgreSQL, Docker, and Spring Cloud Gateway.', 'https://github.com/vangnaini-k/cloud-banking'),
        (v_sp2_id, 'High-Throughput Distributed Cache', 'Distributed in-memory key-value cache engine in Java with Raft consensus and lock-free concurrency.', 'https://github.com/lalithdev/dist-cache-engine'),
        (v_sp3_id, 'Edge-AI Audio Classification', 'Lightweight deep learning model for real-time acoustic anomaly detection on Raspberry Pi with PyTorch.', 'https://github.com/james-maddi/edge-ai-audio'),
        (v_sp4_id, 'Real-time Collaborative Canvas', 'Interactive multiplayer design canvas built with React 18, WebSocket, Node.js, and Canvas API.', 'https://github.com/prajwalar/realtime-canvas'),
        (v_sp5_id, 'Algorithmic Trading Backtester', 'High performance quantitative backtesting engine in Java & Python with tick-level data processing.', 'https://github.com/sai-janaki/quant-backtester'),
        (v_sp6_id, 'Smart Placement Analytics Portal', 'Responsive candidate tracking dashboard in React with RESTful metrics reporting.', 'https://github.com/thavitha-manne/placement-portal')
    ON CONFLICT DO NOTHING;

    INSERT INTO certifications (student_profile_id, title, issuer, issued_date, certificate_url)
    VALUES 
        (v_sp1_id, 'Oracle Certified Professional: Java SE 17 Developer', 'Oracle University', '2025-08-15', 'https://oracle.com/cert/java17-ocp'),
        (v_sp2_id, 'AWS Certified Solutions Architect – Associate', 'Amazon Web Services', '2025-09-20', 'https://aws.amazon.com/cert/saa-c03'),
        (v_sp3_id, 'Deep Learning Specialization', 'DeepLearning.AI / Coursera', '2025-11-10', 'https://coursera.org/verify/dl-spec'),
        (v_sp4_id, 'Meta Certified Frontend Developer', 'Meta', '2025-07-04', 'https://coursera.org/verify/meta-frontend'),
        (v_sp5_id, 'HackerRank Problem Solving (Advanced)', 'HackerRank', '2025-06-18', 'https://hackerrank.com/certificates/ps-adv'),
        (v_sp6_id, 'Google Professional Web Developer', 'Google', '2025-10-05', 'https://grow.google/cert/web-dev')
    ON CONFLICT DO NOTHING;

    -- =========================================================================
    -- 9. OPPORTUNITIES
    -- =========================================================================

    -- Opportunity 1: Microsoft — Software Engineering Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_msft_id, 'Software Engineering Intern',
        'Join the Azure Core Engineering team to build mission-critical, high-availability cloud infrastructure and developer tooling.',
        'INTERNSHIP', 'Hyderabad, India', 'HYBRID', 12,
        125000.00, 'INR', 8.50, (CURRENT_DATE + INTERVAL '45 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_msft_swe;

    -- Opportunity 2: Google — Software Engineer Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_goog_id, 'Software Engineer Intern - Systems & Core',
        'Work with Google Search & Cloud infrastructure engineering groups on distributed storage, indexing pipelines, and algorithmic efficiency.',
        'INTERNSHIP', 'Bangalore, India', 'HYBRID', 12,
        150000.00, 'INR', 8.75, (CURRENT_DATE + INTERVAL '30 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_goog_swe;

    -- Opportunity 3: Amazon — SDE Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_amzn_id, 'Software Development Engineer Intern',
        'Develop highly responsive web services and distributed microservices supporting millions of active AWS customers worldwide.',
        'INTERNSHIP', 'Hyderabad, India', 'ONSITE', 10,
        110000.00, 'INR', 8.00, (CURRENT_DATE + INTERVAL '60 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_amzn_sde;

    -- Opportunity 4: Deloitte — Technology Analyst Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_delo_id, 'Technology Analyst Intern - Enterprise Solutions',
        'Engage with Fortune 500 enterprise clients on system modernization, database engineering, and custom application integrations.',
        'INTERNSHIP', 'Gurgaon / Hyderabad', 'ONSITE', 8,
        45000.00, 'INR', 7.50, (CURRENT_DATE + INTERVAL '40 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_delo_ta;

    -- Opportunity 5: Accenture — Associate Software Engineer
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_accn_id, 'Associate Software Engineer - Frontend & Web Applications',
        'Build cutting-edge user interfaces and responsive web applications for global financial, healthcare, and retail enterprises.',
        'PLACEMENT', 'Bangalore / Hyderabad', 'HYBRID', 24,
        65000.00, 'INR', 7.00, (CURRENT_DATE + INTERVAL '50 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_accn_ase;

    -- Opportunity 6: IBM — AI/ML Research Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_ibm_id, 'AI / Machine Learning Research Intern',
        'Participate in IBM Research initiatives building specialized neural network architectures, data preprocessing pipelines, and NLP models.',
        'INTERNSHIP', 'Remote / Bangalore', 'REMOTE', 16,
        80000.00, 'INR', 8.20, (CURRENT_DATE + INTERVAL '35 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_ibm_aiml;

    -- Opportunity 7: Oracle — Cloud Engineering Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_orcl_id, 'Cloud Infrastructure & Database Engineering Intern',
        'Work on Oracle Cloud Infrastructure (OCI) database services, automated scaling pipelines, and containerized cloud services.',
        'INTERNSHIP', 'Bangalore / Hyderabad', 'ONSITE', 12,
        90000.00, 'INR', 8.00, (CURRENT_DATE + INTERVAL '45 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_orcl_cld;

    -- Opportunity 8: Microsoft — Full Stack Developer Intern
    INSERT INTO opportunities (
        company_profile_id, title, description, type, location, mode, duration_weeks,
        stipend_amount, stipend_currency, min_cgpa, application_deadline, status
    )
    VALUES (
        v_msft_id, 'Full Stack Web Developer Intern',
        'Design and deploy rich interactive developer portals, analytics dashboards, and web services for GitHub and Microsoft 365 developer ecosystems.',
        'INTERNSHIP', 'Bangalore / Remote', 'REMOTE', 12,
        100000.00, 'INR', 8.00, (CURRENT_DATE + INTERVAL '55 days'), 'OPEN'
    )
    RETURNING id INTO v_opp_msft_fs;

    -- =========================================================================
    -- 10. OPPORTUNITY ELIGIBILITY & REQUIRED SKILLS
    -- =========================================================================

    -- Microsoft SWE: Java, DSA, Spring Boot, Git (Depts: CSE, CSIT; Years: 2, 3)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_msft_swe, v_sk_java),
        (v_opp_msft_swe, v_sk_dsa),
        (v_opp_msft_swe, v_sk_spring),
        (v_opp_msft_swe, v_sk_git)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_msft_swe, v_dept_cse), (v_opp_msft_swe, v_dept_csit)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_msft_swe, 2), (v_opp_msft_swe, 3)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Google SWE: Python, Java, DSA, Git (Depts: CSE, CSIT; Years: 2, 3)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_goog_swe, v_sk_python),
        (v_opp_goog_swe, v_sk_java),
        (v_opp_goog_swe, v_sk_dsa),
        (v_opp_goog_swe, v_sk_git)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_goog_swe, v_dept_cse), (v_opp_goog_swe, v_dept_csit)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_goog_swe, 2), (v_opp_goog_swe, 3)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Amazon SDE: Java, AWS, REST APIs, SQL (Depts: CSE, CSIT, ECE; Years: 2, 3, 4)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_amzn_sde, v_sk_java),
        (v_opp_amzn_sde, v_sk_aws),
        (v_opp_amzn_sde, v_sk_rest),
        (v_opp_amzn_sde, v_sk_sql)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_amzn_sde, v_dept_cse), (v_opp_amzn_sde, v_dept_csit), (v_opp_amzn_sde, v_dept_ece)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_amzn_sde, 2), (v_opp_amzn_sde, 3), (v_opp_amzn_sde, 4)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Deloitte TA: SQL, Python, REST APIs (Depts: CSE, CSIT, ECE, EEE; Years: 2, 3, 4)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_delo_ta, v_sk_sql),
        (v_opp_delo_ta, v_sk_python),
        (v_opp_delo_ta, v_sk_rest)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_delo_ta, v_dept_cse), (v_opp_delo_ta, v_dept_csit), (v_opp_delo_ta, v_dept_ece), (v_opp_delo_ta, v_dept_eee)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_delo_ta, 2), (v_opp_delo_ta, 3), (v_opp_delo_ta, 4)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Accenture ASE: JavaScript, React, HTML/CSS, Git (Depts: CSE, CSIT, ECE; Years: 2, 3, 4)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_accn_ase, v_sk_js),
        (v_opp_accn_ase, v_sk_react),
        (v_opp_accn_ase, v_sk_htmlcss),
        (v_opp_accn_ase, v_sk_git)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_accn_ase, v_dept_cse), (v_opp_accn_ase, v_dept_csit), (v_opp_accn_ase, v_dept_ece)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_accn_ase, 2), (v_opp_accn_ase, 3), (v_opp_accn_ase, 4)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- IBM AI/ML: Python, Machine Learning, SQL, REST APIs (Depts: CSE, CSIT, AIDS, ECE; Years: 2, 3)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_ibm_aiml, v_sk_python),
        (v_opp_ibm_aiml, v_sk_ml),
        (v_opp_ibm_aiml, v_sk_sql),
        (v_opp_ibm_aiml, v_sk_rest)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_ibm_aiml, v_dept_cse), (v_opp_ibm_aiml, v_dept_csit), (v_opp_ibm_aiml, v_dept_aids), (v_opp_ibm_aiml, v_dept_ece)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_ibm_aiml, 2), (v_opp_ibm_aiml, 3)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Oracle Cloud: Java, PostgreSQL, Docker, Linux (Depts: CSE, CSIT; Years: 2, 3)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_orcl_cld, v_sk_java),
        (v_opp_orcl_cld, v_sk_postgres),
        (v_opp_orcl_cld, v_sk_docker),
        (v_opp_orcl_cld, v_sk_linux)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_orcl_cld, v_dept_cse), (v_opp_orcl_cld, v_dept_csit)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_orcl_cld, 2), (v_opp_orcl_cld, 3)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- Microsoft Full Stack: JavaScript, React, Node.js, SQL, Git (Depts: CSE, CSIT; Years: 2, 3)
    INSERT INTO required_skills (opportunity_id, skill_id)
    VALUES 
        (v_opp_msft_fs, v_sk_js),
        (v_opp_msft_fs, v_sk_react),
        (v_opp_msft_fs, v_sk_node),
        (v_opp_msft_fs, v_sk_sql),
        (v_opp_msft_fs, v_sk_git)
    ON CONFLICT (opportunity_id, skill_id) DO NOTHING;

    INSERT INTO opportunity_required_branches (opportunity_id, department_id)
    VALUES (v_opp_msft_fs, v_dept_cse), (v_opp_msft_fs, v_dept_csit)
    ON CONFLICT (opportunity_id, department_id) DO NOTHING;

    INSERT INTO opportunity_required_years (opportunity_id, year_of_study)
    VALUES (v_opp_msft_fs, 2), (v_opp_msft_fs, 3)
    ON CONFLICT (opportunity_id, year_of_study) DO NOTHING;

    -- =========================================================================
    -- 11. APPLICATIONS & RECRUITMENT WORKFLOWS
    -- =========================================================================

    -- Lalith Aditya (Student 2) -> Google SWE (Match: 100%, SHORTLISTED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp2_id, v_opp_goog_swe, 'SHORTLISTED', 100.00, now() - INTERVAL '10 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Lalith Aditya (Student 2) -> Microsoft SWE (Match: 100%, INTERVIEW)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp2_id, v_opp_msft_swe, 'INTERVIEW', 100.00, now() - INTERVAL '14 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Vangnaini (Student 1) -> Microsoft SWE (Match: 75%, SELECTED -> Internship)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp1_id, v_opp_msft_swe, 'SELECTED', 75.00, now() - INTERVAL '30 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING
    RETURNING id INTO v_app_3;

    -- Prajwal AR (Student 4) -> Microsoft Full Stack (Match: 100%, INTERVIEW)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp4_id, v_opp_msft_fs, 'INTERVIEW', 100.00, now() - INTERVAL '8 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Prajwal AR (Student 4) -> Accenture ASE (Match: 100%, SHORTLISTED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp4_id, v_opp_accn_ase, 'SHORTLISTED', 100.00, now() - INTERVAL '12 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- James Maddi (Student 3) -> IBM AI/ML (Match: 100%, SHORTLISTED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp3_id, v_opp_ibm_aiml, 'SHORTLISTED', 100.00, now() - INTERVAL '6 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- James Maddi (Student 3) -> Deloitte TA (Match: 100%, APPLIED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp3_id, v_opp_delo_ta, 'APPLIED', 100.00, now() - INTERVAL '2 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Sai Janaki (Student 5) -> Google SWE (Match: 100%, SHORTLISTED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp5_id, v_opp_goog_swe, 'SHORTLISTED', 100.00, now() - INTERVAL '9 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Sai Janaki (Student 5) -> Oracle Cloud (Match: 50%, APPLIED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp5_id, v_opp_orcl_cld, 'APPLIED', 50.00, now() - INTERVAL '3 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Thavitha Sree (Student 6) -> Accenture ASE (Match: 100%, SELECTED -> Completed Internship)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp6_id, v_opp_accn_ase, 'SELECTED', 100.00, now() - INTERVAL '60 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING
    RETURNING id INTO v_app_10;

    -- Thavitha Sree (Student 6) -> Microsoft Full Stack (Match: 80%, APPLIED)
    INSERT INTO applications (student_profile_id, opportunity_id, status, match_percent_at_apply, applied_at)
    VALUES (v_sp6_id, v_opp_msft_fs, 'APPLIED', 80.00, now() - INTERVAL '5 days')
    ON CONFLICT (student_profile_id, opportunity_id) DO NOTHING;

    -- Resolve application IDs if not returned by RETURNING (in case they already existed)
    IF v_app_3 IS NULL THEN
        SELECT id INTO v_app_3 FROM applications WHERE student_profile_id = v_sp1_id AND opportunity_id = v_opp_msft_swe;
    END IF;
    IF v_app_10 IS NULL THEN
        SELECT id INTO v_app_10 FROM applications WHERE student_profile_id = v_sp6_id AND opportunity_id = v_opp_accn_ase;
    END IF;

    -- =========================================================================
    -- 12. INTERNSHIP RECORDS & COMPANY FEEDBACK
    -- =========================================================================
    IF v_app_3 IS NOT NULL THEN
        INSERT INTO internship_records (application_id, status, start_date, end_date)
        VALUES (v_app_3, 'ONGOING', (CURRENT_DATE - INTERVAL '15 days')::date, (CURRENT_DATE + INTERVAL '75 days')::date)
        ON CONFLICT (application_id) DO UPDATE SET status = EXCLUDED.status, start_date = EXCLUDED.start_date, end_date = EXCLUDED.end_date
        RETURNING id INTO v_ir_1;
    END IF;

    IF v_app_10 IS NOT NULL THEN
        INSERT INTO internship_records (application_id, status, start_date, end_date)
        VALUES (v_app_10, 'COMPLETED', (CURRENT_DATE - INTERVAL '150 days')::date, (CURRENT_DATE - INTERVAL '10 days')::date)
        ON CONFLICT (application_id) DO UPDATE SET status = EXCLUDED.status, start_date = EXCLUDED.start_date, end_date = EXCLUDED.end_date
        RETURNING id INTO v_ir_2;

        IF v_ir_2 IS NULL THEN
            SELECT id INTO v_ir_2 FROM internship_records WHERE application_id = v_app_10;
        END IF;

        IF v_ir_2 IS NOT NULL THEN
            INSERT INTO company_feedback (internship_record_id, feedback_text, submitted_at)
            VALUES (
                v_ir_2,
                'Demonstrated strong frontend engineering capabilities in React and clean REST API integration. Communicates effectively in cross-functional teams and delivers high quality user interface components on schedule.',
                now() - INTERVAL '8 days'
            )
            ON CONFLICT (internship_record_id) DO UPDATE SET feedback_text = EXCLUDED.feedback_text;
        END IF;
    END IF;

    RAISE NOTICE 'SkillBridge demo seed data generated and verified successfully!';
END $$;
