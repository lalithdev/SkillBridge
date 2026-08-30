import { Link } from 'react-router-dom';
import { GraduationCap, Twitter, Linkedin, Github, Mail } from 'lucide-react';

export default function Footer() {
  return (
    <footer id="about" style={{ background: 'var(--gray-900)', color: 'var(--gray-300)', padding: 'var(--space-16) 0 var(--space-8)' }}>
      <div className="container">
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr', gap: 'var(--space-8)', marginBottom: 'var(--space-10)' }}>
          <div>
            <Link to="/" className="navbar-logo" style={{ color: '#fff', marginBottom: 'var(--space-4)' }}>
              <span className="navbar-logo-icon">
                <GraduationCap size={20} />
              </span>
              SkillBridge
            </Link>
            <p style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)', maxWidth: 300, lineHeight: 1.6, marginTop: 'var(--space-4)' }}>
              The academia-industry skill intelligence platform bridging the gap between what students learn and what employers need.
            </p>
            <div className="flex gap-3 mt-6">
              {[Twitter, Linkedin, Github, Mail].map((Icon, i) => (
                <a
                  key={i}
                  href="#"
                  onClick={(e) => e.preventDefault()}
                  style={{
                    width: 36, height: 36, borderRadius: 'var(--radius-md)',
                    background: 'var(--gray-800)', display: 'flex', alignItems: 'center', justifyContent: 'center',
                    color: 'var(--gray-400)', transition: 'all var(--transition-fast)',
                  }}
                  onMouseEnter={(e) => { e.currentTarget.style.background = 'var(--primary-600)'; e.currentTarget.style.color = '#fff'; }}
                  onMouseLeave={(e) => { e.currentTarget.style.background = 'var(--gray-800)'; e.currentTarget.style.color = 'var(--gray-400)'; }}
                >
                  <Icon size={16} />
                </a>
              ))}
            </div>
          </div>

          <div>
            <h4 style={{ color: '#fff', fontSize: 'var(--text-sm)', fontWeight: 700, marginBottom: 'var(--space-4)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Platform</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
              {['Home', 'Features', 'How It Works', 'About'].map((item) => (
                <a key={item} href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)', transition: 'color var(--transition-fast)' }}
                  onMouseEnter={(e) => e.currentTarget.style.color = '#fff'}
                  onMouseLeave={(e) => e.currentTarget.style.color = 'var(--gray-400)'}>
                  {item}
                </a>
              ))}
            </div>
          </div>

          <div>
            <h4 style={{ color: '#fff', fontSize: 'var(--text-sm)', fontWeight: 700, marginBottom: 'var(--space-4)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Resources</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
              {['About', 'Contact', 'Privacy', 'Terms'].map((item) => (
                <a key={item} href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)', transition: 'color var(--transition-fast)' }}
                  onMouseEnter={(e) => e.currentTarget.style.color = '#fff'}
                  onMouseLeave={(e) => e.currentTarget.style.color = 'var(--gray-400)'}>
                  {item}
                </a>
              ))}
            </div>
          </div>

          <div>
            <h4 style={{ color: '#fff', fontSize: 'var(--text-sm)', fontWeight: 700, marginBottom: 'var(--space-4)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Get Started</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
              <Link to="/login" style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)' }}>Login</Link>
              <Link to="/register" style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)' }}>Create Account</Link>
              <a href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)' }}>For Companies</a>
              <a href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-400)' }}>For Colleges</a>
            </div>
          </div>
        </div>

        <div style={{ borderTop: '1px solid var(--gray-800)', paddingTop: 'var(--space-6)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <p style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-500)' }}>
            © 2026 SkillBridge. All rights reserved.
          </p>
          <div className="flex gap-6">
            <a href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-500)' }}>Privacy Policy</a>
            <a href="#" onClick={(e) => e.preventDefault()} style={{ fontSize: 'var(--text-sm)', color: 'var(--gray-500)' }}>Terms of Service</a>
          </div>
        </div>
      </div>

      <style>{`
        @media (max-width: 768px) {
          footer > div > div:first-child {
            grid-template-columns: 1fr !important;
          }
        }
      `}</style>
    </footer>
  );
}
