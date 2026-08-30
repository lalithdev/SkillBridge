import { useState, useEffect, useMemo } from 'react';
import { Plus, Edit3, Mail, Phone, MapPin, Award, Briefcase, Save, X, User as UserIcon } from 'lucide-react';
import { useStudentProfile } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import { useAuth } from '@/hooks/useAuth';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import SkillBadge from '@/components/ui/SkillBadge';
import ProgressBar from '@/components/ui/ProgressBar';
import Badge from '@/components/ui/Badge';
import Modal from '@/components/ui/Modal';
import Input from '@/components/ui/Input';

export default function StudentProfile() {
  const { user, updateUser } = useAuth();
  const { data: studentProfile, isLoading, isError, error, refetch } = useStudentProfile();
  const toast = useToast();
  const [editOpen, setEditOpen] = useState(false);
  const [addSkillOpen, setAddSkillOpen] = useState(false);
  const [newSkill, setNewSkill] = useState('');
  const [skills, setSkills] = useState([]);
  const [editForm, setEditForm] = useState({
    name: '', email: '', phone: '', college: '', degree: '', branch: '', graduationYear: '', bio: '',
  });

  const safeProfile = useMemo(() => studentProfile || {
    name: user?.name || '',
    email: user?.email || '',
    phone: '',
    college: user?.collegeName || '',
    degree: '',
    branch: '',
    graduationYear: '',
    bio: '',
    skills: [],
    profileCompletion: 0,
    projects: [],
    certifications: [],
  }, [studentProfile, user?.name, user?.email, user?.collegeName]);

  useEffect(() => {
    setSkills(safeProfile.skills || []);
    setEditForm({
      name: user?.name || safeProfile.name,
      email: user?.email || safeProfile.email,
      phone: safeProfile.phone || '',
      college: safeProfile.college || user?.collegeName || '',
      degree: safeProfile.degree || '',
      branch: safeProfile.branch || '',
      graduationYear: safeProfile.graduationYear || '',
      bio: safeProfile.bio || '',
    });
  }, [safeProfile.name, safeProfile.email, safeProfile.phone, safeProfile.college, safeProfile.degree, safeProfile.branch, safeProfile.graduationYear, safeProfile.bio, safeProfile.skills, user?.name, user?.email, user?.collegeName]);

  const handleSaveEdit = () => {
    updateUser({ name: editForm.name, email: editForm.email });
    toast.success('Profile updated successfully!');
    setEditOpen(false);
  };

  const handleAddSkill = () => {
    if (newSkill && !skills.includes(newSkill)) {
      setSkills([...skills, newSkill]);
      toast.success(`Skill "${newSkill}" added!`);
    }
    setNewSkill('');
    setAddSkillOpen(false);
  };

  const handleRemoveSkill = (skill) => {
    setSkills(skills.filter((s) => s !== skill));
    toast.info(`Skill "${skill}" removed`);
  };

  return (
    <PageQueryState
      isLoading={isLoading}
      isError={isError}
      error={error}
      onRetry={refetch}
      isEmpty={!studentProfile}
      emptyProps={{ title: 'Profile not available', message: 'Your student profile will appear here once loaded.' }}
    >
      <div>
        <div className="flex items-center justify-between mb-8" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <div>
            <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>My Profile</h1>
            <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your personal information and skills</p>
          </div>
          <Button variant="primary" icon={Edit3} onClick={() => setEditOpen(true)}>Edit Profile</Button>
        </div>

        <Card className="mb-6 animate-fade-in-up">
          <CardBody>
            <div className="flex items-center justify-between mb-4">
              <h3 style={{ fontSize: 'var(--text-lg)', fontWeight: 700 }}>Profile Completion</h3>
              <span style={{ fontSize: 'var(--text-2xl)', fontWeight: 800, color: 'var(--primary-600)' }}>{safeProfile.profileCompletion}%</span>
            </div>
            <ProgressBar value={safeProfile.profileCompletion} showValue={false} height={12} />
            <p className="text-sm text-tertiary mt-4">Complete your profile to increase visibility to recruiters</p>
          </CardBody>
        </Card>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }}>
          <Card className="animate-fade-in-up delay-1">
            <CardBody>
              <h3 className="card-title mb-6">Personal Information</h3>
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-3"><UserIcon size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">NAME</div><div className="text-sm font-semibold">{editForm.name}</div></div></div>
                <div className="flex items-center gap-3"><Mail size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">EMAIL</div><div className="text-sm font-semibold">{editForm.email}</div></div></div>
                <div className="flex items-center gap-3"><Phone size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">PHONE</div><div className="text-sm font-semibold">{editForm.phone}</div></div></div>
                <div className="flex items-center gap-3"><MapPin size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">COLLEGE</div><div className="text-sm font-semibold">{editForm.college}</div></div></div>
              </div>
            </CardBody>
          </Card>

          <Card className="animate-fade-in-up delay-2">
            <CardBody>
              <h3 className="card-title mb-6">Education</h3>
              <div className="flex flex-col gap-4">
                <div><div className="text-xs text-tertiary font-semibold">DEGREE</div><div className="text-sm font-semibold">{safeProfile.degree}</div></div>
                <div><div className="text-xs text-tertiary font-semibold">BRANCH</div><div className="text-sm font-semibold">{safeProfile.branch}</div></div>
                <div><div className="text-xs text-tertiary font-semibold">GRADUATION YEAR</div><div className="text-sm font-semibold">{safeProfile.graduationYear}</div></div>
              </div>
            </CardBody>
          </Card>
        </div>

        <Card className="mt-6 animate-fade-in-up delay-3">
          <CardBody>
            <div className="flex items-center justify-between mb-6">
              <h3 className="card-title">Skills</h3>
              <Button variant="secondary" size="sm" icon={Plus} onClick={() => setAddSkillOpen(true)}>Add Skill</Button>
            </div>
            <div className="flex flex-wrap gap-2">
              {skills.map((s) => <SkillBadge key={s} skill={s} removable onRemove={handleRemoveSkill} />)}
            </div>
          </CardBody>
        </Card>

        <Card className="mt-6 animate-fade-in-up delay-4">
          <CardBody>
            <h3 className="card-title mb-6">Projects</h3>
            <div className="flex flex-col gap-4">
              {(safeProfile.projects || []).map((p) => (
                <div key={p.name} style={{ padding: 'var(--space-5)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', background: 'var(--gray-50)' }}>
                  <div className="flex items-center gap-2 mb-2">
                    <Briefcase size={16} style={{ color: 'var(--primary-600)' }} />
                    <h4 style={{ fontSize: 'var(--text-base)', fontWeight: 700 }}>{p.name}</h4>
                  </div>
                  <p className="text-sm text-secondary mb-4">{p.description}</p>
                  <div className="flex flex-wrap gap-2">
                    {p.technologies.map((t) => <SkillBadge key={t} skill={t} />)}
                  </div>
                </div>
              ))}
            </div>
          </CardBody>
        </Card>

        <Card className="mt-6 animate-fade-in-up delay-5">
          <CardBody>
            <h3 className="card-title mb-6">Certifications</h3>
            <div className="flex flex-col gap-4">
              {(safeProfile.certifications || []).map((c) => (
                <div key={c.name} className="flex items-center gap-4" style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                  <div style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', background: 'var(--accent-50)', color: 'var(--accent-600)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Award size={20} />
                  </div>
                  <div style={{ flex: 1 }}>
                    <div className="text-sm font-bold">{c.name}</div>
                    <div className="text-xs text-tertiary">{c.issuer} • {c.year}</div>
                  </div>
                  <Badge variant="success">Verified</Badge>
                </div>
              ))}
            </div>
          </CardBody>
        </Card>

        <Modal open={editOpen} onClose={() => setEditOpen(false)} title="Edit Profile" size="lg"
          footer={<>
            <Button variant="ghost" icon={X} onClick={() => setEditOpen(false)}>Cancel</Button>
            <Button variant="primary" icon={Save} onClick={handleSaveEdit}>Save Changes</Button>
          </>}
        >
          <Input label="Full Name" value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} />
          <Input label="Email" type="email" value={editForm.email} onChange={(e) => setEditForm({ ...editForm, email: e.target.value })} />
          <Input label="Phone" value={editForm.phone} onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} />
          <Input label="College" value={editForm.college} onChange={(e) => setEditForm({ ...editForm, college: e.target.value })} />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
            <Input label="Degree" value={editForm.degree} onChange={(e) => setEditForm({ ...editForm, degree: e.target.value })} />
            <Input label="Branch" value={editForm.branch} onChange={(e) => setEditForm({ ...editForm, branch: e.target.value })} />
          </div>
          <Input label="Graduation Year" type="number" value={editForm.graduationYear} onChange={(e) => setEditForm({ ...editForm, graduationYear: e.target.value })} />
          <Input label="Bio" type="textarea" value={editForm.bio} onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })} />
        </Modal>

        <Modal open={addSkillOpen} onClose={() => setAddSkillOpen(false)} title="Add Skill"
          footer={<>
            <Button variant="ghost" onClick={() => setAddSkillOpen(false)}>Cancel</Button>
            <Button variant="primary" icon={Plus} onClick={handleAddSkill}>Add Skill</Button>
          </>}
        >
          <Input label="Skill Name" placeholder="e.g. Docker" value={newSkill} onChange={(e) => setNewSkill(e.target.value)} autoFocus />
        </Modal>

        <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
