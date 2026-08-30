import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, Eye, ChevronLeft, ChevronRight } from 'lucide-react';
import { useCollegeStudents } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import SearchBar from '@/components/ui/SearchBar';
import FilterDropdown from '@/components/ui/FilterDropdown';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import ProgressBar from '@/components/ui/ProgressBar';
import EmptyState from '@/components/ui/EmptyState';

const statusVariant = {
  Placed: 'success', 'In Process': 'warning', 'Not Applied': 'gray',
};

export default function CollegeStudents() {
  const navigate = useNavigate();
  const { data: collegeStudents = [], isLoading, isError, error, refetch } = useCollegeStudents();
  const [search, setSearch] = useState('');
  const [deptFilter, setDeptFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(1);
  const perPage = 8;

  const filtered = useMemo(() => {
    return collegeStudents.filter((s) => {
      if (search && !s.name.toLowerCase().includes(search.toLowerCase())) return false;
      if (deptFilter && s.department !== deptFilter) return false;
      if (statusFilter && s.placementStatus !== statusFilter) return false;
      return true;
    });
  }, [collegeStudents, search, deptFilter, statusFilter]);

  const totalPages = Math.ceil(filtered.length / perPage);
  const paginated = filtered.slice((page - 1) * perPage, page * perPage);
  const departments = [...new Set(collegeStudents.map((s) => s.department))];

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!collegeStudents.length}
      emptyProps={{
        icon: Users,
        title: 'No students found',
        message: 'Student records will appear here once they are registered with your college.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Students</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage and track student placement progress</p>
        </div>

        <div className="flex flex-col gap-4 mb-6">
          <SearchBar value={search} onChange={(e) => { setSearch(e); setPage(1); }} placeholder="Search by student name..." />
          <div className="flex flex-wrap gap-3">
            <FilterDropdown label="Department" options={departments.map((d) => ({ value: d, label: d }))} value={deptFilter} onChange={(v) => { setDeptFilter(v); setPage(1); }} />
            <FilterDropdown label="Placement Status" options={['Placed', 'In Process', 'Not Applied']} value={statusFilter} onChange={(v) => { setStatusFilter(v); setPage(1); }} />
          </div>
        </div>

        {filtered.length === 0 ? (
          <EmptyState icon={Users} title="No students found" message="Try adjusting your search or filters." />
        ) : (
          <>
            <div className="card" style={{ overflow: 'hidden' }}>
              <div style={{ overflowX: 'auto' }}>
                <table className="data-table">
                  <thead><tr><th>Student</th><th>Department</th><th>Skills</th><th>Profile Completion</th><th>Applications</th><th>Placement Status</th><th>Action</th></tr></thead>
                  <tbody>
                    {paginated.map((s) => (
                      <tr key={s.id}>
                        <td>
                          <div className="flex items-center gap-3">
                            <div className="avatar avatar-sm">{s.name.split(' ').map((n) => n[0]).join('')}</div>
                            <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{s.name}</span>
                          </div>
                        </td>
                        <td>{s.department}</td>
                        <td>{s.skills} skills</td>
                        <td style={{ minWidth: 120 }}>
                          <ProgressBar value={s.profileCompletion} showValue={false} height={6} variant={s.profileCompletion >= 80 ? 'success' : s.profileCompletion >= 60 ? 'default' : 'warning'} />
                          <span className="text-xs text-tertiary mt-1">{s.profileCompletion}%</span>
                        </td>
                        <td>{s.applications}</td>
                        <td><Badge variant={statusVariant[s.placementStatus]}>{s.placementStatus}</Badge></td>
                        <td><Button variant="ghost" size="sm" icon={Eye} onClick={() => navigate('/company/candidates/1')}>View Profile</Button></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {totalPages > 1 && (
              <div className="flex items-center justify-between mt-6">
                <span className="text-sm text-tertiary">Showing {(page - 1) * perPage + 1}–{Math.min(page * perPage, filtered.length)} of {filtered.length}</span>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" icon={ChevronLeft} onClick={() => setPage(Math.max(1, page - 1))} disabled={page === 1}>Prev</Button>
                  <Button variant="outline" size="sm" iconRight={ChevronRight} onClick={() => setPage(Math.min(totalPages, page + 1))} disabled={page === totalPages}>Next</Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </PageQueryState>
  );
}
