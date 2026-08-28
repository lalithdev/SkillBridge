---
description: Guide for developing a backend feature or vertical slice in SkillBridge.
---

# Backend Feature Development

## Quick Sequence
1. Read relevant requirements in `docs/01-product/PRD.md` and `docs/02-requirements/SRS.md`.
2. Inspect technical architecture in `docs/04-architecture/architecture.md`.
3. Check database schema and entity mappings in `docs/05-database/database.md`.
4. Verify REST API contract and DTO schemas in `docs/06-api/openapi.yaml` and `docs/06-api/api-design.md`.
5. Implement persistence layer (Entity, Repository) and Flyway migration if schema updated.
6. Implement business service layer (`Service`, `ServiceImpl`) with validation and exceptions.
7. Implement REST controller with `@PreAuthorize` authorization rules and OpenAPI annotations.
8. Write unit and integration tests (`./mvnw test`).
9. Verify clean build (`./mvnw clean package`).
10. Commit with conventional commit message (`feat(backend): ...`).
