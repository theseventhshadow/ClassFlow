import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './DashboardPage.css';
import { useAuth } from '@context';
import { dashboardService, DashboardResponse } from '@services';
import { Loading } from '@components/common';
import { humanizeRole } from '@utils';

const Icon = {
  Grid: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/>
      <rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/>
    </svg>
  ),
  Users: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
      <circle cx="9" cy="7" r="4"/>
      <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
    </svg>
  ),
  Academic: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
      <path d="M6 12v5c3 3 9 3 12 0v-5"/>
    </svg>
  ),
  Check: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12"/>
    </svg>
  ),
  Chat: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
    </svg>
  ),
  FileText: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
      <polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
    </svg>
  ),
  BarChart: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/>
      <line x1="6" y1="20" x2="6" y2="14"/><line x1="2" y1="20" x2="22" y2="20"/>
    </svg>
  ),
  Settings: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="3"/>
      <path d="M19.07 4.93l-1.41 1.41M4.93 4.93l1.41 1.41M12 2v2M12 20v2M2 12h2M20 12h2M4.93 19.07l1.41-1.41M18.66 18.66l1.41 1.41"/>
    </svg>
  ),
  Download: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
      <polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/>
    </svg>
  ),
  Plus: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
    </svg>
  ),
  UserGroup: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
      <circle cx="9" cy="7" r="4"/>
      <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
    </svg>
  ),
  Book: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
      <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
    </svg>
  ),
  Percent: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="19" y1="5" x2="5" y2="19"/>
      <circle cx="6.5" cy="6.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/>
    </svg>
  ),
  Bell: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
      <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
    </svg>
  ),
  TrendUp: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
      <polyline points="17 6 23 6 23 12"/>
    </svg>
  ),
  AlertTriangle: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
      <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
    </svg>
  ),
  Edit: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
    </svg>
  ),
  Mail: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
      <polyline points="22,6 12,13 2,6"/>
    </svg>
  ),
  Lock: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
      <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
    </svg>
  ),
  CheckCircle: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
      <polyline points="22 4 12 14.01 9 11.01"/>
    </svg>
  ),
  FileDown: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
      <polyline points="14 2 14 8 20 8"/><line x1="12" y1="18" x2="12" y2="12"/><polyline points="9 15 12 18 15 15"/>
    </svg>
  ),
  XCircle: () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10"/>
      <line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
    </svg>
  ),
};


