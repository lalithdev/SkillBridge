import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, X, Save, Send, Briefcase } from 'lucide-react';
import { useMasterSkills } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import SkillBadge from '@/components/ui/SkillBadge';

export default function CreateOpportunity() {
  const navigate = useNavigate();
  const toast = useToast();
  const { data: allSkills = [], isLoading, isError, error, refetch } = useMasterSkills();
  const [loading, setLoading] = useState(false);
  const [skills, setSkills] = useState([]);
  const [skillInput, setSkillInput] = useState('');
  const [errors, setErrors] = useState({});
  const [form, setForm] = useState({ title: '', description: '', type: '', location: '', eligibility: '', deadline: '' });

  const addSkill = () => {
    if (skillInput && !skills.includes(skillInput)) {
      setSkills([...skills, skillInput]);
      setSkillInput('');
    }
  };

  const validate = () => {
    const e = {};
    if (!form.title) e.title = 'Title is required';
    if (!form.description) e.description = 'Description is required';
    if (!form.type) e.type = 'Type is required';
    if (!form.location) e.location = 'Location is required';
    if (!form.deadline) e.deadline = 'Deadline is required';
    if (skills.length === 0) e.skills = 'At least one skill is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handlePublish = (isDraft = false) => {
    if (!isDraft && !validate()) return;
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success(isDraft ? 'Draft saved!' : 'Opportunity published successfully!');
      navigate('/company/opportunities');
    }, 800);
  };

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="mb-8">
          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Create Opportunity</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Post a new internship or placement opportunity</p>
        </div>

        <Card className="animate-fade-in-up">
          <CardBody>
            <div className="flex items-center gap-2 mb-6"><Briefcase size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Basic Information</h3></div>
            <Input label="Opportunity Title" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} error={errors.title} placeholder="e.g. Backend Engineering Intern" />
            <Input label="Description" type="textarea" required value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} error={errors.description} placeholder="Describe the role, responsibilities, and what the candidate will work on..." rows={5} />
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
              <Input label="Opportunity Type" type="select" required options={[{ value: 'Internship', label: 'Internship' }, { value: 'Placement', label: 'Placement (Full-time)' }]} value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })} error={errors.type} />
              <Input label="Location" required value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} error={errors.location} placeholder="e.g. Bangalore, India or Remote" />
            </div>

            <div className="divider" />

            <div className="flex items-center gap-2 mb-6"><Plus size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Required Skills</h3></div>
            <div className="form-group">
              <label className="form-label">Skills <span className="required">*</span></label>
              <div className="flex gap-2 mb-3">
                <input className="form-input" placeholder="Type a skill and press Add" value={skillInput} onChange={(e) => setSkillInput(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addSkill())} style={{ flex: 1 }} />
                <Button variant="secondary" icon={Plus} onClick={addSkill}>Add</Button>
              </div>
              {skills.length > 0 && (
                <div className="flex flex-wrap gap-2">
                  {skills.map((s) => <SkillBadge key={s} skill={s} removable onRemove={() => setSkills(skills.filter((x) => x !== s))} />)}
                </div>
              )}
              {errors.skills && <div className="form-error">{errors.skills}</div>}
              <div className="form-hint">Suggested: {allSkills.slice(0, 8).join(', ')}</div>
            </div>

            <div className="divider" />

            <div className="flex items-center gap-2 mb-6"><Briefcase size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Additional Details</h3></div>
            <Input label="Eligibility Criteria" value={form.eligibility} onChange={(e) => setForm({ ...form, eligibility: e.target.value })} placeholder="e.g. B.Tech CS/IT, 7+ CGPA, 2025 batch" />
            <Input label="Application Deadline" type="date" required value={form.deadline} onChange={(e) => setForm({ ...form, deadline: e.target.value })} error={errors.deadline} />

            <div className="flex justify-end gap-3 mt-8">
              <Button variant="outline" icon={Save} onClick={() => handlePublish(true)} disabled={loading}>Save Draft</Button>
              <Button variant="primary" icon={Send} onClick={() => handlePublish(false)} loading={loading}>Publish Opportunity</Button>
            </div>
          </CardBody>
        </Card>

        <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
