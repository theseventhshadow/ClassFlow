import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '@services';
import { getErrorMessage } from '@utils';
import './LoginPage.css';

export const ForgotPasswordPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sent, setSent] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    try {
      await authService.forgotPassword(email);
      setSent(true);
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudo procesar la solicitud. Intenta nuevamente.'));
    } finally {
      setIsLoading(false);
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
          <h2 className="login-welcome">Recuperar contraseña</h2>
          <p className="login-welcome-sub">
            Ingresa tu correo y te enviaremos un enlace para restablecerla
          </p>

          {sent ? (
            <p className="login-recover-sent">
              Si el correo está registrado, recibirás un enlace de recuperación en tu bandeja de entrada.
            </p>
          ) : (
            <form onSubmit={handleSubmit} className="login-form">
              <div className="login-field">
                <label htmlFor="email">Correo</label>
                <div className="login-input-wrapper">
                  <span className="login-input-icon">👤</span>
                  <input
                    id="email"
                    type="email"
                    placeholder="usuario@classflow.cl"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="login-input"
                    required
                  />
                </div>
              </div>

              {error && <p className="login-error">{error}</p>}

              <button type="submit" className="login-submit-btn" disabled={isLoading}>
                {isLoading ? 'Enviando...' : 'Enviar enlace de recuperación'}
              </button>
            </form>
          )}

          <div className="login-recover">
            <Link to="/login" className="login-recover-link">Volver a iniciar sesión</Link>
          </div>
        </div>
      </div>
    </div>
  );
};
