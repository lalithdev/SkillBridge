import { useNavigate } from 'react-router-dom';
import { Briefcase, FileText, Users, CheckCircle2, ArrowRight, TrendingUp } from 'lucide-react';
import { useCompanyProfile, useCompanyOpportunities, useCompanyApplications, useCompanyRecruitmentOverview } from '@/hooks/useData';
import { useAuth } from '@/hooks/useAuth';
import { getDisplayName } from '@/utils/auth';
import PageQueryState from '@/components/shared/PageQueryState';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Card, { CardBody } from '@/components/ui/Card';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function CompanyDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const profileQuery = useCompanyProfile();
  const opportunitiesQuery = useCompanyOpportunities();
  const applicationsQuery = useCompanyApplications();
  const overviewQuery = useCompanyRecruitmentOverview();

  const isLoading = profileQuery.isLoading || opportunitiesQuery.isLoading || applicationsQuery.isLoading || overviewQuery.isLoading;
  const isError = profileQuery.isError || opportunitiesQuery.isError || applicationsQuery.isError || overviewQuery.isError;
  const error = profileQuery.error || opportunitiesQuery.error || applicationsQuery.error || overviewQuery.error;
  const refetch = () => {
    profileQuery.refetch();
    opportunitiesQuery.refetch();
    applicationsQuery.refetch();
    overviewQuery.refetch();
  };

  const companyProfile = profileQuery.data;
  const companyOpportunities = opportunitiesQuery.data || [];
  const companyApplications = applicationsQuery.data || [];
  const recruitmentOverview = overviewQuery.data || [];

  const displayName = companyProfile?.name || getDisplayName(user);
  const maxCount = recruitmentOverview.length ? Math.max(...recruitmentOverview.map((r) => r.count)) : 1;

  const stats = [
    { label: 'Active Opportunities', value: companyOpportunities.filter((o) => o.status === 'Active').length, icon: Briefcase, color: 'var(--primary-600)', bg: 'var(--primary-50)' },
    { label: 'Total Applications', value: companyApplications.length, icon: FileText, color: 'var(--secondary-600)', bg: 'var(--secondary-50)' },
    { label: 'Matching Candidates', value: companyApplications.filter((a) => a.match >= 70).length, icon: Users, color: 'var(--accent-600)', bg: 'var(--accent-50)' },
    { label: 'Shortlisted', value: companyApplications.filter((a) => a.status === 'Shortlisted').length, icon: CheckCircle2, color: 'var(--success-600)', bg: 'var(--success-50)' },
  ];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="animate-fade-in-up mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Welcome, {displayName}</h1>
          <p style={{ fontSize: 'var(--text-lg)', color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your recruitment pipeline and opportunities</p>
        </div>

        <div className="grid grid-4 mb-8">
          {stats.map((s, i) => (
            <div key={s.label} className={`stat-card animate-fade-in-up delay-${i + 1}`}>
              <div className="stat-card-icon" style={{ background: s.bg, color: s.color }}><s.icon size={22} /></div>
              <div className="stat-card-value">{s.value}</div>
              <div className="stat-card-label">{s.label}</div>
            </div>
          ))}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }} className="mb-8">
          <Card className="animate-fade-in-up delay-1">
            <CardBody>
              <div className="flex items-center gap-2 mb-6"><TrendingUp size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Recruitment Overview</h3></div>
              <div className="chart-bar-container">
                {recruitmentOverview.map((r) => (
                  <div key={r.stage} className="chart-bar-group">
                    <div className="chart-bar-value">{r.count}</div>
                    <div className="chart-bar" style={{ height: `${(r.count / maxCount) * 100}%` }} />
                    <div className="chart-bar-label" style={{ fontSize: 10 }}>{r.stage}</div>
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>

          <Card className="animate-fade-in-up delay-2">
            <CardBody>
              <div className="flex items-center justify-between mb-4">
                <h3 className="card-title">Recent Applications</h3>
                <Button variant="ghost" size="sm" onClick={() => navigate('/company/applications')} iconRight={ArrowRight}>View All</Button>
              </div>
              <div className="flex flex-col gap-3">
                {companyApplications.slice(0, 5).map((app) => (
                  <div key={app.id} className="flex items-center justify-between" style={{ padding: 'var(--space-3)', borderRadius: 'var(--radius-sm)', background: 'var(--gray-50)' }}>
                    <div>
                      <div className="text-sm font-semibold">{app.candidate}</div>
                      <div className="text-xs text-tertiary">{app.opportunity}</div>
                    </div>
                    <Badge variant={statusVariant[app.status]}>{app.status}</Badge>
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>
        </div>

        <Card className="animate-fade-in-up delay-3">
          <CardBody>
            <div className="flex items-center justify-between mb-4">
              <h3 className="card-title">Your Opportunities</h3>
              <Button variant="primary" size="sm" onClick={() => navigate('/company/opportunities/create')}>Create New</Button>
            </div>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead><tr><th>Title</th><th>Type</th><th>Applications</th><th>Deadline</th><th>Status</th></tr></thead>
                <tbody>
                  {companyOpportunities.map((o) => (
                    <tr key={o.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{o.title}</td>
                      <td>{o.type}</td>
                      <td>{o.applications}</td>
                      <td>{o.deadline}</td>
                      <td><Badge variant={o.status === 'Active' ? 'success' : 'gray'}>{o.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardBody>
        </Card>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
