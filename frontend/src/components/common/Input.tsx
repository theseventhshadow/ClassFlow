import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

/**
 * Componente Input reutilizable
 */
export const Input: React.FC<InputProps> = ({
  label,
  error,
  helperText,
  className,
  ...props
}) => {
  return (
    <div className="input-group">
      {label && <label htmlFor={props.id}>{label}</label>}
      <input className={`input ${error ? 'input-error' : ''} ${className || ''}`} {...props} />
      {error && <span className="error-text">{error}</span>}
      {helperText && !error && <span className="helper-text">{helperText}</span>}
    </div>
  );
};

export default Input;
