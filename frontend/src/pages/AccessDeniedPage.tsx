import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context';
import { getDashboardRouteByRole } from '@constants';

type DenialReason = 'NOT_AUTHENTICATED' | 'INSUFFICIENT_PERMISSIONS';

export const AccessDeniedPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [reason, setReason] = useState<DenialReason>('NOT_AUTHENTICATED');

  useEffect(() => {
    const storedReason = localStorage.getItem('access_denial_reason') as DenialReason | null;
    if (storedReason) {
      setReason(storedReason);
      localStorage.removeItem('access_denial_reason');
    }
  }, []);

  const handleGoToLogin = () => {
    navigate('/login', { replace: true });
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  const handleGoToDashboard = () => {
    const targetRoute = getDashboardRouteByRole(user?.rol);
    navigate(targetRoute, { replace: true });
  };

  const isNotAuthenticated = reason === 'NOT_AUTHENTICATED';

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.iconWrapper}>
          <div style={styles.icon}>
            {isNotAuthenticated ? '🔓' : '🚫'}
          </div>
        </div>

        <h1 style={styles.title}>
          {isNotAuthenticated ? 'Sesión requerida' : 'Acceso denegado'}
        </h1>

        <p style={styles.message}>
          {isNotAuthenticated
            ? 'Tu sesión ha expirado o no estás autenticado. Por favor, inicia sesión nuevamente para continuar.'
            : `No tienes permisos para acceder a esta sección. Tu rol actual es: ${user?.rol || 'Usuario'}`}
        </p>

        {!isNotAuthenticated && user?.rol === 'STUDENT' && (
          <p style={styles.hint}>
            💡 Solo los administradores pueden acceder a esta área.
          </p>
        )}

        <div style={styles.buttonsContainer}>
          {isNotAuthenticated ? (
            <>
              <button style={{ ...styles.button, ...styles.buttonPrimary }} onClick={handleGoToLogin}>
                Ir a iniciar sesión
              </button>
            </>
          ) : (
            <>
              <button style={{ ...styles.button, ...styles.buttonPrimary }} onClick={handleGoToDashboard}>
                Ir a Mi Dashboard
              </button>
              <button style={{ ...styles.button, ...styles.buttonSecondary }} onClick={handleGoBack}>
                Volver atrás
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f5f5f5',
    padding: '20px',
  } as React.CSSProperties,
  card: {
    backgroundColor: 'white',
    borderRadius: '12px',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
    padding: '40px 30px',
    maxWidth: '500px',
    textAlign: 'center',
  } as React.CSSProperties,
  iconWrapper: {
    marginBottom: '20px',
  } as React.CSSProperties,
  icon: {
    fontSize: '60px',
    marginBottom: '10px',
  } as React.CSSProperties,
  title: {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1a1a1a',
    marginBottom: '12px',
  } as React.CSSProperties,
  message: {
    fontSize: '16px',
    color: '#666',
    lineHeight: '1.6',
    marginBottom: '16px',
  } as React.CSSProperties,
  hint: {
    fontSize: '14px',
    color: '#0066cc',
    backgroundColor: '#e6f0ff',
    padding: '12px',
    borderRadius: '8px',
    marginBottom: '24px',
  } as React.CSSProperties,
  buttonsContainer: {
    display: 'flex',
    gap: '12px',
    flexDirection: 'column' as const,
  } as React.CSSProperties,
  button: {
    padding: '12px 24px',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: '500',
    border: 'none',
    cursor: 'pointer',
    transition: 'all 0.3s ease',
  } as React.CSSProperties,
  buttonPrimary: {
    backgroundColor: '#0066cc',
    color: 'white',
  } as React.CSSProperties,
  buttonSecondary: {
    backgroundColor: '#f0f0f0',
    color: '#1a1a1a',
    border: '1px solid #ddd',
  } as React.CSSProperties,
};
