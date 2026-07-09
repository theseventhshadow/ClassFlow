import React from 'react';
import { DashboardCourse, DashboardAttendance } from '@services';

interface Props {
  courses: DashboardCourse[];
  attendances: DashboardAttendance[];
  onSelectCourse: (courseId: number) => void;
}

export const TeacherCoursesListView: React.FC<Props> = ({ courses, attendances, onSelectCourse }) => {
  if (courses.length === 0) {
    return <p className="tp-empty-hint">No hay cursos registrados en el sistema.</p>;
  }

  return (
    <div className="tp-card">
      <div className="tp-card-header">
        <h3>Mis cursos</h3>
      </div>
      <div className="tp-course-list">
        {courses.map((course) => {
          const knownStudents = new Set(
            attendances.filter((a) => a.courseId === course.id).map((a) => a.studentId)
          ).size;
          return (
            <div key={course.id} className="tp-course-item">
              <div className="tp-course-dot" style={{ backgroundColor: '#7C3AED' }} />
              <div className="tp-course-info">
                <div className="tp-course-title">
                  <span className="tp-course-name">{course.name}</span>
                  <span className="tp-course-students">{course.description ?? 'Sin descripción'}</span>
                </div>
                <span className="tp-badge tp-badge--next">{knownStudents} alumnos conocidos</span>
              </div>
              <button className="tp-btn tp-btn--outline" onClick={() => onSelectCourse(course.id)}>
                Ver curso →
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default TeacherCoursesListView;
