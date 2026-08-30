import { useState, useEffect } from 'react';
import { Mail, Phone, Globe, MapPin, Edit3, Save, X } from 'lucide-react';
import { useCompanyProfile } from '@/hooks/useData';
import PageQueryState from '@/components/shared/PageQueryState';
import { useToast } from '@/components/ui/Toast';
import Card, { CardBody } from '@/components/ui/Card';
import Button from '@/components/ui/Button';
import Badge from '@/components/ui/Badge';
import Modal from '@/components/ui/Modal';
import Input from '@/components/ui/Input';

export default function CompanyProfile() {
  const toast = useToast();
  const { data: companyProfile, isLoading, isError, error, refetch } = useCompanyProfile();
  const [editOpen, setEditOpen] = useState(false);
  const [form, setForm] = useState({
    name: '', email: '', phone: '',
    website: '', industry: '', size: '',
    location: '', description: '',
  });

  useEffect(() => {
    if (!companyProfile) return;
    setForm({
      name: companyProfile.name, email: companyProfile.email, phone: companyProfile.phone,
      website: companyProfile.website, industry: companyProfile.industry, size: companyProfile.size,
      location: companyProfile.location, description: companyProfile.description,
    });
  }, [companyProfile]);

  const handleSave = () => { toast.success('Company profile updated!'); setEditOpen(false); };

  return (
    <PageQueryState isLoading={isLoading} isError={isError} error={error} onRetry={refetch}>
      <div>
        <div className="flex items-center justify-between mb-8" style={{ flexWrap: 'wrap', gap: 'var(--space-4)' }}>
          <div>
            <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800 }}>Company Profile</h1>
            <p style={{ color: 'var(--text-secondary)', marginTop: 'var(--space-2)' }}>Manage your company information</p>
          </div>
          <Button variant="primary" icon={Edit3} onClick={() => setEditOpen(true)}>Edit Profile</Button>
        </div>

        <Card className="animate-fade-in-up mb-6">
          <CardBody>
            <div className="flex items-start gap-4" style={{ flexWrap: 'wrap' }}>
              <div style={{ width: 80, height: 80, borderRadius: 'var(--radius-lg)', background: 'linear-gradient(135deg, var(--primary-100), var(--secondary-100))', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 'var(--text-3xl)', fontWeight: 800, color: 'var(--primary-700)' }}>{companyProfile?.logo}</div>
              <div style={{ flex: 1 }}>
                <h2 style={{ fontSize: 'var(--text-2xl)', fontWeight: 800 }}>{companyProfile?.name}</h2>
                <div className="flex items-center gap-3 mt-2 flex-wrap">
                  <Badge variant="primary">{companyProfile?.industry}</Badge>
                  <Badge variant="gray">{companyProfile?.size} employees</Badge>
                  <Badge variant="accent">Founded {companyProfile?.founded}</Badge>
                </div>
                <p className="text-sm text-secondary mt-4" style={{ lineHeight: 1.7, maxWidth: 600 }}>{companyProfile?.description}</p>
              </div>
            </div>
          </CardBody>
        </Card>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-6)' }}>
          <Card className="animate-fade-in-up delay-1">
            <CardBody>
              <h3 className="card-title mb-6">Contact Information</h3>
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-3"><Mail size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">EMAIL</div><div className="text-sm font-semibold">{form.email}</div></div></div>
                <div className="flex items-center gap-3"><Phone size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">PHONE</div><div className="text-sm font-semibold">{form.phone}</div></div></div>
                <div className="flex items-center gap-3"><Globe size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">WEBSITE</div><div className="text-sm font-semibold">{form.website}</div></div></div>
                <div className="flex items-center gap-3"><MapPin size={18} style={{ color: 'var(--text-tertiary)' }} /><div><div className="text-xs text-tertiary font-semibold">LOCATION</div><div className="text-sm font-semibold">{form.location}</div></div></div>
              </div>
            </CardBody>
          </Card>
          <Card className="animate-fade-in-up delay-2">
            <CardBody>
              <h3 className="card-title mb-6">Company Details</h3>
              <div className="flex flex-col gap-4">
                <div><div className="text-xs text-tertiary font-semibold">INDUSTRY</div><div className="text-sm font-semibold">{form.industry}</div></div>
                <div><div className="text-xs text-tertiary font-semibold">COMPANY SIZE</div><div className="text-sm font-semibold">{form.size} employees</div></div>
                <div><div className="text-xs text-tertiary font-semibold">FOUNDED</div><div className="text-sm font-semibold">{companyProfile?.founded}</div></div>
              </div>
            </CardBody>
          </Card>
        </div>

        <Modal open={editOpen} onClose={() => setEditOpen(false)} title="Edit Company Profile" size="lg"
          footer={<><Button variant="ghost" icon={X} onClick={() => setEditOpen(false)}>Cancel</Button><Button variant="primary" icon={Save} onClick={handleSave}>Save Changes</Button></>}>
          <Input label="Company Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <Input label="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
            <Input label="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            <Input label="Website" value={form.website} onChange={(e) => setForm({ ...form, website: e.target.value })} />
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
            <Input label="Industry" value={form.industry} onChange={(e) => setForm({ ...form, industry: e.target.value })} />
            <Input label="Company Size" value={form.size} onChange={(e) => setForm({ ...form, size: e.target.value })} />
          </div>
          <Input label="Location" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
          <Input label="Description" type="textarea" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        </Modal>

        <style>{`@media (max-width: 768px) { div[style*="grid-template-columns: 1fr 1fr"] { grid-template-columns: 1fr !important; } }`}</style>
      </div>
    </PageQueryState>
  );
}
