import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { normalizeRole } from '@/utils/auth';
import LoadingSpinner from '@/components/ui/LoadingSpinner';

export default function ProtectedRoute({ allowedRoles }) {
  const { user, token, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return <LoadingSpinner size="lg" label="Verifying session..." />;
  }

  if (!token || !user) {
    return <Navigate to={`/login?redirect=${encodeURIComponent(location.pathname)}`} replace />;
  }

  const currentRole = normalizeRole(user.role);
  const allowed = (allowedRoles || []).map((role) => normalizeRole(role));

  if (allowed.length > 0 && !allowed.includes(currentRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}
