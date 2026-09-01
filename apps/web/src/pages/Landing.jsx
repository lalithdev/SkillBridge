import { Link, useNavigate } from 'react-router-dom';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import Button from '@/components/ui/Button';
import { usePlatformStats } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import {
  ArrowRight,
  Search,
  Users,
  Building2,
  GraduationCap,
  Briefcase,
  Award,
  Target,
  Sparkles,
  TrendingUp,
  CheckCircle2,
  BarChart3,
  Zap,
} from 'lucide-react';

/* ---------- shared inline style fragments ---------- */

const sectionPadding = {
  padding: 'var(--space-20) 0',
};

const sectionInner = {
  maxWidth: 'var(--max-content)',
  margin: '0 auto',
  padding: '0 var(--space-6)',
};

const eyebrowStyle = {
  display: 'inline-flex',
  alignItems: 'center',
  gap: 'var(--space-2)',
  padding: '6px 14px',
  borderRadius: 'var(--radius-full)',
  background: 'var(--primary-50)',
  color: 'var(--primary-700)',
  fontSize: 'var(--text-sm)',
  fontWeight: 600,
  border: '1px solid var(--primary-100)',
};

const sectionHeading = {
  fontSize: 'clamp(var(--text-3xl), 4vw, var(--text-5xl))',
  fontWeight: 800,
  lineHeight: 1.15,
  color: 'var(--text-primary)',
  marginTop: 'var(--space-4)',
};

const sectionSub = {
  fontSize: 'var(--text-lg)',
  color: 'var(--text-secondary)',
  marginTop: 'var(--space-4)',
  maxWidth: 620,
  lineHeight: 1.6,
};

const iconBox = (bg, color) => ({
  width: 56,
  height: 56,
  borderRadius: 'var(--radius-lg)',
  background: bg,
  color,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  marginBottom: 'var(--space-5)',
  transition: 'transform var(--transition-base)',
});

/* ---------- data ---------- */

const features = [
  {
    icon: GraduationCap,
    title: 'For Students',
    iconBg: 'var(--primary-50)',
    iconColor: 'var(--primary-700)',
    points: [
      'Build a verified skill profile with projects & certifications',
      'Get matched to internships and placements automatically',
      'See exactly which skills you need to land your dream role',
      'Track skill gaps and get personalized learning paths',
    ],
  },
  {
    icon: Building2,
    title: 'For Companies',
    iconBg: 'var(--secondary-50)',
    iconColor: 'var(--secondary-700)',
    points: [
      'Post opportunities and reach pre-screened, matched talent',
      'Filter candidates by real skill proficiency, not just resumes',
      'Reduce time-to-hire with AI-powered candidate ranking',
      'Access analytics on applicant pipelines and skill coverage',
    ],
  },
  {
    icon: Briefcase,
    title: 'For Colleges',
    iconBg: 'var(--accent-50)',
    iconColor: 'var(--accent-700)',
    points: [
      'Monitor student placement readiness across departments',
      'Identify curriculum gaps against live industry demand',
      'Connect students directly with recruiting partners',
      'Track placement metrics and skill trends over time',
    ],
  },
];

const steps = [
  {
    icon: Target,
    title: 'Build Skill Profile',
    description:
      'Students create a verified profile with skills, projects, and certifications — validated against industry benchmarks.',
  },
  {
    icon: Search,
    title: 'Match With Opportunities',
    description:
      'Our intelligence engine matches profiles to internships and placements using real skill demand data.',
  },
  {
    icon: BarChart3,
    title: 'Identify Skill Gaps',
    description:
      'See the precise gap between current skills and what employers need, with recommended learning paths.',
  },
  {
    icon: Zap,
    title: 'Connect With Industry',
    description:
      'Apply with confidence, get shortlisted by matching companies, and bridge the academia-industry divide.',
  },
];

const stats = [
  { key: 'students', label: 'Students', icon: GraduationCap, color: 'var(--primary-600)' },
  { key: 'companies', label: 'Companies', icon: Building2, color: 'var(--secondary-600)' },
  { key: 'opportunities', label: 'Opportunities', icon: Briefcase, color: 'var(--accent-600)' },
  { key: 'skills', label: 'Skills Tracked', icon: Award, color: 'var(--primary-700)' },
];

const formatStat = (n) => {
  if (n === null || n === undefined) return '0';
  const num = Number(n);
  return isNaN(num) ? String(n) : num.toLocaleString('en-US');
};

