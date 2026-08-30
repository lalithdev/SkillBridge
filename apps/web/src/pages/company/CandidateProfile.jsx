import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Mail, Phone, GraduationCap, Award, Briefcase, CheckCircle2, XCircle } from 'lucide-react';
import { useCandidate } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import MatchScore from '@/components/ui/MatchScore';
import SkillBadge from '@/components/ui/SkillBadge';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import Card, { CardBody } from '@/components/ui/Card';
import EmptyState from '@/components/ui/EmptyState';

const statusVariant = {
  Applied: 'blue', 'Under Review': 'warning', Shortlisted: 'accent',
  Selected: 'success', Rejected: 'error',
};

export default function CandidateProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data: c, isLoading, isError, error, refetch } = useCandidate(id);

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      {!c ? (
        <EmptyState icon={Briefcase} title="Candidate not found" message="This candidate profile may no longer be available." action={<Button onClick={() => navigate('/company/candidates')}>Back to Candidates</Button>} />
      ) : (
        <div>
          <Button variant="ghost" size="sm" icon={ArrowLeft} onClick={() => navigate('/company/candidates')} className="mb-6">Back to Candidates</Button>

          <Card className="animate-fade-in-up mb-6">
            <CardBody>
              <div className="flex items-start gap-4" style={{ flexWrap: 'wrap' }}>
                <div className="avatar avatar-xl">{c.name.split(' ').map((n) => n[0]).join('')}</div>
                <div style={{ flex: 1 }}>
                  <h1 style={{ fontSize: 'var(--text-2xl)', fontWeight: 800 }}>{c.name}</h1>
                  <div className="flex items-center gap-4 mt-2 flex-wrap">
                    <span className="flex items-center gap-1 text-sm text-secondary"><Mail size={14} /> {c.email}</span>
                    <span className="flex items-center gap-1 text-sm text-secondary"><Phone size={14} /> {c.phone}</span>
                  </div>
                  <div className="flex items-center gap-2 mt-3">
                    <Badge variant="primary">{c.education.degree} {c.education.branch}</Badge>
                    <Badge variant="gray">CGPA: {c.education.cgpa}</Badge>
                    <Badge variant="accent">Class of {c.education.graduationYear}</Badge>
                  </div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <MatchScore percentage={c.matchPercentage} size={120} />
                </div>
              </div>
            </CardBody>
          </Card>

          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 'var(--space-6)' }}>
            <div className="flex flex-col gap-6">
              <Card className="animate-fade-in-up delay-1"><CardBody>
                <div className="flex items-center gap-2 mb-4"><GraduationCap size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Education</h3></div>
                <div className="flex flex-col gap-3">
                  <div><div className="text-xs text-tertiary font-semibold">DEGREE</div><div className="text-sm font-semibold">{c.education.degree}</div></div>
                  <div><div className="text-xs text-tertiary font-semibold">BRANCH</div><div className="text-sm font-semibold">{c.education.branch}</div></div>
                  <div><div className="text-xs text-tertiary font-semibold">COLLEGE</div><div className="text-sm font-semibold">{c.education.college}</div></div>
                  <div><div className="text-xs text-tertiary font-semibold">GRADUATION YEAR</div><div className="text-sm font-semibold">{c.education.graduationYear}</div></div>
                </div>
              </CardBody></Card>

              <Card className="animate-fade-in-up delay-2"><CardBody>
                <h3 className="card-title mb-4">Skills</h3>
                <div className="flex flex-wrap gap-2">{c.skills.map((s) => <SkillBadge key={s} skill={s} variant={c.matchedSkills.includes(s) ? 'matched' : 'default'} />)}</div>
              </CardBody></Card>

              <Card className="animate-fade-in-up delay-3"><CardBody>
                <h3 className="card-title mb-4">Projects</h3>
                <div className="flex flex-col gap-4">
                  {c.projects.map((p) => (
                    <div key={p.name} style={{ padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', background: 'var(--gray-50)' }}>
                      <div className="flex items-center gap-2 mb-2"><Briefcase size={16} style={{ color: 'var(--primary-600)' }} /><h4 style={{ fontSize: 'var(--text-sm)', fontWeight: 700 }}>{p.name}</h4></div>
                      <p className="text-sm text-secondary mb-3">{p.description}</p>
                      <div className="flex flex-wrap gap-2">{p.technologies.map((t) => <SkillBadge key={t} skill={t} />)}</div>
                    </div>
                  ))}
                </div>
              </CardBody></Card>

              <Card className="animate-fade-in-up delay-4"><CardBody>
                <h3 className="card-title mb-4">Certifications</h3>
                <div className="flex flex-col gap-3">
                  {c.certifications.map((cert) => (
                    <div key={cert.name} className="flex items-center gap-3" style={{ padding: 'var(--space-3)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-light)' }}>
                      <Award size={18} style={{ color: 'var(--accent-600)' }} />
                      <div style={{ flex: 1 }}><div className="text-sm font-semibold">{cert.name}</div><div className="text-xs text-tertiary">{cert.issuer} • {cert.year}</div></div>
                      <Badge variant="success">Verified</Badge>
                    </div>
                  ))}
                </div>
              </CardBody></Card>
            </div>

            <div className="flex flex-col gap-6">
              <Card className="animate-fade-in-up delay-1"><CardBody>
                <h3 className="card-title mb-4">Match Analysis</h3>
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 'var(--space-5)' }}>
                  <MatchScore percentage={c.matchPercentage} size={140} label="Skill Match" />
                </div>
                <div className="mb-4">
                  <div className="flex items-center gap-2 mb-2"><CheckCircle2 size={16} style={{ color: 'var(--success-600)' }} /><span className="text-sm font-semibold">Matched</span></div>
                  <div className="flex flex-wrap gap-2">{c.matchedSkills.map((s) => <SkillBadge key={s} skill={s} variant="matched" />)}</div>
                </div>
                {c.missingSkills.length > 0 && (
                  <div>
                    <div className="flex items-center gap-2 mb-2"><XCircle size={16} style={{ color: 'var(--error-600)' }} /><span className="text-sm font-semibold">Missing</span></div>
                    <div className="flex flex-wrap gap-2">{c.missingSkills.map((s) => <SkillBadge key={s} skill={s} variant="missing" />)}</div>
                  </div>
                )}
              </CardBody></Card>

              <Card className="animate-fade-in-up delay-2"><CardBody>
                <h3 className="card-title mb-4">Application History</h3>
                <div className="flex flex-col gap-3">
                  {c.applicationHistory.map((a, i) => (
                    <div key={i} style={{ padding: 'var(--space-3)', borderRadius: 'var(--radius-sm)', background: 'var(--gray-50)' }}>
                      <div className="text-sm font-semibold">{a.position}</div>
                      <div className="text-xs text-tertiary">{a.company} • {a.date}</div>
                      <div className="mt-2"><Badge variant={statusVariant[a.status]}>{a.status}</Badge></div>
                    </div>
                  ))}
                </div>
              </CardBody></Card>

              <Button variant="primary" size="lg" fullWidth icon={CheckCircle2}>Shortlist Candidate</Button>
            </div>
          </div>

          <style>{`@media (max-width: 1024px) { div[style*="grid-template-columns: 2fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
        </div>
      )}
    </PageQueryState>
  );
}
