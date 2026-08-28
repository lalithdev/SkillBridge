# SkillBridge — Architecture Guardrails

## Purpose
Protect the approved SkillBridge system architecture during AI-assisted development and collaborative feature implementation.

---

## 1. Hierarchy of Authority

All architectural decisions, database schemas, API contracts, and implementation code must strictly flow top-down according to this hierarchy:

```
      PRD (Product Requirements Document)
                      ↓
       SRS (Software Requirements Spec)
                      ↓
          UX / Screen Specifications
                      ↓
            Technical Architecture
                      ↓
               Database Design
                      ↓
              REST API Contract
                      ↓
                Implementation
```

> **Core Rule:** Implementation must conform to the approved design. Implementation code must NEVER redefine or alter the approved design without explicit approval.

---

## 2. Locked Technology Stack

Every component must adhere strictly to the approved stack:

| Domain | Approved Technology | Prohibited Additions |
|---|---|---|
| **Architecture Style** | Modular Monolith | Microservices, Distributed Gateways |
| **Backend** | Spring Boot, Java, Spring Security (JWT), Spring Data JPA / Hibernate, Jakarta Validation | OAuth2 servers, Redis, Kafka, Elasticsearch, GraphQL, Kubernetes |
| **Database** | PostgreSQL, Flyway Migrations | MongoDB, NoSQL, In-memory only DBs in prod |
| **Frontend** | React 18, Vite, **JavaScript (ES2022)**, Tailwind CSS, shadcn/ui, React Router, Axios, TanStack Query, Recharts | **TypeScript (`.ts`, `.tsx`)**, Redux, MobX, Material UI, Chakra UI, Next.js |
| **API Boundary** | RESTful HTTP under `/api/v1`, OpenAPI 3.0.3 (`docs/06-api/openapi.yaml`) | RPC, gRPC, WebSockets (non-MVP) |

---

## 3. Strict Architectural Invariants

1. **No Microservices or Unapproved Middleware:**
   SkillBridge is a single deployable modular monolith. Do not introduce message queues (Kafka/RabbitMQ), caching tiers (Redis), or microservice orchestration.
2. **No Invented APIs or Schema Alterations:**
   - Frontend must strictly consume endpoints and DTO shapes defined in `docs/06-api/openapi.yaml`.
   - Backend must strictly implement entities, constraints, and tables defined in `docs/05-database/database.md`.
   - Never invent new endpoints, request/response fields, database columns, user roles, or business rules.
3. **No TypeScript in Frontend:**
   The frontend codebase must remain pure JavaScript (`.js`, `.jsx`). Do not add TypeScript configs, `.ts`/`.tsx` files, or type interfaces.
4. **Scope Control & Deferred Features:**
   Features marked as "Should-Have" or "Later" in PRD/SRS (e.g. AI JD extraction, automated recommendations, external email dispatch) must remain out of scope for the MVP build.
5. **Independent Evolution with Shared Contract:**
   Backend and frontend developers work simultaneously. Both tracks may evolve independently, but **both must strictly conform to the common OpenAPI contract**.
6. **No Silent Architectural Modifications:**
   Never silently alter an API endpoint, database relationship, or architecture rule because it appears easier to implement in code.
7. **Minimal Changes & Preservation of Working Behavior:**
   Prefer minimal, surgical, clean edits over broad refactorings. Never overwrite or dismantle approved working components.
8. **Contradiction Escalation Protocol:**
   If a genuine architectural contradiction or blocking missing requirement is discovered during coding:
   - **STOP immediately.**
   - Identify the exact conflicting specifications.
   - Report the finding clearly to the team rather than silently deciding on an ad-hoc fix.
