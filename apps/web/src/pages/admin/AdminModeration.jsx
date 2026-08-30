import { CheckCheck, FileText, Search, ShieldAlert } from 'lucide-react';
import { useAdminModeration } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';

export default function AdminModeration() {
  const { data = [], isLoading, isError, error, refetch } = useAdminModeration();

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch} isEmpty={!data.length} emptyProps={{ icon: FileText, title: 'No moderation queue items', message: 'Opportunity review tasks will appear here after integration.' }}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Opportunity Moderation</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Review, approve, and flag platform opportunities</p>
        </div>

        <Card className="mb-6">
          <CardBody>
            <div className="flex items-center gap-3">
              <Search size={18} style={{ color: 'var(--text-tertiary)' }} />
              <Input placeholder="Search opportunities or companies" />
            </div>
          </CardBody>
        </Card>

        <div className="space-y-4">
          {data.map((item) => (
            <Card key={item.id}>
              <CardBody className="flex items-center justify-between gap-4" style={{ flexWrap: 'wrap' }}>
                <div>
                  <h3 style={{ fontWeight: 800 }}>{item.title}</h3>
                  <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-1)' }}>{item.company} • {item.location}</p>
                </div>
                <div className="flex items-center gap-3" style={{ flexWrap: 'wrap' }}>
                  <Badge variant={item.status === 'approved' ? 'success' : item.status === 'flagged' ? 'warning' : 'danger'}>
                    {item.status === 'approved' ? 'Approved' : item.status === 'flagged' ? 'Flagged' : 'Needs Review'}
                  </Badge>
                  <Button variant="primary" size="sm" icon={CheckCheck}>Approve</Button>
                  <Button variant="outline" size="sm" icon={ShieldAlert}>Flag</Button>
                </div>
              </CardBody>
            </Card>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
