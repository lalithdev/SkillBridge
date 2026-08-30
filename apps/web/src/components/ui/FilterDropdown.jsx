import { useState, useRef, useEffect } from 'react';
import { ChevronDown, Check } from 'lucide-react';

export default function FilterDropdown({ label, options, value, onChange, multi = false }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const isActive = multi ? value?.length > 0 : !!value;

  const handleSelect = (opt) => {
    if (multi) {
      const val = opt.value || opt;
      const current = value || [];
      if (current.includes(val)) {
        onChange(current.filter((v) => v !== val));
      } else {
        onChange([...current, val]);
      }
    } else {
      onChange(opt.value || opt);
      setOpen(false);
    }
  };

  const isSelected = (opt) => {
    const val = opt.value || opt;
    return multi ? (value || []).includes(val) : value === val;
  };

  return (
    <div className="filter-dropdown" ref={ref}>
      <button
        className={`filter-trigger ${isActive ? 'active' : ''}`}
        onClick={() => setOpen(!open)}
        type="button"
      >
        {label}
        <ChevronDown size={14} style={{ transform: open ? 'rotate(180deg)' : 'none', transition: 'var(--transition-fast)' }} />
      </button>
      {open && (
        <div className="filter-menu">
          {options.map((opt) => (
            <div
              key={opt.value || opt}
              className={`filter-option ${isSelected(opt) ? 'selected' : ''}`}
              onClick={() => handleSelect(opt)}
            >
              {multi && (
                <span style={{ width: 16, display: 'flex' }}>
                  {isSelected(opt) && <Check size={14} />}
                </span>
              )}
              {opt.label || opt}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
