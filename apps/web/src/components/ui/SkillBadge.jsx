export default function SkillBadge({ skill, variant = 'default', onRemove, removable = false }) {
  const variantClass =
    variant === 'matched' ? 'matched' : variant === 'missing' ? 'missing' : '';
  return (
    <span
      className={`skill-chip ${variantClass} ${removable ? 'removable' : ''}`}
      onClick={removable ? () => onRemove?.(skill) : undefined}
    >
      {skill}
      {removable && (
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      )}
    </span>
  );
}
