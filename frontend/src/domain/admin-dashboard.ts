import type {
  DashboardAnnotation,
  DashboardNotification,
  DashboardResponse,
  DashboardMessage,
} from '@services';

export interface DashboardStatCard {
  value: string;
  label: string;
  trend: string;
  trendUp: boolean | null;
}

export interface DashboardAttendanceRow {
  name: string;
  pct: number;
}

export interface DashboardActivityRow {
  type: 'success' | 'warning' | 'info' | 'error';
  text: string;
  time: string;
}

export interface DashboardAlertRow {
  id: string;
  text: string;
  severity: 'low' | 'medium' | 'high';
}

export interface DashboardViewModel {
  stats: DashboardStatCard[];
  courseAttendance: DashboardAttendanceRow[];
  activity: DashboardActivityRow[];
  alerts: DashboardAlertRow[];
}

function formatDateTime(value?: string | null): Date | null {
  if (!value) {
    return null;
  }

  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

export function formatRelativeTime(value?: string | null): string {
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

export function getLatestDate(...values: Array<string | null | undefined>): string | null {
  const dates = values
    .map(formatDateTime)
    .filter((value): value is Date => value !== null)
    .sort((left, right) => right.getTime() - left.getTime());

  return dates[0]?.toISOString() ?? null;
}

export function getUserStatus(
  user: { activo: boolean },
  lastAccess?: string | null
): 'activo' | 'inactivo' | 'pendiente' {
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

export function buildStats(response: DashboardResponse): DashboardStatCard[] {
  const uniqueStudents = new Set<number>([
    ...response.grades.map((grade) => grade.studentId),
    ...response.attendances.map((attendance) => attendance.studentId),
    ...response.annotations.map((annotation) => annotation.studentId),
  ]).size;

  const activeCourses = response.courses.filter((course) => course.active !== false).length;
  const attendanceTotal = response.attendances.length;
  const presentCount = response.attendances.filter((attendance) => attendance.present).length;
  const attendanceRate = attendanceTotal > 0 ? Math.round((presentCount / attendanceTotal) * 100) : 0;
  const activeAlerts =
    response.pendingNotifications.length +
    response.notifications.filter((notification) => notification.sent === false || Boolean(notification.errorMessage)).length;

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

export function buildAttendance(response: DashboardResponse): DashboardAttendanceRow[] {
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

export function buildActivity(response: DashboardResponse): DashboardActivityRow[] {
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

export function buildAlerts(response: DashboardResponse): DashboardAlertRow[] {
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
      const pct =
        attendances.length > 0
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
