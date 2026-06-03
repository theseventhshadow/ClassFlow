import React from 'react';
import { Outlet } from 'react-router-dom';
import { useTheme } from '@context';

/**
 * Componente Layout principal
 */
export const Layout: React.FC = () => {
  const { theme } = useTheme();

  return (
    <div className={`layout layout-${theme}`}>
      <Header />
      <main className="main-content">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

/**
 * Componente Header
 */
const Header: React.FC = () => {
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo">ClassFlow</div>
        <button className="theme-toggle" onClick={toggleTheme}>
          {theme === 'light' ? '🌙' : '☀️'}
        </button>
      </div>
    </header>
  );
};

/**
 * Componente Footer
 */
const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <p>&copy; 2026 ClassFlow. Todos los derechos reservados.</p>
    </footer>
  );
};

export default Layout;
