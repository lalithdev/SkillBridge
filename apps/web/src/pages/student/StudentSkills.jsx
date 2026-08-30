import { useState, useMemo } from 'react';
import { Award, Plus, TrendingUp } from 'lucide-react';
import { useQueryClient } from '@tanstack/react-query';
import { useStudentSkills, useMasterSkills } from '@/hooks/useData';
import { studentApi } from '@/api';
import { queryKeys } from '@/utils/constants';
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
  const queryClient = useQueryClient();
  const skillsQuery = useStudentSkills();
  const masterSkillsQuery = useMasterSkills();
  const [addOpen, setAddOpen] = useState(false);
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const isLoading = skillsQuery.isLoading || masterSkillsQuery.isLoading;
  const isError = skillsQuery.isError || masterSkillsQuery.isError;
  const error = skillsQuery.error || masterSkillsQuery.error;
  const refetch = () => {
    skillsQuery.refetch();
    masterSkillsQuery.refetch();
  };

  const skills = useMemo(() => skillsQuery.data || [], [skillsQuery.data]);

  const masterSkills = useMemo(() => {
    const list = masterSkillsQuery.data || [];
    return Array.isArray(list) ? list : list.content || [];
  }, [masterSkillsQuery.data]);

  const currentSkillIds = useMemo(() => {
    return new Set(skills.map((s) => (typeof s === 'object' ? s.id : null)).filter(Boolean));
  }, [skills]);

  const availableMasterSkills = useMemo(() => {
    return masterSkills.filter((s) => !currentSkillIds.has(s.id));
  }, [masterSkills, currentSkillIds]);

  const handleAdd = async () => {
    if (!selectedSkillId) {
      toast.error('Please select a skill');
      return;
    }

    setSubmitting(true);
    try {
      await studentApi.addSkill(Number(selectedSkillId));
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.students.skills }),
        queryClient.invalidateQueries({ queryKey: queryKeys.students.profile }),
      ]);
      toast.success('Skill added successfully!');
      setSelectedSkillId('');
      setAddOpen(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.message || 'Failed to add skill.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemove = async (skillItem) => {
    const skillId = typeof skillItem === 'object' ? skillItem.id : null;
    const skillName = typeof skillItem === 'object' ? skillItem.name : skillItem;
    if (!skillId) {
      toast.error('Unable to find skill ID for deletion.');
      return;
    }

    try {
      await studentApi.removeSkill(skillId);
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.students.skills }),
        queryClient.invalidateQueries({ queryKey: queryKeys.students.profile }),
      ]);
      toast.info(`Skill "${skillName}" removed`);
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.message || 'Failed to remove skill.');
    }
  };

  const categories = useMemo(() => {
    return [...new Set(skills.map((s) => (typeof s === 'object' ? s.category || 'General' : 'General')))];
  }, [skills]);

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
          <div className="stat-card">
            <div className="stat-card-icon" style={{ background: 'var(--primary-50)', color: 'var(--primary-600)' }}><Award size={22} /></div>
            <div className="stat-card-value">{skills.length}</div>
            <div className="stat-card-label">Total Skills</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-icon" style={{ background: 'var(--success-50)', color: 'var(--success-600)' }}><TrendingUp size={22} /></div>
            <div className="stat-card-value">{skills.filter((s) => (s.level || 75) >= 80).length}</div>
            <div className="stat-card-label">Advanced Skills</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-icon" style={{ background: 'var(--warning-50)', color: 'var(--warning-600)' }}><Award size={22} /></div>
            <div className="stat-card-value">{skills.filter((s) => (s.level || 75) < 70).length}</div>
            <div className="stat-card-label">Skills to Improve</div>
          </div>
        </div>

        {skills.length === 0 ? (
          <Card className="mb-6">
            <CardBody style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
              <p className="text-secondary mb-4">You have not added any skills to your profile yet.</p>
              <Button variant="primary" icon={Plus} onClick={() => setAddOpen(true)}>Add Your First Skill</Button>
            </CardBody>
          </Card>
        ) : (
          categories.map((cat) => {
            const catSkills = skills.filter((s) => (s.category || 'General') === cat);
            return (
              <Card key={cat} className="mb-6">
                <CardBody>
                  <h3 className="card-title mb-6">{cat}</h3>
                  <div className="flex flex-col gap-5">
                    {catSkills.map((s) => {
                      const skillName = typeof s === 'object' ? s.name : s;
                      const level = typeof s === 'object' ? (s.level || 75) : 75;
                      return (
                        <div key={typeof s === 'object' ? (s.id || s.name) : s}>
                          <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center gap-3">
                              <span className="text-sm font-semibold">{skillName}</span>
                              <SkillBadge skill={`${level}%`} />
                            </div>
                            <button
                              onClick={() => handleRemove(s)}
                              className="text-xs"
                              style={{ cursor: 'pointer', color: 'var(--error-500)', background: 'none', border: 'none', padding: 0 }}
                            >
                              Remove
                            </button>
                          </div>
                          <ProgressBar
                            value={level}
                            showValue={false}
                            height={6}
                            variant={level >= 80 ? 'success' : level >= 60 ? 'default' : 'warning'}
                          />
                        </div>
                      );
                    })}
                  </div>
                </CardBody>
              </Card>
            );
          })
        )}

        <Modal
          open={addOpen}
          onClose={() => setAddOpen(false)}
          title="Add New Skill"
          footer={
            <>
              <Button variant="ghost" onClick={() => setAddOpen(false)}>Cancel</Button>
              <Button variant="primary" icon={Plus} loading={submitting} onClick={handleAdd}>Add Skill</Button>
            </>
          }
        >
          {availableMasterSkills.length > 0 ? (
            <Input
              label="Select Skill"
              type="select"
              value={selectedSkillId}
              onChange={(e) => setSelectedSkillId(e.target.value)}
              options={availableMasterSkills.map((s) => ({ value: String(s.id), label: `${s.name} (${s.category || 'General'})` }))}
              autoFocus
            />
          ) : (
            <p className="text-sm text-secondary">All available master skills are already present on your profile.</p>
          )}
        </Modal>
      </div>
    </PageQueryState>
  );
}
