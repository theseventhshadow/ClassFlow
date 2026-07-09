import React, { useState } from 'react';
import './DashboardPage.css';
import { useAuth } from '@context';
import { DashboardGrade, DashboardAttendance } from '@services';
import { Loading, LogoutModal, Icon } from '@components/common';
import { useRawDashboard, useLogout } from '@hooks';
import { humanizeRole, calculateAverageScore, formatAverageScore } from '@utils';

export const GuardianDashboardPage: React.FC = () => {
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const { user } = useAuth();
  const { dashboard, loading, error } = useRawDashboard();
  const handleLogout = useLogout();

  const initials = user?.nombre
    ? user.nombre.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'AP';
  const displayName = user?.nombre ?? 'Apoderado';
  const displayEmail = user?.email ?? '';
  const displayRole = humanizeRole(user?.rol);

  const grades = dashboard?.grades ?? [];
  const attendances = dashboard?.attendances ?? [];
  const annotations = dashboard?.annotations ?? [];

  const studentIds = [...new Set([
    ...grades.map((g: DashboardGrade) => g.studentId),
    ...attendances.map((a: DashboardAttendance) => a.studentId),
    ...annotations.map((a: { studentId: number }) => a.studentId),
  ])];

  const totalStudents = studentIds.length;
  const totalAttendances = attendances.length;
  const presentCount = attendances.filter((a: DashboardAttendance) => a.present).length;
  const attendanceRate = totalAttendances > 0 ? Math.round((presentCount / totalAttendances) * 100) : 0;
  const totalAnnotations = annotations.length;

  // Build name map from enriched BFF data (studentName is injected by BFF)
  const studentNameMap = new Map<number, string>();
  for (const grade of grades) {
    if (grade.studentName && !studentNameMap.has(grade.studentId)) {
      studentNameMap.set(grade.studentId, grade.studentName);
    }
  }
  for (const attendance of attendances) {
    if (attendance.studentName && !studentNameMap.has(attendance.studentId)) {
      studentNameMap.set(attendance.studentId, attendance.studentName);
    }
  }
  for (const annotation of annotations) {
    if ((annotation as DashboardGrade).studentName && !studentNameMap.has(annotation.studentId)) {
      studentNameMap.set(annotation.studentId, (annotation as DashboardGrade).studentName!);
    }
  }

  const studentsByCourse = studentIds.map((sid) => {
    const studentGrades = grades.filter((g: DashboardGrade) => g.studentId === sid);
    const studentAttendances = attendances.filter((a: DashboardAttendance) => a.studentId === sid);
    const attPct = studentAttendances.length > 0
      ? Math.round((studentAttendances.filter((a: DashboardAttendance) => a.present).length / studentAttendances.length) * 100)
      : 0;
    const average = calculateAverageScore(studentGrades);
    const name = studentNameMap.get(sid) ?? `Estudiante #${sid}`;
    return { id: sid, attPct, average, avg: formatAverageScore(average), name };
  });

  const overallAverages = studentsByCourse
    .map((s) => s.average)
    .filter((avg): avg is number => avg !== null);
  const overallAverage = overallAverages.length > 0
    ? formatAverageScore(overallAverages.reduce((a, b) => a + b, 0) / overallAverages.length)
    : '—';

  const novedades: Array<{ type: 'success' | 'warning' | 'info'; text: string; time: string }> = [];
  annotations.slice(0, 3).forEach((a: { type: string; description: string; date: string }) => {
    novedades.push({
      type: a.type === 'POSITIVE' ? 'success' : 'warning',
      text: `Anotación: ${a.description}`,
      time: a.date ? new Date(a.date).toLocaleDateString('es-CL') : '',
    });
  });
  if (novedades.length === 0) {
    novedades.push({ type: 'info', text: 'Sin novedades recientes', time: '' });
  }

  if (loading) return <Loading size="lg" message="Cargando datos del apoderado..." />;
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
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Users /></span><span className="nav-label">Mis pupilos</span></button>
            <button className="sidebar-nav-item"><span className="nav-icon"><Icon.Academic /></span><span className="nav-label">Rendimiento</span></button>
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
            <h1 className="dashboard-title">Panel Apoderado</h1>
            <span className="dashboard-subtitle">
              {new Date().toLocaleDateString('es-CL', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })} &mdash; Bienvenido, {user?.nombre ? user.nombre.split(' ')[0] : 'Apoderado'}
            </span>
          </div>
        </header>

        <div className="dashboard-body">
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Pupilos a cargo</span>
                <span className="stat-value">{totalStudents}</span>
                <span className="stat-change positive">{totalStudents > 0 ? 'Activos' : 'Sin registro'}</span>
              </div>
              <div className="stat-icon-wrap stat-icon-blue"><Icon.Users /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Promedio general</span>
                <span className="stat-value">{overallAverage}</span>
                <span className="stat-change positive">
                  <span className="stat-change-icon"><Icon.TrendUp /></span>
                  Basado en notas reales
                </span>
              </div>
              <div className="stat-icon-wrap stat-icon-green"><Icon.BarChart /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Asistencia</span>
                <span className="stat-value">{attendanceRate}%</span>
                <span className="stat-change positive">{attendanceRate >= 80 ? 'Excelente' : attendanceRate >= 60 ? 'Regular' : 'Preocupante'}</span>
              </div>
              <div className="stat-icon-wrap stat-icon-teal"><Icon.Percent /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Anotaciones</span>
                <span className="stat-value">{totalAnnotations}</span>
                <span className="stat-change warning">{totalAnnotations > 0 ? 'Revisar' : 'Sin novedades'}</span>
              </div>
              <div className="stat-icon-wrap stat-icon-orange"><Icon.Bell /></div>
            </div>
          </div>

          <div className="content-grid">
            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Mis pupilos</h2>
                  <button className="dash-card-link">Ver detalle</button>
                </div>
                <table className="users-table">
                  <thead>
                    <tr>
                      <th>ESTUDIANTE #</th>
                      <th>PROMEDIO</th>
                      <th>ASISTENCIA</th>
                      <th>ESTADO</th>
                    </tr>
                  </thead>
                  <tbody>
                    {studentsByCourse.length > 0 ? studentsByCourse.map((s) => (
                      <tr key={s.id}>
                        <td><div className="user-cell"><div className="user-initials">{s.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}</div><span className="user-name">{s.name}</span></div></td>
                        <td><span className="role-badge role-docente">{s.avg}</span></td>
                        <td><span className="role-badge role-docente">{s.attPct}%</span></td>
                        <td><span className="status-badge status-activo">{s.attPct >= 80 ? 'Activo' : 'Precaución'}</span></td>
                      </tr>
                    )) : (
                      <tr><td colSpan={4} style={{ textAlign: 'center', padding: '1rem', color: '#666' }}>No hay estudiantes vinculados</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Novedades</h2>
                  <button className="dash-card-link">Ver todas</button>
                </div>
                <div className="activity-list">
                  {novedades.map((n, i) => (
                    <div key={i} className="activity-item">
                      <div className={`activity-dot dot-${n.type}`}>
                        {n.type === 'success' ? <Icon.CheckCircle /> : <Icon.AlertTriangle />}
                      </div>
                      <div className="activity-content">
                        <span className="activity-text">{n.text}</span>
                        <span className="activity-time">{n.time}</span>
                      </div>
                    </div>
                  ))}
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

export default GuardianDashboardPage;
