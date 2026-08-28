# SkillBridge — Git & Collaboration Workflow

## Purpose
Define the version control and collaboration standards for the two-person hackathon team (one Backend developer and one Frontend developer working concurrently in the same repository).

---

## 1. Branch Strategy & Structure

The repository maintains two primary development tracks stemming from `main`:

```
main (Production / Stable Baseline)
├── backend (Active Backend Track)
│   ├── backend/auth
│   ├── backend/opportunities
│   ├── backend/applications
│   └── backend/analytics
│
└── frontend (Active Frontend Track)
    ├── frontend/auth
    ├── frontend/student-dashboard
    ├── frontend/opportunity-browse
    └── frontend/college-analytics
```

- **`main`:** Contains verified, integrated, and passing code. Direct commits to `main` are reserved for baseline documentation milestones.
- **`backend` & `frontend`:** Persistent integration branches for the backend and frontend developers respectively.
- **Feature Branches:** Created for isolated vertical slices or screens (`backend/<feature>`, `frontend/<screen>`), merged back into their respective track branch once tested.

---

## 2. Core Git Rules

1. **Pull Before Starting:** Always run `git pull --rebase` on your working branch before starting a task to avoid diverged commit histories.
2. **Small Logical Commits:** Make small, atomic commits focused on a single responsibility.
3. **No Cross-Track Interference:** Never modify files on the other developer's feature branch. Keep backend (`apps/api/` or `backend/`) and frontend (`apps/web/` or `frontend/`) changes separate in distinct commits.
4. **Never Force-Push Shared Branches:** `git push --force` is strictly forbidden on `main`, `backend`, and `frontend`.
5. **Never Commit Secrets or Build Artifacts:**
   - Never commit passwords, JWT secrets, database connection strings, or `.env` files.
   - Ensure `.gitignore` ignores `node_modules/`, `target/`, `build/`, `dist/`, `.idea/`, `.vscode/`, `.DS_Store`.
6. **No Unrelated Formatting in Feature Commits:** Do not run whole-project automatic linters or formatters that introduce massive noisy diffs across untouched files.
7. **Careful Conflict Resolution:** When merging, review conflicts line by line. Never blindly accept "ours" or "theirs".
8. **Pre-Merge Verification:** Run build and tests (`npm run build`, `./mvnw test`) before opening a PR or merging into integration branches.

---

## 3. Handling Shared System Documentation

Approved baseline documents (`docs/01-product/`, `docs/02-requirements/`, `docs/03-ux/`, `docs/04-architecture/`, `docs/05-database/`, `docs/06-api/`) are the **shared source of truth** and must not be casually altered from feature branches.

### If an implementation exposes a genuine inconsistency:
1. **Identify** the exact contradiction between the code and specification documents.
2. **Cross-verify** against PRD, SRS, database schemas, and API contracts.
3. **Report** the issue clearly to the team/user before making changes.
4. **Make the smallest justified repair** to the specification document once agreed upon.
5. **Commit the repair separately** with a dedicated `docs:` commit (never bundle doc repairs inside unrelated feature commits).

---

## 4. Conventional Commit Standard

Use semantic, meaningful conventional commit messages:

```
<type>(<scope>): <short description in present tense>
```

### Examples for SkillBridge:
- `docs: define frontend UX specifications`
- `chore: establish frontend agent standards`
- `docs: define project testing strategy`
- `feat(backend): implement authentication vertical slice`
- `feat(frontend): implement authentication screens`
- `feat(frontend): implement student skill management view`
- `fix(backend): correct application status transition check`
- `fix(frontend): resolve missing required skill badge border style`
- `test(backend): add unit tests for skill matching calculation`
- `docs: repair API traceability inconsistency in openapi.yaml`
