import { CheckCircle2, Clock3, Search, ShieldAlert } from 'lucide-react';
import { useAdminVerifications } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';

export default function AdminVerifications() {
  const { data = [], isLoading, isError, error, refetch } = useAdminVerifications();

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch} isEmpty={!data.length} emptyProps={{ icon: ShieldAlert, title: 'No verification requests', message: 'Verification items will appear here once the backend is connected.' }}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Verification Queue</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Review and approve institutional and profile verifications</p>
        </div>

        <Card className="mb-6">
          <CardBody>
            <div className="flex items-center gap-3">
              <Search size={18} style={{ color: 'var(--text-tertiary)' }} />
              <Input placeholder="Search verification requests" />
            </div>
          </CardBody>
        </Card>

        <div className="space-y-4">
          {data.map((item) => (
            <Card key={item.id}>
              <CardBody className="flex items-center justify-between gap-4" style={{ flexWrap: 'wrap' }}>
                <div>
                  <h3 style={{ fontWeight: 800 }}>{item.entity}</h3>
                  <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-1)' }}>{item.details}</p>
                </div>
                <div className="flex items-center gap-3" style={{ flexWrap: 'wrap' }}>
                  <Badge variant={item.status === 'approved' ? 'success' : item.status === 'pending' ? 'warning' : 'danger'}>
                    {item.status === 'approved' ? 'Approved' : item.status === 'pending' ? 'Pending' : 'Rejected'}
                  </Badge>
                  {item.status === 'pending' ? (
                    <>
                      <Button variant="success" size="sm" icon={CheckCircle2}>Approve</Button>
                      <Button variant="outline" size="sm" icon={Clock3}>Review Later</Button>
                    </>
                  ) : (
                    <Button variant="outline" size="sm">View History</Button>
                  )}
                </div>
              </CardBody>
            </Card>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
