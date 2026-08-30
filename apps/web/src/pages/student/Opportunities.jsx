import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Clock, Search } from 'lucide-react';
import { useOpportunities, useMasterSkills } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import SearchBar from '@/components/ui/SearchBar';
import FilterDropdown from '@/components/ui/FilterDropdown';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';

export default function Opportunities() {
  const navigate = useNavigate();
  const opportunitiesQuery = useOpportunities();
  const masterSkillsQuery = useMasterSkills();
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [skillFilter, setSkillFilter] = useState([]);
  const [matchFilter, setMatchFilter] = useState('');

  const isLoading = opportunitiesQuery.isLoading || masterSkillsQuery.isLoading;
  const isError = opportunitiesQuery.isError || masterSkillsQuery.isError;
  const error = opportunitiesQuery.error || masterSkillsQuery.error;
  const refetch = () => {
    opportunitiesQuery.refetch();
    masterSkillsQuery.refetch();
  };

  const opportunities = opportunitiesQuery.data || [];
  const allSkills = masterSkillsQuery.data || [];

  const filtered = useMemo(() => {
    return opportunities.filter((o) => {
      if (search && !o.title.toLowerCase().includes(search.toLowerCase()) && !o.company.toLowerCase().includes(search.toLowerCase())) return false;
      if (typeFilter && o.type !== typeFilter) return false;
      if (skillFilter.length > 0 && !skillFilter.some((s) => o.requiredSkills.includes(s))) return false;
      if (matchFilter === 'high' && o.matchPercentage < 80) return false;
      if (matchFilter === 'medium' && (o.matchPercentage < 60 || o.matchPercentage >= 80)) return false;
      if (matchFilter === 'low' && o.matchPercentage >= 60) return false;
      return true;
    });
  }, [opportunities, search, typeFilter, skillFilter, matchFilter]);

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!opportunities.length}
      emptyProps={{
        icon: Search,
        title: 'No opportunities available',
        message: 'Check back later for new internships and placements.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Find Your Next Opportunity</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Discover internships and placements matched to your skills</p>
        </div>

        <div className="flex flex-col gap-4 mb-8">
          <SearchBar value={search} onChange={setSearch} placeholder="Search by role, company or skill..." />
          <div className="flex flex-wrap gap-3">
            <FilterDropdown label="Type" options={[{ value: 'Internship', label: 'Internship' }, { value: 'Placement', label: 'Placement' }]} value={typeFilter} onChange={setTypeFilter} />
            <FilterDropdown label="Skills" options={allSkills.slice(0, 15).map((s) => ({ value: s, label: s }))} value={skillFilter} onChange={setSkillFilter} multi />
            <FilterDropdown label="Match %" options={[{ value: 'high', label: 'High (80%+)' }, { value: 'medium', label: 'Medium (60-79%)' }, { value: 'low', label: 'Low (<60%)' }]} value={matchFilter} onChange={setMatchFilter} />
          </div>
        </div>

        {filtered.length === 0 ? (
          <EmptyState icon={Search} title="No opportunities found" message="Try adjusting your search or filters to find more opportunities." />
        ) : (
          <div className="grid grid-2">
            {filtered.map((opp, i) => (
              <div key={opp.id} className={`opportunity-card animate-fade-in-up delay-${Math.min(i + 1, 6)}`}>
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
        )}
      </div>
    </PageQueryState>
  );
}
