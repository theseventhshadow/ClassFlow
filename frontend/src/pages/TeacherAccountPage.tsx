import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context';

type NavKey = 'dashboard' | 'courses' | 'attendance' | 'annotations' | 'grades' | 'messages';

const COURSES = [
  { id: 1, name: '1° Medio A', subject: 'Matemáticas', room: 'Sala 12', students: 32, progress: 92, color: '#7C3AED' },
  { id: 2, name: '2° Medio B', subject: 'Matemáticas', room: 'Sala 08', students: 28, progress: 85, color: '#10B981' },
  { id: 3, name: '3° Básico C', subject: 'Ciencias', room: 'Sala 04', students: 29, progress: 95, color: '#F59E0B' },
  { id: 4, name: '4° Básico A', subject: 'Ciencias', room: 'Sala 03', students: 31, progress: 72, color: '#EF4444' },
];

const UPCOMING_CLASSES = [
  { id: 1, time: '08:00', course: '1° Medio A', subject: 'Matemáticas', room: 'Sala 12', status: 'En Curso' },
  { id: 2, time: '10:00', course: '2° Medio B', subject: 'Matemáticas', room: 'Sala 08', status: 'Próxima' },
  { id: 3, time: '14:00', course: '3° Básico C', subject: 'Ciencias', room: 'Sala 04', status: 'Pendiente' },
];

const ATTENDANCE_LIST = [
  { id: 1, initials: 'VM', name: 'Valentina Morales', status: 'Presente' },
  { id: 2, initials: 'JP', name: 'Joaquín Pérez', status: 'Ausente' },
  { id: 3, initials: 'DF', name: 'Daniela Fuentes', status: 'Presente' },
  { id: 4, initials: 'MR', name: 'Matías Rojas', status: 'Tardanza' },
  { id: 5, initials: 'CS', name: 'Camila Soto', status: 'Presente' },
];

const ANNOTATIONS_LIST = [
  { id: 1, initials: 'MR', name: 'Matías Rojas', note: 'Conducta disruptiva en clases', time: 'Hoy', type: 'negative' },
  { id: 2, initials: 'VM', name: 'Valentina Morales', note: 'Excelente participación', time: 'Hoy', type: 'positive' },
  { id: 3, initials: 'JP', name: 'Joaquín Pérez', note: '3° inasistencia consecutiva', time: 'Ayer', type: 'negative' },
  { id: 4, initials: 'CS', name: 'Camila Soto', note: 'Ayuda a compañeros en prueba grupal', time: 'Ayer', type: 'positive' },
];

const NAV_ITEMS: { key: NavKey; label: string; icon: string; badge?: number }[] = [
  { key: 'dashboard', label: 'Dashboard', icon: '⊞' },
  { key: 'courses', label: 'Mis Cursos', icon: '📚' },
  { key: 'attendance', label: 'Asistencia', icon: '✓' },
  { key: 'annotations', label: 'Anotaciones', icon: '📝' },
  { key: 'grades', label: 'Calificaciones', icon: '📊' },
  { key: 'messages', label: 'Mensajería', icon: '💬', badge: 2 },
];

const STATUS_CLASS: Record<string, string> = {
  'En Curso': 'tp-badge--active',
  Próxima: 'tp-badge--next',
  Pendiente: 'tp-badge--pending',
  Presente: 'tp-badge--present',
  Ausente: 'tp-badge--absent',
  Tardanza: 'tp-badge--late',
};

