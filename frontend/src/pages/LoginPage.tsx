import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context';
import './LoginPage.css';

export const LoginPage: React.FC = () => {
  const { login, isLoading, error } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const loggedUser = await login(email, password);
      if (loggedUser.rol === 'ADMINISTRATOR') {
        navigate('/dashboard/admin', { replace: true });
      } else if (loggedUser.rol === 'TEACHER') {
        navigate('/dashboard/teacher', { replace: true });
      } else if (loggedUser.rol === 'STUDENT') {
        navigate('/dashboard/student', { replace: true });
      } else if (loggedUser.rol === 'GUARDIAN') {
        navigate('/dashboard/guardian', { replace: true });
      } else {
        navigate('/dashboard', { replace: true });
      }
    } catch {
      // el error ya queda en el contexto
    }
  };

  return (
    <div className="login-page">
      <div className="login-left">
        <div className="login-left-content">
          <p className="login-platform-label">PLATAFORMA ESCOLAR</p>
          <h1 className="login-brand-name">ClassFlow</h1>
          <p className="login-brand-desc">
            Gestión educativa moderna para estudiantes,<br />
            apoderados, docentes y administradores.
          </p>
        </div>
      </div>

      <div className="login-right">
        <div className="login-form-box">
          <h2 className="login-welcome">Bienvenido</h2>
          <p className="login-welcome-sub">Ingresa tus credenciales para continuar</p>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="login-field">
              <label htmlFor="email">Usuario</label>
              <div className="login-input-wrapper">
                <span className="login-input-icon">👤</span>
                <input
                  id="email"
                  type="text"
                  placeholder="usuario@classflow.cl"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="login-input"
                  required
                />
              </div>
            </div>

            <div className="login-field">
              <label htmlFor="password">Contraseña</label>
              <div className="login-input-wrapper">
                <span className="login-input-icon">🔒</span>
                <input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="login-input"
                  required
                />
              </div>
            </div>

            {error && <p className="login-error">{error}</p>}

            <button type="submit" className="login-submit-btn" disabled={isLoading}>
              {isLoading ? 'Ingresando...' : 'Iniciar sesión'}
            </button>
          </form>

          <div className="login-recover">
            <span className="login-recover-divider">¿Problemas para ingresar?</span>
            <a href="#" className="login-recover-link">Recuperar contraseña</a>
          </div>
        </div>
      </div>
    </div>
  );
};