export const StudentDashboardPage: React.FC = () => {
  const [activeNav, setActiveNav] = useState('Dashboard');
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user?.id) return;
    setLoading(true);
    dashboardService.getDashboard(user.id)
      .then(setDashboard)
      .catch((err) => setError(err instanceof Error ? err.message : 'Error al cargar datos'))
      .finally(() => setLoading(false));
  }, [user?.id]);

  const initials = user?.nombre
    ? user.nombre.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'US';
  const displayName = user?.nombre ?? 'Usuario';
  const displayEmail = user?.email ?? '';
  const displayRole = humanizeRole(user?.rol);

  const studentId = Number(user?.id);
  const myCourses = dashboard?.courses ?? [];
  const myGrades = dashboard?.grades.filter(g => g.studentId === studentId) ?? [];
  const myEvaluations = dashboard?.evaluations ?? [];
  const myAttendances = dashboard?.attendances.filter(a => a.studentId === studentId) ?? [];

  const avgScore = myGrades.length > 0
    ? (myGrades.reduce((sum, g) => sum + (g.score ?? 0), 0) / myGrades.length).toFixed(1)
    : '—';
  const attendancePct = myAttendances.length > 0
    ? Math.round((myAttendances.filter(a => a.present).length / myAttendances.length) * 100)
    : 0;

  const handleLogout = () => {
    logout();
    localStorage.clear();
    sessionStorage.clear();
    document.cookie.split(';').forEach((c) => {
      document.cookie = c.replace(/^ +/, '').replace(/=.*/, `=;expires=${new Date().toUTCString()};path=/`);
    });
    navigate('/login', { replace: true });
  };

  const handleLogoutCancel = () => {
    setShowLogoutModal(false);
  };

  if (loading) return <Loading size="lg" message="Cargando datos del estudiante..." />;
  if (error) return <div className="dashboard-layout"><div className="dashboard-main"><p style={{ padding: '2rem', color: '#dc2626' }}>{error}</p></div></div>;

  return (
    <div className="dashboard-layout">
      <aside className="dashboard-sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-brand-name">
            <div className="sidebar-brand-icon">CF</div>
            ClassFlow
          </div>
          <div className="sidebar-role">{displayRole || 'Usuario'}</div>
          <div className="sidebar-email">{displayEmail || ''}</div>
        </div>

        <nav className="sidebar-nav">
          <div>
            <div className="sidebar-section-label">GESTIÓN</div>
            <button className="sidebar-nav-item active"><span className="nav-icon"><Icon.Grid /></span><span className="nav-label">Dashboard</span></button>
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Academic /></span><span className="nav-label">Mis cursos</span></button>
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Check /></span><span className="nav-label">Asistencia</span></button>
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Chat /></span><span className="nav-label">Mensajería</span></button>
          </div>
          <div>
            <div className="sidebar-section-label">SISTEMA</div>
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Settings /></span><span className="nav-label">Configuración</span></button>
          </div>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-avatar">{initials}</div>
          <div>
            <div className="sidebar-user-name">{displayName}</div>
            <div className="sidebar-user-role">{displayRole || 'Usuario'}</div>
          </div>
          <button
            className="sidebar-logout-btn"
            onClick={() => setShowLogoutModal(true)}
            title="Cerrar sesión"
            style={{
              background: 'none',
              border: 'none',
              color: 'inherit',
              cursor: 'pointer',
              fontSize: '1.2rem',
              padding: '0.5rem',
            }}
          >
            ⏻
          </button>
        </div>
      </aside>

      <div className="dashboard-main">
        <header className="dashboard-header">
          <div className="dashboard-header-left">
            <h1 className="dashboard-title">Panel Estudiante</h1>
            <span className="dashboard-subtitle">
              {new Date().toLocaleDateString('es-CL', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })} &mdash; Bienvenido, {user?.nombre ? user.nombre.split(' ')[0] : 'Usuario'}
            </span>
          </div>
        </header>

        <div className="dashboard-body">
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Mis cursos</span>
                <span className="stat-value">{myCourses.length}</span>
                <span className="stat-change positive">Activos</span>
              </div>
              <div className="stat-icon-wrap stat-icon-blue"><Icon.Book /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Promedio general</span>
                <span className="stat-value">{avgScore}</span>
                <span className="stat-change positive">
                  {myGrades.length > 0 && <><span className="stat-change-icon"><Icon.TrendUp /></span> {myGrades.length} notas</>}
                </span>
              </div>
              <div className="stat-icon-wrap stat-icon-green"><Icon.BarChart /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Asistencia</span>
                <span className="stat-value">{attendancePct}%</span>
                <span className="stat-change positive">{attendancePct >= 80 ? 'Excelente' : attendancePct >= 60 ? 'Regular' : 'Preocupante'}</span>
              </div>
              <div className="stat-icon-wrap stat-icon-teal"><Icon.Percent /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Evaluaciones</span>
                <span className="stat-value">{myEvaluations.length}</span>
                <span className="stat-change warning">{myEvaluations.filter(e => e.date && new Date(e.date) > new Date()).length} próximas</span>
              </div>
              <div className="stat-icon-wrap stat-icon-orange"><Icon.Bell /></div>
            </div>
          </div>

          <div className="content-grid">
            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Mis calificaciones</h2>
                  <button className="dash-card-link">Ver todas</button>
                </div>
                <table className="users-table">
                  <thead>
                    <tr>
                      <th>ASIGNATURA</th>
                      <th>NOTA</th>
                      <th>ESTADO</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myGrades.length > 0 ? myGrades.map((g) => {
                      const evalName = myEvaluations.find(e => e.id === g.evaluationId)?.name ?? `Evaluación #${g.evaluationId}`;
                      const score = g.score ?? 0;
                      return (
                        <tr key={g.id}>
                          <td><div className="user-cell"><span className="user-name">{evalName}</span></div></td>
                          <td><span className="role-badge role-docente">{score}</span></td>
                          <td><span className="status-badge status-activo">{score >= 4 ? 'Aprobado' : 'Reprobado'}</span></td>
                        </tr>
                      );
                    }) : (
                      <tr><td colSpan={3} className="admin-table-muted" style={{ textAlign: 'center', padding: '1rem' }}>Sin calificaciones registradas</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Próximas evaluaciones</h2>
                  <button className="dash-card-link">Ver calendario</button>
                </div>
                <div className="activity-list">
                  {myEvaluations.length > 0 ? myEvaluations.slice(0, 5).map((ev) => (
                    <div key={ev.id} className="activity-item">
                      <div className="activity-dot dot-info"><Icon.FileText /></div>
                      <div className="activity-content">
                        <span className="activity-text">{ev.name}</span>
                        <span className="activity-time">{ev.date ? new Date(ev.date).toLocaleDateString('es-CL') : 'Sin fecha'}</span>
                      </div>
                    </div>
                  )) : (
                    <p style={{ padding: '1rem', textAlign: 'center', color: '#666' }}>No hay evaluaciones próximas</p>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {showLogoutModal && (
        <div
          style={{
            position: 'fixed',
            top: 0, left: 0, right: 0, bottom: 0,
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
                onClick={handleLogoutCancel}
                style={{ padding: '0.75rem 1.5rem', backgroundColor: '#f0f0f0', border: '1px solid #ddd', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem', fontWeight: '500' }}
              >
                No, cancelar
              </button>
              <button
                onClick={handleLogout}
                style={{ padding: '0.75rem 1.5rem', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem', fontWeight: '500' }}
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

export default StudentDashboardPage;
