import { PieChart, TrendingUp, Users, Award } from 'lucide-react';
import { useRecruitmentAnalytics } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';

export default function RecruitmentAnalytics() {
  const { data: recruitmentAnalytics, isLoading, isError, error, refetch } = useRecruitmentAnalytics();

  const monthlyData = recruitmentAnalytics?.monthlyData || [];
  const topRecruiters = recruitmentAnalytics?.topRecruiters || [];
  const totalApplications = recruitmentAnalytics?.totalApplications || 0;
  const shortlisted = recruitmentAnalytics?.shortlisted || 0;
  const interviewed = recruitmentAnalytics?.interviewed || 0;
  const selected = recruitmentAnalytics?.selected || 0;
  const placementRate = recruitmentAnalytics?.placementRate || 0;

  const maxApps = monthlyData.length ? Math.max(...monthlyData.map((d) => d.applications)) : 1;

  const funnel = [
    { stage: 'Applications', count: totalApplications, pct: 100, color: 'var(--primary-500)' },
    { stage: 'Shortlisted', count: shortlisted, pct: totalApplications ? (shortlisted / totalApplications) * 100 : 0, color: 'var(--secondary-500)' },
    { stage: 'Interviewed', count: interviewed, pct: totalApplications ? (interviewed / totalApplications) * 100 : 0, color: 'var(--accent-500)' },
    { stage: 'Selected', count: selected, pct: totalApplications ? (selected / totalApplications) * 100 : 0, color: 'var(--success-500)' },
  ];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Recruitment Analytics</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Track placement performance and recruitment trends</p>
        </div>

        <div className="grid grid-4 mb-8">
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--primary-50)', color: 'var(--primary-600)' }}><Users size={22} /></div><div className="stat-card-value">{totalApplications}</div><div className="stat-card-label">Total Applications</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--secondary-50)', color: 'var(--secondary-600)' }}><Award size={22} /></div><div className="stat-card-value">{shortlisted}</div><div className="stat-card-label">Shortlisted</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--accent-50)', color: 'var(--accent-600)' }}><TrendingUp size={22} /></div><div className="stat-card-value">{selected}</div><div className="stat-card-label">Selected</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--success-50)', color: 'var(--success-600)' }}><PieChart size={22} /></div><div className="stat-card-value">{placementRate}%</div><div className="stat-card-label">Placement Rate</div></div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)' }} className="mb-6">
          <Card className="animate-fade-in-up"><CardBody>
            <div className="flex items-center gap-2 mb-6"><TrendingUp size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Monthly Application Trends</h3></div>
            <div className="chart-bar-container">
              {monthlyData.map((d) => (
                <div key={d.month} className="chart-bar-group">
                  <div className="chart-bar-value">{d.applications}</div>
                  <div className="chart-bar" style={{ height: `${(d.applications / maxApps) * 100}%` }} />
                  <div className="chart-bar" style={{ height: `${(d.placements / maxApps) * 100}%`, background: 'linear-gradient(180deg, var(--success-400), var(--success-600))', marginTop: 2 }} />
                  <div className="chart-bar-label">{d.month}</div>
                </div>
              ))}
            </div>
            <div className="flex gap-6 mt-4">
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--primary-500)' }} /><span className="text-xs text-tertiary">Applications</span></div>
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--success-500)' }} /><span className="text-xs text-tertiary">Placements</span></div>
            </div>
          </CardBody></Card>

          <Card className="animate-fade-in-up delay-1"><CardBody>
            <div className="flex items-center gap-2 mb-6"><PieChart size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Recruitment Funnel</h3></div>
            <div className="flex flex-col gap-4">
              {funnel.map((f) => (
                <div key={f.stage}>
                  <div className="flex justify-between text-sm mb-2"><span className="font-semibold">{f.stage}</span><span className="font-bold">{f.count}</span></div>
                  <div style={{ height: 28, background: 'var(--gray-100)', borderRadius: 'var(--radius-full)', overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${f.pct}%`, background: f.color, borderRadius: 'var(--radius-full)', display: 'flex', alignItems: 'center', justifyContent: 'flex-end', paddingRight: 'var(--space-2)', fontSize: 10, fontWeight: 700, color: '#fff', transition: 'width 0.8s ease' }}>
                      {Math.round(f.pct)}%
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardBody></Card>
        </div>

        <Card className="animate-fade-in-up delay-2"><CardBody>
          <h3 className="card-title mb-6">Top Recruiting Companies</h3>
          <div className="h-bar-chart">
            {topRecruiters.map((r) => {
              const max = Math.max(...topRecruiters.map((x) => x.hires));
              return (
                <div key={r.company} className="h-bar-item">
                  <div className="h-bar-label" style={{ width: 160 }}>{r.company}</div>
                  <div className="h-bar-track"><div className="h-bar-fill" style={{ width: `${(r.hires / max) * 100}%` }} /></div>
                  <div className="h-bar-value">{r.hires}</div>
                </div>
              );
            })}
          </div>
        </CardBody></Card>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 2fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
