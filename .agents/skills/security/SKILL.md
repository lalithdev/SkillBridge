---
name: security
description: Security guidelines, JWT handling, authentication, authorization, and secret hygiene for SkillBridge.
---

# SkillBridge — Security Skill

## Purpose
Enforce security standards across backend and frontend, protecting authentication secrets, API authorization, and data privacy.

## Required Behavior
1. **Secret Hygiene:** Never hardcode or commit passwords, JWT secrets, database credentials, or API keys. Use environment variables.
2. **Stateless JWT:** HS256-signed tokens with 24h expiration. Frontend stores token in `sessionStorage` and `AuthContext`.
3. **Defense in Depth:** Frontend route guards provide UX routing, but Spring Security backend `@PreAuthorize` rules enforce true authorization and data scoping.
4. **Institutional Data Privacy:**
   - Students can only view/mutate their own profile and applications.
   - Companies can only view applicants for postings they own.
   - Colleges can only access students and feedback affiliated with their own institution (`collegeId` derived from token).
