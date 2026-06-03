import React from 'react';

interface ErrorProps {
  message: string;
  onRetry?: () => void;
}

/**
 * Componente de error
 */
export const Error: React.FC<ErrorProps> = ({ message, onRetry }) => {
  return (
    <div className="error-container">
      <p className="error-message">{message}</p>
      {onRetry && (
        <button className="btn btn-primary" onClick={onRetry}>
          Reintentar
        </button>
      )}
    </div>
  );
};

export default Error;
