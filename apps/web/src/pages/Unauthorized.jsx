import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { getDashboardPath } from '@/utils/auth';
import Button from '@/components/ui/Button';

export default function Unauthorized() {
  const { user } = useAuth();
  const dashboardPath = user ? getDashboardPath(user.role) : '/';

  return (
    <div style={{ minHeight: '100vh', display: 'grid', placeItems: 'center', background: 'var(--bg-body)', padding: 'var(--space-8)' }}>
      <div style={{ maxWidth: 520, width: '100%', background: '#fff', border: '1px solid var(--border-default)', borderRadius: 'var(--radius-xl)', boxShadow: 'var(--shadow-md)', padding: 'var(--space-8)', textAlign: 'center' }}>
        <div style={{ display: 'inline-flex', width: 64, height: 64, borderRadius: '50%', background: 'rgba(239,68,68,0.08)', alignItems: 'center', justifyContent: 'center', marginBottom: 'var(--space-5)' }}>
          <ShieldAlert size={28} color="var(--error-500)" />
        </div>
        <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800, marginBottom: 'var(--space-3)' }}>Access denied</h1>
        <p style={{ fontSize: 'var(--text-base)', color: 'var(--text-secondary)', marginBottom: 'var(--space-6)' }}>
          You do not have permission to view this page. Please return to your role dashboard.
        </p>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 'var(--space-3)', flexWrap: 'wrap' }}>
          <Link to={dashboardPath}>
            <Button>Go to my dashboard</Button>
          </Link>
          <Link to="/">
            <Button variant="outline">Go home</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
