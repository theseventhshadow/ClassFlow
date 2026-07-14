import React, { useState } from 'react';
import './DashboardPage.css';
import { useAuth } from '@context';
import { Loading, LogoutModal, Icon } from '@components/common';
import { useRawDashboard, useLogout } from '@hooks';
import { humanizeRole, calculateAverageScore, formatAverageScore } from '@utils';

export const StudentDashboardPage: React.FC = () => {
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const { user } = useAuth();
  const { dashboard, loading, error } = useRawDashboard();
  const handleLogout = useLogout();

  const initials = user?.nombre
    ? user.nombre.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'US';
  const displayName = user?.nombre ?? 'Usuario';
  const displayEmail = user?.email ?? '';
  const displayRole = humanizeRole(user?.rol);

  const studentId = Number(user?.id);
  const myCourses = dashboard?.courses ?? [];
  const myGrades = (dashboard?.grades ?? []).filter(g => g.studentId === studentId);
  const myEvaluations = dashboard?.evaluations ?? [];
  const myAttendances = (dashboard?.attendances ?? []).filter(a => a.studentId === studentId);

  const average = calculateAverageScore(myGrades);
  const avgScore = formatAverageScore(average);
  const attendancePct = myAttendances.length > 0
    ? Math.round((myAttendances.filter(a => a.present).length / myAttendances.length) * 100)
    : 0;

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
            className="dashboard-logout-btn"
            onClick={() => setShowLogoutModal(true)}
            aria-label="Cerrar sesión"
            title="Cerrar sesión"
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
                      const isPending = g.score === null || g.score === undefined;
                      return (
                        <tr key={g.id}>
                          <td><div className="user-cell"><span className="user-name">{evalName}</span></div></td>
                          <td><span className="role-badge role-docente">{isPending ? '—' : g.score}</span></td>
                          <td>
                            {isPending ? (
                              <span className="status-badge status-pendiente">Pendiente</span>
                            ) : (
                              <span className="status-badge status-activo">{(g.score as number) >= 4 ? 'Aprobado' : 'Reprobado'}</span>
                            )}
                          </td>
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

      <LogoutModal
        open={showLogoutModal}
        onCancel={() => setShowLogoutModal(false)}
        onConfirm={handleLogout}
      />
    </div>
  );
};

export default StudentDashboardPage;
