export default function ProgressBar({ value, max = 100, variant = 'default', label, showValue = true, height = 8 }) {
  const pct = Math.min(100, (value / max) * 100);
  const variantClass = variant === 'success' ? 'success' : variant === 'warning' ? 'warning' : variant === 'error' ? 'error' : '';

  return (
    <div>
      {label && (
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-secondary">{label}</span>
          {showValue && <span className="text-sm font-bold text-primary">{Math.round(pct)}%</span>}
        </div>
      )}
      <div className="progress-bar" style={{ height }}>
        <div className={`progress-fill ${variantClass}`} style={{ width: `${pct}%` }} />
      </div>
      {!label && showValue && (
        <div className="text-xs font-semibold text-tertiary mt-2">{Math.round(pct)}%</div>
      )}
    </div>
  );
}
