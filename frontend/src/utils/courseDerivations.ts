import {
  DashboardSubject,
  DashboardEvaluation,
  DashboardGrade,
  DashboardAttendance,
  DashboardAnnotation,
} from '@services';

export interface CourseRosterEntry {
  studentId: number;
  studentName: string;
}

export function getSubjectsForCourse(subjects: DashboardSubject[], courseId: number): DashboardSubject[] {
  return subjects.filter((s) => s.courseId === courseId);
}

export function getEvaluationsForSubject(evaluations: DashboardEvaluation[], subjectId: number): DashboardEvaluation[] {
  return evaluations.filter((e) => e.subjectId === subjectId);
}

export function getEvaluationsForCourse(evaluations: DashboardEvaluation[], subjectIds: number[]): DashboardEvaluation[] {
  const ids = new Set(subjectIds);
  return evaluations.filter((e) => e.subjectId != null && ids.has(e.subjectId));
}

export function getGradesForEvaluation(grades: DashboardGrade[], evaluationId: number): DashboardGrade[] {
  return grades.filter((g) => g.evaluationId === evaluationId);
}

export function getGradesForCourse(grades: DashboardGrade[], evaluationIds: number[]): DashboardGrade[] {
  const ids = new Set(evaluationIds);
  return grades.filter((g) => g.evaluationId != null && ids.has(g.evaluationId));
}

// "Roster" aproximado: alumnos que ya tienen al menos un registro de asistencia
// en este curso. No existe un endpoint real de matrícula/roster por curso.
export function getCourseRoster(attendances: DashboardAttendance[], courseId: number): CourseRosterEntry[] {
  const map = new Map<number, string>();
  for (const att of attendances) {
    if (att.courseId === courseId && !map.has(att.studentId)) {
      map.set(att.studentId, att.studentName ?? `Estudiante #${att.studentId}`);
    }
  }
  return [...map.entries()]
    .map(([studentId, studentName]) => ({ studentId, studentName }))
    .sort((a, b) => a.studentName.localeCompare(b.studentName));
}

// Las anotaciones no tienen courseId; se acotan al roster del curso para no
// mezclar anotaciones de alumnos de otros cursos.
export function getAnnotationsForRoster(annotations: DashboardAnnotation[], rosterStudentIds: Set<number>): DashboardAnnotation[] {
  return annotations.filter((a) => rosterStudentIds.has(a.studentId));
}
