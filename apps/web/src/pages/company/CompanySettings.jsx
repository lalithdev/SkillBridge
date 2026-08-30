import { useState } from 'react';
import { Building2, Bell, Shield, Save, LogOut } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';

export default function CompanySettings() {
  const { user, logout } = useAuth();
  const toast = useToast();
  const [notifications, setNotifications] = useState({ email: true, newApp: true, matches: false });

  return (
    <div>
      <div className="mb-8">
        <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Settings</h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your company account</p>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }}>
        <Card className="animate-fade-in-up"><CardBody>
          <div className="flex items-center gap-2 mb-6"><Building2 size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Account Information</h3></div>
          <Input label="Company Name" defaultValue={user?.name || 'TechVista Solutions'} />
          <Input label="Email" type="email" defaultValue={user?.email || ''} />
          <Button variant="primary" icon={Save} onClick={() => toast.success('Settings saved!')}>Save Changes</Button>
        </CardBody></Card>
        <Card className="animate-fade-in-up delay-1"><CardBody>
          <div className="flex items-center gap-2 mb-6"><Bell size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Notifications</h3></div>
          {[{ key: 'email', label: 'Email Notifications', desc: 'Receive updates via email' }, { key: 'newApp', label: 'New Application Alerts', desc: 'Notify when candidates apply' }, { key: 'matches', label: 'New Match Alerts', desc: 'Notify when new matching candidates are found' }].map((n) => (
            <div key={n.key} className="flex items-center justify-between" style={{ padding: 'var(--space-4) 0', borderBottom: '1px solid var(--border-light)' }}>
              <div><div className="text-sm font-semibold">{n.label}</div><div className="text-xs text-tertiary">{n.desc}</div></div>
              <button onClick={() => setNotifications({ ...notifications, [n.key]: !notifications[n.key] })} style={{ width: 44, height: 24, borderRadius: 12, background: notifications[n.key] ? 'var(--primary-600)' : 'var(--gray-200)', position: 'relative', transition: 'all var(--transition-fast)' }}>
                <div style={{ position: 'absolute', top: 2, left: notifications[n.key] ? 22 : 2, width: 20, height: 20, borderRadius: '50%', background: '#fff', transition: 'all var(--transition-fast)' }} />
              </button>
            </div>
          ))}
        </CardBody></Card>
        <Card className="animate-fade-in-up delay-2"><CardBody>
          <div className="flex items-center gap-2 mb-6"><Shield size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Security</h3></div>
          <Input label="Current Password" type="password" placeholder="••••••••" />
          <Input label="New Password" type="password" placeholder="Enter new password" />
          <Button variant="outline" icon={Save} onClick={() => toast.success('Password updated!')}>Update Password</Button>
        </CardBody></Card>
        <Card className="animate-fade-in-up delay-3"><CardBody>
          <h3 className="card-title mb-4" style={{ color: 'var(--error-600)' }}>Danger Zone</h3>
          <p className="text-sm text-secondary mb-4">Log out of your company account.</p>
          <Button variant="danger" icon={LogOut} onClick={() => { logout(); window.location.href = '/'; }}>Logout</Button>
        </CardBody></Card>
      </div>
      <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
    </div>
  );
}
