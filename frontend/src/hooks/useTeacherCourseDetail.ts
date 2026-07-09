import { useMemo } from 'react';
import { DashboardResponse, DashboardCourse, DashboardSubject, DashboardEvaluation, DashboardGrade, DashboardAnnotation } from '@services';
import {
  CourseRosterEntry,
  getSubjectsForCourse,
  getEvaluationsForSubject,
  getEvaluationsForCourse,
  getGradesForEvaluation,
  getGradesForCourse,
  getCourseRoster,
  getAnnotationsForRoster,
} from '@utils';

export interface TeacherCourseDetail {
  course: DashboardCourse | null;
  subjects: DashboardSubject[];
  evaluationsBySubjectId: Map<number, DashboardEvaluation[]>;
  courseEvaluations: DashboardEvaluation[];
  gradesByEvaluationId: Map<number, DashboardGrade[]>;
  courseGrades: DashboardGrade[];
  roster: CourseRosterEntry[];
  annotations: DashboardAnnotation[];
}

export function useTeacherCourseDetail(
  dashboard: DashboardResponse | null,
  courseId: number | null
): TeacherCourseDetail | null {
  return useMemo(() => {
    if (!dashboard || courseId == null) return null;

    const course = dashboard.courses.find((c) => c.id === courseId) ?? null;
    const subjects = getSubjectsForCourse(dashboard.subjects, courseId);
    const subjectIds = subjects.map((s) => s.id);

    const evaluationsBySubjectId = new Map<number, DashboardEvaluation[]>(
      subjects.map((s) => [s.id, getEvaluationsForSubject(dashboard.evaluations, s.id)])
    );
    const courseEvaluations = getEvaluationsForCourse(dashboard.evaluations, subjectIds);
    const evaluationIds = courseEvaluations.map((e) => e.id);

    const gradesByEvaluationId = new Map<number, DashboardGrade[]>(
      courseEvaluations.map((e) => [e.id, getGradesForEvaluation(dashboard.grades, e.id)])
    );
    const courseGrades = getGradesForCourse(dashboard.grades, evaluationIds);

    const roster = getCourseRoster(dashboard.attendances, courseId);
    const rosterIds = new Set(roster.map((r) => r.studentId));
    const annotations = getAnnotationsForRoster(dashboard.annotations, rosterIds);

    return {
      course,
      subjects,
      evaluationsBySubjectId,
      courseEvaluations,
      gradesByEvaluationId,
      courseGrades,
      roster,
      annotations,
    };
  }, [dashboard, courseId]);
}
