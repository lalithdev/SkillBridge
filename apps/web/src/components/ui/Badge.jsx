export default function Badge({ children, variant = 'gray', className = '' }) {
  return <span className={`badge badge-${variant} ${className}`}>{children}</span>;
}
