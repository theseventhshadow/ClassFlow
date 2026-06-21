import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '@context';
import { humanizeRole } from '@utils';
import {
  dashboardService,
  userService,
  DashboardResponse,
  DashboardNotification,
  DashboardAnnotation,
  DashboardMessage,
  UserRole,
  User,
} from '@services';

interface DashboardStatCard {
  value: string;
  label: string;
  trend: string;
  trendUp: boolean | null;
}

interface DashboardUserRow {
  name: string;
  rol: string;
  rolClass: string;
  estado: string;
  estadoClass: string;
  acceso: string;
}

interface DashboardAttendanceRow {
  name: string;
  pct: number;
}

interface DashboardActivityRow {
  type: 'success' | 'warning' | 'info' | 'error';
  text: string;
  time: string;
}

interface DashboardAlertRow {
  id: string;
  text: string;
  severity: 'low' | 'medium' | 'high';
}

interface DashboardData {
  stats: DashboardStatCard[];
  users: DashboardUserRow[];
  courseAttendance: DashboardAttendanceRow[];
  activity: DashboardActivityRow[];
  alerts: DashboardAlertRow[];
}

interface UseDashboardDataResult extends DashboardData {
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

const ROLE_CLASS: Record<UserRole, string> = {
  ADMINISTRATOR: 'badge--administrador',
  TEACHER: 'badge--docente',
  GUARDIAN: 'badge--apoderado',
  STUDENT: 'badge--estudiante',
};

const STATUS_CLASS: Record<'activo' | 'inactivo' | 'pendiente', string> = {
  activo: 'badge--activo',
  inactivo: 'badge--inactivo',
  pendiente: 'badge--pendiente',
};

function formatDateTime(value?: string | null): Date | null {
  if (!value) {
    return null;
  }

  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

function formatRelativeTime(value?: string | null): string {
  const date = formatDateTime(value);
  if (!date) {
    return 'Sin actividad';
  }

  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  if (diffDays <= 0) {
    return `Hoy ${date.toLocaleTimeString('es-CL', { hour: '2-digit', minute: '2-digit' })}`;
  }

  if (diffDays === 1) {
    return `Ayer ${date.toLocaleTimeString('es-CL', { hour: '2-digit', minute: '2-digit' })}`;
  }

  return `Hace ${diffDays} días`;
}

function getLatestDate(...values: Array<string | null | undefined>): string | null {
  const dates = values
    .map(formatDateTime)
    .filter((value): value is Date => value !== null)
    .sort((left, right) => right.getTime() - left.getTime());

  return dates[0]?.toISOString() ?? null;
}

function getUserStatus(user: User, lastAccess?: string | null): 'activo' | 'inactivo' | 'pendiente' {
  if (!user.activo) {
    return 'inactivo';
  }

  const accessDate = formatDateTime(lastAccess);
  if (!accessDate) {
    return 'pendiente';
  }

  const diffDays = Math.floor((Date.now() - accessDate.getTime()) / (1000 * 60 * 60 * 24));
  return diffDays <= 7 ? 'activo' : 'pendiente';
}

function buildStats(response: DashboardResponse): DashboardStatCard[] {
  const uniqueStudents = new Set<number>([
    ...response.grades.map((grade) => grade.studentId),
    ...response.attendances.map((attendance) => attendance.studentId),
    ...response.annotations.map((annotation) => annotation.studentId),
  ]).size;

  const activeCourses = response.courses.filter((course) => course.active !== false).length;
  const attendanceTotal = response.attendances.length;
  const presentCount = response.attendances.filter((attendance) => attendance.present).length;
  const attendanceRate = attendanceTotal > 0 ? Math.round((presentCount / attendanceTotal) * 100) : 0;
  const activeAlerts = response.pendingNotifications.length + response.notifications.filter((notification) => notification.sent === false || Boolean(notification.errorMessage)).length;

  return [
    {
      value: uniqueStudents.toString(),
      label: 'Total alumnos',
      trend: 'Consolidado desde registros reales',
      trendUp: null,
    },
    {
      value: activeCourses.toString(),
      label: 'Cursos activos',
      trend: 'Cursos activos en el BFF',
      trendUp: null,
    },
    {
      value: `${attendanceRate}%`,
      label: 'Asistencia general',
      trend: 'Calculada desde asistencias registradas',
      trendUp: attendanceRate >= 80 ? true : attendanceRate < 70 ? false : null,
    },
    {
      value: activeAlerts.toString(),
      label: 'Alertas activas',
      trend: activeAlerts > 0 ? 'Pendientes de gestión' : 'Sin alertas críticas',
      trendUp: activeAlerts === 0 ? true : false,
    },
  ];
}

function buildAttendance(response: DashboardResponse): DashboardAttendanceRow[] {
  return response.courses.map((course) => {
    const attendances = response.attendances.filter((attendance) => attendance.courseId === course.id);
    const presentCount = attendances.filter((attendance) => attendance.present).length;
    const pct = attendances.length > 0 ? Math.round((presentCount / attendances.length) * 100) : 0;

    return {
      name: course.name,
      pct,
    };
  });
}

function buildActivity(response: DashboardResponse): DashboardActivityRow[] {
  const entries: Array<{ type: DashboardActivityRow['type']; text: string; timestamp: string | null }> = [];

  response.annotations.forEach((annotation: DashboardAnnotation) => {
    entries.push({
      type: annotation.type.toUpperCase() === 'POSITIVE' ? 'success' : 'warning',
      text: `Anotación ${annotation.type.toLowerCase()} para estudiante #${annotation.studentId}: ${annotation.description}`,
      timestamp: annotation.date,
    });
  });

  response.messages.forEach((message: DashboardMessage) => {
    entries.push({
      type: message.read ? 'info' : 'warning',
      text: `Mensaje recibido: ${message.subject}`,
      timestamp: message.sentAt ?? null,
    });
  });

  response.announcements.forEach((announcement) => {
    entries.push({
      type: announcement.active === false ? 'warning' : 'info',
      text: `Anuncio publicado: ${announcement.title}`,
      timestamp: announcement.publishedAt ?? null,
    });
  });

  response.notifications.forEach((notification: DashboardNotification) => {
    entries.push({
      type: notification.errorMessage ? 'error' : notification.sent === false ? 'warning' : 'info',
      text: `Notificación: ${notification.subject}`,
      timestamp: notification.sentAt ?? notification.createdAt ?? null,
    });
  });

  return entries
    .sort((left, right) => {
      const leftTime = formatDateTime(left.timestamp)?.getTime() ?? 0;
      const rightTime = formatDateTime(right.timestamp)?.getTime() ?? 0;
      return rightTime - leftTime;
    })
    .slice(0, 4)
    .map((entry) => ({
      type: entry.type,
      text: entry.text,
      time: formatRelativeTime(entry.timestamp),
    }));
}

function buildAlerts(response: DashboardResponse): DashboardAlertRow[] {
  const alerts: DashboardAlertRow[] = [];

  response.pendingNotifications.slice(0, 4).forEach((notification) => {
    alerts.push({
      id: `pending-${notification.id}`,
      text: `${notification.subject}: ${notification.content}`,
      severity: 'high',
    });
  });

  response.notifications
    .filter((notification) => Boolean(notification.errorMessage))
    .slice(0, 4)
    .forEach((notification) => {
      alerts.push({
        id: `error-${notification.id}`,
        text: notification.errorMessage ?? notification.subject,
        severity: 'medium',
      });
    });

  response.courses
    .map((course) => {
      const attendances = response.attendances.filter((attendance) => attendance.courseId === course.id);
      const pct = attendances.length > 0
        ? Math.round((attendances.filter((attendance) => attendance.present).length / attendances.length) * 100)
        : 0;

      return { course, pct };
    })
    .filter(({ pct }) => pct > 0 && pct < 70)
    .slice(0, 4)
    .forEach(({ course, pct }) => {
      alerts.push({
        id: `attendance-${course.id}`,
        text: `Asistencia crítica en ${course.name} (${pct}%)`,
        severity: 'medium',
      });
    });

  return alerts.slice(0, 4);
}

async function fetchUserProfile(userId: number): Promise<User | null> {
  try {
    const response = await userService.getUserById(String(userId));
    return response.data;
  } catch {
    return null;
  }
}

async function buildUsers(response: DashboardResponse, adminUserId?: string): Promise<DashboardUserRow[]> {
  const candidateIds = new Set<number>([
    ...response.grades.map((grade) => grade.studentId),
    ...response.attendances.map((attendance) => attendance.studentId),
    ...response.annotations.map((annotation) => annotation.studentId),
  ]);

  const adminNumericId = Number(adminUserId);
  if (!Number.isNaN(adminNumericId)) {
    candidateIds.delete(adminNumericId);
  }

  const ids = Array.from(candidateIds).slice(0, 5);
  const profiles = await Promise.all(ids.map(async (id) => ({ id, profile: await fetchUserProfile(id) })));

  return profiles.map(({ id, profile }) => {
    const userDates = [
      ...response.attendances.filter((attendance) => attendance.studentId === id).map((attendance) => attendance.date),
      ...response.annotations.filter((annotation) => annotation.studentId === id).map((annotation) => annotation.date),
    ];

    const lastAccess = getLatestDate(...userDates);
    const resolvedUser = profile ?? {
      id: String(id),
      nombre: `Usuario #${id}`,
      email: `usuario${id}@classflow.local`,
      rol: 'STUDENT' as UserRole,
      activo: true,
      createdAt: lastAccess ?? new Date().toISOString(),
    };
    const status = getUserStatus(resolvedUser, lastAccess);
    const role = resolvedUser.rol ?? 'STUDENT';

    return {
      name: resolvedUser.nombre,
      rol: humanizeRole(role),
      rolClass: ROLE_CLASS[role],
      estado: status === 'activo' ? 'Activo' : status === 'inactivo' ? 'Inactivo' : 'Pendiente',
      estadoClass: STATUS_CLASS[status],
      acceso: formatRelativeTime(lastAccess ?? resolvedUser.createdAt),
    };
  });
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
      const [users, stats] = await Promise.all([
        buildUsers(response, user.id),
        Promise.resolve(buildStats(response)),
      ]);

      setData({
        stats,
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
