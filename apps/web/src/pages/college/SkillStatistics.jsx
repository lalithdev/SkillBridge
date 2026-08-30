import { Award, TrendingUp, BarChart3 } from 'lucide-react';
import { useSkillStatistics } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import ProgressBar from '@/components/ui/ProgressBar';
import Badge from '@/components/ui/Badge';

export default function SkillStatistics() {
  const { data, isLoading, isError, error, refetch } = useSkillStatistics();

  const studentSkills = data?.studentSkills || [];
  const industryDemandedSkills = data?.industryDemandedSkills || [];
  const totalSkills = studentSkills.length;
  const avgLevel = studentSkills.length
    ? Math.round(studentSkills.reduce((a, s) => a + s.level, 0) / studentSkills.length)
    : 0;

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Skill Statistics</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Overview of student skill proficiency across the college</p>
        </div>

        <div className="grid grid-3 mb-8">
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--primary-50)', color: 'var(--primary-600)' }}><Award size={22} /></div><div className="stat-card-value">{totalSkills}</div><div className="stat-card-label">Skills Tracked</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--success-50)', color: 'var(--success-600)' }}><TrendingUp size={22} /></div><div className="stat-card-value">{avgLevel}%</div><div className="stat-card-label">Avg Proficiency</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--accent-50)', color: 'var(--accent-600)' }}><BarChart3 size={22} /></div><div className="stat-card-value">{industryDemandedSkills.length || 156}</div><div className="stat-card-label">Unique Skills</div></div>
        </div>

        <Card className="mb-6 animate-fade-in-up"><CardBody>
          <h3 className="card-title mb-6">Student Skill Proficiency Levels</h3>
          <div className="h-bar-chart">
            {studentSkills.map((s) => (
              <div key={s.name} className="h-bar-item">
                <div className="h-bar-label">{s.name}</div>
                <div className="h-bar-track"><div className="h-bar-fill" style={{ width: `${s.level}%`, background: s.level >= 80 ? 'linear-gradient(90deg, var(--success-500), var(--success-600))' : 'linear-gradient(90deg, var(--primary-500), var(--primary-600))' }} /></div>
                <div className="h-bar-value">{s.level}%</div>
              </div>
            ))}
          </div>
        </CardBody></Card>

        <Card className="animate-fade-in-up delay-1"><CardBody>
          <h3 className="card-title mb-6">Skills by Category</h3>
          <div className="flex flex-col gap-4">
            {['Programming', 'Database', 'Frontend', 'Backend', 'Tools'].map((cat) => {
              const catSkills = studentSkills.filter((s) => s.category === cat);
              if (catSkills.length === 0) return null;
              return (
                <div key={cat} style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                  <div className="flex items-center justify-between mb-3">
                    <h4 style={{ fontSize: 'var(--text-sm)', fontWeight: 700 }}>{cat}</h4>
                    <Badge variant="primary">{catSkills.length} skills</Badge>
                  </div>
                  <div className="flex flex-col gap-2">
                    {catSkills.map((s) => (
                      <div key={s.name}>
                        <div className="flex justify-between text-xs mb-1"><span className="text-secondary">{s.name}</span><span className="font-bold">{s.level}%</span></div>
                        <ProgressBar value={s.level} showValue={false} height={5} variant={s.level >= 80 ? 'success' : 'default'} />
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </CardBody></Card>
      </div>
    </PageQueryState>
  );
}
