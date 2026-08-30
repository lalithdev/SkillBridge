import { useState, useEffect, useMemo } from 'react';
import { Plus, Edit3, Mail, Phone, MapPin, Award, Briefcase, Save, X, User as UserIcon } from 'lucide-react';
import { useQueryClient } from '@tanstack/react-query';
import { useStudentProfile, useMasterSkills } from '@/hooks/useData';
import { studentApi, collegeApi } from '@/api';
import { apiClient } from '@/api/client';
import { queryKeys } from '@/utils/constants';
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
  const queryClient = useQueryClient();
  const { data: studentProfile, isLoading, isError, error, refetch } = useStudentProfile();
  const masterSkillsQuery = useMasterSkills();
  const toast = useToast();

  const [editOpen, setEditOpen] = useState(false);
  const [addSkillOpen, setAddSkillOpen] = useState(false);
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [saving, setSaving] = useState(false);
  const [departments, setDepartments] = useState([]);
  const [colleges, setColleges] = useState([]);

  const [editForm, setEditForm] = useState({
    name: '',
    email: '',
    phone: '',
    collegeId: '',
    college: '',
    departmentId: '',
    degree: 'B.Tech',
    branch: '',
    graduationYear: '',
    yearOfStudy: '',
    cgpa: '',
    bio: '',
    portfolioUrl: '',
    githubUrl: '',
  });

  const masterSkills = useMemo(() => {
    const list = masterSkillsQuery.data || [];
    return Array.isArray(list) ? list : list.content || [];
  }, [masterSkillsQuery.data]);

  useEffect(() => {
    let cancelled = false;
    async function loadAuxiliaryData() {
      try {
        const [deptsRes, collegesRes] = await Promise.allSettled([
          apiClient.get('/departments').then((r) => r.data),
          collegeApi.getPublic(),
        ]);
        if (!cancelled) {
          if (deptsRes.status === 'fulfilled' && Array.isArray(deptsRes.value)) {
            setDepartments(deptsRes.value);
          }
          if (collegesRes.status === 'fulfilled' && Array.isArray(collegesRes.value)) {
            setColleges(collegesRes.value);
          }
        }
      } catch {
        // best-effort
      }
    }
    loadAuxiliaryData();
    return () => { cancelled = true; };
  }, []);

  const safeProfile = useMemo(() => studentProfile || {
    id: null,
    name: user?.name || '',
    email: user?.email || '',
    phone: user?.phone || '',
    collegeId: user?.collegeId || null,
    college: user?.collegeName || '',
    collegeName: user?.collegeName || '',
    departmentId: user?.departmentId || null,
    degree: 'B.Tech',
    branch: '',
    departmentName: '',
    graduationYear: user?.graduationYear ? String(user.graduationYear) : '',
    yearOfStudy: '',
    cgpa: '',
    bio: '',
    portfolioUrl: '',
    githubUrl: '',
    skills: [],
    profileCompletion: 0,
    projects: [],
    certifications: [],
  }, [studentProfile, user]);

  useEffect(() => {
    if (studentProfile) {
      setEditForm({
        name: studentProfile.name || `${studentProfile.firstName || ''} ${studentProfile.lastName || ''}`.trim() || user?.name || '',
        email: studentProfile.email || user?.email || '',
        phone: studentProfile.phone || user?.phone || '',
        collegeId: studentProfile.collegeId ? String(studentProfile.collegeId) : '',
        college: studentProfile.collegeName || studentProfile.college || user?.collegeName || '',
        departmentId: studentProfile.departmentId ? String(studentProfile.departmentId) : '',
        degree: studentProfile.degree || 'B.Tech',
        branch: studentProfile.departmentName || studentProfile.branch || '',
        graduationYear: studentProfile.graduationYear ? String(studentProfile.graduationYear) : '',
        yearOfStudy: studentProfile.yearOfStudy ? String(studentProfile.yearOfStudy) : '',
        cgpa: studentProfile.cgpa != null ? String(studentProfile.cgpa) : '',
        bio: studentProfile.careerInterests || studentProfile.bio || '',
        portfolioUrl: studentProfile.portfolioUrl || '',
        githubUrl: studentProfile.githubUrl || '',
      });
    }
  }, [studentProfile, user]);

  const handleSaveEdit = async () => {
    setSaving(true);
    try {
      const payload = {
        name: editForm.name.trim(),
        phone: editForm.phone.trim() || null,
        collegeId: editForm.collegeId ? Number(editForm.collegeId) : undefined,
        departmentId: editForm.departmentId ? Number(editForm.departmentId) : null,
        graduationYear: editForm.graduationYear ? Number(editForm.graduationYear) : null,
        yearOfStudy: editForm.yearOfStudy ? Number(editForm.yearOfStudy) : null,
        cgpa: editForm.cgpa ? Number(editForm.cgpa) : null,
        careerInterests: editForm.bio.trim() || null,
        bio: editForm.bio.trim() || null,
        portfolioUrl: editForm.portfolioUrl.trim() || null,
        githubUrl: editForm.githubUrl.trim() || null,
      };

      const updated = await studentApi.updateProfile(payload);
      updateUser({
        name: updated.name || editForm.name,
        phone: updated.phone || editForm.phone,
        collegeName: updated.collegeName || updated.college || editForm.college,
        collegeId: updated.collegeId || (editForm.collegeId ? Number(editForm.collegeId) : undefined),
        graduationYear: updated.graduationYear || (editForm.graduationYear ? Number(editForm.graduationYear) : undefined),
      });

      await queryClient.invalidateQueries({ queryKey: queryKeys.students.profile });
      toast.success('Profile updated successfully!');
      setEditOpen(false);
    } catch (err) {
      toast.error(err?.message || 'Failed to update profile. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleAddSkill = async () => {
    if (!selectedSkillId) {
      toast.error('Please select a skill to add');
      return;
    }

    try {
      await studentApi.addSkill(Number(selectedSkillId));
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.students.profile }),
        queryClient.invalidateQueries({ queryKey: queryKeys.students.skills }),
      ]);
      toast.success('Skill added successfully!');
      setSelectedSkillId('');
      setAddSkillOpen(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.message || 'Failed to add skill.');
    }
  };

  const handleRemoveSkill = async (skillItem) => {
    const skillId = typeof skillItem === 'object' ? skillItem.id : null;
    if (!skillId) {
      toast.error('Unable to identify skill ID for removal.');
      return;
    }

    try {
      await studentApi.removeSkill(skillId);
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.students.profile }),
        queryClient.invalidateQueries({ queryKey: queryKeys.students.skills }),
      ]);
      toast.info('Skill removed successfully');
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.message || 'Failed to remove skill.');
    }
  };

  const currentSkillIds = useMemo(() => {
    return new Set((safeProfile.skills || []).map((s) => (typeof s === 'object' ? s.id : null)).filter(Boolean));
  }, [safeProfile.skills]);

  const availableMasterSkills = useMemo(() => {
    return masterSkills.filter((s) => !currentSkillIds.has(s.id));
  }, [masterSkills, currentSkillIds]);

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
                <div className="flex items-center gap-3">
                  <UserIcon size={18} style={{ color: 'var(--text-tertiary)' }} />
                  <div>
                    <div className="text-xs text-tertiary font-semibold">NAME</div>
                    <div className="text-sm font-semibold">{safeProfile.name || 'Not specified'}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <Mail size={18} style={{ color: 'var(--text-tertiary)' }} />
                  <div>
                    <div className="text-xs text-tertiary font-semibold">EMAIL</div>
                    <div className="text-sm font-semibold">{safeProfile.email || 'Not specified'}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <Phone size={18} style={{ color: 'var(--text-tertiary)' }} />
                  <div>
                    <div className="text-xs text-tertiary font-semibold">PHONE</div>
                    <div className="text-sm font-semibold">{safeProfile.phone || 'Not provided'}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <MapPin size={18} style={{ color: 'var(--text-tertiary)' }} />
                  <div>
                    <div className="text-xs text-tertiary font-semibold">COLLEGE</div>
                    <div className="text-sm font-semibold">{safeProfile.collegeName || safeProfile.college || 'Not specified'}</div>
                  </div>
                </div>
              </div>
            </CardBody>
          </Card>

          <Card className="animate-fade-in-up delay-2">
            <CardBody>
              <h3 className="card-title mb-6">Education</h3>
              <div className="flex flex-col gap-4">
                <div>
                  <div className="text-xs text-tertiary font-semibold">DEGREE</div>
                  <div className="text-sm font-semibold">{safeProfile.degree || 'B.Tech'}</div>
                </div>
                <div>
                  <div className="text-xs text-tertiary font-semibold">DEPARTMENT / BRANCH</div>
                  <div className="text-sm font-semibold">{safeProfile.departmentName || safeProfile.branch || 'Not specified'}</div>
                </div>
                <div>
                  <div className="text-xs text-tertiary font-semibold">GRADUATION YEAR</div>
                  <div className="text-sm font-semibold">{safeProfile.graduationYear || 'Not specified'}</div>
                </div>
                {safeProfile.cgpa && (
                  <div>
                    <div className="text-xs text-tertiary font-semibold">CGPA</div>
                    <div className="text-sm font-semibold">{safeProfile.cgpa} / 10.0</div>
                  </div>
                )}
              </div>
            </CardBody>
          </Card>
        </div>

        {/* Skills Section */}
        <Card className="mt-6 animate-fade-in-up delay-3">
          <CardBody>
            <div className="flex items-center justify-between mb-6">
              <h3 className="card-title">Skills</h3>
              <Button variant="secondary" size="sm" icon={Plus} onClick={() => setAddSkillOpen(true)}>Add Skill</Button>
            </div>
            <div className="flex flex-wrap gap-2">
              {(safeProfile.skills || []).length > 0 ? (
                safeProfile.skills.map((s) => {
                  const skillName = typeof s === 'object' ? s.name : s;
                  return (
                    <SkillBadge
                      key={typeof s === 'object' ? (s.id || s.name) : s}
                      skill={skillName}
                      removable
                      onRemove={() => handleRemoveSkill(s)}
                    />
                  );
                })
              ) : (
                <p className="text-sm text-tertiary">No skills added yet. Click &quot;Add Skill&quot; to build your profile.</p>
              )}
            </div>
          </CardBody>
        </Card>

        {/* Projects */}
        <Card className="mt-6 animate-fade-in-up delay-4">
          <CardBody>
            <h3 className="card-title mb-6">Projects</h3>
            <div className="flex flex-col gap-4">
              {(safeProfile.projects || []).length > 0 ? (
                safeProfile.projects.map((p) => (
                  <div key={p.id || p.name || p.title} style={{ padding: 'var(--space-5)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', background: 'var(--gray-50)' }}>
                    <div className="flex items-center gap-2 mb-2">
                      <Briefcase size={16} style={{ color: 'var(--primary-600)' }} />
                      <h4 style={{ fontSize: 'var(--text-base)', fontWeight: 700 }}>{p.title || p.name}</h4>
                    </div>
                    <p className="text-sm text-secondary mb-4">{p.description}</p>
                    {p.technologies && (
                      <div className="flex flex-wrap gap-2">
                        {p.technologies.map((t) => <SkillBadge key={t} skill={t} />)}
                      </div>
                    )}
                  </div>
                ))
              ) : (
                <p className="text-sm text-tertiary">No projects listed.</p>
              )}
            </div>
          </CardBody>
        </Card>

        {/* Certifications */}
        <Card className="mt-6 animate-fade-in-up delay-5">
          <CardBody>
            <h3 className="card-title mb-6">Certifications</h3>
            <div className="flex flex-col gap-4">
              {(safeProfile.certifications || []).length > 0 ? (
                safeProfile.certifications.map((c) => (
                  <div key={c.id || c.name || c.title} className="flex items-center gap-4" style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                    <div style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', background: 'var(--accent-50)', color: 'var(--accent-600)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <Award size={20} />
                    </div>
                    <div style={{ flex: 1 }}>
                      <div className="text-sm font-bold">{c.title || c.name}</div>
                      <div className="text-xs text-tertiary">{c.issuer} • {c.issuedDate || c.year || 'Verified'}</div>
                    </div>
                    <Badge variant="success">Verified</Badge>
                  </div>
                ))
              ) : (
                <p className="text-sm text-tertiary">No certifications listed.</p>
              )}
            </div>
          </CardBody>
        </Card>

        {/* Edit Profile Modal */}
        <Modal
          open={editOpen}
          onClose={() => setEditOpen(false)}
          title="Edit Profile"
          size="lg"
          footer={
            <>
              <Button variant="ghost" icon={X} onClick={() => setEditOpen(false)}>Cancel</Button>
              <Button variant="primary" icon={Save} loading={saving} onClick={handleSaveEdit}>Save Changes</Button>
            </>
          }
        >
          <Input
            label="Full Name"
            value={editForm.name}
            onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
            required
          />
          <Input
            label="Phone Number"
            icon={Phone}
            placeholder="+91 98765 43210"
            value={editForm.phone}
            onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
          />

          {colleges.length > 0 ? (
            <Input
              label="College"
              type="select"
              value={editForm.collegeId}
              onChange={(e) => setEditForm({ ...editForm, collegeId: e.target.value })}
              options={colleges.map((c) => ({ value: String(c.id), label: c.name }))}
            />
          ) : (
            <Input
              label="College Name"
              value={editForm.college}
              disabled
            />
          )}

          {departments.length > 0 && (
            <Input
              label="Department / Branch"
              type="select"
              value={editForm.departmentId}
              onChange={(e) => setEditForm({ ...editForm, departmentId: e.target.value })}
              options={departments.map((d) => ({ value: String(d.id), label: `${d.name} (${d.code})` }))}
            />
          )}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
            <Input
              label="Graduation Year"
              type="number"
              placeholder="2026"
              value={editForm.graduationYear}
              onChange={(e) => setEditForm({ ...editForm, graduationYear: e.target.value })}
            />
            <Input
              label="CGPA (0.0 - 10.0)"
              type="number"
              step="0.01"
              placeholder="8.5"
              value={editForm.cgpa}
              onChange={(e) => setEditForm({ ...editForm, cgpa: e.target.value })}
            />
          </div>

          <Input
            label="Bio / Career Interests"
            type="textarea"
            placeholder="Tell recruiters about your career goals and interests..."
            value={editForm.bio}
            onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })}
          />

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
            <Input
              label="Portfolio URL"
              placeholder="https://yourportfolio.dev"
              value={editForm.portfolioUrl}
              onChange={(e) => setEditForm({ ...editForm, portfolioUrl: e.target.value })}
            />
            <Input
              label="GitHub URL"
              placeholder="https://github.com/username"
              value={editForm.githubUrl}
              onChange={(e) => setEditForm({ ...editForm, githubUrl: e.target.value })}
            />
          </div>
        </Modal>

        {/* Add Skill Modal */}
        <Modal
          open={addSkillOpen}
          onClose={() => setAddSkillOpen(false)}
          title="Add Skill"
          footer={
            <>
              <Button variant="ghost" onClick={() => setAddSkillOpen(false)}>Cancel</Button>
              <Button variant="primary" icon={Plus} onClick={handleAddSkill}>Add Skill</Button>
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
            <p className="text-sm text-secondary">All available master skills have already been added to your profile.</p>
          )}
        </Modal>

        <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
