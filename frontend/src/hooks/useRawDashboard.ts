import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '@context';
import { dashboardService, DashboardResponse } from '@services';

interface UseRawDashboardResult {
  dashboard: DashboardResponse | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

/**
 * Fetch del dashboard sin procesar, compartido por las páginas de estudiante,
 * docente y apoderado (antes cada una reimplementaba este mismo efecto a mano,
 * sin cancelar la petición si el componente se desmontaba o el usuario cambiaba).
 */
export function useRawDashboard(): UseRawDashboardResult {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  useEffect(() => {
    if (!user?.id) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    setLoading(true);
    setError(null);

    dashboardService
      .getDashboard(user.id)
      .then((response) => {
        if (!cancelled) setDashboard(response);
      })
      .catch((err) => {
        if (!cancelled) setError(err instanceof Error ? err.message : 'Error al cargar datos');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [user?.id, reloadToken]);

  const refetch = useCallback(() => setReloadToken((token) => token + 1), []);

  return { dashboard, loading, error, refetch };
}
