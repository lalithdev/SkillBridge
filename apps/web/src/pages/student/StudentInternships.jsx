import { GraduationCap } from 'lucide-react';
import PageQueryState from '@/components/shared/PageQueryState';
import Badge from '@/components/ui/Badge';
import Card, { CardBody } from '@/components/ui/Card';
import { useStudentInternships } from '@/hooks/useData';
import { SKILL_COVERAGE_DISCLAIMER } from '@/utils/constants';

export default function StudentInternships() {
  const { data: internships, isLoading, isError, error, refetch } = useStudentInternships();
  const internshipList = Array.isArray(internships) ? internships : [];

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!internshipList.length}
      emptyProps={{
        icon: GraduationCap,
        title: 'No confirmed internships',
        message: 'When an employer selects you for an opportunity, your placement details and completion feedback will appear here.',
      }}
    >
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>My Internships</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>
            Confirmed placements and employer feedback
          </p>
        </div>

        <div className="flex flex-col gap-4">
          {internshipList.map((item) => (
            <Card key={item.id}>
              <CardBody>
                <div className="flex items-start justify-between gap-4" style={{ flexWrap: 'wrap' }}>
                  <div>
                    <h2 style={{ fontSize: 'var(--text-xl)', fontWeight: 700 }}>{item.opportunity}</h2>
                    <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-1)' }}>{item.company}</p>
                    <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-tertiary)', marginTop: 'var(--space-2)' }}>
                      {item.startDate} – {item.endDate}
                    </p>
                  </div>
                  <Badge variant={item.status === 'COMPLETED' ? 'success' : 'blue'}>{item.status}</Badge>
                </div>
                {item.feedback && (
                  <p style={{ marginTop: 'var(--space-4)', fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>
                    <strong>Feedback:</strong> {item.feedback}
                  </p>
                )}
              </CardBody>
            </Card>
          ))}
        </div>

        <p style={{ marginTop: 'var(--space-6)', fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)', fontStyle: 'italic' }}>
          {SKILL_COVERAGE_DISCLAIMER}
        </p>
      </div>
    </PageQueryState>
  );
}
