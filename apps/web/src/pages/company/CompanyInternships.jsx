import { GraduationCap } from 'lucide-react';
import PageQueryState from '@/components/shared/PageQueryState';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Card, { CardBody } from '@/components/ui/Card';
import { useCompanyInternships } from '@/hooks/useData';
import { useToast } from '@/components/ui/Toast';

export default function CompanyInternships() {
  const { data: internships, isLoading, isError, error, refetch } = useCompanyInternships();
  const toast = useToast();

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!internships?.length}
      emptyProps={{
        icon: GraduationCap,
        title: 'No active internships',
        message: 'Confirmed hires from selected applications will appear here for lifecycle tracking and feedback.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Internship Management</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>
            Track active hires and submit completion feedback
          </p>
        </div>

        <div className="flex flex-col gap-4">
          {internships.map((item) => (
            <Card key={item.id}>
              <CardBody>
                <div className="flex items-start justify-between gap-4" style={{ flexWrap: 'wrap' }}>
                  <div>
                    <h2 style={{ fontSize: 'var(--text-xl)', fontWeight: 700 }}>{item.candidate}</h2>
                    <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-1)' }}>{item.opportunity}</p>
                    <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-tertiary)', marginTop: 'var(--space-2)' }}>
                      {item.startDate} – {item.endDate}
                    </p>
                  </div>
                  <Badge variant={item.status === 'COMPLETED' ? 'success' : 'blue'}>{item.status}</Badge>
                </div>
                <div style={{ marginTop: 'var(--space-4)' }}>
                  {item.feedbackSubmitted ? (
                    <Badge variant="success">Feedback submitted</Badge>
                  ) : (
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => toast.info('Feedback submission will be available after backend integration.')}
                    >
                      Submit Feedback
                    </Button>
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
