import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  children: React.ReactNode;
}

/**
 * Componente Button reutilizable
 */
export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  isLoading = false,
  children,
  disabled,
  ...props
}) => {
  return (
    <button
      className={`btn btn-${variant} btn-${size}`}
      disabled={isLoading || disabled}
      {...props}
    >
      {isLoading ? 'Cargando...' : children}
    </button>
  );
};

export default Button;
