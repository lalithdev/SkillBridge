import { TrendingUp, BarChart3, Briefcase } from 'lucide-react';
import { useIndustryDemand } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import ProgressBar from '@/components/ui/ProgressBar';
import Badge from '@/components/ui/Badge';

export default function IndustryDemand() {
  const { data, isLoading, isError, error, refetch } = useIndustryDemand();

  const industryDemandedSkills = data?.industryDemandedSkills || [];
  const skillDemandByIndustry = data?.skillDemandByIndustry || [];
  const maxDemand = industryDemandedSkills.length
    ? Math.max(...industryDemandedSkills.map((s) => s.demand))
    : 1;
  const maxOpportunities = skillDemandByIndustry.length
    ? Math.max(...skillDemandByIndustry.map((i) => i.opportunities))
    : 1;

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Industry Demand Analytics</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Track which skills are most demanded by industry</p>
        </div>

        <Card className="mb-6 animate-fade-in-up"><CardBody>
          <div className="flex items-center gap-2 mb-6"><TrendingUp size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Most Demanded Skills</h3></div>
          <div className="chart-bar-container">
            {industryDemandedSkills.map((s) => (
              <div key={s.name} className="chart-bar-group">
                <div className="chart-bar-value">{s.demand}%</div>
                <div className="chart-bar" style={{ height: `${(s.demand / maxDemand) * 100}%` }} />
                <div className="chart-bar-label">{s.name}</div>
              </div>
            ))}
          </div>
        </CardBody></Card>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }} className="mb-6">
          <Card className="animate-fade-in-up delay-1"><CardBody>
            <div className="flex items-center gap-2 mb-6"><BarChart3 size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Demand vs Student Availability</h3></div>
            <div className="h-bar-chart">
              {industryDemandedSkills.slice(0, 6).map((s) => (
                <div key={s.name} className="h-bar-item">
                  <div className="h-bar-label">{s.name}</div>
                  <div className="h-bar-track">
                    <div className="h-bar-fill" style={{ width: `${s.demand}%` }} />
                  </div>
                  <div className="h-bar-value">{s.availability}%</div>
                </div>
              ))}
            </div>
            <div className="flex gap-6 mt-4">
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--primary-500)' }} /><span className="text-xs text-tertiary">Industry Demand</span></div>
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--gray-300)' }} /><span className="text-xs text-tertiary">Student Availability</span></div>
            </div>
          </CardBody></Card>

          <Card className="animate-fade-in-up delay-2"><CardBody>
            <div className="flex items-center gap-2 mb-6"><Briefcase size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Skill Demand by Industry</h3></div>
            <div className="flex flex-col gap-4">
              {skillDemandByIndustry.map((ind) => (
                <div key={ind.industry} style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-semibold">{ind.industry}</span>
                    <Badge variant="accent">{ind.opportunities} jobs</Badge>
                  </div>
                  <div className="text-xs text-tertiary mb-2">Top skill: {ind.topSkill}</div>
                  <ProgressBar value={ind.demand} showValue={false} height={5} />
                </div>
              ))}
            </div>
          </CardBody></Card>
        </div>

        <Card className="animate-fade-in-up delay-3"><CardBody>
          <h3 className="card-title mb-6">Opportunities by Industry</h3>
          <div className="chart-bar-container">
            {skillDemandByIndustry.map((ind) => (
              <div key={ind.industry} className="chart-bar-group">
                <div className="chart-bar-value">{ind.opportunities}</div>
                <div className="chart-bar secondary" style={{ height: `${(ind.opportunities / maxOpportunities) * 100}%` }} />
                <div className="chart-bar-label" style={{ fontSize: 10 }}>{ind.industry.split('/')[0]}</div>
              </div>
            ))}
          </div>
        </CardBody></Card>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
