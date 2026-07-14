import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context';

/**
 * Cierra sesión y redirige a login. Delega la limpieza de sesión en AuthContext.logout()
 * (que ya borra user_token/user_data) en vez de vaciar localStorage/sessionStorage por
 * completo, para no arrastrar preferencias no relacionadas con la sesión (ej. el tema).
 */
export function useLogout(): () => void {
  const { logout } = useAuth();
  const navigate = useNavigate();

  return useCallback(() => {
    logout();
    navigate('/login', { replace: true });
  }, [logout, navigate]);
}
