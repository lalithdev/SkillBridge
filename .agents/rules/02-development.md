---
trigger: always_on
---

# SkillBridge — Development Rules

## Technology Stack

Frontend:
- React
- Vite
- JavaScript
- Tailwind CSS
- shadcn/ui
- React Router
- Axios
- useState / useReducer
- Recharts

Backend:
- Spring Boot
- Java
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- REST APIs

Database:
- PostgreSQL

API:
- REST
- OpenAPI / Swagger

Validation:
- Jakarta Bean Validation

Optional:
- Docker
- GitHub Actions

Do NOT add:
- Redux
- OAuth
- Redis
- Kafka
- Elasticsearch
- GraphQL
- Microservices
- Kubernetes

Important:
JavaScript must be used for the frontend.
Do not introduce TypeScript.

Keep the rule concise.
Do not modify unrelated rules.

## Architecture

Use a modular monolith.

Do not introduce microservices unless explicitly approved.

Backend separation:

Controller
→ Service(ServiceImpl also)
→ Repository
→ Entity

Do not put business logic in controllers.

## Frontend

Use reusable components.

Do not duplicate UI unnecessarily.

Handle:
- loading
- success
- empty
- error
states.

## Backend

Use DTOs for API boundaries.

Validate incoming requests.

Use centralized exception handling.

Do not expose database entities directly through APIs.

Use proper HTTP status codes.

## Database

Use migrations.

Do not manually modify production schema.

Use foreign keys and constraints.

Avoid unnecessary tables.

## Security

Never hardcode:
- passwords
- JWT secrets
- API keys
- database credentials

Use environment variables.

Every protected endpoint must enforce authorization.

## General Rule

Prefer the simplest implementation that satisfies
the approved requirements.

Do not introduce dependencies without a reason.