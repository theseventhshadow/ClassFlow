import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '@context';
import { dashboardService } from '@services';
import { buildAlerts, buildAttendance, buildActivity, buildStats, type DashboardViewModel } from '../domain/admin-dashboard';
import { buildDashboardUsers, type DashboardUserRow } from '../application/admin-dashboard';

interface DashboardData extends DashboardViewModel {
  users: DashboardUserRow[];
}

interface UseDashboardDataResult extends DashboardData {
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useDashboardData(): UseDashboardDataResult {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<DashboardData>({
    stats: [],
    users: [],
    courseAttendance: [],
    activity: [],
    alerts: [],
  });

  const fetchAll = useCallback(async () => {
    if (!user?.id) {
      setError('No se encontró una sesión activa.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await dashboardService.getDashboard(user.id);
      const users = await buildDashboardUsers(response, user.id);

      setData({
        stats: buildStats(response),
        users,
        courseAttendance: buildAttendance(response),
        activity: buildActivity(response),
        alerts: buildAlerts(response),
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los datos del panel');
      setData({
        stats: [],
        users: [],
        courseAttendance: [],
        activity: [],
        alerts: [],
      });
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    void fetchAll();
  }, [fetchAll]);

  return { ...data, loading, error, refetch: fetchAll };
}
