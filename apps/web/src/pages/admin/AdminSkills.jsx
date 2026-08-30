import { BookOpenText, Plus, Search, Sparkles } from 'lucide-react';
import { useAdminSkills } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import Card, { CardBody } from '@/components/ui/Card';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';

export default function AdminSkills() {
  const { data = [], isLoading, isError, error, refetch } = useAdminSkills();

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch} isEmpty={!data.length} emptyProps={{ icon: Sparkles, title: 'No skill taxonomy entries', message: 'Skill definitions will appear here after the API is connected.' }}>
      <div>
        <div className="flex items-center justify-between mb-8" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <div>
            <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Skills Taxonomy</h1>
            <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage platform skill categories and definitions</p>
          </div>
          <Button variant="primary" icon={Plus}>Add Skill</Button>
        </div>

        <Card className="mb-6">
          <CardBody>
            <div className="flex items-center gap-3">
              <Search size={18} style={{ color: 'var(--text-tertiary)' }} />
              <Input placeholder="Search skills" />
            </div>
          </CardBody>
        </Card>

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {data.map((skill) => (
            <Card key={skill.id}>
              <CardBody>
                <div className="flex items-center justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div style={{ width: 40, height: 40, borderRadius: 'var(--radius-md)', background: 'var(--primary-50)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <BookOpenText size={18} style={{ color: 'var(--primary-600)' }} />
                    </div>
                    <div>
                      <h3 style={{ fontWeight: 800 }}>{skill.name}</h3>
                      <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-secondary)' }}>{skill.category}</p>
                    </div>
                  </div>
                  <Badge variant={skill.isActive ? 'success' : 'gray'}>{skill.isActive ? 'Active' : 'Draft'}</Badge>
                </div>
                <p className="mt-4" style={{ color: 'var(--text-secondary)', lineHeight: 1.7 }}>{skill.description}</p>
              </CardBody>
            </Card>
          ))}
        </div>
      </div>
    </PageQueryState>
  );
}
