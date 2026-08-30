import { Shield, Users, Building2, GraduationCap } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { getDisplayName } from '@/utils/auth';
import Card, { CardBody } from '@/components/ui/Card';
import EmptyState from '@/components/ui/EmptyState';

export default function AdminDashboard() {
  const { user } = useAuth();
  const displayName = getDisplayName(user);

  return (
    <div>
      <div className="mb-8">
        <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Admin Dashboard</h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>
          Welcome, {displayName}. Platform administration tools will be connected after backend integration.
        </p>
      </div>

      <div className="grid grid-4" style={{ marginBottom: 'var(--space-8)' }}>
        {[
          { label: 'User Management', icon: Users, description: 'Manage accounts and roles' },
          { label: 'Verifications', icon: Shield, description: 'Review organization verification requests' },
          { label: 'Skills Taxonomy', icon: GraduationCap, description: 'Maintain master skill catalog' },
          { label: 'Moderation', icon: Building2, description: 'Review flagged opportunities' },
        ].map((item) => (
          <Card key={item.label}>
            <CardBody>
              <div className="stat-card-icon" style={{ background: 'var(--primary-50)', color: 'var(--primary-600)', marginBottom: 'var(--space-4)' }}>
                <item.icon size={22} />
              </div>
              <h2 style={{ fontSize: 'var(--text-lg)', fontWeight: 700 }}>{item.label}</h2>
              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>{item.description}</p>
            </CardBody>
          </Card>
        ))}
      </div>

      <EmptyState
        icon={Shield}
        title="Admin modules pending backend integration"
        message="User management, verifications, skills taxonomy, and moderation screens are defined in the approved specifications and will be wired to /api/v1/admin/* endpoints during integration."
      />
    </div>
  );
}
