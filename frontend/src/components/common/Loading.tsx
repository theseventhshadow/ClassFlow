import React from 'react';

interface LoadingProps {
  size?: 'sm' | 'md' | 'lg';
  message?: string;
}

/**
 * Componente de carga
 */
export const Loading: React.FC<LoadingProps> = ({ size = 'md', message = 'Cargando...' }) => {
  return (
    <div className="loading-container">
      <div className={`spinner spinner-${size}`}></div>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Loading;
