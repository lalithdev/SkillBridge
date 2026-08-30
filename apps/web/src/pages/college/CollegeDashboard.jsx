import { useNavigate } from 'react-router-dom';
import { Users, Award, Briefcase, TrendingUp, ArrowRight, PieChart } from 'lucide-react';
import { useCollegeStats, useRecruitmentAnalytics, useCollegeStudents } from '@/hooks/useData';
import { useAuth } from '@/hooks/useAuth';
import { getDisplayName } from '@/utils/auth';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import ProgressBar from '@/components/ui/ProgressBar';

export default function CollegeDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const statsQuery = useCollegeStats();
  const analyticsQuery = useRecruitmentAnalytics();
  const studentsQuery = useCollegeStudents();

  const isLoading = statsQuery.isLoading || analyticsQuery.isLoading || studentsQuery.isLoading;
  const isError = statsQuery.isError || analyticsQuery.isError || studentsQuery.isError;
  const error = statsQuery.error || analyticsQuery.error || studentsQuery.error;
  const refetch = () => {
    statsQuery.refetch();
    analyticsQuery.refetch();
    studentsQuery.refetch();
  };

  const collegeStats = statsQuery.data;
  const recruitmentAnalytics = analyticsQuery.data;
  const collegeStudents = studentsQuery.data || [];

  const displayName = user?.collegeName || getDisplayName(user);
  const placementRate = recruitmentAnalytics?.placementRate ?? collegeStats?.placementRate ?? 0;
  const maxApps = recruitmentAnalytics?.monthlyData?.length
    ? Math.max(...recruitmentAnalytics.monthlyData.map((d) => d.applications))
    : 1;

  const stats = collegeStats ? [
    { label: 'Total Students', value: collegeStats.totalStudents, icon: Users, color: 'var(--primary-600)', bg: 'var(--primary-50)' },
    { label: 'Total Skills', value: collegeStats.totalSkills, icon: Award, color: 'var(--secondary-600)', bg: 'var(--secondary-50)' },
    { label: 'Industry Opportunities', value: collegeStats.industryOpportunities, icon: Briefcase, color: 'var(--accent-600)', bg: 'var(--accent-50)' },
    { label: 'Placement Rate', value: `${collegeStats.placementRate}%`, icon: TrendingUp, color: 'var(--success-600)', bg: 'var(--success-50)' },
  ] : [];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="animate-fade-in-up mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>{displayName !== 'User' ? `${displayName} Dashboard` : 'College Dashboard'}</h1>
          <p style={{ fontSize: 'var(--text-lg)', color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Analytics overview across students, skills, and recruitment</p>
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

        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)' }} className="mb-8">
          <Card className="animate-fade-in-up delay-1"><CardBody>
            <div className="flex items-center gap-2 mb-6"><TrendingUp size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Monthly Applications & Placements</h3></div>
            <div className="chart-bar-container">
              {(recruitmentAnalytics?.monthlyData || []).map((d) => (
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

          <Card className="animate-fade-in-up delay-2"><CardBody>
            <div className="flex items-center gap-2 mb-6"><PieChart size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Placement Rate</h3></div>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 'var(--space-6)' }}>
              <div className="donut-chart" style={{ width: 160, height: 160 }}>
                <svg width="160" height="160">
                  <circle cx="80" cy="80" r="60" fill="none" stroke="var(--gray-100)" strokeWidth="16" />
                  <circle cx="80" cy="80" r="60" fill="none" stroke="var(--success-500)" strokeWidth="16" strokeDasharray={`${2 * Math.PI * 60 * (placementRate / 100)} ${2 * Math.PI * 60 * (1 - placementRate / 100)}`} strokeLinecap="round" />
                </svg>
                <div className="donut-chart-center">
                  <div style={{ fontSize: 'var(--text-3xl)', fontWeight: 800, color: 'var(--success-600)', fontFamily: "'Plus Jakarta Sans', sans-serif" }}>{placementRate}%</div>
                  <div style={{ fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)' }}>Placed</div>
                </div>
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="flex justify-between text-sm"><span className="text-tertiary">Total Applications</span><span className="font-bold">{recruitmentAnalytics?.totalApplications}</span></div>
              <div className="flex justify-between text-sm"><span className="text-tertiary">Shortlisted</span><span className="font-bold">{recruitmentAnalytics?.shortlisted}</span></div>
              <div className="flex justify-between text-sm"><span className="text-tertiary">Selected</span><span className="font-bold">{recruitmentAnalytics?.selected}</span></div>
            </div>
          </CardBody></Card>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }}>
          <Card className="animate-fade-in-up delay-3"><CardBody>
            <div className="flex items-center justify-between mb-4">
              <h3 className="card-title">Top Recruiters</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/college/recruitment')} iconRight={ArrowRight}>Details</Button>
            </div>
            <div className="flex flex-col gap-3">
              {(recruitmentAnalytics?.topRecruiters || []).map((r, i) => (
                <div key={r.company} className="flex items-center gap-3">
                  <div style={{ width: 32, height: 32, borderRadius: 'var(--radius-sm)', background: 'var(--primary-50)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: 'var(--text-sm)' }}>{i + 1}</div>
                  <span className="text-sm font-semibold" style={{ flex: 1 }}>{r.company}</span>
                  <Badge variant="success">{r.hires} hires</Badge>
                </div>
              ))}
            </div>
          </CardBody></Card>

          <Card className="animate-fade-in-up delay-4"><CardBody>
            <div className="flex items-center justify-between mb-4">
              <h3 className="card-title">Student Placement Status</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/college/students')} iconRight={ArrowRight}>View All</Button>
            </div>
            <div className="flex flex-col gap-4">
              {[
                { label: 'Placed', count: collegeStudents.filter((s) => s.placementStatus === 'Placed').length, color: 'success' },
                { label: 'In Process', count: collegeStudents.filter((s) => s.placementStatus === 'In Process').length, color: 'warning' },
                { label: 'Not Applied', count: collegeStudents.filter((s) => s.placementStatus === 'Not Applied').length, color: 'gray' },
              ].map((s) => (
                <div key={s.label}>
                  <div className="flex justify-between text-sm mb-1"><span className="text-secondary">{s.label}</span><span className="font-bold">{s.count} students</span></div>
                  <ProgressBar value={s.count} max={collegeStudents.length || 1} showValue={false} height={6} variant={s.color} />
                </div>
              ))}
            </div>
          </CardBody></Card>
        </div>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 2fr 1fr"], div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
