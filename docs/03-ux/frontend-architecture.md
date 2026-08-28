# SkillBridge — Frontend Architecture Specification

**Phase:** System Design (UX / Frontend Architecture)  
**Version:** 1.0  
**Status:** APPROVED — Frontend Architecture Baseline  
**Date:** 2026-08-28  
**Derived from:** PRD.md · SRS.md · architecture.md · openapi.yaml · api-design.md · user-flows.md · screen-specifications.md · design-system.md  
**Tech Stack:** React 18, Vite, JavaScript (ES2022, no TypeScript), Tailwind CSS, shadcn/ui, React Router v6, Axios, TanStack Query v5, Recharts, Lucide React  

---

## Table of Contents

1. [Architectural Overview & Core Tenets](#1-architectural-overview--core-tenets)
2. [Folder & Package Structure](#2-folder--package-structure)
3. [Routing & Role-Based Access Control Architecture](#3-routing--role-based-access-control-architecture)
4. [API Layer & Axios Interceptor Architecture](#4-api-layer--axios-interceptor-architecture)
5. [Server State Management (TanStack Query)](#5-server-state-management-tanstack-query)
6. [Client State & Authentication Session Lifecycle](#6-client-state--authentication-session-lifecycle)
7. [Component Architecture & shadcn/ui Integration](#7-component-architecture--shadcnui-integration)
8. [Form Handling & Validation Architecture](#8-form-handling--validation-architecture)
9. [Data Visualization & Chart Architecture (Recharts)](#9-data-visualization--chart-architecture-recharts)
10. [Error, Loading & Empty State Architecture](#10-error-loading--empty-state-architecture)
11. [Responsive & Layout Shell Architecture](#11-responsive--layout-shell-architecture)
12. [Frontend Security Architecture](#12-frontend-security-architecture)
13. [Frontend-Backend Contract Integration Boundaries](#13-frontend-backend-contract-integration-boundaries)
14. [Frontend AI Agent & Developer Execution Rules](#14-frontend-ai-agent--developer-execution-rules)

---

## 1. Architectural Overview & Core Tenets

The SkillBridge frontend is engineered as a **modular, domain-driven, single-page application (SPA)** built with React 18 and Vite.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              React Router v6                                │
│                     (Route Guards & Role Dispatcher)                        │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
         ┌─────────────────────────────┼─────────────────────────────┐
         ▼                             ▼                             ▼
  ┌──────────────┐              ┌──────────────┐              ┌──────────────┐
  │ Public Shell │              │  Auth Shell  │              │  App Shell   │
  │ (Landing)    │              │(Login/Reg/403│              │ (Role Nav)   │
  └──────────────┘              └──────────────┘              └──────┬───────┘
                                                                     │
  ┌──────────────────────────────────────────────────────────────────┴───────┐
  │                            Feature Modules                               │
  │   ├── student/       ├── company/       ├── college/       ├── admin/    │
  │   ├── matching/      ├── applications/  ├── internships/   ├── skills/   │
  └──────────────────────────────────┬───────────────────────────────────────┘
                                     │
         ┌───────────────────────────┴───────────────────────────┐
         ▼                                                       ▼
  ┌──────────────────────────────┐                ┌──────────────────────────┐
  │   Local UI State             │                │  Server State            │
  │   (useState / useReducer /   │                │  (TanStack Query v5 /    │
  │    AuthContext)              │                │   Query Cache)           │
  └──────────────────────────────┘                └──────────────┬───────────┘
                                                                 │
                                                  ┌──────────────▼───────────┐
                                                  │ Axios HTTP Client        │
                                                  │ (JWT Auth / Error Inter) │
                                                  └──────────────┬───────────┘
                                                                 │ (REST JSON)
                                                                 ▼
                                                  ┌──────────────────────────┐
                                                  │ Spring Boot Monolith API │
                                                  │ (/api/v1/...)            │
                                                  └──────────────────────────┘
```

### Core Tenets
1. **JavaScript Only:** 100% pure JavaScript (`.js`, `.jsx`). No TypeScript interfaces, types, or build tooling (per `rules/02-development.md`).
2. **Strict OpenAPI Conformance:** All API calls, request bodies, and response consumption map 1:1 to [`docs/06-api/openapi.yaml`](file:///e:/LALITH%20PROJECTS/SIH%202026%20-%20PS044/SkillBridge/skillbridge/docs/06-api/openapi.yaml).
3. **Server State vs. Client State Separation:**
   - **Server State** (data fetching, caching, deduplication, invalidation) is managed exclusively via **TanStack Query**.
   - **Client State** (form inputs, active modal visibility, UI toggles) is managed via **`useState`** / **`useReducer`**.
   - **Global Session State** is restricted to a lightweight **`AuthContext`** (user token and claims). No Redux or complex global stores.
4. **Deterministic 4-State UI Contract:** Every data-bound screen and component cleanly implements **Loading** (Skeleton), **Success**, **Empty**, and **Error** states.

---

## 2. Folder & Package Structure

```
apps/web/
├── public/
│   ├── favicon.ico
│   └── logo.svg
│
├── src/
│   ├── assets/                   # Static images, icons, illustrations
│   │
│   ├── api/                      # Axios client & domain API services
│   │   ├── client.js             # Base Axios instance & interceptors
│   │   ├── authApi.js            # /api/v1/auth/*
│   │   ├── studentApi.js         # /api/v1/students/*
│   │   ├── companyApi.js         # /api/v1/companies/*
│   │   ├── collegeApi.js         # /api/v1/colleges/*
│   │   ├── opportunityApi.js     # /api/v1/opportunities/*
│   │   ├── applicationApi.js     # /api/v1/applications/*
│   │   ├── internshipApi.js      # /api/v1/internships/*
│   │   ├── feedbackApi.js        # /api/v1/internships/{id}/feedback, /colleges/feedback
│   │   ├── matchingApi.js        # /api/v1/matching/*
│   │   ├── analyticsApi.js       # /api/v1/analytics/*
│   │   └── adminApi.js           # /api/v1/admin/*, /skills, /departments
│   │
│   ├── components/               # Presentation & reusable UI building blocks
│   │   ├── ui/                   # Direct shadcn/ui components (Tailwind-styled)
│   │   │   ├── button.jsx
│   │   │   ├── input.jsx
│   │   │   ├── textarea.jsx
│   │   │   ├── select.jsx
│   │   │   ├── card.jsx
│   │   │   ├── table.jsx
│   │   │   ├── badge.jsx
│   │   │   ├── dialog.jsx
│   │   │   ├── sheet.jsx
│   │   │   ├── tabs.jsx
│   │   │   ├── alert.jsx
│   │   │   ├── skeleton.jsx
│   │   │   ├── progress.jsx
│   │   │   ├── dropdown-menu.jsx
│   │   │   └── toast.jsx
│   │   │
│   │   ├── layout/               # Shell containers & navigation
│   │   │   ├── AppShell.jsx      # Authenticated shell (Header + dynamic Sidebar)
│   │   │   ├── PublicNavbar.jsx  # Unauthenticated public navbar
│   │   │   ├── Sidebar.jsx       # Dynamic role-scoped navigation drawer
│   │   │   └── Header.jsx        # Sticky top bar with profile & logout
│   │   │
│   │   └── shared/               # Domain-specific reusable components
│   │       ├── SkillBadge.jsx        # Tagged skill badge with category/match style
│   │       ├── MatchScoreRing.jsx    # Circular progress match indicator
│   │       ├── VerificationBadge.jsx # Trust badge for verified orgs
│   │       ├── StatusStepper.jsx     # Visual recruitment pipeline stage tracker
│   │       ├── EmptyState.jsx        # Standard empty container with icon & CTA
│   │       ├── ErrorMessage.jsx      # Retryable error callout banner
│   │       └── LoadingSpinner.jsx    # Centered spinner loader
│   │
│   ├── context/                  # React Context providers
│   │   └── AuthContext.jsx       # JWT token, session status, user role claims
│   │
│   ├── features/                 # Domain-specific business components & hooks
│   │   ├── auth/                 # LoginForm, RegisterForm, RoleSelector
│   │   ├── students/             # ProfileCard, SkillPicker, ResumeUploader
│   │   ├── opportunities/        # OpportunityCard, OpportunityFilter, PostingForm
│   │   ├── applications/         # CandidateTable, StageTransitionModal, ApplicationTracker
│   │   ├── internships/          # InternRoster, FeedbackModal, FeedbackCard
│   │   ├── analytics/            # SkillGapChart, SkillDemandChart, PlacementFunnelChart
│   │   └── admin/                # UserTable, VerificationQueue, SkillsTaxonomyTable
│   │
│   ├── hooks/                    # Reusable React & TanStack Query custom hooks
│   │   ├── useAuth.js            # Consumes AuthContext
│   │   ├── useOpportunities.js   # TanStack query & mutation hooks for postings
│   │   ├── useApplications.js    # TanStack query & mutation hooks for pipeline
│   │   ├── useAnalytics.js       # TanStack query hooks for college gap/funnel
│   │   └── useSkills.js          # Master taxonomy typeahead & profile skills
│   │
│   ├── pages/                    # Route-level screens (One file per screen specification)
│   │   ├── public/               # LandingPage, UnauthorizedPage, NotFoundPage
│   │   ├── auth/                 # LoginPage, RegisterPage
│   │   ├── student/              # Dashboard, Profile, Skills, Opportunities, Detail, Apps, Internships
│   │   ├── company/              # Dashboard, Profile, PostOpportunity, Manage, Applicants, Interns
│   │   ├── college/              # Dashboard, Profile, Students, Availability, Demand, SkillGap, Funnel, Feedback
│   │   └── admin/                # Dashboard, Users, Verifications, Skills, Departments, Moderation
│   │
│   ├── routes/                   # Routing configuration & route guards
│   │   ├── AppRoutes.jsx         # Complete application route tree
│   │   └── ProtectedRoute.jsx    # JWT check & role-based route guard
│   │
│   ├── utils/                    # Pure utility functions
│   │   ├── formatters.js         # Currency (INR), date (DD MMM YYYY), percentages
│   │   ├── validators.js         # CGPA bounds, password length, file type checks
│   │   └── constants.js          # Pipeline stages, gap thresholds, route paths
│   │
│   ├── App.jsx                   # QueryClientProvider, AuthProvider, RouterProvider
│   ├── index.css                 # Tailwind directives, CSS custom variables, fonts
│   └── main.jsx                  # React DOM root render
│
├── index.html
├── package.json
├── tailwind.config.js
└── vite.config.js
```

---

## 3. Routing & Role-Based Access Control Architecture

### 3.1 Route Hierarchy & Path Registry

```
/ (Public Landing)
├── /login (Public Auth)
├── /register (Public Auth)
├── /unauthorized (403 Error)
│
├── /student (Role: STUDENT)
│   ├── /dashboard
│   ├── /profile
│   ├── /skills
│   ├── /opportunities
│   ├── /opportunities/:id
│   ├── /recommendations
│   ├── /applications
│   └── /internships
│
├── /company (Role: COMPANY)
│   ├── /dashboard
│   ├── /profile
│   ├── /opportunities
│   ├── /opportunities/create
│   ├── /opportunities/:id/edit
│   ├── /opportunities/:id/applicants
│   └── /internships
│
├── /college (Role: COLLEGE)
│   ├── /dashboard
│   ├── /profile
│   ├── /students
│   ├── /analytics/availability
│   ├── /analytics/demand
│   ├── /analytics/skill-gap
│   ├── /analytics/funnel
│   └── /feedback
│
└── /admin (Role: ADMIN)
    ├── /dashboard
    ├── /users
    ├── /verifications
    ├── /skills
    ├── /departments
    └── /opportunities
```

### 3.2 Protected Route Guard Architecture (`ProtectedRoute.jsx`)

The route guard executes deterministic checks before mounting child route components:

```javascript
// src/routes/ProtectedRoute.jsx
import React from 'react';
import { Navigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import LoadingSpinner from '../components/shared/LoadingSpinner';

export default function ProtectedRoute({ allowedRoles }) {
  const { user, token, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return <LoadingSpinner fullScreen text="Verifying session..." />;
  }

  // 1. Unauthenticated -> Redirect to Login with return path
  if (!token || !user) {
    return <Navigate to={`/login?redirect=${encodeURIComponent(location.pathname)}`} replace />;
  }

  // 2. Role mismatch -> Redirect to Unauthorized 403 page
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  // 3. Authorized -> Render requested route view
  return <Outlet />;
}
```

---

## 4. API Layer & Axios Interceptor Architecture

### 4.1 Axios Central Configuration (`src/api/client.js`)

```javascript
// src/api/client.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  timeout: 15000,
});

// Request Interceptor: Attach Bearer JWT
apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('skillbridge_jwt');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Central Error Handling & 401 Session Purge
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response ? error.response.status : null;

    if (status === 401) {
      // Token expired or invalid -> purge local storage and broadcast logout
      sessionStorage.removeItem('skillbridge_jwt');
      sessionStorage.removeItem('skillbridge_user');
      if (window.location.pathname !== '/login') {
        window.location.href = `/login?expired=true&redirect=${encodeURIComponent(window.location.pathname)}`;
      }
    }

    // Standardize error payload extraction matching Spring GlobalExceptionHandler
    const standardError = {
      status: status || 500,
      message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      fieldErrors: error.response?.data?.fieldErrors || {},
      raw: error,
    };

    return Promise.reject(standardError);
  }
);
```

### 4.2 API Service Modules Mapping to OpenAPI

Every API service file encapsulates domain endpoints strictly matching `openapi.yaml`:

```javascript
// src/api/matchingApi.js
import { apiClient } from './client';

export const matchingApi = {
  // GET /api/v1/matching/opportunities/{id}
  getOpportunityMatch: (opportunityId) =>
    apiClient.get(`/matching/opportunities/${opportunityId}`).then((res) => res.data),

  // GET /api/v1/matching/recommendations?page=0&size=10
  getRecommendations: (params = { page: 0, size: 10 }) =>
    apiClient.get('/matching/recommendations', { params }).then((res) => res.data),
};
```

---

## 5. Server State Management (TanStack Query)

TanStack Query v5 is the authoritative server state manager for SkillBridge. It provides automatic query caching, request deduplication, optimistic UI updates, and predictable cache invalidation.

### 5.1 Query Client Configuration (`src/App.jsx`)

```javascript
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 2, // 2 minutes fresh window
      gcTime: 1000 * 60 * 10,    // 10 minutes cache garbage collection
      retry: (failureCount, error) => {
        // Do not retry 401, 403, or 404 client errors
        if ([401, 403, 404].includes(error?.status)) return false;
        return failureCount < 2;
      },
      refetchOnWindowFocus: false, // Prevents aggressive background refetches
    },
  },
});
```

### 5.2 Standard Query Key Hierarchy

```javascript
export const queryKeys = {
  auth: {
    me: ['auth', 'me'],
  },
  students: {
    profile: (id) => ['students', 'profile', id],
    skills: ['students', 'skills'],
    resume: (id) => ['students', 'resume', id],
  },
  opportunities: {
    all: (filters) => ['opportunities', 'list', filters],
    detail: (id) => ['opportunities', 'detail', id],
    myCompany: (filters) => ['opportunities', 'company', filters],
  },
  matching: {
    opportunity: (id) => ['matching', 'opportunity', id],
    recommendations: (params) => ['matching', 'recommendations', params],
  },
  applications: {
    my: (params) => ['applications', 'my', params],
    byOpportunity: (oppId, stage) => ['applications', 'opportunity', oppId, { stage }],
  },
  internships: {
    my: ['internships', 'my'],
    company: ['internships', 'company'],
    feedback: (id) => ['internships', 'feedback', id],
  },
  analytics: {
    availability: ['analytics', 'skills', 'availability'],
    demand: (type) => ['analytics', 'skills', 'demand', { type }],
    gap: ['analytics', 'skills', 'gap'],
    funnel: (deptId) => ['analytics', 'funnel', { deptId }],
  },
  admin: {
    users: (filters) => ['admin', 'users', filters],
    verifications: ['admin', 'verifications'],
  },
  skills: {
    master: (search) => ['skills', 'master', { search }],
  },
  departments: {
    master: ['departments', 'master'],
  },
};
```

### 5.3 Mutation & Invalidation Pattern

```javascript
// src/hooks/useApplications.js
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { applicationApi } from '../api/applicationApi';
import { queryKeys } from '../utils/constants';

export function useSubmitApplication() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (opportunityId) => applicationApi.submit(opportunityId),
    onSuccess: (newApplication, opportunityId) => {
      // Invalidate relevant caches to trigger instant UI refresh
      queryClient.invalidateQueries({ queryKey: queryKeys.applications.my() });
      queryClient.invalidateQueries({ queryKey: queryKeys.matching.opportunity(opportunityId) });
      queryClient.invalidateQueries({ queryKey: queryKeys.matching.recommendations() });
    },
  });
}
```

---

## 6. Client State & Authentication Session Lifecycle

### 6.1 State Categorization Matrix

| State Type | Management Mechanism | Scope | Examples |
|---|---|---|---|
| **Auth Session** | `AuthContext` + `sessionStorage` | App-wide | JWT token, user role, userId, collegeId |
| **Server Data** | TanStack Query Cache | App-wide | Opportunities, student profile, gap analytics |
| **Form Inputs** | `useState` | Page/Component | Email/password, opportunity creation fields |
| **Complex UI Flow** | `useReducer` | Component | Multi-step candidate filter & sort parameters |
| **Modals / Drawers** | `useState(boolean)` | Component | `isFeedbackOpen`, `isResumeUploadOpen` |

### 6.2 AuthContext Architecture (`src/context/AuthContext.jsx`)

```javascript
// src/context/AuthContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => sessionStorage.getItem('skillbridge_jwt'));
  const [user, setUser] = useState(() => {
    const saved = sessionStorage.getItem('skillbridge_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function initAuth() {
      if (token) {
        try {
          const profile = await authApi.getMe();
          setUser(profile);
          sessionStorage.setItem('skillbridge_user', JSON.stringify(profile));
        } catch {
          // Token invalid -> clear
          logout();
        }
      }
      setIsLoading(false);
    }
    initAuth();
  }, [token]);

  const login = (authResponse) => {
    const { token: jwt, ...userData } = authResponse;
    setToken(jwt);
    setUser(userData);
    sessionStorage.setItem('skillbridge_jwt', jwt);
    sessionStorage.setItem('skillbridge_user', JSON.stringify(userData));
  };

  const logout = async () => {
    try {
      if (token) await authApi.logout();
    } catch {
      // Best-effort logout acknowledgement
    } finally {
      setToken(null);
      setUser(null);
      sessionStorage.removeItem('skillbridge_jwt');
      sessionStorage.removeItem('skillbridge_user');
    }
  };

  return (
    <AuthContext.Provider value={{ token, user, isLoading, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}
```

---

## 7. Component Architecture & shadcn/ui Integration

### 7.1 Component Tier Hierarchy

```
┌────────────────────────────────────────────────────────┐
│ Page Components (pages/*)                              │
│ - Connects route params, TanStack hooks, and layout   │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│ Feature Modules (features/*)                           │
│ - Domain cards, tables, forms, modals                  │
│ - Dispatches mutations and triggers state changes      │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│ Shared Domain Components (components/shared/*)         │
│ - SkillBadge, MatchScoreRing, StatusStepper, EmptyState│
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│ Base UI Primitives (components/ui/*)                   │
│ - shadcn/ui buttons, inputs, dialogs, cards, badges    │
└────────────────────────────────────────────────────────┘
```

### 7.2 shadcn/ui Primitive Usage Mapping

- Avoid writing ad-hoc raw `<button>` or `<div>` modals.
- Always use the predefined shadcn primitives in `src/components/ui/`:
  - Modals: `<Dialog>`, `<DialogContent>`, `<DialogHeader>`, `<DialogFooter>`
  - Drawers: `<Sheet>`, `<SheetContent>`, `<SheetHeader>`
  - Menus: `<DropdownMenu>`, `<DropdownMenuContent>`, `<DropdownMenuItem>`
  - Inputs: `<Input>`, `<Textarea>`, `<Select>`, `<Checkbox>`
  - Layout: `<Card>`, `<CardHeader>`, `<CardTitle>`, `<CardContent>`, `<CardFooter>`

---

## 8. Form Handling & Validation Architecture

### 8.1 Pure React Controlled Forms
Forms use pure React state (`useState`) without heavy third-party form libraries.

```javascript
// Example Form Pattern
const [formData, setFormData] = useState(initialState);
const [fieldErrors, setFieldErrors] = useState({});
const [isSubmitting, setIsSubmitting] = useState(false);

const handleChange = (e) => {
  const { name, value } = e.target;
  setFormData((prev) => ({ ...prev, [name]: value }));
  if (fieldErrors[name]) {
    setFieldErrors((prev) => ({ ...prev, [name]: null }));
  }
};
```

### 8.2 Client-Side Validation Rules
- **CGPA:** Must be numeric between `0.00` and `10.00`.
- **Password:** Minimum 8 characters.
- **Required Skills on Posting:** `requiredSkillIds.length >= 1`.
- **Deadline Date:** Must be strictly future date (`deadline > new Date()`).
- **File Uploads:** MIME type check (`application/pdf`, `.docx`), max size 5 MB ($5{,}242{,}880\text{ bytes}$).

### 8.3 Server Error Mapping
When API returns `400 Bad Request` with field validation errors:
```json
{
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": {
    "cgpa": "CGPA must be between 0.0 and 10.0",
    "requiredSkillIds": "At least one required skill must be specified"
  }
}
```
The frontend catches the response in the mutation `onError` handler and maps `error.fieldErrors` directly into `setFieldErrors()`, highlighting the offending inputs inline.

---

## 9. Data Visualization & Chart Architecture (Recharts)

All analytical visualizations for the College Dashboard (SCR-COL-04, 05, 06, 07) are built with **Recharts** wrapped in responsive containers.

```javascript
// src/features/analytics/SkillGapChart.jsx
import React from 'react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';

export default function SkillGapChart({ data }) {
  // data: [{ skillName: 'Java', demandPct: 65, availabilityPct: 40, gapPct: 25 }]
  return (
    <div className="w-full h-80">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#E2E8F0" />
          <XAxis dataKey="skillName" tick={{ fill: '#475569', fontSize: 12 }} />
          <YAxis unit="%" tick={{ fill: '#475569', fontSize: 12 }} domain={[0, 100]} />
          <Tooltip content={<CustomGapTooltip />} />
          <Legend wrapperStyle={{ paddingTop: '10px' }} />
          <Bar dataKey="demandPct" name="Industry Demand %" fill="#3B82F6" radius={[4, 4, 0, 0]} />
          <Bar dataKey="availabilityPct" name="Student Availability %" fill="#10B981" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
```

---

## 10. Error, Loading & Empty State Architecture

### 10.1 Uniform Page State Pattern

Every page component implements the standard 4-state switch:

```javascript
export default function StudentApplicationsPage() {
  const { data: applications, isLoading, isError, error, refetch } = useMyApplications();

  if (isLoading) {
    return <ApplicationsTableSkeleton />;
  }

  if (isError) {
    return <ErrorMessage message={error.message} onRetry={refetch} />;
  }

  if (!applications || applications.length === 0) {
    return (
      <EmptyState
        title="No applications submitted"
        description="You have not applied to any opportunities yet. Explore open postings to get started."
        action={<Button href="/student/opportunities">Explore Postings</Button>}
      />
    );
  }

  return <ApplicationsTable applications={applications} />;
}
```

---

## 11. Responsive & Layout Shell Architecture

### 11.1 AppShell Layout (`src/components/layout/AppShell.jsx`)

The AppShell integrates a responsive sidebar and sticky top header:

- **Desktop ($\ge 1024px$):** Fixed left navigation rail (`w-64`), main scrollable content area (`ml-64 p-8`).
- **Tablet / Mobile ($< 1024px$):** Left sidebar hidden behind a mobile burger icon triggering a shadcn `<Sheet>` slide-over drawer; main content uses full width (`p-4`).

### 11.2 Data-Heavy Screen Adaptation
- Tables on screens $< 768px$ switch from HTML `<table>` grids to vertical stacked cards with touch-friendly action buttons.

---

## 12. Frontend Security Architecture

1. **No Client Secrets:** No backend API keys, JWT secret keys, or database credentials exist in frontend code or `.env` files.
2. **JWT Storage in `sessionStorage`:** To prevent persistent token extraction across browser restarts, active tokens reside only in `sessionStorage` and React memory.
3. **Defense in Depth:** Frontend route guards (`ProtectedRoute`) provide smooth user UX, but **all authorization and resource access rules are enforced by Spring Security backend APIs**.
4. **Sanitized User Output:** React default JSX string escaping prevents Cross-Site Scripting (XSS) in candidate profiles and qualitative company feedback.

---

## 13. Frontend-Backend Contract Integration Boundaries

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API Contract Rule                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. The OpenAPI specification at docs/06-api/openapi.yaml is the SOLE        │
│    source of truth for endpoints, DTO field names, and HTTP methods.        │
│ 2. The frontend MUST NOT invent mock endpoints or modify URL paths.         │
│ 3. If an endpoint is missing or returns unexpected data, STOP and report    │
│    the inconsistency rather than altering frontend/backend contracts.       │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 14. Frontend AI Agent & Developer Execution Rules

When implementing frontend components and pages:
1. **Follow the Design System:** Strictly use colors, spacing tokens, typography, and shadcn/ui components specified in [`docs/03-ux/design-system.md`](file:///e:/LALITH%20PROJECTS/SIH%202026%20-%20PS044/SkillBridge/skillbridge/docs/03-ux/design-system.md).
2. **Follow Screen Specifications:** Build screens strictly adhering to the 30 screen specifications in [`docs/03-ux/screen-specifications.md`](file:///e:/LALITH%20PROJECTS/SIH%202026%20-%20PS044/SkillBridge/skillbridge/docs/03-ux/screen-specifications.md).
3. **No Unapproved Libraries:** Do not install Redux, MobX, TypeScript, Formik, styled-components, or other external state/styling frameworks.
4. **Always Implement All 4 UI States:** Never ship a screen missing loading skeleton, empty state, or retryable error handling.
5. **Enforce Skill Coverage Semantics:** Always include the coverage vs. proficiency disclaimer on all match and availability indicators.

---

*SkillBridge Frontend Architecture Specification Complete.*  
*Status: APPROVED — Ready for Frontend Code Implementation.*
