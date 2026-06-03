import React from 'react';

/**
 * Página de inicio
 */
export const HomePage: React.FC = () => {
  return (
    <div className="page home-page">
      <h1>Bienvenido a ClassFlow</h1>
      <p>Estructura profesional y escalable para tu aplicación React.</p>
      <section className="features">
        <h2>Características</h2>
        <ul>
          <li>✅ Componentes modulares reutilizables</li>
          <li>✅ Servicios centralizados de API</li>
          <li>✅ Gestión de estado con Context API</li>
          <li>✅ Hooks personalizados</li>
          <li>✅ TypeScript para seguridad de tipos</li>
          <li>✅ Configuración de linting y formateo</li>
        </ul>
      </section>
    </div>
  );
};

export default HomePage;
