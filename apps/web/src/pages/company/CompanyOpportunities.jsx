import { useNavigate } from 'react-router-dom';
import { Plus, MapPin, Clock, Users } from 'lucide-react';
import { useCompanyOpportunities } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';

export default function CompanyOpportunities() {
  const navigate = useNavigate();
  const { data: companyOpportunities = [], isLoading, isError, error, refetch } = useCompanyOpportunities();

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!companyOpportunities.length}
      emptyProps={{
        icon: Plus,
        title: 'No opportunities posted',
        message: 'Create your first internship or placement opportunity to start receiving applications.',
        action: <Button variant="primary" icon={Plus} onClick={() => navigate('/company/opportunities/create')}>Create Opportunity</Button>,
      }}
    >
      <div>
        <div className="flex items-center justify-between mb-8" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <div>
            <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>My Opportunities</h1>
            <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your posted opportunities</p>
          </div>
          <Button variant="primary" icon={Plus} onClick={() => navigate('/company/opportunities/create')}>Create Opportunity</Button>
        </div>

        <div className="flex flex-col gap-4">
          {companyOpportunities.map((opp, i) => (
            <Card key={opp.id} className={`card-hover animate-fade-in-up delay-${Math.min(i + 1, 6)}`}>
              <CardBody>
                <div className="flex items-start justify-between" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
                  <div style={{ flex: 1, minWidth: 200 }}>
                    <div className="flex items-center gap-3 mb-2">
                      <h3 style={{ fontSize: 'var(--text-lg)', fontWeight: 700 }}>{opp.title}</h3>
                      <Badge variant={opp.status === 'Active' ? 'success' : 'gray'}>{opp.status}</Badge>
                    </div>
                    <div className="flex items-center gap-4 flex-wrap mb-4">
                      <Badge variant={opp.type === 'Internship' ? 'blue' : 'accent'}>{opp.type}</Badge>
                      <span className="flex items-center gap-1 text-xs text-tertiary"><MapPin size={12} /> {opp.location}</span>
                      <span className="flex items-center gap-1 text-xs text-tertiary"><Clock size={12} /> {opp.deadline}</span>
                      <span className="flex items-center gap-1 text-xs text-tertiary"><Users size={12} /> {opp.applications} applications</span>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      {opp.requiredSkills.map((s) => <SkillBadge key={s} skill={s} />)}
                    </div>
                  </div>
                  <div className="flex flex-col gap-2">
                    <Button variant="outline" size="sm" onClick={() => navigate('/company/candidates')}>View Candidates</Button>
                    <Button variant="ghost" size="sm">Edit</Button>
                  </div>
                </div>
              </CardBody>
            </Card>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
