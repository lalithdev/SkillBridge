import { useMemo, useState } from 'react';
import { Search, ShieldOff, Users } from 'lucide-react';
import { useAdminUsers } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import EmptyState from '@/components/ui/EmptyState';

export default function AdminUsers() {
  const { data = [], isLoading, isError, error, refetch } = useAdminUsers();
  const [search, setSearch] = useState('');

  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return data;
    return data.filter((user) => `${user.name} ${user.email} ${user.role}`.toLowerCase().includes(term));
  }, [data, search]);

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch} isEmpty={!data.length} emptyProps={{ icon: Users, title: 'No users available', message: 'User accounts will appear here once the backend is connected.' }}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>User Management</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Review account status and platform access</p>
        </div>

        <Card className="mb-6">
          <CardBody>
            <div className="flex items-center gap-3">
              <Search size={18} style={{ color: 'var(--text-tertiary)' }} />
              <Input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search users by name, email, or role" />
            </div>
          </CardBody>
        </Card>

        {filtered.length === 0 ? (
          <EmptyState icon={Users} title="No matching users" message="Try a different search term." />
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((user) => (
                  <tr key={user.id}>
                    <td style={{ fontWeight: 700 }}>{user.name}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>
                      <Badge variant={user.isActive ? 'success' : 'gray'}>{user.isActive ? 'Active' : 'Inactive'}</Badge>
                    </td>
                    <td>
                      <Button variant={user.isActive ? 'outline' : 'primary'} size="sm" icon={ShieldOff}>
                        {user.isActive ? 'Deactivate' : 'Activate'}
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </PageQueryState>
  );
}
