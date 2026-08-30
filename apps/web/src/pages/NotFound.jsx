import { Link } from 'react-router-dom';
import { FileQuestion } from 'lucide-react';
import Button from '@/components/ui/Button';

export default function NotFound() {
  return (
    <div style={{ minHeight: '100vh', display: 'grid', placeItems: 'center', background: 'var(--bg-body)', padding: 'var(--space-8)' }}>
      <div style={{ maxWidth: 520, width: '100%', background: '#fff', border: '1px solid var(--border-default)', borderRadius: 'var(--radius-xl)', boxShadow: 'var(--shadow-md)', padding: 'var(--space-8)', textAlign: 'center' }}>
        <div style={{ display: 'inline-flex', width: 64, height: 64, borderRadius: '50%', background: 'var(--primary-50)', alignItems: 'center', justifyContent: 'center', marginBottom: 'var(--space-5)' }}>
          <FileQuestion size={28} color="var(--primary-600)" />
        </div>
        <h1 style={{ fontSize: 'var(--text-3xl)', fontWeight: 800, marginBottom: 'var(--space-3)' }}>Page not found</h1>
        <p style={{ fontSize: 'var(--text-base)', color: 'var(--text-secondary)', marginBottom: 'var(--space-6)' }}>
          The page you are looking for does not exist or has been moved.
        </p>
        <Link to="/">
          <Button>Go home</Button>
        </Link>
      </div>
    </div>
  );
}
