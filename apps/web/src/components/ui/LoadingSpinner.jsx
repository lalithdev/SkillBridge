export default function LoadingSpinner({ size = 'md', label }) {
  return (
    <div className="spinner-container">
      <div className={`spinner ${size === 'lg' ? 'spinner-lg' : ''}`} />
      {label && <p>{label}</p>}
    </div>
  );
}
