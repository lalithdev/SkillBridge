export const ROLE_MAP = {
  student: 'STUDENT',
  company: 'COMPANY',
  college: 'COLLEGE',
  admin: 'ADMIN',
};

export function normalizeRole(role) {
  if (!role) return '';
  const value = String(role).trim();
  const key = value.toLowerCase();
  return ROLE_MAP[key] || value.toUpperCase();
}

export function getDashboardPath(role) {
  const normalized = normalizeRole(role);
  const routeMap = {
    STUDENT: '/student/dashboard',
    COMPANY: '/company/dashboard',
    COLLEGE: '/college/dashboard',
    ADMIN: '/admin/dashboard',
  };
  return routeMap[normalized] || '/';
}

export function getDisplayName(user) {
  if (!user) return 'User';
  return user.name || user.displayName || user.email?.split('@')[0] || 'User';
}
