export const STORAGE_KEYS = {
  JWT: 'skillbridge_jwt',
  USER: 'skillbridge_user',
  MOCK_REGISTRY: 'skillbridge_mock_registry',
};

/** When true, API modules use the mock data layer. Default is false (real backend). */
export const USE_MOCK_API = import.meta.env.VITE_USE_MOCK_API === 'true';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const SKILL_COVERAGE_DISCLAIMER =
  'Skill match/availability measures self-reported skill presence and curriculum coverage, not verified individual proficiency.';

/** Shown when a write action is not yet wired to the backend API layer. */
export const INTEGRATION_PENDING_MSG =
  'This action will be available after backend integration.';

export const queryKeys = {
  auth: { me: ['auth', 'me'] },
  students: {
    profile: ['students', 'profile'],
    skills: ['students', 'skills'],
    applications: (status) => ['students', 'applications', { status }],
    skillGaps: ['students', 'skillGaps'],
    internships: ['students', 'internships'],
  },
  opportunities: {
    all: (filters) => ['opportunities', 'list', filters],
    detail: (id) => ['opportunities', 'detail', id],
    recommendations: ['opportunities', 'recommendations'],
  },
  company: {
    profile: ['company', 'profile'],
    opportunities: ['company', 'opportunities'],
    candidates: (filters) => ['company', 'candidates', filters],
    candidate: (id) => ['company', 'candidate', id],
    applications: (status) => ['company', 'applications', { status }],
    internships: ['company', 'internships'],
  },
  college: {
    profile: ['college', 'profile'],
    stats: ['college', 'stats'],
    students: (filters) => ['college', 'students', filters],
    skillStatistics: ['college', 'skillStatistics'],
    industryDemand: ['college', 'industryDemand'],
    skillGaps: ['college', 'skillGaps'],
    recruitment: ['college', 'recruitment'],
  },
  admin: {
    users: ['admin', 'users'],
    verifications: ['admin', 'verifications'],
    skills: ['admin', 'skills'],
    opportunities: ['admin', 'opportunities'],
  },
  platform: { stats: ['platform', 'stats'] },
  skills: { master: ['skills', 'master'] },
};
