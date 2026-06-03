import React from 'react';

/**
 * Página 404 - No encontrada
 */
export const NotFoundPage: React.FC = () => {
  return (
    <div className="page not-found-page">
      <h1>404</h1>
      <p>Página no encontrada</p>
      <a href="/" className="btn btn-primary">
        Volver al inicio
      </a>
    </div>
  );
};

export default NotFoundPage;
