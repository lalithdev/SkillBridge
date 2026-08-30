import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { authApi } from '@/api/authApi';
import { STORAGE_KEYS } from '@/utils/constants';
import { getDashboardPath, normalizeRole } from '@/utils/auth';

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEYS.USER);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => sessionStorage.getItem(STORAGE_KEYS.JWT));
  const [user, setUser] = useState(() => readStoredUser());
  const [isLoading, setIsLoading] = useState(() => Boolean(sessionStorage.getItem(STORAGE_KEYS.JWT)));

  useEffect(() => {
    let cancelled = false;

    async function initAuth() {
      if (!token) {
        setIsLoading(false);
        return;
      }

      try {
        const profile = await authApi.getMe();
        if (!cancelled) {
          setUser(profile);
          sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(profile));
        }
      } catch {
        if (!cancelled) {
          sessionStorage.removeItem(STORAGE_KEYS.JWT);
          sessionStorage.removeItem(STORAGE_KEYS.USER);
          setToken(null);
          setUser(null);
        }
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }

    initAuth();
    return () => { cancelled = true; };
  }, [token]);

  const establishSession = useCallback(async (authResponse) => {
    sessionStorage.setItem(STORAGE_KEYS.JWT, authResponse.token);
    setToken(authResponse.token);
    try {
      const profile = await authApi.getMe();
      const combinedUser = {
        ...authResponse,
        ...profile,
        role: normalizeRole(profile.role || authResponse.role),
      };
      setUser(combinedUser);
      sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(combinedUser));
      return { ...combinedUser, dashboardPath: getDashboardPath(combinedUser.role) };
    } catch {
      const fallbackUser = {
        ...authResponse,
        role: normalizeRole(authResponse.role),
      };
      setUser(fallbackUser);
      sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(fallbackUser));
      return { ...fallbackUser, dashboardPath: getDashboardPath(fallbackUser.role) };
    }
  }, []);

  const login = useCallback(async ({ email, password }) => {
    const authResponse = await authApi.login({ email, password });
    return establishSession(authResponse);
  }, [establishSession]);

  const register = useCallback(async (profile) => {
    const normRole = normalizeRole(profile.role);
    const backendRole = normRole.toUpperCase();
    const payload = {
      email: profile.email,
      password: profile.password,
      role: backendRole,
      name:
        normRole === 'company'
          ? (profile.company || profile.companyName || profile.name)
          : normRole === 'college'
          ? (profile.college || profile.collegeName || profile.name)
          : profile.name,
    };
    if (profile.phone) {
      payload.phone = profile.phone;
    }
    if (backendRole === 'STUDENT') {
      if (profile.collegeId) {
        payload.collegeId = Number(profile.collegeId);
      }
      if (profile.departmentId) {
        payload.departmentId = Number(profile.departmentId);
      }
      if (profile.graduationYear) {
        payload.graduationYear = Number(profile.graduationYear);
      }
      if (profile.yearOfStudy) {
        payload.yearOfStudy = Number(profile.yearOfStudy);
      }
    }
    const authResponse = await authApi.register(payload);
    return establishSession(authResponse);
  }, [establishSession]);


  const logout = useCallback(async () => {
    try {
      if (token) await authApi.logout();
    } catch {
      // best-effort logout
    } finally {
      setToken(null);
      setUser(null);
      sessionStorage.removeItem(STORAGE_KEYS.JWT);
      sessionStorage.removeItem(STORAGE_KEYS.USER);
    }
  }, [token]);

  const updateUser = useCallback((updates) => {
    setUser((prev) => {
      const next = { ...(prev || {}), ...updates };
      if (next.role) next.role = normalizeRole(next.role);
      sessionStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(next));
      return next;
    });
  }, []);

  const value = useMemo(() => ({
    user,
    token,
    isAuthenticated: Boolean(token && user),
    isLoading,
    login,
    register,
    logout,
    updateUser,
  }), [user, token, isLoading, login, register, logout, updateUser]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
