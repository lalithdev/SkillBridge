---
name: backend-development
description: Backend implementation standards, modular monolith architecture, Spring Boot, JPA, and security rules for SkillBridge.
---

# SkillBridge — Backend Development Skill

## Purpose
Guide the backend implementation in Spring Boot, maintaining modular monolith architecture, DTO boundaries, and security standards.

## Required Behavior
1. **Architecture:** Modular monolith. Follow `Controller -> Service(Impl) -> Repository -> Entity` pattern.
2. **DTO Boundaries:** Never expose database entities directly via REST controllers. Use DTOs for request inputs and response payloads.
3. **Validation & Errors:** Use Jakarta Bean Validation (`@Valid`, `@NotNull`, etc.) and centralized `GlobalExceptionHandler`.
4. **Security & Authorization:** Enforce method-level `@PreAuthorize` on protected endpoints using JWT claims (`userId`, `role`, `collegeId`, `companyProfileId`).
5. **OpenAPI Conformance:** Endpoints must strictly match `docs/06-api/openapi.yaml`.
