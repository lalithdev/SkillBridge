import { Target, TrendingUp, BookOpen, Lightbulb, ExternalLink } from 'lucide-react';
import { useStudentSkillGaps } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import ProgressBar from '@/components/ui/ProgressBar';

export default function SkillGaps() {
  const { data: skillGapAnalysis, isLoading, isError, error, refetch } = useStudentSkillGaps();

  const currentSkills = skillGapAnalysis?.currentSkills || [];
  const demandedSkills = skillGapAnalysis?.demandedSkills || [];
  const missingSkills = skillGapAnalysis?.missingSkills || [];
  const recommendedSkills = skillGapAnalysis?.recommendedSkills || [];

  const industryDemandedSkills = recommendedSkills.map((s) => ({
    name: s.skill,
    demand: s.demand,
    availability: currentSkills.includes(s.skill) ? 70 : 0,
  }));

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Your Skill Gap Analysis</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Identify gaps between your skills and industry demand</p>
        </div>

        <div className="grid grid-3 mb-8">
          <Card className="animate-fade-in-up delay-1">
            <CardBody>
              <div className="flex items-center gap-2 mb-4"><BookOpen size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Current Skills</h3></div>
              <div className="flex flex-wrap gap-2">{currentSkills.map((s) => <SkillBadge key={s} skill={s} />)}</div>
            </CardBody>
          </Card>
          <Card className="animate-fade-in-up delay-2">
            <CardBody>
              <div className="flex items-center gap-2 mb-4"><TrendingUp size={18} style={{ color: 'var(--secondary-600)' }} /><h3 className="card-title">Industry-Demanded</h3></div>
              <div className="flex flex-wrap gap-2">{demandedSkills.map((s) => <SkillBadge key={s} skill={s} variant="matched" />)}</div>
            </CardBody>
          </Card>
          <Card className="animate-fade-in-up delay-3">
            <CardBody>
              <div className="flex items-center gap-2 mb-4"><Target size={18} style={{ color: 'var(--error-600)' }} /><h3 className="card-title">Missing Skills</h3></div>
              <div className="flex flex-wrap gap-2">{missingSkills.map((s) => <SkillBadge key={s} skill={s} variant="missing" />)}</div>
            </CardBody>
          </Card>
        </div>

        <Card className="mb-6 animate-fade-in-up delay-4">
          <CardBody>
            <h3 className="card-title mb-6">Current vs Required Skills</h3>
            <div className="h-bar-chart">
              {industryDemandedSkills.slice(0, 8).map((s) => {
                const has = currentSkills.includes(s.name);
                return (
                  <div key={s.name} className="h-bar-item">
                    <div className="h-bar-label">{s.name}</div>
                    <div className="h-bar-track">
                      <div className="h-bar-fill" style={{ width: `${has ? s.availability : 0}%`, background: has ? 'linear-gradient(90deg, var(--success-500), var(--success-600))' : 'linear-gradient(90deg, var(--error-500), var(--error-600))' }} />
                    </div>
                    <div className="h-bar-value">{has ? s.availability : 0}%</div>
                  </div>
                );
              })}
            </div>
            <div className="flex gap-6 mt-6">
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--success-500)' }} /><span className="text-xs text-tertiary">You have this skill</span></div>
              <div className="flex items-center gap-2"><div style={{ width: 12, height: 12, borderRadius: 4, background: 'var(--error-500)' }} /><span className="text-xs text-tertiary">Missing skill</span></div>
            </div>
          </CardBody>
        </Card>

        <Card className="mb-8 animate-fade-in-up delay-5">
          <CardBody>
            <h3 className="card-title mb-6">Skill Demand in Industry</h3>
            <div className="chart-bar-container">
              {industryDemandedSkills.slice(0, 8).map((s) => (
                <div key={s.name} className="chart-bar-group">
                  <div className="chart-bar-value">{s.demand}%</div>
                  <div className="chart-bar" style={{ height: `${s.demand}%` }} />
                  <div className="chart-bar-label">{s.name}</div>
                </div>
              ))}
            </div>
          </CardBody>
        </Card>

        <div className="section-header">
          <h2 className="section-title" style={{ fontSize: 'var(--text-xl)' }}>Recommended Skills to Learn</h2>
        </div>
        <div className="grid grid-3">
          {recommendedSkills.map((s, i) => (
            <Card key={s.skill} className={`card-hover animate-fade-in-up delay-${i + 1}`}>
              <CardBody>
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', background: 'var(--primary-50)', color: 'var(--primary-600)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <Lightbulb size={20} />
                    </div>
                    <div>
                      <div className="text-base font-bold">{s.skill}</div>
                      <div className="text-xs text-tertiary">{s.category}</div>
                    </div>
                  </div>
                  <Badge variant={s.status === 'In Progress' ? 'warning' : 'gray'}>{s.status}</Badge>
                </div>
                <div className="mb-2">
                  <div className="flex justify-between text-xs mb-1"><span className="text-tertiary">Industry Demand</span><span className="font-bold">{s.demand}%</span></div>
                  <ProgressBar value={s.demand} showValue={false} height={6} />
                </div>
                <Button variant="outline" size="sm" fullWidth className="mt-4" icon={ExternalLink}>Learn {s.skill}</Button>
              </CardBody>
            </Card>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
