import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DashboardPage.css';
import { useAuth } from '@context';

const mockActivity = [
  { id: 1, type: 'success', text: 'Su hijo(a) asistió a todas las clases', time: 'Hoy' },
  { id: 2, type: 'warning', text: 'Anotación negativa registrada', time: 'Ayer' },
  { id: 3, type: 'info', text: 'Nuevo material disponible en plataforma', time: 'Hace 2 días' },
];

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
};

function humanizeRole(role?: string): string {
  switch (role) {
    case 'ADMINISTRATOR':
    case 'ADMIN':
      return 'Administrador';
    case 'TEACHER':
    case 'DOCENTE':
      return 'Docente';
    case 'GUARDIAN':
    case 'APODERADO':
      return 'Apoderado';
    case 'STUDENT':
    case 'ESTUDIANTE':
      return 'Estudiante';
    default:
      return role ?? '';
  }
}

export const GuardianDashboardPage: React.FC = () => {
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const initials = user?.nombre
    ? user.nombre.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'AP';
  const displayName = user?.nombre ?? 'Apoderado';
  const displayEmail = user?.email ?? '';
  const displayRole = humanizeRole(user?.rol);

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
            className="sidebar-logout-btn"
            onClick={() => setShowLogoutModal(true)}
            title="Cerrar sesión"
            style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontSize: '1.2rem', padding: '0.5rem' }}
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
                <span className="stat-value">2</span>
                <span className="stat-change positive">Activos</span>
              </div>
              <div className="stat-icon-wrap stat-icon-blue"><Icon.Users /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Promedio general</span>
                <span className="stat-value">6.0</span>
                <span className="stat-change positive">
                  <span className="stat-change-icon"><Icon.TrendUp /></span>
                  +0.2 este mes
                </span>
              </div>
              <div className="stat-icon-wrap stat-icon-green"><Icon.BarChart /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Asistencia</span>
                <span className="stat-value">94%</span>
                <span className="stat-change positive">Excelente</span>
              </div>
              <div className="stat-icon-wrap stat-icon-teal"><Icon.Percent /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Anotaciones</span>
                <span className="stat-value">1</span>
                <span className="stat-change warning">Revisar</span>
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
                      <th>NOMBRE</th>
                      <th>CURSO</th>
                      <th>ASISTENCIA</th>
                      <th>ESTADO</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td><div className="user-cell"><div className="user-initials">JP</div><span className="user-name">Juan Pérez</span></div></td>
                      <td>1° Medio A</td>
                      <td><span className="role-badge role-docente">96%</span></td>
                      <td><span className="status-badge status-activo">Activo</span></td>
                    </tr>
                    <tr>
                      <td><div className="user-cell"><div className="user-initials">MP</div><span className="user-name">María Pérez</span></div></td>
                      <td>3° Básico C</td>
                      <td><span className="role-badge role-docente">92%</span></td>
                      <td><span className="status-badge status-activo">Activo</span></td>
                    </tr>
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
                  {mockActivity.map((a) => (
                    <div key={a.id} className="activity-item">
                      <div className={`activity-dot dot-${a.type}`}>
                        {a.type === 'success' ? <Icon.CheckCircle /> : <Icon.AlertTriangle />}
                      </div>
                      <div className="activity-content">
                        <span className="activity-text">{a.text}</span>
                        <span className="activity-time">{a.time}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {showLogoutModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', zIndex: 9999,
        }}>
          <div style={{
            backgroundColor: 'white', borderRadius: '8px', padding: '2rem',
            minWidth: '300px', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', textAlign: 'center',
          }}>
            <h2 style={{ marginTop: 0, color: '#333' }}>¿Cerrar sesión?</h2>
            <p style={{ color: '#666', marginBottom: '1.5rem' }}>
              Se cerrará tu sesión y deberás iniciar sesión nuevamente para acceder a la plataforma.
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
              <button onClick={handleLogoutCancel}
                style={{ padding: '0.75rem 1.5rem', backgroundColor: '#f0f0f0', border: '1px solid #ddd', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem', fontWeight: '500' }}>
                No, cancelar
              </button>
              <button onClick={handleLogout}
                style={{ padding: '0.75rem 1.5rem', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem', fontWeight: '500' }}>
                Sí, cerrar sesión
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GuardianDashboardPage;
