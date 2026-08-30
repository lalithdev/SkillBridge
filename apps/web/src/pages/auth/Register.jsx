import { useState, useMemo } from 'react';
import { Link, useNavigate, Navigate } from 'react-router-dom';
import { Mail, Lock, User, Building2, GraduationCap, Eye, EyeOff, Check, ArrowRight } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { useToast } from '@/components/ui/Toast';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';
import { getDashboardPath } from '@/utils/auth';

export default function Register() {
  const navigate = useNavigate();
  const { register, user } = useAuth();
  const toast = useToast();
  const [role, setRole] = useState('student');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [form, setForm] = useState({
    name: '', email: '', password: '', confirmPassword: '',
    phone: '', college: '', company: '', website: '', graduationYear: '',
  });

  const roles = [
    { value: 'student', label: 'Student', icon: GraduationCap },
    { value: 'company', label: 'Company', icon: Building2 },
    { value: 'college', label: 'College', icon: GraduationCap },
  ];

  const passwordStrength = useMemo(() => {
    const p = form.password;
    if (!p) return 0;
    let s = 0;
    if (p.length >= 8) s++;
    if (p.length >= 10) s++;
    if (/[A-Z]/.test(p)) s++;
    if (/[0-9]/.test(p)) s++;
    if (/[^A-Za-z0-9]/.test(p)) s++;
    return Math.min(s, 4);
  }, [form.password]);

  const strengthLabels = ['Too weak', 'Weak', 'Fair', 'Good', 'Strong'];
  const strengthColors = ['var(--error-500)', 'var(--error-500)', 'var(--warning-500)', 'var(--primary-500)', 'var(--success-500)'];

  const validate = () => {
    const e = {};
    if (!form.name) e.name = 'Name is required';
    if (!form.email) e.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Enter a valid email';
    if (!form.password) e.password = 'Password is required';
    else if (form.password.length < 8) e.password = 'Password must be at least 8 characters';
    if (form.password !== form.confirmPassword) e.confirmPassword = 'Passwords do not match';
    if (role === 'student' && !form.college) e.college = 'College is required';
    if (role === 'company' && !form.company) e.company = 'Company name is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  if (user) {
    return <Navigate to={getDashboardPath(user.role)} replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const registeredUser = await register({
        name: form.name,
        email: form.email,
        password: form.password,
        role,
        phone: form.phone,
        college: form.college,
        company: form.company,
        website: form.website,
        graduationYear: form.graduationYear,
      });
      toast.success('Account created! Welcome to SkillBridge.');
      navigate(getDashboardPath(registeredUser.role || role), { replace: true });
    } catch (error) {
      setErrors({ form: error.message || 'Unable to create your account. Please try again.' });
      toast.error(error.message || 'Unable to create your account. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const setField = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  return (
    <div style={{ minHeight: '100vh', display: 'grid', gridTemplateColumns: '1fr 1fr' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 'var(--space-8)', background: 'var(--bg-body)' }}>
        <div style={{ width: '100%', maxWidth: 440 }}>
          <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', marginBottom: 'var(--space-8)', fontSize: 'var(--text-xl)', fontWeight: 800, fontFamily: "'Plus Jakarta Sans', sans-serif", color: 'var(--text-primary)' }}>
            <span className="navbar-logo-icon"><GraduationCap size={20} /></span>
            SkillBridge
          </Link>

          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800, marginBottom: 'var(--space-2)' }}>Create your account</h1>
          <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', marginBottom: 'var(--space-6)' }}>
            Join SkillBridge and bridge your skills to your career.
          </p>

          {/* Role selector */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 'var(--space-3)', marginBottom: 'var(--space-6)' }}>
            {roles.map((r) => (
              <button
                key={r.value} onClick={() => setRole(r.value)}
                style={{
                  padding: 'var(--space-4)', borderRadius: 'var(--radius-md)', border: `2px solid ${role === r.value ? 'var(--primary-600)' : 'var(--border-default)'}`,
                  background: role === r.value ? 'var(--primary-50)' : '#fff', transition: 'all var(--transition-fast)', textAlign: 'center',
                }}
              >
                <r.icon size={22} style={{ color: role === r.value ? 'var(--primary-600)' : 'var(--text-tertiary)', margin: '0 auto var(--space-2)' }} />
                <div style={{ fontSize: 'var(--text-sm)', fontWeight: 600, color: role === r.value ? 'var(--primary-700)' : 'var(--text-secondary)' }}>{r.label}</div>
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit}>
            {errors.form && (
              <div style={{ marginBottom: 'var(--space-4)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)', background: 'rgba(239,68,68,0.08)', border: '1px solid rgba(239,68,68,0.2)', color: 'var(--error-600)', fontSize: 'var(--text-sm)' }}>
                {errors.form}
              </div>
            )}

            <Input label="Full Name" name="name" icon={User} placeholder="Enter your name" required value={form.name} onChange={setField('name')} error={errors.name} />

            <Input label="Email Address" name="email" type="email" icon={Mail} placeholder="you@example.com" required value={form.email} onChange={setField('email')} error={errors.email} />

            {role === 'student' && (
              <>
                <Input label="Phone Number" name="phone" icon={User} placeholder="+91 98765 43210" value={form.phone} onChange={setField('phone')} />
                <Input label="College Name" name="college" placeholder="Your college name" required value={form.college} onChange={setField('college')} error={errors.college} />
                <Input label="Graduation Year" name="graduationYear" type="number" placeholder="2025" value={form.graduationYear} onChange={setField('graduationYear')} />
              </>
            )}

            {role === 'company' && (
              <>
                <Input label="Company Name" name="company" placeholder="Company name" required value={form.company} onChange={setField('company')} error={errors.company} />
                <Input label="Website" name="website" placeholder="www.company.com" value={form.website} onChange={setField('website')} />
              </>
            )}

            {role === 'college' && (
              <>
                <Input label="College Name" name="college" placeholder="College name" required value={form.college} onChange={setField('college')} error={errors.college} />
                <Input label="Website" name="website" placeholder="www.college.edu" value={form.website} onChange={setField('website')} />
              </>
            )}

            <div style={{ position: 'relative' }}>
              <Input label="Password" name="password" type={showPassword ? 'text' : 'password'} icon={Lock} placeholder="Create a password" required value={form.password} onChange={setField('password')} error={errors.password} />
              <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: 12, top: 38, color: 'var(--text-tertiary)' }}>
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            {form.password && (
              <div style={{ marginBottom: 'var(--space-5)' }}>
                <div style={{ display: 'flex', gap: 4, marginBottom: 4 }}>
                  {[0, 1, 2, 3].map((i) => (
                    <div key={i} style={{ flex: 1, height: 4, borderRadius: 2, background: i < passwordStrength ? strengthColors[passwordStrength] : 'var(--gray-200)', transition: 'all var(--transition-fast)' }} />
                  ))}
                </div>
                <span style={{ fontSize: 'var(--text-xs)', color: strengthColors[passwordStrength], fontWeight: 600 }}>
                  {strengthLabels[passwordStrength]}
                </span>
              </div>
            )}

            <Input label="Confirm Password" name="confirmPassword" type="password" icon={Lock} placeholder="Re-enter password" required value={form.confirmPassword} onChange={setField('confirmPassword')} error={errors.confirmPassword} />

            <Button type="submit" fullWidth size="lg" loading={loading} iconRight={ArrowRight}>
              Create Account
            </Button>
          </form>

          <p style={{ textAlign: 'center', marginTop: 'var(--space-6)', fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--primary-600)', fontWeight: 600 }}>Sign in</Link>
          </p>
        </div>
      </div>

      <div style={{
        background: 'radial-gradient(800px 600px at 70% 20%, rgba(99,102,241,0.3), transparent 60%), linear-gradient(135deg, var(--primary-700), var(--secondary-800))',
        display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 'var(--space-12)', position: 'relative', overflow: 'hidden',
      }}>
        <div aria-hidden style={{ position: 'absolute', inset: 0, backgroundImage: 'linear-gradient(rgba(255,255,255,0.06) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '40px 40px' }} />
        <div style={{ position: 'relative', zIndex: 1, color: '#fff', maxWidth: 420 }}>
          <h2 style={{ fontSize: 'var(--text-4xl)', fontWeight: 800, color: '#fff', lineHeight: 1.15, marginBottom: 'var(--space-5)' }}>
            Start your journey today.
          </h2>
          <p style={{ fontSize: 'var(--text-lg)', color: 'rgba(255,255,255,0.85)', lineHeight: 1.6, marginBottom: 'var(--space-8)' }}>
            Create a free account and get access to skill matching, gap analysis, and career opportunities.
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
            {['Free for students', 'Reach matched candidates', 'Analytics for colleges'].map((item) => (
              <div key={item} style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                <div style={{ width: 28, height: 28, borderRadius: '50%', background: 'rgba(255,255,255,0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Check size={14} />
                </div>
                <span style={{ fontSize: 'var(--text-base)', fontWeight: 500 }}>{item}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <style>{`@media (max-width: 900px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } div[style*="linear-gradient(135deg, var(--primary-700)"] { display: none !important; } }`}</style>
    </div>
  );
}