/* ---------- mock dashboard preview data ---------- */

const previewBars = [
  { label: 'Python', value: 95, color: 'var(--primary-500)' },
  { label: 'React', value: 88, color: 'var(--secondary-500)' },
  { label: 'AWS', value: 72, color: 'var(--accent-500)' },
  { label: 'Docker', value: 64, color: 'var(--primary-600)' },
  { label: 'ML', value: 58, color: 'var(--secondary-600)' },
];

const previewMiniStats = [
  { label: 'Profile', value: '85%', icon: CheckCircle2, color: 'var(--success-600)' },
  { label: 'Matches', value: '12', icon: Target, color: 'var(--primary-600)' },
  { label: 'Skill Gaps', value: '4', icon: TrendingUp, color: 'var(--warning-600)' },
];

/* ---------- component ---------- */

export default function Landing() {
  const navigate = useNavigate();
  const { data: platformStats } = usePlatformStats();
  const statsData = {
    students: platformStats?.students ?? platformStats?.totalStudents ?? 0,
    companies: platformStats?.companies ?? platformStats?.totalCompanies ?? 0,
    opportunities: platformStats?.opportunities ?? platformStats?.totalOpportunities ?? 0,
    skills: platformStats?.skills ?? platformStats?.totalSkillsMapped ?? 0,
  };

  return (
    <>
      <Navbar />

      {/* =====================================================
          1. HERO
         ===================================================== */}
      <section
        id="home"
        style={{
          position: 'relative',
          paddingTop: 'calc(var(--navbar-height) + var(--space-20))',
          paddingBottom: 'var(--space-20)',
          overflow: 'hidden',
          background:
            'radial-gradient(1200px 600px at 80% -10%, rgba(99,102,241,0.18), transparent 60%),' +
            'radial-gradient(900px 500px at 10% 10%, rgba(37,99,235,0.15), transparent 55%),' +
            'linear-gradient(180deg, var(--primary-50) 0%, var(--secondary-50) 45%, var(--bg-body) 100%)',
        }}
      >
        {/* decorative grid lines */}
        <div
          aria-hidden
          style={{
            position: 'absolute',
            inset: 0,
            backgroundImage:
              'linear-gradient(rgba(79,70,229,0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(79,70,229,0.04) 1px, transparent 1px)',
            backgroundSize: '48px 48px',
            maskImage: 'radial-gradient(ellipse 80% 60% at 50% 30%, #000 40%, transparent 100%)',
            WebkitMaskImage: 'radial-gradient(ellipse 80% 60% at 50% 30%, #000 40%, transparent 100%)',
            pointerEvents: 'none',
          }}
        />

        <div style={{ ...sectionInner, position: 'relative', zIndex: 1 }}>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: '1.05fr 0.95fr',
              gap: 'var(--space-16)',
              alignItems: 'center',
            }}
          >
            {/* ---- left: copy + CTAs ---- */}
            <div>
              <span className="animate-fade-in-up" style={eyebrowStyle}>
                <Sparkles size={14} />
                Skill Intelligence Platform
              </span>

              <h1
                className="animate-fade-in-up delay-1"
                style={{
                  fontSize: 'clamp(var(--text-4xl), 5vw, var(--text-6xl))',
                  fontWeight: 800,
                  lineHeight: 1.08,
                  letterSpacing: '-0.02em',
                  marginTop: 'var(--space-6)',
                  color: 'var(--text-primary)',
                }}
              >
                Bridge Your Skills to{' '}
                <span className="gradient-text">Your Career.</span>
              </h1>

              <p
                className="animate-fade-in-up delay-2"
                style={{
                  fontSize: 'var(--text-lg)',
                  color: 'var(--text-secondary)',
                  marginTop: 'var(--space-5)',
                  maxWidth: 540,
                  lineHeight: 1.65,
                }}
              >
                SkillBridge connects students, companies, and colleges through a
                shared skill intelligence layer — matching real proficiency to real
                opportunity, and closing the gap between what's taught and what's
                hired.
              </p>

              <div
                className="animate-fade-in-up delay-3"
                style={{
                  display: 'flex',
                  gap: 'var(--space-4)',
                  marginTop: 'var(--space-8)',
                  flexWrap: 'wrap',
                }}
              >
                <Button
                  variant="primary"
                  size="lg"
                  iconRight={ArrowRight}
                  onClick={() => navigate('/register')}
                >
                  Get Started
                </Button>
                <Button
                  variant="outline"
                  size="lg"
                  onClick={() => navigate('/login')}
                >
                  Explore Opportunities
                </Button>
              </div>

              {/* trust row */}
              <div
                className="animate-fade-in-up delay-4"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 'var(--space-6)',
                  marginTop: 'var(--space-10)',
                  flexWrap: 'wrap',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                  <Users size={18} style={{ color: 'var(--primary-600)' }} />
                  <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', fontWeight: 600 }}>
                    {formatStat(statsData.students)}+ students
                  </span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                  <Building2 size={18} style={{ color: 'var(--secondary-600)' }} />
                  <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', fontWeight: 600 }}>
                    {formatStat(statsData.companies)}+ companies
                  </span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                  <Award size={18} style={{ color: 'var(--accent-600)' }} />
                  <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', fontWeight: 600 }}>
                    {formatStat(statsData.skills)} skills tracked
                  </span>
                </div>
              </div>
            </div>

            {/* ---- right: mock dashboard preview ---- */}
            <div className="animate-fade-in-up delay-3" style={{ position: 'relative' }}>
              {/* glow */}
              <div
                aria-hidden
                style={{
                  position: 'absolute',
                  inset: '-20px',
                  background:
                    'linear-gradient(135deg, rgba(79,70,229,0.25), rgba(37,99,235,0.18))',
                  borderRadius: 'var(--radius-2xl)',
                  filter: 'blur(40px)',
                  zIndex: 0,
                }}
              />

              <div
                className="card"
                style={{
                  position: 'relative',
                  zIndex: 1,
                  padding: 'var(--space-6)',
                  borderRadius: 'var(--radius-2xl)',
                  boxShadow: 'var(--shadow-2xl)',
                  border: '1px solid rgba(255,255,255,0.6)',
                }}
              >
                {/* card header */}
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    paddingBottom: 'var(--space-4)',
                    borderBottom: '1px solid var(--border-light)',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                    <div
                      style={{
                        width: 36,
                        height: 36,
                        borderRadius: 'var(--radius-md)',
                        background:
                          'linear-gradient(135deg, var(--primary-600), var(--secondary-600))',
                        color: '#fff',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <BarChart3 size={18} />
                    </div>
                    <div>
                      <div style={{ fontSize: 'var(--text-sm)', fontWeight: 700, color: 'var(--text-primary)' }}>
                        Skill Analytics
                      </div>
                      <div style={{ fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)' }}>
                        Live dashboard preview
                      </div>
                    </div>
                  </div>
                  <span className="badge badge-success">
                    <CheckCircle2 size={12} /> Active
                  </span>
                </div>

                {/* mini stat row */}
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(3, 1fr)',
                    gap: 'var(--space-3)',
                    marginTop: 'var(--space-5)',
                  }}
                >
                  {previewMiniStats.map((s) => (
                    <div
                      key={s.label}
                      style={{
                        padding: 'var(--space-3)',
                        borderRadius: 'var(--radius-md)',
                        background: 'var(--gray-50)',
                        border: '1px solid var(--border-light)',
                      }}
                    >
                      <s.icon size={16} style={{ color: s.color }} />
                      <div
                        style={{
                          fontSize: 'var(--text-xl)',
                          fontWeight: 800,
                          color: 'var(--text-primary)',
                          marginTop: 'var(--space-1)',
                          fontFamily: "'Plus Jakarta Sans', sans-serif",
                        }}
                      >
                        {s.value}
                      </div>
                      <div style={{ fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)' }}>
                        {s.label}
                      </div>
                    </div>
                  ))}
                </div>

                {/* bar chart */}
                <div style={{ marginTop: 'var(--space-6)' }}>
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      marginBottom: 'var(--space-4)',
                    }}
                  >
                    <span style={{ fontSize: 'var(--text-sm)', fontWeight: 600, color: 'var(--text-primary)' }}>
                      Industry Skill Demand
                    </span>
                    <span className="badge badge-primary">
                      <TrendingUp size={12} /> Trending
                    </span>
                  </div>

                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'flex-end',
                      gap: 'var(--space-3)',
                      height: 160,
                    }}
                  >
                    {previewBars.map((b) => (
                      <div
                        key={b.label}
                        style={{
                          flex: 1,
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: 'center',
                          gap: 'var(--space-2)',
                          height: '100%',
                          justifyContent: 'flex-end',
                        }}
                      >
                        <div
                          style={{
                            width: '100%',
                            maxWidth: 44,
                            height: `${b.value}%`,
                            borderRadius: 'var(--radius-sm) var(--radius-sm) 0 0',
                            background: `linear-gradient(180deg, ${b.color}, ${b.color})`,
                            transition: 'height 0.8s cubic-bezier(0.4,0,0.2,1)',
                          }}
                        />
                        <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-tertiary)', fontWeight: 500 }}>
                          {b.label}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* match row */}
                <div
                  style={{
                    marginTop: 'var(--space-6)',
                    padding: 'var(--space-4)',
                    borderRadius: 'var(--radius-md)',
                    background: 'linear-gradient(135deg, var(--primary-50), var(--secondary-50))',
                    border: '1px solid var(--primary-100)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                  }}
                >
                  <div>
                    <div style={{ fontSize: 'var(--text-xs)', color: 'var(--text-secondary)', fontWeight: 600 }}>
                      Best Match
                    </div>
                    <div style={{ fontSize: 'var(--text-sm)', fontWeight: 700, color: 'var(--text-primary)', marginTop: 2 }}>
                      Backend Engineering Intern
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                    <div
                      style={{
                        width: 44,
                        height: 44,
                        borderRadius: '50%',
                        background:
                          'conic-gradient(var(--success-500) 0% 88%, var(--gray-100) 88% 100%)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <div
                        style={{
                          width: 34,
                          height: 34,
                          borderRadius: '50%',
                          background: '#fff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontSize: 'var(--text-xs)',
                          fontWeight: 800,
                          color: 'var(--success-600)',
                          fontFamily: "'Plus Jakarta Sans', sans-serif",
                        }}
                      >
                        88%
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* floating accent chip */}
              <div
                className="animate-fade-in-up delay-5"
                style={{
                  position: 'absolute',
                  top: -16,
                  right: -12,
                  background: '#fff',
                  borderRadius: 'var(--radius-lg)',
                  boxShadow: 'var(--shadow-xl)',
                  padding: 'var(--space-3) var(--space-4)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 'var(--space-2)',
                  border: '1px solid var(--border-light)',
                  zIndex: 2,
                }}
              >
                <div
                  style={{
                    width: 28,
                    height: 28,
                    borderRadius: 'var(--radius-sm)',
                    background: 'var(--success-50)',
                    color: 'var(--success-600)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  <Zap size={14} />
                </div>
                <div>
                  <div style={{ fontSize: 'var(--text-xs)', fontWeight: 700, color: 'var(--text-primary)' }}>
                    Skill gap closed
                  </div>
                  <div style={{ fontSize: 10, color: 'var(--text-tertiary)' }}>+1 skill this week</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* responsive: stack hero columns */}
        <style>{`
          @media (max-width: 980px) {
            #home > div > div { grid-template-columns: 1fr !important; gap: var(--space-12) !important; }
          }
        `}</style>
      </section>

      {/* =====================================================
          2. FEATURES
         ===================================================== */}
      <section id="features" style={{ ...sectionPadding, background: 'var(--bg-body)' }}>
        <div style={sectionInner}>
          <div className="text-center" style={{ maxWidth: 640, margin: '0 auto var(--space-12)' }}>
            <span className="animate-fade-in-up" style={eyebrowStyle}>
              <Sparkles size={14} /> Who It's For
            </span>
            <h2 className="animate-fade-in-up delay-1" style={sectionHeading}>
              One platform. <span className="gradient-text">Three audiences.</span>
            </h2>
            <p className="animate-fade-in-up delay-2" style={{ ...sectionSub, margin: 'var(--space-4) auto 0' }}>
              SkillBridge serves every side of the placement ecosystem — students,
              companies, and colleges — with a shared, intelligent skill graph.
            </p>
          </div>

          <div className="grid grid-3">
            {features.map((f, i) => (
              <div
                key={f.title}
                className={`card card-hover animate-fade-in-up delay-${i + 1}`}
                style={{ padding: 'var(--space-8)' }}
              >
                <div
                  style={iconBox(f.iconBg, f.iconColor)}
                  onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.08)')}
                  onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
                >
                  <f.icon size={26} />
                </div>
                <h3 style={{ fontSize: 'var(--text-xl)', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {f.title}
                </h3>
                <ul style={{ marginTop: 'var(--space-4)', display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                  {f.points.map((p) => (
                    <li key={p} style={{ display: 'flex', gap: 'var(--space-3)', alignItems: 'flex-start' }}>
                      <CheckCircle2
                        size={18}
                        style={{ color: 'var(--primary-600)', flexShrink: 0, marginTop: 2 }}
                      />
                      <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', lineHeight: 1.55 }}>
                        {p}
                      </span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* =====================================================
          3. HOW IT WORKS
         ===================================================== */}
      <section
        id="how-it-works"
        style={{
          ...sectionPadding,
          background:
            'linear-gradient(180deg, var(--bg-body) 0%, var(--primary-50) 100%)',
        }}
      >
        <div style={sectionInner}>
          <div className="text-center" style={{ maxWidth: 640, margin: '0 auto var(--space-12)' }}>
            <span className="animate-fade-in-up" style={eyebrowStyle}>
              <Zap size={14} /> How It Works
            </span>
            <h2 className="animate-fade-in-up delay-1" style={sectionHeading}>
              From profile to <span className="gradient-text">placement</span> in four steps.
            </h2>
            <p className="animate-fade-in-up delay-2" style={{ ...sectionSub, margin: 'var(--space-4) auto 0' }}>
              A guided flow that turns raw skills into matched, real-world
              opportunities — with continuous feedback from industry demand.
            </p>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(4, 1fr)',
              gap: 'var(--space-6)',
            }}
          >
            {steps.map((s, i) => (
              <div
                key={s.title}
                className={`card card-hover animate-fade-in-up delay-${i + 1}`}
                style={{ padding: 'var(--space-6)', position: 'relative' }}
              >
                {/* step number badge */}
                <div
                  style={{
                    position: 'absolute',
                    top: -14,
                    right: 'var(--space-6)',
                    width: 32,
                    height: 32,
                    borderRadius: '50%',
                    background: 'linear-gradient(135deg, var(--primary-600), var(--secondary-600))',
                    color: '#fff',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 'var(--text-sm)',
                    fontWeight: 800,
                    fontFamily: "'Plus Jakarta Sans', sans-serif",
                    boxShadow: 'var(--shadow-primary)',
                  }}
                >
                  {i + 1}
                </div>

                <div
                  style={{
                    ...iconBox('var(--primary-50)', 'var(--primary-700)'),
                    marginBottom: 'var(--space-4)',
                  }}
                >
                  <s.icon size={24} />
                </div>
                <h3 style={{ fontSize: 'var(--text-lg)', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {s.title}
                </h3>
                <p
                  style={{
                    fontSize: 'var(--text-sm)',
                    color: 'var(--text-secondary)',
                    marginTop: 'var(--space-3)',
                    lineHeight: 1.6,
                  }}
                >
                  {s.description}
                </p>

                {/* connector arrow (hidden on last) */}
                {i < steps.length - 1 && (
                  <div
                    aria-hidden
                    style={{
                      position: 'absolute',
                      top: '50%',
                      right: -28,
                      transform: 'translateY(-50%)',
                      color: 'var(--primary-300)',
                      zIndex: 2,
                    }}
                  >
                    <ArrowRight size={20} />
                  </div>
                )}
              </div>
            ))}
          </div>

          <style>{`
            @media (max-width: 1024px) {
              #how-it-works .grid { grid-template-columns: repeat(2, 1fr) !important; }
            }
            @media (max-width: 640px) {
              #how-it-works .grid { grid-template-columns: 1fr !important; }
            }
          `}</style>
        </div>
      </section>

      {/* =====================================================
          4. STATISTICS
         ===================================================== */}
      <section style={{ ...sectionPadding, background: 'var(--bg-body)' }}>
        <div style={sectionInner}>
          <div className="text-center" style={{ maxWidth: 640, margin: '0 auto var(--space-12)' }}>
            <span className="animate-fade-in-up" style={eyebrowStyle}>
              <TrendingUp size={14} /> By the Numbers
            </span>
            <h2 className="animate-fade-in-up delay-1" style={sectionHeading}>
              A growing <span className="gradient-text">skill network.</span>
            </h2>
            <p className="animate-fade-in-up delay-2" style={{ ...sectionSub, margin: 'var(--space-4) auto 0' }}>
              Real scale across students, employers, opportunities, and tracked
              skills — all on one intelligence layer.
            </p>
          </div>

          <div className="grid grid-4">
            {stats.map((s, i) => (
              <div
                key={s.key}
                className={`stat-card animate-fade-in-up delay-${i + 1}`}
                style={{ textAlign: 'center', padding: 'var(--space-8) var(--space-6)' }}
              >
                <div
                  className="stat-card-icon"
                  style={{
                    margin: '0 auto var(--space-4)',
                    width: 52,
                    height: 52,
                    background: `${s.color}15`,
                    color: s.color,
                  }}
                >
                  <s.icon size={24} />
                </div>
                <div className="stat-card-value" style={{ color: s.color }}>
                  {formatStat(statsData[s.key])}{s.key === 'students' ? '+' : ''}
                </div>
                <div className="stat-card-label" style={{ marginTop: 'var(--space-2)' }}>
                  {s.label}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* =====================================================
          5. CTA
         ===================================================== */}
      <section style={{ padding: 'var(--space-16) 0 var(--space-20)' }}>
        <div style={sectionInner}>
          <div
            className="animate-fade-in-up"
            style={{
              position: 'relative',
              overflow: 'hidden',
              borderRadius: 'var(--radius-2xl)',
              padding: 'var(--space-16) var(--space-12)',
              textAlign: 'center',
              background:
                'radial-gradient(800px 400px at 20% 0%, rgba(99,102,241,0.35), transparent 60%),' +
                'radial-gradient(700px 400px at 90% 100%, rgba(37,99,235,0.30), transparent 60%),' +
                'linear-gradient(135deg, var(--primary-700) 0%, var(--secondary-800) 100%)',
              boxShadow: 'var(--shadow-2xl)',
            }}
          >
            {/* decorative grid */}
            <div
              aria-hidden
              style={{
                position: 'absolute',
                inset: 0,
                backgroundImage:
                  'linear-gradient(rgba(255,255,255,0.06) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.06) 1px, transparent 1px)',
                backgroundSize: '40px 40px',
                maskImage: 'radial-gradient(ellipse 70% 70% at 50% 50%, #000 30%, transparent 100%)',
                WebkitMaskImage: 'radial-gradient(ellipse 70% 70% at 50% 50%, #000 30%, transparent 100%)',
                pointerEvents: 'none',
              }}
            />

            <div style={{ position: 'relative', zIndex: 1 }}>
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 'var(--space-2)',
                  padding: '6px 14px',
                  borderRadius: 'var(--radius-full)',
                  background: 'rgba(255,255,255,0.15)',
                  color: '#fff',
                  fontSize: 'var(--text-sm)',
                  fontWeight: 600,
                  border: '1px solid rgba(255,255,255,0.2)',
                }}
              >
                <Sparkles size={14} /> Start today
              </span>

              <h2
                style={{
                  fontSize: 'clamp(var(--text-3xl), 4vw, var(--text-5xl))',
                  fontWeight: 800,
                  color: '#fff',
                  marginTop: 'var(--space-5)',
                  lineHeight: 1.15,
                  letterSpacing: '-0.02em',
                }}
              >
                Ready to bridge the skill gap?
              </h2>
              <p
                style={{
                  fontSize: 'var(--text-lg)',
                  color: 'rgba(255,255,255,0.85)',
                  marginTop: 'var(--space-4)',
                  maxWidth: 560,
                  margin: 'var(--space-4) auto 0',
                  lineHeight: 1.6,
                }}
              >
                Join thousands of students, companies, and colleges already using
                SkillBridge to turn skills into careers.
              </p>

              <div
                style={{
                  display: 'flex',
                  gap: 'var(--space-4)',
                  marginTop: 'var(--space-8)',
                  justifyContent: 'center',
                  flexWrap: 'wrap',
                }}
              >
                <Button
                  variant="primary"
                  size="lg"
                  iconRight={ArrowRight}
                  onClick={() => navigate('/register')}
                  style={{
                    background: '#fff',
                    color: 'var(--primary-700)',
                    boxShadow: '0 10px 30px -8px rgba(0,0,0,0.3)',
                  }}
                >
                  Get Started Free
                </Button>
                <Button
                  size="lg"
                  onClick={() => navigate('/login')}
                  style={{
                    background: 'rgba(255,255,255,0.1)',
                    color: '#fff',
                    border: '1.5px solid rgba(255,255,255,0.3)',
                  }}
                >
                  Sign In
                </Button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </>
  );
}
