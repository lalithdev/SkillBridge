import { createContext, useContext, useState, useCallback } from 'react';
import { CheckCircle, XCircle, Info, X } from 'lucide-react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const addToast = useCallback((message, type = 'info') => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => removeToast(id), 3500);
  }, [removeToast]);

  const toast = {
    success: (msg) => addToast(msg, 'success'),
    error: (msg) => addToast(msg, 'error'),
    info: (msg) => addToast(msg, 'info'),
  };

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="toast-container">
        {toasts.map((t) => {
          const Icon = t.type === 'success' ? CheckCircle : t.type === 'error' ? XCircle : Info;
          const color = t.type === 'success' ? 'var(--success-500)' : t.type === 'error' ? 'var(--error-500)' : 'var(--primary-500)';
          return (
            <div key={t.id} className={`toast toast-${t.type}`}>
              <Icon size={20} style={{ color, flexShrink: 0 }} />
              <span style={{ flex: 1, fontSize: 'var(--text-sm)', fontWeight: 500 }}>{t.message}</span>
              <button onClick={() => removeToast(t.id)} style={{ color: 'var(--text-tertiary)', cursor: 'pointer' }}>
                <X size={16} />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
