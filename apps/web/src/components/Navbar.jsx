import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X, GraduationCap } from 'lucide-react';
import Button from './ui/Button';

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const navLinks = [
    { label: 'Home', target: 'home' },
    { label: 'Features', target: 'features' },
    { label: 'How It Works', target: 'how-it-works' },
    { label: 'About', target: 'about' },
  ];

  const scrollTo = (id) => {
    setMobileOpen(false);
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <>
      <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
        <div className="navbar-inner">
          <Link to="/" className="navbar-logo">
            <span className="navbar-logo-icon">
              <GraduationCap size={20} />
            </span>
            SkillBridge
          </Link>

          <div className="navbar-links">
            {navLinks.map((link) => (
              <button
                key={link.label}
                className="navbar-link"
                onClick={() => scrollTo(link.target)}
              >
                {link.label}
              </button>
            ))}
          </div>

          <div className="navbar-actions">
            <Button variant="ghost" size="sm" onClick={() => navigate('/login')}>
              Login
            </Button>
            <Button variant="primary" size="sm" onClick={() => navigate('/register')}>
              Get Started
            </Button>
          </div>

          <button
            className="navbar-mobile-toggle btn-icon"
            onClick={() => setMobileOpen(!mobileOpen)}
          >
            {mobileOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </nav>

      {mobileOpen && (
        <div className="mobile-menu">
          {navLinks.map((link) => (
            <button
              key={link.label}
              className="mobile-menu-link"
              onClick={() => scrollTo(link.target)}
            >
              {link.label}
            </button>
          ))}
          <div className="flex gap-3 mt-4">
            <Button variant="outline" size="sm" fullWidth onClick={() => navigate('/login')}>
              Login
            </Button>
            <Button variant="primary" size="sm" fullWidth onClick={() => navigate('/register')}>
              Get Started
            </Button>
          </div>
        </div>
      )}
    </>
  );
}
