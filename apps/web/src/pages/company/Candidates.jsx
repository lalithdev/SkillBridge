import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, Star, Eye, Search } from 'lucide-react';
import { useCandidates, useMasterSkills } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import SearchBar from '@/components/ui/SearchBar';
import FilterDropdown from '@/components/ui/FilterDropdown';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';
import { useToast } from '@/components/ui/Toast';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function Candidates() {
  const navigate = useNavigate();
  const toast = useToast();
  const candidatesQuery = useCandidates();
  const masterSkillsQuery = useMasterSkills();
  const [search, setSearch] = useState('');
  const [matchFilter, setMatchFilter] = useState('');
  const [skillFilter, setSkillFilter] = useState([]);
  const [statusFilter, setStatusFilter] = useState('');

  const isLoading = candidatesQuery.isLoading || masterSkillsQuery.isLoading;
  const isError = candidatesQuery.isError || masterSkillsQuery.isError;
  const error = candidatesQuery.error || masterSkillsQuery.error;
  const refetch = () => {
    candidatesQuery.refetch();
    masterSkillsQuery.refetch();
  };

  const candidates = candidatesQuery.data || [];
  const allSkills = masterSkillsQuery.data || [];

  const filtered = useMemo(() => {
    return candidates.filter((c) => {
      if (search && !c.name.toLowerCase().includes(search.toLowerCase())) return false;
      if (matchFilter === 'high' && c.matchPercentage < 85) return false;
      if (matchFilter === 'medium' && (c.matchPercentage < 70 || c.matchPercentage >= 85)) return false;
      if (matchFilter === 'low' && c.matchPercentage >= 70) return false;
      if (skillFilter.length > 0 && !skillFilter.some((s) => c.skills.includes(s))) return false;
      if (statusFilter && c.applicationStatus !== statusFilter) return false;
      return true;
    });
  }, [candidates, search, matchFilter, skillFilter, statusFilter]);

  const handleShortlist = (name) => toast.success(`${name} shortlisted!`);

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!candidates.length}
      emptyProps={{
        icon: Users,
        title: 'No candidates yet',
        message: 'Candidates will appear here when students apply to your opportunities.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Candidates</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Discover and shortlist matched candidates</p>
        </div>

        <div className="flex flex-col gap-4 mb-8">
          <SearchBar value={search} onChange={setSearch} placeholder="Search by candidate name..." />
          <div className="flex flex-wrap gap-3">
            <FilterDropdown label="Match %" options={[{ value: 'high', label: 'High (85%+)' }, { value: 'medium', label: 'Medium (70-84%)' }, { value: 'low', label: 'Low (<70%)' }]} value={matchFilter} onChange={setMatchFilter} />
            <FilterDropdown label="Skills" options={allSkills.slice(0, 15).map((s) => ({ value: s, label: s }))} value={skillFilter} onChange={setSkillFilter} multi />
            <FilterDropdown label="Status" options={['Applied', 'Under Review', 'Shortlisted', 'Selected', 'Rejected']} value={statusFilter} onChange={setStatusFilter} />
          </div>
        </div>

        {filtered.length === 0 ? (
          <EmptyState icon={Search} title="No candidates found" message="Try adjusting your filters to find more candidates." />
        ) : (
          <div className="grid grid-2">
            {filtered.map((c, i) => (
              <div key={c.id} className={`card card-hover animate-fade-in-up delay-${Math.min(i + 1, 6)}`} style={{ padding: 'var(--space-6)' }}>
                <div className="flex items-start gap-4 mb-4">
                  <div className="avatar avatar-lg">{c.name.split(' ').map((n) => n[0]).join('')}</div>
                  <div style={{ flex: 1 }}>
                    <h3 style={{ fontSize: 'var(--text-base)', fontWeight: 700 }}>{c.name}</h3>
                    <p className="text-xs text-tertiary mt-1">{c.education}</p>
                    <p className="text-xs text-tertiary">{c.location}</p>
                    <div className="mt-2"><Badge variant={statusVariant[c.applicationStatus]}>{c.applicationStatus}</Badge></div>
                  </div>
                  <div className="match-percentage">
                    <span className={`match-percentage-value match-${c.matchPercentage >= 85 ? 'high' : c.matchPercentage >= 70 ? 'medium' : 'low'}`}>{c.matchPercentage}%</span>
                  </div>
                </div>
                <div className="mb-3">
                  <div className="text-xs font-semibold text-tertiary mb-2">SKILLS</div>
                  <div className="flex flex-wrap gap-2">
                    {c.skills.slice(0, 5).map((s) => <SkillBadge key={s} skill={s} variant={c.matchedSkills.includes(s) ? 'matched' : 'default'} />)}
                  </div>
                </div>
                {c.missingSkills.length > 0 && (
                  <div className="mb-4">
                    <div className="text-xs font-semibold text-tertiary mb-2">MISSING SKILLS</div>
                    <div className="flex flex-wrap gap-2">
                      {c.missingSkills.map((s) => <SkillBadge key={s} skill={s} variant="missing" />)}
                    </div>
                  </div>
                )}
                <div className="flex gap-2" style={{ borderTop: '1px solid var(--border-light)', paddingTop: 'var(--space-4)' }}>
                  <Button variant="outline" size="sm" icon={Eye} onClick={() => navigate(`/company/candidates/${c.id}`)}>View Profile</Button>
                  <Button variant="primary" size="sm" icon={Star} onClick={() => handleShortlist(c.name)}>Shortlist</Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </PageQueryState>
  );
}
