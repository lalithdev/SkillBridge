import { useNavigate } from 'react-router-dom';
import { Sparkles, ArrowRight, MapPin, Clock } from 'lucide-react';
import { useRecommendations } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';

export default function Recommendations() {
  const navigate = useNavigate();
  const { data: recommended = [], isLoading, isError, error, refetch } = useRecommendations();

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!recommended.length}
      emptyProps={{
        icon: Sparkles,
        title: 'No recommendations yet',
        message: 'Add more skills to your profile to get personalized opportunity recommendations.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Recommended For You</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Opportunities ranked by your skill match score</p>
        </div>

        <div className="flex flex-col gap-4">
          {recommended.map((opp, i) => (
            <div key={opp.id} className={`opportunity-card animate-fade-in-up delay-${Math.min(i + 1, 6)}`}>
              <div className="opportunity-card-header">
                <div className="opportunity-logo">{opp.companyInitial}</div>
                <div style={{ flex: 1 }}>
                  <div className="flex items-center gap-2">
                    <Sparkles size={14} style={{ color: 'var(--primary-500)' }} />
                    <span className="text-xs font-bold" style={{ color: 'var(--primary-600)' }}>RECOMMENDED</span>
                  </div>
                  <div className="opportunity-card-title mt-1">{opp.title}</div>
                  <div className="opportunity-card-company">{opp.company}</div>
                  <div className="opportunity-card-meta" style={{ marginTop: 'var(--space-2)' }}>
                    <span className="opportunity-card-meta-item"><MapPin size={12} /> {opp.location}</span>
                    <span className="opportunity-card-meta-item"><Clock size={12} /> {opp.deadline}</span>
                    <Badge variant={opp.type === 'Internship' ? 'blue' : 'accent'}>{opp.type}</Badge>
                  </div>
                </div>
                <div className="match-percentage">
                  <span className={`match-percentage-value match-${opp.matchPercentage >= 80 ? 'high' : 'medium'}`}>{opp.matchPercentage}%</span>
                </div>
              </div>
              <div className="flex flex-wrap gap-2">
                {opp.requiredSkills.slice(0, 6).map((s) => (
                  <SkillBadge key={s} skill={s} variant={opp.matchedSkills.includes(s) ? 'matched' : 'missing'} />
                ))}
              </div>
              <div className="opportunity-card-footer">
                <Button variant="outline" size="sm" onClick={() => navigate(`/student/opportunities/${opp.id}`)}>View Details</Button>
                <Button variant="primary" size="sm" onClick={() => navigate(`/student/opportunities/${opp.id}`)} iconRight={ArrowRight}>Apply Now</Button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
