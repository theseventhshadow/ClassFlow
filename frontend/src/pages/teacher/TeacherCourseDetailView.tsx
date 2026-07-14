import React from 'react';
import { DashboardResponse } from '@services';
import { useTeacherCourseDetail } from '@hooks';
import { CourseGradesSection } from './CourseGradesSection';
import { CourseAnnotationsSection } from './CourseAnnotationsSection';

interface Props {
  dashboard: DashboardResponse;
  courseId: number;
  teacherId: number;
  onBack: () => void;
  refetch: () => void;
}

export const TeacherCourseDetailView: React.FC<Props> = ({ dashboard, courseId, teacherId, onBack, refetch }) => {
  const detail = useTeacherCourseDetail(dashboard, courseId);

  if (!detail || !detail.course) {
    return (
      <div className="tp-card">
        <p className="tp-empty-hint">Curso no encontrado.</p>
        <button className="tp-back-btn" onClick={onBack}>← Volver a Mis cursos</button>
      </div>
    );
  }

  const { course, subjects, evaluationsBySubjectId, gradesByEvaluationId, courseEvaluations, roster, annotations } = detail;

  return (
    <div className="tp-course-detail">
      <button className="tp-back-btn" onClick={onBack}>← Volver a Mis cursos</button>

      <div className="tp-course-detail-header">
        <h2>{course.name}</h2>
        <p>{course.description ?? 'Sin descripción'}{course.academicYear ? ` — Año ${course.academicYear}` : ''}</p>
      </div>

      <div className="tp-card">
        <div className="tp-card-header">
          <h3>Asignaturas</h3>
        </div>
        {subjects.length > 0 ? (
          <div className="tp-list">
            {subjects.map((subject) => {
              const evals = evaluationsBySubjectId.get(subject.id) ?? [];
              return (
                <div key={subject.id} className="tp-list-item">
                  <span className="tp-list-name">{subject.name}</span>
                  {evals.length > 0 ? (
                    <span className="tp-badge tp-badge--active">{evals.length} evaluaciones</span>
                  ) : (
                    <span className="tp-empty-hint">Esta asignatura no tiene evaluaciones registradas todavía.</span>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <p className="tp-empty-hint">Este curso no tiene asignaturas registradas.</p>
        )}
      </div>

      <CourseGradesSection
        subjects={subjects}
        evaluationsBySubjectId={evaluationsBySubjectId}
        gradesByEvaluationId={gradesByEvaluationId}
        courseEvaluations={courseEvaluations}
        roster={roster}
        refetch={refetch}
      />

      <CourseAnnotationsSection
        roster={roster}
        annotations={annotations}
        teacherId={teacherId}
        refetch={refetch}
      />
    </div>
  );
};

export default TeacherCourseDetailView;
