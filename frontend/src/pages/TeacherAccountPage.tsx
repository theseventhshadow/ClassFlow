import React, { useState } from 'react';
import { useAuth } from '@context';
import { Loading, LogoutModal } from '@components/common';
import { useRawDashboard, useLogout } from '@hooks';
import { TeacherCoursesListView } from './teacher/TeacherCoursesListView';
import { TeacherCourseDetailView } from './teacher/TeacherCourseDetailView';

type NavKey = 'dashboard' | 'courses' | 'attendance' | 'annotations' | 'grades' | 'messages';

const NAV_ITEMS: { key: NavKey; label: string; icon: string; badge?: number }[] = [
  { key: 'dashboard', label: 'Dashboard', icon: '⊞' },
  { key: 'courses', label: 'Mis Cursos', icon: '📚' },
  { key: 'attendance', label: 'Asistencia', icon: '✓' },
  { key: 'annotations', label: 'Anotaciones', icon: '📝' },
  { key: 'grades', label: 'Calificaciones', icon: '📊' },
  { key: 'messages', label: 'Mensajería', icon: '💬', badge: 2 },
];

export const TeacherAccountPage: React.FC = () => {
  const { user } = useAuth();
  const handleLogout = useLogout();
  const [activeNav, setActiveNav] = useState<NavKey>('dashboard');
  const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const { dashboard, loading, error, refetch } = useRawDashboard();

  const handleNavClick = (key: NavKey) => {
    setActiveNav(key);
    if (key !== 'courses') setSelectedCourseId(null);
  };

  const teacherName = user?.nombre || 'Docente';
  const firstName = teacherName.split(' ')[0];
  const initials = teacherName
    .split(' ')
    .map((n) => n[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  const today = new Date();
  const formattedDate = today.toLocaleDateString('es-CL', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
  const hour = today.getHours();
  const greeting = hour < 12 ? '¡Buenos días' : hour < 18 ? '¡Buenas tardes' : '¡Buenas noches';

  const courses = dashboard?.courses ?? [];
  const attendances = dashboard?.attendances ?? [];
  const annotations = dashboard?.annotations ?? [];
  const messages = dashboard?.messages ?? [];
  const unreadMessages = dashboard?.unreadMessages ?? [];
  // Build student name map from enriched BFF data
  const studentNameMap = new Map<number, string>();
  for (const att of attendances) {
    if (att.studentName && !studentNameMap.has(att.studentId)) {
      studentNameMap.set(att.studentId, att.studentName);
    }
  }
  for (const ann of annotations) {
    if (ann.studentName && !studentNameMap.has(ann.studentId)) {
      studentNameMap.set(ann.studentId, ann.studentName);
    }
  }

  const totalStudents = [...new Set(attendances.map(a => a.studentId))].length;
  const attendanceRate = attendances.length > 0
    ? Math.round((attendances.filter(a => a.present).length / attendances.length) * 100)
    : 0;

  if (loading) return <Loading size="lg" message="Cargando portal docente..." />;
  if (error) return <div className="tp-portal"><main className="tp-main"><p style={{ padding: '2rem', color: '#dc2626' }}>{error}</p></main></div>;

  return (
    <div className="tp-portal">
      {/* Sidebar */}
      <aside className="tp-sidebar">
        <div className="tp-brand">
          <span className="tp-brand-name">ClassFlow</span>
          <span className="tp-brand-sub">PORTAL DOCENTE</span>
        </div>

        <nav className="tp-nav">
          <span className="tp-nav-section">PRINCIPAL</span>
          <button
            className={`tp-nav-item ${activeNav === 'dashboard' ? 'tp-nav-item--active' : ''}`}
            onClick={() => handleNavClick('dashboard')}
          >
            <span className="tp-nav-icon">⊞</span>
            Dashboard
          </button>

          <span className="tp-nav-section">MIS MÓDULOS</span>
          {NAV_ITEMS.filter((i) => i.key !== 'dashboard').map((item) => (
            <button
              key={item.key}
              className={`tp-nav-item ${activeNav === item.key ? 'tp-nav-item--active' : ''}`}
              onClick={() => handleNavClick(item.key)}
            >
              <span className="tp-nav-icon">{item.icon}</span>
              {item.label}
              {item.badge && <span className="tp-nav-badge">{item.badge}</span>}
            </button>
          ))}
        </nav>

        <div className="tp-sidebar-user">
          <div className="tp-user-avatar">{initials}</div>
          <div className="tp-user-info">
            <span className="tp-user-name">{teacherName}</span>
            <span className="tp-user-role">DOCENTE</span>
          </div>
          <button className="dashboard-logout-btn" onClick={() => setShowLogoutModal(true)} aria-label="Cerrar sesión" title="Cerrar sesión">
            ⏻
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="tp-main">
      {activeNav === 'courses' ? (
        selectedCourseId == null ? (
          <TeacherCoursesListView
            courses={courses}
            attendances={attendances}
            onSelectCourse={setSelectedCourseId}
          />
        ) : (
          <TeacherCourseDetailView
            dashboard={dashboard!}
            courseId={selectedCourseId}
            teacherId={Number(user!.id)}
            onBack={() => setSelectedCourseId(null)}
            refetch={refetch}
          />
        )
      ) : (
        <>
        {/* Top bar */}
        <div className="tp-topbar">
          <div>
            <h1 className="tp-title">Mi Dashboard</h1>
            <p className="tp-date">{formattedDate}</p>
          </div>
          <div className="tp-topbar-actions">
            <button className="tp-btn tp-btn--outline">Ver horario</button>
            <button className="tp-btn tp-btn--primary">+ Registrar asistencia</button>
          </div>
        </div>

        {/* Welcome banner */}
        <div className="tp-welcome">
          <div className="tp-welcome-text">
            <h2>
              {greeting}, {firstName}! 👋
            </h2>
            <p>{courses.length} cursos con {totalStudents} estudiantes registrados.</p>
          </div>
          <div className="tp-welcome-stats">
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">{courses.length}</span>
              <span className="tp-welcome-stat-label">Cursos</span>
            </div>
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">{totalStudents}</span>
              <span className="tp-welcome-stat-label">Estudiantes</span>
            </div>
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">{attendanceRate}%</span>
              <span className="tp-welcome-stat-label">Asistencia</span>
            </div>
          </div>
        </div>

        {/* Metric cards */}
        <div className="tp-metrics">
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Cursos activos</span>
              <span className="tp-metric-icon tp-metric-icon--blue">📚</span>
            </div>
            <span className="tp-metric-value">{courses.length}</span>
            <span className="tp-metric-sub">{totalStudents} estudiantes</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Asistencia</span>
              <span className="tp-metric-icon tp-metric-icon--green">✓</span>
            </div>
            <span className="tp-metric-value">{attendanceRate}%</span>
            <span className="tp-metric-sub tp-metric-sub--up">{attendances.length} registros</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Anotaciones</span>
              <span className="tp-metric-icon tp-metric-icon--orange">📝</span>
            </div>
            <span className="tp-metric-value">{annotations.length}</span>
            <span className="tp-metric-sub tp-metric-sub--warn">{annotations.filter(a => a.type === 'NEGATIVE').length} negativas</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Mensajes</span>
              <span className="tp-metric-icon tp-metric-icon--purple">💬</span>
            </div>
            <span className="tp-metric-value">{messages.length}</span>
            <span className="tp-metric-sub tp-metric-sub--warn">{unreadMessages.length} sin leer</span>
          </div>
        </div>

        {/* Courses + Upcoming classes */}
        <div className="tp-grid-2">
          {/* Mis cursos */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Mis cursos</h3>
              <button className="tp-link">Ver todos →</button>
            </div>
            <div className="tp-course-list">
              {courses.length > 0 ? courses.map((course) => (
                <div key={course.id} className="tp-course-item">
                  <div className="tp-course-dot" style={{ backgroundColor: '#7C3AED' }} />
                  <div className="tp-course-info">
                    <div className="tp-course-title">
                      <span className="tp-course-name">{course.name}</span>
                      <span className="tp-course-students">{course.description ?? 'Sin descripción'}</span>
                    </div>
                  </div>
                </div>
              )) : (
                <p style={{ padding: '1rem', color: '#666' }}>No hay cursos asignados</p>
              )}
            </div>
          </div>

          {/* Próximas clases */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Cursos y asistencias</h3>
              <button className="tp-link">Ver detalle →</button>
            </div>
            <div className="tp-schedule-list">
              {courses.length > 0 ? courses.slice(0, 5).map((course) => {
                const courseAttendances = attendances.filter(a => a.courseId === course.id);
                const pct = courseAttendances.length > 0
                  ? Math.round((courseAttendances.filter(a => a.present).length / courseAttendances.length) * 100)
                  : 0;
                return (
                  <div key={course.id} className="tp-schedule-item">
                    <span className="tp-schedule-time">{course.name}</span>
                    <div className="tp-schedule-info">
                      <span className="tp-schedule-course">{courseAttendances.length} asistencias</span>
                      <span className="tp-schedule-sub">{pct}% presente</span>
                    </div>
                    <span className={`tp-badge ${pct >= 80 ? 'tp-badge--active' : pct >= 60 ? 'tp-badge--next' : 'tp-badge--pending'}`}>
                      {pct}%
                    </span>
                  </div>
                );
              }) : (
                <p style={{ padding: '1rem', color: '#666' }}>Sin datos de asistencia</p>
              )}
            </div>
          </div>
        </div>

        {/* Attendance + Annotations */}
        <div className="tp-grid-2">
          {/* Asistencia */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Asistencia reciente</h3>
              <button className="tp-link">Ver completo →</button>
            </div>
            <div className="tp-list">
              {attendances.slice(0, 5).map((att) => {
                const name = studentNameMap.get(att.studentId) ?? `Estudiante #${att.studentId}`;
                const initials = name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
                return (
                  <div key={att.id} className="tp-list-item">
                    <div className="tp-avatar tp-avatar--sm">{initials}</div>
                    <span className="tp-list-name">{name}</span>
                    <span className={`tp-badge ${att.present ? 'tp-badge--present' : 'tp-badge--absent'}`}>
                      {att.present ? 'Presente' : 'Ausente'}
                    </span>
                  </div>
                );
              })}
              {attendances.length === 0 && <p style={{ padding: '1rem', color: '#666' }}>Sin asistencias registradas</p>}
            </div>
          </div>

          {/* Anotaciones recientes */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Anotaciones recientes</h3>
              <button className="tp-link">Ver todas →</button>
            </div>
            <div className="tp-list">
              {annotations.slice(0, 4).map((ann) => {
                const name = studentNameMap.get(ann.studentId) ?? `Estudiante #${ann.studentId}`;
                const initials = name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
                return (
                  <div key={ann.id} className="tp-list-item tp-list-item--annotation">
                    <div className="tp-avatar tp-avatar--sm">{initials}</div>
                    <div className="tp-annotation-body">
                      <span className="tp-annotation-name">{name}</span>
                      <span className="tp-annotation-note">{ann.description}</span>
                    </div>
                    <div className="tp-annotation-meta">
                      <span className="tp-annotation-time">{ann.date ? new Date(ann.date).toLocaleDateString('es-CL') : ''}</span>
                      <span className={ann.type === 'POSITIVE' ? 'tp-icon--positive' : 'tp-icon--negative'}>
                        {ann.type === 'POSITIVE' ? '✓' : '⚠'}
                      </span>
                    </div>
                  </div>
                );
              })}
              {annotations.length === 0 && <p style={{ padding: '1rem', color: '#666' }}>Sin anotaciones registradas</p>}
            </div>
          </div>
        </div>
        </>
      )}
      </main>

      <LogoutModal
        open={showLogoutModal}
        onCancel={() => setShowLogoutModal(false)}
        onConfirm={handleLogout}
      />
    </div>
  );
};

export default TeacherAccountPage;
