import { authService, courseService, type DashboardResponse, type UserRole } from '@services';
import { humanizeRole } from '@utils';
import { buildAlerts, buildAttendance, buildActivity, buildStats, formatRelativeTime, getLatestDate, getUserStatus } from '../domain/admin-dashboard';

export interface DashboardUserRow {
  name: string;
  rol: string;
  rolClass: string;
  estado: string;
  estadoClass: string;
  acceso: string;
}

const ROLE_LABEL: Record<UserRole, string> = {
  ADMINISTRATOR: 'Administrador',
  TEACHER: 'Docente',
  GUARDIAN: 'Apoderado',
  STUDENT: 'Estudiante',
};

export interface CreateUserFormState {
  nombres: string;
  apellidos: string;
  email: string;
  password: string;
  rol: UserRole;
  idNumber: string;
}

export interface CreateCourseFormState {
  name: string;
  description: string;
  academicYear: string;
}

export interface AdminDashboardViewModel {
  stats: ReturnType<typeof buildStats>;
  users: DashboardUserRow[];
  courseAttendance: ReturnType<typeof buildAttendance>;
  activity: ReturnType<typeof buildActivity>;
  alerts: ReturnType<typeof buildAlerts>;
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

function extractStudentNames(response: DashboardResponse): Map<number, string> {
  const nameMap = new Map<number, string>();

  for (const grade of response.grades) {
    if (grade.studentName && !nameMap.has(grade.studentId)) {
      nameMap.set(grade.studentId, grade.studentName);
    }
  }

  for (const attendance of response.attendances) {
    if (attendance.studentName && !nameMap.has(attendance.studentId)) {
      nameMap.set(attendance.studentId, attendance.studentName);
    }
  }

  for (const annotation of response.annotations) {
    if (annotation.studentName && !nameMap.has(annotation.studentId)) {
      nameMap.set(annotation.studentId, annotation.studentName);
    }
  }

  return nameMap;
}

export function buildDashboardUsers(response: DashboardResponse, adminUserId?: string): DashboardUserRow[] {
  const nameMap = extractStudentNames(response);

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

  return ids.map((id) => {
    const userDates = [
      ...response.attendances.filter((attendance) => attendance.studentId === id).map((attendance) => attendance.date),
      ...response.annotations.filter((annotation) => annotation.studentId === id).map((annotation) => annotation.date),
    ];

    const lastAccess = getLatestDate(...userDates);
    const name = nameMap.get(id) ?? `Usuario #${id}`;

    const resolvedUser = {
      id: String(id),
      nombre: name,
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

export function validateCreateUserForm(form: CreateUserFormState): string | null {
  if (!form.nombres.trim() || !form.apellidos.trim() || !form.email.trim() || !form.password.trim() || !form.idNumber.trim()) {
    return 'Todos los campos son obligatorios.';
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(form.email)) {
    return 'Ingresa un correo electrónico válido.';
  }

  if (form.password.length < 6) {
    return 'La contraseña debe tener al menos 6 caracteres.';
  }

  const rutRegex = /^\d{1,2}\.\d{3}\.\d{3}-[0-9kK]$/;
  if (!rutRegex.test(form.idNumber)) {
    return 'El RUT debe tener formato: 12.345.678-9';
  }

  return null;
}

export function validateCreateCourseForm(form: CreateCourseFormState): string | null {
  if (!form.name.trim() || !form.academicYear.trim()) {
    return 'Nombre y año académico son obligatorios.';
  }

  const year = Number(form.academicYear);
  if (!Number.isInteger(year) || year < 2000 || year > 2100) {
    return 'Año académico inválido.';
  }

  return null;
}

export function buildCreatedUserRow(form: CreateUserFormState): DashboardUserRow {
  return {
    name: `${form.nombres.trim()} ${form.apellidos.trim()}`,
    rol: ROLE_LABEL[form.rol],
    rolClass: ROLE_CLASS[form.rol],
    estado: 'Activo',
    estadoClass: 'badge--activo',
    acceso: 'Ahora',
  };
}

export async function registerAdminUser(form: CreateUserFormState): Promise<User> {
  const validationError = validateCreateUserForm(form);
  if (validationError) {
    throw new Error(validationError);
  }

  return authService.register({
    firstName: form.nombres.trim(),
    lastName: form.apellidos.trim(),
    idNumber: form.idNumber.trim(),
    email: form.email.trim(),
    password: form.password,
    role: form.rol,
  });
}

export async function createAdminCourse(form: CreateCourseFormState) {
  const validationError = validateCreateCourseForm(form);
  if (validationError) {
    throw new Error(validationError);
  }

  const academicYear = Number(form.academicYear);

  return courseService.createCourse({
    name: form.name.trim(),
    description: form.description.trim() || undefined,
    academicYear,
  });
}
