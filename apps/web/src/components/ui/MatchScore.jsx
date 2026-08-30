export default function MatchScore({ percentage, size = 120, label = 'Match' }) {
  const radius = (size - 12) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (percentage / 100) * circumference;

  const colorClass =
    percentage >= 80 ? 'var(--success-500)' :
    percentage >= 60 ? 'var(--warning-500)' :
    'var(--error-500)';

  return (
    <div className="match-ring" style={{ width: size, height: size }}>
      <svg width={size} height={size}>
        <defs>
          <linearGradient id="matchGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor={colorClass} />
            <stop offset="100%" stopColor="var(--primary-600)" />
          </linearGradient>
        </defs>
        <circle
          className="match-ring-bg"
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          strokeWidth="8"
        />
        <circle
          className="match-ring-fill"
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          strokeWidth="8"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
        />
      </svg>
      <div className="match-ring-text">
        <div className="match-ring-percent">{percentage}%</div>
        <div className="match-ring-label">{label}</div>
      </div>
    </div>
  );
}
