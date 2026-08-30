import { useNavigate } from 'react-router-dom';
import { Briefcase, Award, Target, FileText, ArrowRight, MapPin, Clock } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { getDisplayName } from '@/utils/auth';
import { useStudentProfile, useOpportunities, useStudentApplications } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import MatchScore from '@/components/ui/MatchScore';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import { SKILL_COVERAGE_DISCLAIMER } from '@/utils/constants';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function StudentDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const profileQuery = useStudentProfile();
  const opportunitiesQuery = useOpportunities();
  const applicationsQuery = useStudentApplications();

  const isLoading = profileQuery.isLoading || opportunitiesQuery.isLoading || applicationsQuery.isLoading;
  const isError = profileQuery.isError || opportunitiesQuery.isError || applicationsQuery.isError;
  const error = profileQuery.error || opportunitiesQuery.error || applicationsQuery.error;
  const refetch = () => {
    profileQuery.refetch();
    opportunitiesQuery.refetch();
    applicationsQuery.refetch();
  };

  const profile = profileQuery.data;
  const opportunities = opportunitiesQuery.data || [];
  const applications = applicationsQuery.data || [];
  const firstName = getDisplayName(user).split(' ')[0];
  const topOpps = [...opportunities].sort((a, b) => b.matchPercentage - a.matchPercentage).slice(0, 3);

  const stats = profile ? [
    { label: 'Profile Completion', value: `${profile.profileCompletion}%`, icon: Target, color: 'var(--primary-600)', bg: 'var(--primary-50)' },
    { label: 'Skills Added', value: profile.skills?.length || 0, icon: Award, color: 'var(--secondary-600)', bg: 'var(--secondary-50)' },
    { label: 'Matching Opportunities', value: opportunities.filter((o) => o.matchPercentage >= 60).length, icon: Briefcase, color: 'var(--accent-600)', bg: 'var(--accent-50)' },
    { label: 'Applications', value: applications.length, icon: FileText, color: 'var(--success-600)', bg: 'var(--success-50)' },
  ] : [];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="animate-fade-in-up" style={{ marginBottom: 'var(--space-8)' }}>
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>
            Welcome back, {firstName} <span style={{ fontSize: 'var(--text-2xl)' }}>👋</span>
          </h1>
          <p style={{ fontSize: 'var(--text-lg)', color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>
            Discover opportunities that match your skills.
          </p>
        </div>

        <div className="grid grid-4" style={{ marginBottom: 'var(--space-8)' }}>
          {stats.map((s, i) => (
            <div key={s.label} className={`stat-card animate-fade-in-up delay-${i + 1}`}>
              <div className="stat-card-icon" style={{ background: s.bg, color: s.color }}><s.icon size={22} /></div>
              <div className="stat-card-value">{s.value}</div>
              <div className="stat-card-label">{s.label}</div>
            </div>
          ))}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)', marginBottom: 'var(--space-8)' }}>
          <div>
            <div className="section-header">
              <div>
                <h2 className="section-title" style={{ fontSize: 'var(--text-xl)' }}>Recommended Opportunities</h2>
                <p className="section-subtitle">Based on your skill profile</p>
              </div>
              <Button variant="ghost" size="sm" onClick={() => navigate('/student/opportunities')} iconRight={ArrowRight}>View All</Button>
            </div>
            <div className="flex flex-col gap-4">
              {topOpps.map((opp, i) => (
                <div key={opp.id} className={`opportunity-card animate-fade-in-up delay-${i + 1}`} style={{ padding: 'var(--space-5)' }}>
                  <div className="opportunity-card-header">
                    <div className="opportunity-logo">{opp.companyInitial}</div>
                    <div style={{ flex: 1 }}>
                      <div className="opportunity-card-title">{opp.title}</div>
                      <div className="opportunity-card-company">{opp.company}</div>
                      <div className="opportunity-card-meta" style={{ marginTop: 'var(--space-2)' }}>
                        <span className="opportunity-card-meta-item"><MapPin size={12} /> {opp.location}</span>
                        <span className="opportunity-card-meta-item"><Clock size={12} /> {opp.deadline}</span>
                        <Badge variant={opp.type === 'Internship' ? 'blue' : 'accent'}>{opp.type}</Badge>
                      </div>
                    </div>
                    <div className="match-percentage">
                      <span className={`match-percentage-value match-${opp.matchPercentage >= 80 ? 'high' : opp.matchPercentage >= 60 ? 'medium' : 'low'}`}>{opp.matchPercentage}%</span>
                    </div>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {opp.requiredSkills.slice(0, 5).map((s) => (
                      <SkillBadge key={s} skill={s} variant={opp.matchedSkills.includes(s) ? 'matched' : 'missing'} />
                    ))}
                  </div>
                  <div className="opportunity-card-footer">
                    <Button variant="outline" size="sm" onClick={() => navigate(`/student/opportunities/${opp.id}`)}>View Details</Button>
                    <Button variant="primary" size="sm" onClick={() => navigate(`/student/opportunities/${opp.id}`)}>Apply</Button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div>
            <div className="card" style={{ padding: 'var(--space-6)', textAlign: 'center' }}>
              <h3 style={{ fontSize: 'var(--text-lg)', fontWeight: 700, marginBottom: 'var(--space-5)' }}>Your Skill Match</h3>
              <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 'var(--space-5)' }}>
                <MatchScore percentage={topOpps[0]?.matchPercentage || 0} size={140} />
              </div>
              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)', fontStyle: 'italic', marginBottom: 'var(--space-4)' }}>
                {SKILL_COVERAGE_DISCLAIMER}
              </p>
            </div>
          </div>
        </div>

        <div>
          <div className="section-header">
            <h2 className="section-title" style={{ fontSize: 'var(--text-xl)' }}>Recent Applications</h2>
            <Button variant="ghost" size="sm" onClick={() => navigate('/student/applications')} iconRight={ArrowRight}>View All</Button>
          </div>
          <div className="card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr><th>Position</th><th>Company</th><th>Applied Date</th><th>Status</th></tr>
                </thead>
                <tbody>
                  {applications.slice(0, 5).map((app) => (
                    <tr key={app.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{app.opportunity}</td>
                      <td>{app.company}</td>
                      <td>{app.appliedDate}</td>
                      <td><Badge variant={statusVariant[app.status]}>{app.status}</Badge></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 2fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
