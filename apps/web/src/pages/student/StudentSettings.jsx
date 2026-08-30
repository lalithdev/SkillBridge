import { useState } from 'react';
import { User, Mail, Phone, Bell, Shield, Save } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';

export default function StudentSettings() {
  const { user, logout } = useAuth();
  const toast = useToast();
  const [notifications, setNotifications] = useState({ email: true, push: false, matches: true });

  const handleSave = () => toast.success('Settings saved successfully!');

  return (
    <div>
      <div className="mb-8">
        <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Settings</h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your account and preferences</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }}>
        <Card className="animate-fade-in-up">
          <CardBody>
            <div className="flex items-center gap-2 mb-6"><User size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Account Information</h3></div>
            <Input label="Full Name" defaultValue={user?.name || ''} />
            <Input label="Email" type="email" defaultValue={user?.email || ''} />
            <Input label="Phone" defaultValue="+91 98765 43210" />
            <Button variant="primary" icon={Save} onClick={handleSave}>Save Changes</Button>
          </CardBody>
        </Card>

        <Card className="animate-fade-in-up delay-1">
          <CardBody>
            <div className="flex items-center gap-2 mb-6"><Bell size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Notifications</h3></div>
            {[
              { key: 'email', label: 'Email Notifications', desc: 'Receive updates via email' },
              { key: 'push', label: 'Push Notifications', desc: 'Get browser push alerts' },
              { key: 'matches', label: 'New Match Alerts', desc: 'Notify when new opportunities match' },
            ].map((n) => (
              <div key={n.key} className="flex items-center justify-between" style={{ padding: 'var(--space-4) 0', borderBottom: '1px solid var(--border-light)' }}>
                <div><div className="text-sm font-semibold">{n.label}</div><div className="text-xs text-tertiary">{n.desc}</div></div>
                <button onClick={() => setNotifications({ ...notifications, [n.key]: !notifications[n.key] })} style={{ width: 44, height: 24, borderRadius: 12, background: notifications[n.key] ? 'var(--primary-600)' : 'var(--gray-200)', position: 'relative', transition: 'all var(--transition-fast)' }}>
                  <div style={{ position: 'absolute', top: 2, left: notifications[n.key] ? 22 : 2, width: 20, height: 20, borderRadius: '50%', background: '#fff', transition: 'all var(--transition-fast)' }} />
                </button>
              </div>
            ))}
          </CardBody>
        </Card>

        <Card className="animate-fade-in-up delay-2">
          <CardBody>
            <div className="flex items-center gap-2 mb-6"><Shield size={18} style={{ color: 'var(--primary-600)' }} /><h3 className="card-title">Security</h3></div>
            <Input label="Current Password" type="password" placeholder="••••••••" />
            <Input label="New Password" type="password" placeholder="Enter new password" />
            <Button variant="outline" icon={Save} onClick={handleSave}>Update Password</Button>
          </CardBody>
        </Card>

        <Card className="animate-fade-in-up delay-3">
          <CardBody>
            <h3 className="card-title mb-4" style={{ color: 'var(--error-600)' }}>Danger Zone</h3>
            <p className="text-sm text-secondary mb-4">Once you log out, you'll need to sign in again to access your dashboard.</p>
            <Button variant="danger" onClick={() => { logout(); window.location.href = '/'; }}>Logout</Button>
          </CardBody>
        </Card>
      </div>

      <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
    </div>
  );
}
