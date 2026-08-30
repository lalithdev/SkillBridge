/**
 * TEMPORARY mock authentication store for pre-backend development.
 * Registered accounts persist in sessionStorage only for the browser session.
 * Replace with real authApi calls when the backend is available.
 */
import { STORAGE_KEYS } from '@/utils/constants';
import { normalizeRole } from '@/utils/auth';
import { mockDelay, mockReject } from './mockDelay';

let nextUserId = 1000;
let nextProfileId = 5000;

function createToken() {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return `mock_jwt_${crypto.randomUUID()}`;
  }
  return `mock_jwt_${Date.now()}`;
}

function readRegistry() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEYS.MOCK_REGISTRY);
    return raw ? JSON.parse(raw) : { users: [] };
  } catch {
    return { users: [] };
  }
}

function writeRegistry(registry) {
  sessionStorage.setItem(STORAGE_KEYS.MOCK_REGISTRY, JSON.stringify(registry));
}

function buildAuthResponse(user) {
  const response = {
    token: createToken(),
    role: user.role,
    userId: user.userId,
  };
  if (user.studentProfileId) response.studentProfileId = user.studentProfileId;
  if (user.companyProfileId) response.companyProfileId = user.companyProfileId;
  if (user.collegeId) response.collegeId = user.collegeId;
  return response;
}

function buildCurrentUser(user) {
  return {
    userId: user.userId,
    email: user.email,
    role: user.role,
    isActive: true,
    name: user.name,
    studentProfileId: user.studentProfileId,
    companyProfileId: user.companyProfileId,
    collegeId: user.collegeId,
    phone: user.phone,
    collegeName: user.collegeName,
    companyName: user.companyName,
    website: user.website,
    graduationYear: user.graduationYear,
  };
}

function persistSession(authResponse, user) {
  sessionStorage.setItem(STORAGE_KEYS.JWT, authResponse.token);
  sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(buildCurrentUser(user)));
}

export async function mockLogin({ email, password }) {
  const registry = readRegistry();
  const targetEmail = String(email || '').trim().toLowerCase();
  const existing = registry.users.find((u) => u.email === targetEmail);

  if (!existing) {
    return mockReject('Invalid email or password.');
  }
  if (existing.password !== String(password || '')) {
    return mockReject('Invalid email or password.');
  }
  if (!existing.isActive) {
    return mockReject('Account has been deactivated. Please contact platform administrator.');
  }

  const authResponse = buildAuthResponse(existing);
  persistSession(authResponse, existing);
  return mockDelay(authResponse);
}

export async function mockRegister(profile) {
  const registry = readRegistry();
  const email = String(profile.email || '').trim().toLowerCase();
  const role = normalizeRole(profile.role);
  const password = String(profile.password || '');
  const name = String(profile.name || '').trim();

  if (!email) return mockReject('Email is required.');
  if (!name) return mockReject('Name is required.');
  if (!role || !['STUDENT', 'COMPANY', 'COLLEGE'].includes(role)) {
    return mockReject('Invalid registration role.');
  }
  if (password.length < 8) {
    return mockReject('Password must be at least 8 characters.');
  }
  if (registry.users.some((u) => u.email === email)) {
    return mockReject('An account with this email already exists.');
  }

  const userId = nextUserId++;
  const newUser = {
    userId,
    email,
    password,
    role,
    name,
    isActive: true,
    phone: profile.phone || '',
    website: profile.website || '',
    graduationYear: profile.graduationYear || null,
  };

  if (role === 'STUDENT') {
    newUser.studentProfileId = nextProfileId++;
    newUser.collegeId = profile.collegeId || 1;
    newUser.collegeName = profile.collegeName || profile.college || '';
  } else if (role === 'COMPANY') {
    newUser.companyProfileId = nextProfileId++;
    newUser.companyName = profile.companyName || profile.company || name;
  } else if (role === 'COLLEGE') {
    newUser.collegeId = nextProfileId++;
    newUser.collegeName = profile.collegeName || profile.college || name;
  }

  writeRegistry({ users: [...registry.users, newUser] });
  const authResponse = buildAuthResponse(newUser);
  persistSession(authResponse, newUser);
  return mockDelay(authResponse);
}

export async function mockGetMe() {
  const token = sessionStorage.getItem(STORAGE_KEYS.JWT);
  if (!token) {
    return mockReject('Session expired. Please sign in again.', 100);
  }
  const raw = sessionStorage.getItem(STORAGE_KEYS.USER);
  if (!raw) {
    return mockReject('Session expired. Please sign in again.', 100);
  }
  return mockDelay(JSON.parse(raw));
}

export async function mockLogout() {
  sessionStorage.removeItem(STORAGE_KEYS.JWT);
  sessionStorage.removeItem(STORAGE_KEYS.USER);
  return mockDelay(null, 150);
}
