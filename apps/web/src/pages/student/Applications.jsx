import { useState, useMemo } from 'react';
import { FileText, Eye, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useStudentApplications } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function Applications() {
  const navigate = useNavigate();
  const { data: studentApplications = [], isLoading, isError, error, refetch } = useStudentApplications();
  const [filter, setFilter] = useState('All');

  const filters = ['All', 'Applied', 'Under Review', 'Shortlisted', 'Selected', 'Rejected'];

  const filtered = useMemo(() => {
    if (filter === 'All') return studentApplications;
    return studentApplications.filter((a) => a.status === filter);
  }, [studentApplications, filter]);

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>My Applications</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Track the status of your job applications</p>
        </div>

        <div className="tabs mb-6">
          {filters.map((f) => (
            <button key={f} className={`tab ${filter === f ? 'active' : ''}`} onClick={() => setFilter(f)}>{f}</button>
          ))}
        </div>

        {filtered.length === 0 ? (
          <EmptyState icon={FileText} title="No applications found" message={`You have no applications with status "${filter}".`} action={<Button onClick={() => navigate('/student/opportunities')}>Browse Opportunities</Button>} />
        ) : (
          <div className="card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr><th>Opportunity</th><th>Company</th><th>Applied Date</th><th>Match</th><th>Status</th><th>Action</th></tr>
                </thead>
                <tbody>
                  {filtered.map((app) => (
                    <tr key={app.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{app.opportunity}</td>
                      <td>{app.company}</td>
                      <td>{app.appliedDate}</td>
                      <td><span className={`match-percentage-value match-${app.match >= 80 ? 'high' : app.match >= 60 ? 'medium' : 'low'}`} style={{ fontSize: 'var(--text-sm)' }}>{app.match}%</span></td>
                      <td><Badge variant={statusVariant[app.status]}>{app.status}</Badge></td>
                      <td><Button variant="ghost" size="sm" icon={Eye} onClick={() => navigate('/student/opportunities')}>View</Button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </PageQueryState>
  );
}