export const TeacherAccountPage: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [activeNav, setActiveNav] = useState<NavKey>('dashboard');
  const [showLogoutModal, setShowLogoutModal] = useState(false);

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

  const handleLogout = () => {
    logout();
    localStorage.clear();
    sessionStorage.clear();
    document.cookie.split(';').forEach((c) => {
      document.cookie = c.replace(/^ +/, '').replace(/=.*/, `=;expires=${new Date().toUTCString()};path=/`);
    });
    navigate('/login', { replace: true });
  };

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
            onClick={() => setActiveNav('dashboard')}
          >
            <span className="tp-nav-icon">⊞</span>
            Dashboard
          </button>

          <span className="tp-nav-section">MIS MÓDULOS</span>
          {NAV_ITEMS.filter((i) => i.key !== 'dashboard').map((item) => (
            <button
              key={item.key}
              className={`tp-nav-item ${activeNav === item.key ? 'tp-nav-item--active' : ''}`}
              onClick={() => setActiveNav(item.key)}
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
          <button className="tp-logout-btn" onClick={() => setShowLogoutModal(true)} title="Cerrar sesión">
            ⏻
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="tp-main">
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
            <p>Tienes 3 clases programadas hoy. La primera comienza a las 08:00.</p>
          </div>
          <div className="tp-welcome-stats">
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">4</span>
              <span className="tp-welcome-stat-label">Cursos</span>
            </div>
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">128</span>
              <span className="tp-welcome-stat-label">Estudiantes</span>
            </div>
            <div className="tp-welcome-stat">
              <span className="tp-welcome-stat-value">91%</span>
              <span className="tp-welcome-stat-label">Asistencia</span>
            </div>
          </div>
        </div>

        {/* Metric cards */}
        <div className="tp-metrics">
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Clases hoy</span>
              <span className="tp-metric-icon tp-metric-icon--blue">📅</span>
            </div>
            <span className="tp-metric-value">3</span>
            <span className="tp-metric-sub">Próximo: 08:00</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Asistencia hoy</span>
              <span className="tp-metric-icon tp-metric-icon--green">✓</span>
            </div>
            <span className="tp-metric-value">91%</span>
            <span className="tp-metric-sub tp-metric-sub--up">▲ +2% vs ayer</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Notas pendientes</span>
              <span className="tp-metric-icon tp-metric-icon--orange">📄</span>
            </div>
            <span className="tp-metric-value">8</span>
            <span className="tp-metric-sub tp-metric-sub--warn">Sin ingresar</span>
          </div>
          <div className="tp-metric-card">
            <div className="tp-metric-header">
              <span className="tp-metric-label">Mensajes</span>
              <span className="tp-metric-icon tp-metric-icon--purple">💬</span>
            </div>
            <span className="tp-metric-value">2</span>
            <span className="tp-metric-sub tp-metric-sub--warn">Sin leer</span>
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
              {COURSES.map((course) => (
                <div key={course.id} className="tp-course-item">
                  <div className="tp-course-dot" style={{ backgroundColor: course.color }} />
                  <div className="tp-course-info">
                    <div className="tp-course-title">
                      <span className="tp-course-name">{course.name} — {course.subject}</span>
                      <span className="tp-course-students">{course.students} estudiantes · {course.room}</span>
                    </div>
                    <div className="tp-progress-row">
                      <div className="tp-progress-bar">
                        <div
                          className="tp-progress-fill"
                          style={{ width: `${course.progress}%`, backgroundColor: course.color }}
                        />
                      </div>
                      <span className="tp-progress-pct">{course.progress}%</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Próximas clases */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Próximas clases</h3>
              <button className="tp-link">Horario →</button>
            </div>
            <div className="tp-schedule-list">
              {UPCOMING_CLASSES.map((cls) => (
                <div key={cls.id} className="tp-schedule-item">
                  <span className="tp-schedule-time">{cls.time}</span>
                  <div className="tp-schedule-info">
                    <span className="tp-schedule-course">{cls.course}</span>
                    <span className="tp-schedule-sub">{cls.subject} · {cls.room}</span>
                  </div>
                  <span className={`tp-badge ${STATUS_CLASS[cls.status]}`}>{cls.status}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Attendance + Annotations */}
        <div className="tp-grid-2">
          {/* Asistencia */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Asistencia — 1° Medio A</h3>
              <button className="tp-link">Ver completo →</button>
            </div>
            <div className="tp-list">
              {ATTENDANCE_LIST.map((student) => (
                <div key={student.id} className="tp-list-item">
                  <div className="tp-avatar tp-avatar--sm">{student.initials}</div>
                  <span className="tp-list-name">{student.name}</span>
                  <span className={`tp-badge ${STATUS_CLASS[student.status]}`}>{student.status}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Anotaciones recientes */}
          <div className="tp-card">
            <div className="tp-card-header">
              <h3>Anotaciones recientes</h3>
              <button className="tp-link">Ver todas →</button>
            </div>
            <div className="tp-list">
              {ANNOTATIONS_LIST.map((ann) => (
                <div key={ann.id} className="tp-list-item tp-list-item--annotation">
                  <div className="tp-avatar tp-avatar--sm">{ann.initials}</div>
                  <div className="tp-annotation-body">
                    <span className="tp-annotation-name">{ann.name}</span>
                    <span className="tp-annotation-note">{ann.note}</span>
                  </div>
                  <div className="tp-annotation-meta">
                    <span className="tp-annotation-time">{ann.time}</span>
                    <span className={ann.type === 'positive' ? 'tp-icon--positive' : 'tp-icon--negative'}>
                      {ann.type === 'positive' ? '✓' : '⚠'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>

      {showLogoutModal && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 9999,
          }}
        >
          <div
            style={{
              backgroundColor: 'white',
              borderRadius: '8px',
              padding: '2rem',
              minWidth: '300px',
              boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
              textAlign: 'center',
            }}
          >
            <h2 style={{ marginTop: 0, color: '#333' }}>¿Cerrar sesión?</h2>
            <p style={{ color: '#666', marginBottom: '1.5rem' }}>
              Se cerrará tu sesión y deberás iniciar sesión nuevamente para acceder a la plataforma.
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
              <button
                onClick={() => setShowLogoutModal(false)}
                style={{
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#f0f0f0',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                No, cancelar
              </button>
              <button
                onClick={handleLogout}
                style={{
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#dc2626',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Sí, cerrar sesión
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TeacherAccountPage;
