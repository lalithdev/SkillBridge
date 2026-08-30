import { Target, TrendingUp, AlertTriangle, Sparkles } from 'lucide-react';
import { useCollegeSkillGaps } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Badge from '@/components/ui/Badge';
import ProgressBar from '@/components/ui/ProgressBar';

export default function SkillGapAnalysis() {
  const { data: skillGapAnalytics, isLoading, isError, error, refetch } = useCollegeSkillGaps();

  const highDemandLowAvailability = skillGapAnalytics?.highDemandLowAvailability || [];
  const highAvailability = skillGapAnalytics?.highAvailability || [];
  const emergingSkills = skillGapAnalytics?.emergingSkills || [];
  const topSkillGaps = skillGapAnalytics?.topSkillGaps || [];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Skill Gap Analytics</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Industry requirements vs student skills</p>
        </div>

        <Card className="mb-6 animate-fade-in-up"><CardBody>
          <div className="flex items-center gap-2 mb-6"><Target size={18} style={{ color: 'var(--error-600)' }} /><h3 className="card-title">Top Skill Gaps</h3></div>
          <div className="h-bar-chart">
            {topSkillGaps.map((g) => (
              <div key={g.skill} className="h-bar-item">
                <div className="h-bar-label" style={{ width: 140 }}>{g.skill}</div>
                <div className="h-bar-track">
                  <div className="h-bar-fill" style={{ width: `${g.gap}%`, background: 'linear-gradient(90deg, var(--error-500), var(--error-600))' }} />
                </div>
                <div className="h-bar-value" style={{ color: 'var(--error-600)' }}>{g.gap}% gap</div>
              </div>
            ))}
          </div>
        </CardBody></Card>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }} className="mb-6">
          <Card className="animate-fade-in-up delay-1"><CardBody>
            <div className="flex items-center gap-2 mb-4"><AlertTriangle size={18} style={{ color: 'var(--error-600)' }} /><h3 className="card-title">High Demand, Low Availability</h3></div>
            <p className="text-sm text-tertiary mb-4">Critical skills students need to learn</p>
            <div className="flex flex-col gap-4">
              {highDemandLowAvailability.map((s) => (
                <div key={s.skill}>
                  <div className="flex justify-between text-sm mb-2"><span className="font-semibold">{s.skill}</span><Badge variant="error">Gap: {s.demand - s.availability}%</Badge></div>
                  <div className="flex gap-2 items-center">
                    <div style={{ flex: 1 }}><div className="text-xs text-tertiary mb-1">Demand</div><ProgressBar value={s.demand} showValue={false} height={6} variant="error" /></div>
                    <div style={{ flex: 1 }}><div className="text-xs text-tertiary mb-1">Availability</div><ProgressBar value={s.availability} showValue={false} height={6} variant="default" /></div>
                  </div>
                </div>
              ))}
            </div>
          </CardBody></Card>

          <Card className="animate-fade-in-up delay-2"><CardBody>
            <div className="flex items-center gap-2 mb-4"><TrendingUp size={18} style={{ color: 'var(--success-600)' }} /><h3 className="card-title">High Availability Skills</h3></div>
            <p className="text-sm text-tertiary mb-4">Skills students are well-prepared in</p>
            <div className="flex flex-col gap-4">
              {highAvailability.map((s) => (
                <div key={s.skill}>
                  <div className="flex justify-between text-sm mb-2"><span className="font-semibold">{s.skill}</span><Badge variant="success">Surplus: {s.availability - s.demand}%</Badge></div>
                  <div className="flex gap-2 items-center">
                    <div style={{ flex: 1 }}><div className="text-xs text-tertiary mb-1">Demand</div><ProgressBar value={s.demand} showValue={false} height={6} variant="default" /></div>
                    <div style={{ flex: 1 }}><div className="text-xs text-tertiary mb-1">Availability</div><ProgressBar value={s.availability} showValue={false} height={6} variant="success" /></div>
                  </div>
                </div>
              ))}
            </div>
          </CardBody></Card>
        </div>

        <Card className="animate-fade-in-up delay-3"><CardBody>
          <div className="flex items-center gap-2 mb-4"><Sparkles size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Emerging Skills</h3></div>
          <p className="text-sm text-tertiary mb-4">New technologies with growing demand but low student coverage</p>
          <div className="grid grid-4">
            {emergingSkills.map((s) => (
              <div key={s.skill} style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', background: 'var(--gray-50)' }}>
                <div className="text-sm font-bold mb-2">{s.skill}</div>
                <div className="flex flex-col gap-2">
                  <div><div className="text-xs text-tertiary">Demand</div><ProgressBar value={s.demand} showValue={false} height={5} variant="warning" /></div>
                  <div><div className="text-xs text-tertiary">Availability</div><ProgressBar value={s.availability} showValue={false} height={5} variant="error" /></div>
                </div>
              </div>
            ))}
          </div>
        </CardBody></Card>

        <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } } @media (max-width: 640px) { .grid-4 { grid-template-columns: 1fr 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
