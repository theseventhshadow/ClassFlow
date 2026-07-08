import React, { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { authService } from '@services';
import { getErrorMessage } from '@utils';
import './LoginPage.css';

export const ResetPasswordPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') ?? '';

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (newPassword !== confirmPassword) {
      setError('Ingresa una contraseña válida');
      return;
    }

    setIsLoading(true);
    try {
      await authService.resetPassword(token, newPassword);
      setSuccess(true);
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudo restablecer la contraseña. Intenta nuevamente.'));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-page">
      {success && (
        <div className="toast-success" role="status">Tu contraseña ha sido modificada</div>
      )}

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
          {!success && (
            <>
              <h2 className="login-welcome">Restablecer contraseña</h2>
              <p className="login-welcome-sub">Ingresa tu nueva contraseña</p>
            </>
          )}

          {success && (
            <img
              src="/assets/images/reset-success.svg"
              alt=""
              className="reset-success-illustration"
            />
          )}

          {success ? null : !token ? (
            <p className="login-error">
              El enlace no es válido. Solicita uno nuevo desde la pantalla de recuperación.
            </p>
          ) : (
            <form onSubmit={handleSubmit} className="login-form">
              <div className="login-field">
                <label htmlFor="newPassword">Nueva contraseña</label>
                <div className="login-input-wrapper">
                  <span className="login-input-icon">🔒</span>
                  <input
                    id="newPassword"
                    type="password"
                    placeholder="••••••••"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="login-input"
                    minLength={8}
                    required
                  />
                </div>
              </div>

              <div className="login-field">
                <label htmlFor="confirmPassword">Confirmar contraseña</label>
                <div className="login-input-wrapper">
                  <span className="login-input-icon">🔒</span>
                  <input
                    id="confirmPassword"
                    type="password"
                    placeholder="••••••••"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="login-input"
                    minLength={8}
                    required
                  />
                </div>
              </div>

              {error && <p className="login-error">{error}</p>}

              <button type="submit" className="login-submit-btn" disabled={isLoading}>
                {isLoading ? 'Guardando...' : 'Restablecer contraseña'}
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
