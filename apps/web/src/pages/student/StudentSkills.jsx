import { useState, useEffect } from 'react';
import { Award, Plus, TrendingUp } from 'lucide-react';
import { useStudentSkills, useMasterSkills } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import SkillBadge from '@/components/ui/SkillBadge';
import ProgressBar from '@/components/ui/ProgressBar';
import Modal from '@/components/ui/Modal';
import Input from '@/components/ui/Input';

export default function StudentSkills() {
  const toast = useToast();
  const skillsQuery = useStudentSkills();
  const masterSkillsQuery = useMasterSkills();
  const [skills, setSkills] = useState([]);
  const [addOpen, setAddOpen] = useState(false);
  const [newSkill, setNewSkill] = useState('');

  const isLoading = skillsQuery.isLoading || masterSkillsQuery.isLoading;
  const isError = skillsQuery.isError || masterSkillsQuery.isError;
  const error = skillsQuery.error || masterSkillsQuery.error;
  const refetch = () => {
    skillsQuery.refetch();
    masterSkillsQuery.refetch();
  };

  useEffect(() => {
    if (skillsQuery.data) setSkills(skillsQuery.data);
  }, [skillsQuery.data]);

  const allSkills = masterSkillsQuery.data || [];

  const handleAdd = () => {
    if (!newSkill) return;
    if (skills.some((s) => s.name === newSkill)) {
      toast.error('Skill already exists');
      return;
    }
    setSkills([...skills, { name: newSkill, level: 50, category: 'General' }]);
    toast.success(`Skill "${newSkill}" added!`);
    setNewSkill('');
    setAddOpen(false);
  };

  const handleRemove = (name) => {
    setSkills(skills.filter((s) => s.name !== name));
    toast.info(`Skill "${name}" removed`);
  };

  const categories = [...new Set(skills.map((s) => s.category))];

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="flex items-center justify-between mb-8" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <div>
            <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>My Skills</h1>
            <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage and track your skill proficiency</p>
          </div>
          <Button variant="primary" icon={Plus} onClick={() => setAddOpen(true)}>Add Skill</Button>
        </div>

        <div className="grid grid-3 mb-8">
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--primary-50)', color: 'var(--primary-600)' }}><Award size={22} /></div><div className="stat-card-value">{skills.length}</div><div className="stat-card-label">Total Skills</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--success-50)', color: 'var(--success-600)' }}><TrendingUp size={22} /></div><div className="stat-card-value">{skills.filter((s) => s.level >= 80).length}</div><div className="stat-card-label">Advanced Skills</div></div>
          <div className="stat-card"><div className="stat-card-icon" style={{ background: 'var(--warning-50)', color: 'var(--warning-600)' }}><Award size={22} /></div><div className="stat-card-value">{skills.filter((s) => s.level < 70).length}</div><div className="stat-card-label">Skills to Improve</div></div>
        </div>

        {categories.map((cat) => (
          <Card key={cat} className="mb-6">
            <CardBody>
              <h3 className="card-title mb-6">{cat}</h3>
              <div className="flex flex-col gap-5">
                {skills.filter((s) => s.category === cat).map((s) => (
                  <div key={s.name}>
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center gap-3">
                        <span className="text-sm font-semibold">{s.name}</span>
                        <SkillBadge skill={`${s.level}%`} />
                      </div>
                      <button onClick={() => handleRemove(s.name)} className="text-xs text-tertiary" style={{ cursor: 'pointer', color: 'var(--error-500)' }}>Remove</button>
                    </div>
                    <ProgressBar value={s.level} showValue={false} height={6} variant={s.level >= 80 ? 'success' : s.level >= 60 ? 'default' : 'warning'} />
                  </div>
                ))}
              </div>
            </CardBody>
          </Card>
        ))}

        <Modal open={addOpen} onClose={() => setAddOpen(false)} title="Add New Skill"
          footer={<><Button variant="ghost" onClick={() => setAddOpen(false)}>Cancel</Button><Button variant="primary" icon={Plus} onClick={handleAdd}>Add Skill</Button></>}
        >
          <Input label="Skill Name" placeholder="e.g. Docker" value={newSkill} onChange={(e) => setNewSkill(e.target.value)} autoFocus />
          <Input label="Proficiency Level" type="select" options={[{ value: '30', label: 'Beginner (30%)' }, { value: '60', label: 'Intermediate (60%)' }, { value: '85', label: 'Advanced (85%)' }]} />
          {allSkills.length > 0 && (
            <p className="form-hint" style={{ marginTop: 'var(--space-2)' }}>Suggested: {allSkills.slice(0, 8).join(', ')}</p>
          )}
        </Modal>
      </div>
    </PageQueryState>
  );
}
