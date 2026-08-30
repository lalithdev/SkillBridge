import { AlertCircle } from 'lucide-react';

export default function ErrorMessage({ message, onRetry }) {
  return (
    <div className="error-message">
      <AlertCircle size={18} />
      <span style={{ flex: 1 }}>{message}</span>
      {onRetry && (
        <button onClick={onRetry} className="btn btn-sm btn-ghost" style={{ color: 'var(--error-700)' }}>
          Retry
        </button>
      )}
    </div>
  );
}
