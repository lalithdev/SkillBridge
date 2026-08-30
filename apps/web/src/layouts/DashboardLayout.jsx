import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Menu, GraduationCap, LogOut, ChevronRight } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';

export default function DashboardLayout({ navItems, roleLabel }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const userInitials = (user?.name || 'U')
    .split(' ')
    .map((n) => n[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => {
    if (path === location.pathname) return true;
    if (location.pathname.startsWith(path) && path !== '/dashboard') return true;
    return false;
  };

  const currentTitle = navItems.find((item) => isActive(item.path))?.label || 'Dashboard';

  return (
    <div className="dashboard-layout">
      {sidebarOpen && (
        <div className="sidebar-mobile-overlay" onClick={() => setSidebarOpen(false)} />
      )}

      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <span className="sidebar-logo-icon">
            <GraduationCap size={18} />
          </span>
          <span className="sidebar-logo">SkillBridge</span>
        </div>

        <nav className="sidebar-nav">
          <div className="sidebar-section-label">{roleLabel}</div>
          {navItems.map((item) => {
            const Icon = item.icon;
            if (item.action === 'logout') {
              return (
                <button
                  key={item.label}
                  className="sidebar-link logout"
                  onClick={handleLogout}
                >
                  <Icon size={18} />
                  {item.label}
                </button>
              );
            }
            return (
              <button
                key={item.label}
                className={`sidebar-link ${isActive(item.path) ? 'active' : ''}`}
                onClick={() => {
                  navigate(item.path);
                  setSidebarOpen(false);
                }}
              >
                <Icon size={18} />
                {item.label}
              </button>
            );
          })}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="sidebar-user-avatar">{userInitials}</div>
            <div className="sidebar-user-info">
              <div className="sidebar-user-name">{user?.name || 'User'}</div>
              <div className="sidebar-user-role">{roleLabel}</div>
            </div>
          </div>
        </div>
      </aside>

      <div className="dashboard-main">
        <header className="dashboard-topbar">
          <div className="flex items-center gap-4">
            <button
              className="btn-icon"
              onClick={() => setSidebarOpen(!sidebarOpen)}
              style={{ display: 'block' }}
            >
              <Menu size={22} />
            </button>
            <div>
              <h1 className="dashboard-topbar-title">{currentTitle}</h1>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <span className="badge badge-primary" style={{ display: 'none' }}></span>
            <div className="avatar avatar-sm">{userInitials}</div>
          </div>
        </header>

        <main className="dashboard-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
