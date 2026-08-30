import { useState, useMemo } from 'react';
import { ClipboardList, Eye } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useCompanyApplications } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function CompanyApplications() {
  const navigate = useNavigate();
  const { data: companyApplications = [], isLoading, isError, error, refetch } = useCompanyApplications();
  const [filter, setFilter] = useState('All');
  const filters = ['All', 'Applied', 'Under Review', 'Shortlisted', 'Selected', 'Rejected'];

  const filtered = useMemo(() => {
    if (filter === 'All') return companyApplications;
    return companyApplications.filter((a) => a.status === filter);
  }, [companyApplications, filter]);

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Applications</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Review and manage candidate applications</p>
        </div>

        <div className="tabs mb-6">
          {filters.map((f) => <button key={f} className={`tab ${filter === f ? 'active' : ''}`} onClick={() => setFilter(f)}>{f}</button>)}
        </div>

        {filtered.length === 0 ? (
          <EmptyState icon={ClipboardList} title="No applications found" message={`No applications with status "${filter}".`} />
        ) : (
          <div className="card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead><tr><th>Candidate</th><th>Opportunity</th><th>Applied Date</th><th>Match</th><th>Status</th><th>Action</th></tr></thead>
                <tbody>
                  {filtered.map((app) => (
                    <tr key={app.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{app.candidate}</td>
                      <td>{app.opportunity}</td>
                      <td>{app.appliedDate}</td>
                      <td><span className={`match-percentage-value match-${app.match >= 85 ? 'high' : app.match >= 70 ? 'medium' : 'low'}`} style={{ fontSize: 'var(--text-sm)' }}>{app.match}%</span></td>
                      <td><Badge variant={statusVariant[app.status]}>{app.status}</Badge></td>
                      <td><Button variant="ghost" size="sm" icon={Eye} onClick={() => navigate('/company/candidates')}>View</Button></td>
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
