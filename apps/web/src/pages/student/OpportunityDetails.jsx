import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Clock, Briefcase, CheckCircle2, XCircle, ArrowLeft, FileText } from 'lucide-react';
import { useOpportunity } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import { useToast } from '@/components/ui/Toast';
import MatchScore from '@/components/ui/MatchScore';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';
import EmptyState from '@/components/ui/EmptyState';

export default function OpportunityDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { data: opp, isLoading, isError, error, refetch } = useOpportunity(id);
  const [applyOpen, setApplyOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({ name: '', phone: '', email: '', skills: '', qualification: '', experience: '', resume: '' });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.name) e.name = 'Name is required';
    if (!form.phone) e.phone = 'Phone number is required';
    if (!form.email) e.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Enter a valid email';
    if (!form.skills) e.skills = 'Skills are required';
    if (!form.resume) e.resume = 'Resume is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    setSubmitting(true);
    setTimeout(() => {
      setSubmitting(false);
      setApplyOpen(false);
      toast.success('Application submitted successfully!');
      navigate('/student/applications');
    }, 800);
  };

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      {!opp ? (
        <EmptyState icon={Briefcase} title="Opportunity not found" message="This opportunity may have been removed or is no longer available." action={<Button onClick={() => navigate('/student/opportunities')}>Back to Opportunities</Button>} />
      ) : (
        <div>
          <Button variant="ghost" size="sm" icon={ArrowLeft} onClick={() => navigate('/student/opportunities')} className="mb-6">Back to Opportunities</Button>

          <div className="card animate-fade-in-up" style={{ padding: 'var(--space-8)', marginBottom: 'var(--space-6)' }}>
            <div className="flex items-start gap-4" style={{ flexWrap: 'wrap' }}>
              <div className="opportunity-logo" style={{ width: 64, height: 64, fontSize: 'var(--text-2xl)' }}>{opp.companyInitial}</div>
              <div style={{ flex: 1 }}>
                <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>{opp.title}</h1>
                <div className="flex items-center gap-4 mt-2 flex-wrap">
                  <span className="text-lg font-semibold text-secondary">{opp.company}</span>
                  <Badge variant={opp.type === 'Internship' ? 'blue' : 'accent'}>{opp.type}</Badge>
                </div>
                <div className="flex items-center gap-4 mt-4 flex-wrap">
                  <span className="flex items-center gap-1 text-sm text-tertiary"><MapPin size={14} /> {opp.location}</span>
                  <span className="flex items-center gap-1 text-sm text-tertiary"><Clock size={14} /> Deadline: {opp.deadline}</span>
                  <span className="flex items-center gap-1 text-sm text-tertiary"><Briefcase size={14} /> {opp.salary}</span>
                </div>
              </div>
              <Button variant="primary" size="lg" icon={FileText} onClick={() => setApplyOpen(true)}>Apply Now</Button>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)' }}>
            <div className="flex flex-col gap-6">
              <div className="card animate-fade-in-up delay-1" style={{ padding: 'var(--space-6)' }}>
                <h3 className="card-title mb-4">Job Description</h3>
                <p className="text-sm text-secondary" style={{ lineHeight: 1.7 }}>{opp.description}</p>
              </div>

              <div className="card animate-fade-in-up delay-2" style={{ padding: 'var(--space-6)' }}>
                <h3 className="card-title mb-4">Required Skills</h3>
                <div className="flex flex-wrap gap-2">
                  {opp.requiredSkills.map((s) => (
                    <SkillBadge key={s} skill={s} variant={opp.matchedSkills.includes(s) ? 'matched' : 'missing'} />
                  ))}
                </div>
              </div>

              <div className="card animate-fade-in-up delay-3" style={{ padding: 'var(--space-6)' }}>
                <h3 className="card-title mb-4">Eligibility</h3>
                <p className="text-sm text-secondary">{opp.eligibility}</p>
              </div>
            </div>

            <div>
              <div className="card animate-fade-in-up delay-1" style={{ padding: 'var(--space-6)', textAlign: 'center' }}>
                <h3 className="card-title mb-6">Your Skill Match</h3>
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 'var(--space-6)' }}>
                  <MatchScore percentage={opp.matchPercentage} size={160} />
                </div>
                <div style={{ textAlign: 'left' }}>
                  <div className="mb-4">
                    <div className="flex items-center gap-2 mb-3"><CheckCircle2 size={16} style={{ color: 'var(--success-600)' }} /><span className="text-sm font-semibold">Your Matching Skills</span></div>
                    <div className="flex flex-wrap gap-2">
                      {opp.matchedSkills.map((s) => <SkillBadge key={s} skill={s} variant="matched" />)}
                    </div>
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-3"><XCircle size={16} style={{ color: 'var(--error-600)' }} /><span className="text-sm font-semibold">Skills You Need</span></div>
                    <div className="flex flex-wrap gap-2">
                      {opp.missingSkills.map((s) => <SkillBadge key={s} skill={s} variant="missing" />)}
                    </div>
                  </div>
                </div>
                <Button variant="primary" fullWidth size="lg" className="mt-6" icon={FileText} onClick={() => setApplyOpen(true)}>Apply Now</Button>
              </div>
            </div>
          </div>

          {applyOpen && (
            <div className="modal-overlay" onClick={() => setApplyOpen(false)}>
              <div className="modal modal-large" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                  <h2 className="modal-title">Apply for {opp.title}</h2>
                  <button className="modal-close" onClick={() => setApplyOpen(false)}>✕</button>
                </div>
                <form onSubmit={handleSubmit}>
                  <div className="modal-body">
                    <p className="text-sm text-secondary mb-6">Fill in your details to apply for this position at {opp.company}.</p>
                    <Input label="Full Name" name="name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} error={errors.name} placeholder="Enter your full name" />
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
                      <Input label="Phone Number" name="phone" required value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} error={errors.phone} placeholder="+91 98765 43210" />
                      <Input label="Email" name="email" type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} error={errors.email} placeholder="you@example.com" />
                    </div>
                    <Input label="Skills" name="skills" required value={form.skills} onChange={(e) => setForm({ ...form, skills: e.target.value })} error={errors.skills} placeholder="e.g. Java, Python, SQL, React" hint="List your relevant skills separated by commas" />
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
                      <Input label="Qualification" name="qualification" value={form.qualification} onChange={(e) => setForm({ ...form, qualification: e.target.value })} placeholder="e.g. B.Tech CS" />
                      <Input label="Experience" name="experience" value={form.experience} onChange={(e) => setForm({ ...form, experience: e.target.value })} placeholder="e.g. 2 years" />
                    </div>
                    <Input label="Resume Link" name="resume" required value={form.resume} onChange={(e) => setForm({ ...form, resume: e.target.value })} error={errors.resume} placeholder="Paste your resume/portfolio link" hint="Google Drive, Dropbox, or portfolio URL" />
                  </div>
                  <div className="modal-footer">
                    <Button variant="ghost" type="button" onClick={() => setApplyOpen(false)}>Cancel</Button>
                    <Button variant="primary" type="submit" loading={submitting}>Submit Application</Button>
                  </div>
                </form>
              </div>
            </div>
          )}

          <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 2fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
        </div>
      )}
    </PageQueryState>
  );
}
