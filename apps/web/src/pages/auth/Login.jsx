import { useState } from 'react';
import { Link, useNavigate, useLocation, Navigate } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, GraduationCap, ArrowRight, AlertCircle } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { useToast } from '@/components/ui/Toast';
import Button from '@/components/ui/Button';
import Input from '@/components/ui/Input';
import { getDashboardPath, getDisplayName } from '@/utils/auth';

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, user, isLoading: authLoading } = useAuth();
  const toast = useToast();
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [form, setForm] = useState({ email: '', password: '', remember: false });

  if (authLoading) return null;

  if (user) {
    const redirectPath = new URLSearchParams(location.search).get('redirect') || getDashboardPath(user.role);
    return <Navigate to={redirectPath} replace />;
  }

  const validate = () => {
    const e = {};
    if (!form.email) e.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Enter a valid email';
    if (!form.password) e.password = 'Password is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const authenticatedUser = await login({ email: form.email, password: form.password });
      const redirectTarget = new URLSearchParams(location.search).get('redirect') || getDashboardPath(authenticatedUser.role);
      toast.success(`Welcome back, ${getDisplayName(authenticatedUser)}!`);
      navigate(redirectTarget, { replace: true });
    } catch (error) {
      setErrors({ form: error.message || 'Unable to sign in. Please try again.' });
      toast.error(error.message || 'Unable to sign in. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const expired = new URLSearchParams(location.search).get('expired') === 'true';

  return (
    <div style={{ minHeight: '100vh', display: 'grid', gridTemplateColumns: '1fr 1fr' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 'var(--space-8)', background: 'var(--bg-body)' }}>
        <div style={{ width: '100%', maxWidth: 400 }}>
          <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', marginBottom: 'var(--space-10)', fontSize: 'var(--text-xl)', fontWeight: 800, fontFamily: "'Plus Jakarta Sans', sans-serif", color: 'var(--text-primary)' }}>
            <span className="navbar-logo-icon"><GraduationCap size={20} /></span>
            SkillBridge
          </Link>

          <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800, marginBottom: 'var(--space-2)' }}>Welcome back</h1>
          <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', marginBottom: 'var(--space-8)' }}>
            Sign in to access your dashboard and opportunities.
          </p>

          {expired && (
            <div style={{ marginBottom: 'var(--space-4)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)', background: 'rgba(245,158,11,0.08)', border: '1px solid rgba(245,158,11,0.2)', color: 'var(--warning-700)', fontSize: 'var(--text-sm)' }}>
              Your session has expired. Please sign in again.
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {errors.form && (
              <div style={{ marginBottom: 'var(--space-4)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)', background: 'rgba(239,68,68,0.08)', border: '1px solid rgba(239,68,68,0.2)', color: 'var(--error-600)', fontSize: 'var(--text-sm)' }}>
                {errors.form}
              </div>
            )}

            <Input
              label="Email Address" name="email" type="email" icon={Mail}
              placeholder="you@example.com" required
              value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })}
              error={errors.email}
            />
            <div style={{ position: 'relative' }}>
              <Input
                label="Password" name="password" type={showPassword ? 'text' : 'password'} icon={Lock}
                placeholder="Enter your password" required
                value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })}
                error={errors.password}
              />
              <button
                type="button" onClick={() => setShowPassword(!showPassword)}
                style={{ position: 'absolute', right: 12, top: 38, color: 'var(--text-tertiary)' }}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--space-6)' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <input type="checkbox" checked={form.remember} onChange={(e) => setForm({ ...form, remember: e.target.checked })} style={{ accentColor: 'var(--primary-600)' }} />
                Remember me
              </label>
              <button type="button" onClick={() => toast.info('Password reset will be available after backend integration.')} style={{ fontSize: 'var(--text-sm)', color: 'var(--primary-600)', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer' }}>
                Forgot password?
              </button>
            </div>

            <Button type="submit" fullWidth size="lg" loading={loading} iconRight={ArrowRight}>
              Sign In
            </Button>
          </form>

          <p style={{ textAlign: 'center', marginTop: 'var(--space-6)', fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>
            Don&apos;t have an account?{' '}
            <Link to="/register" style={{ color: 'var(--primary-600)', fontWeight: 600 }}>Create account</Link>
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
            Bridge your skills to your career.
          </h2>
          <p style={{ fontSize: 'var(--text-lg)', color: 'rgba(255,255,255,0.85)', lineHeight: 1.6, marginBottom: 'var(--space-8)' }}>
            Join students, companies, and colleges using SkillBridge to connect talent with opportunity.
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
            {['Smart skill matching', 'Real-time skill gap analysis', 'Industry demand insights'].map((item) => (
              <div key={item} style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                <div style={{ width: 32, height: 32, borderRadius: 'var(--radius-sm)', background: 'rgba(255,255,255,0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <AlertCircle size={16} />
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
