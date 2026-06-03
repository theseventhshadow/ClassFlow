import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DashboardPage.css';
import { useAuth } from '@context';

// ── Data ───────────────────────────────────────────────────────────────────────
const mockUsers = [
  { id: 1, name: 'Elena Arenas', role: 'Docente', status: 'Activo', lastAccess: 'Hoy' },
  { id: 2, name: 'María González', role: 'Apoderado', status: 'Activo', lastAccess: 'Hoy' },
  { id: 3, name: 'Joaquín Neira', role: 'Estudiante', status: 'Inactivo', lastAccess: 'Ayer 12:28' },
  { id: 4, name: 'Carmen Zúñiga', role: 'Apoderado', status: 'Activo', lastAccess: 'Hace 3 días 07:35' },
  { id: 5, name: 'Pedro Soto', role: 'Apoderado', status: 'Inactivo', lastAccess: 'Hace 3 días' },
];

const mockAttendance = [
  { course: '1° Medio A', percentage: 52 },
  { course: '1° Básico 6', percentage: 65 },
  { course: '2° Básico 1', percentage: 78 },
  { course: '3° Básico 3', percentage: 55 },
  { course: 'Básico 6', percentage: 67 },
];

const mockActivity = [
  { id: 1, type: 'success', text: 'Usuario Carmen Díaz clasificado como Docente', time: 'Hace 31 min' },
  { id: 2, type: 'warning', text: 'Asistencia crítica en 1° Básico 6 (31%)', time: 'Hace 1h' },
  { id: 3, type: 'info', text: 'Informe mensual generado automáticamente', time: 'Hace 2h' },
  { id: 4, type: 'danger', text: 'Acceso fallido de usuario desconocido', time: 'Hace 3h' },
];

const mockAlerts = [
  { id: 1, type: 'warning', text: 'Estudiante con 3+ inasistencias consecutivas', detail: 'Hace 2h' },
  { id: 2, type: 'info', text: '2 anotaciones negativas pendientes de revisión', detail: 'Hace 4h' },
  { id: 3, type: 'info', text: '3 mensajes sin respuesta de apoderados', detail: 'Hace 6h' },
  { id: 4, type: 'danger', text: 'Intento de acceso no autorizado registrado', detail: 'Hace 8h' },
];

// ── SVG Icons ──────────────────────────────────────────────────────────────────
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

// ── Helpers ────────────────────────────────────────────────────────────────────
function getRoleBadgeClass(role: string): string {
  const map: Record<string, string> = {
    Docente: 'role-badge role-docente',
    Apoderado: 'role-badge role-apoderado',
    Estudiante: 'role-badge role-estudiante',
  };
  return map[role] ?? 'role-badge';
}

function getStatusBadgeClass(status: string): string {
  return status === 'Activo' ? 'status-badge status-activo' : 'status-badge status-inactivo';
}

function getProgressClass(pct: number): string {
  if (pct < 60) return 'progress-bar-fill progress-low';
  if (pct < 75) return 'progress-bar-fill progress-mid';
  return 'progress-bar-fill progress-high';
}

function humanizeRole(role?: string): string {
  switch (role) {
    case 'ADMINISTRADOR':
    case 'ADMIN':
      return 'Administrador';
    case 'DOCENTE':
    case 'TEACHER':
      return 'Docente';
    case 'APODERADO':
    case 'GUARDIAN':
      return 'Apoderado';
    case 'ESTUDIANTE':
    case 'STUDENT':
      return 'Estudiante';
    default:
      return role ?? '';
  }
}

const activityIconMap: Record<string, React.ReactNode> = {
  success: <Icon.CheckCircle />,
  warning: <Icon.AlertTriangle />,
  info: <Icon.FileDown />,
  danger: <Icon.Lock />,
};

const alertIconMap: Record<string, React.ReactNode> = {
  warning: <Icon.AlertTriangle />,
  info: <Icon.Edit />,
  danger: <Icon.Lock />,
};

const alertIconForId: Record<number, React.ReactNode> = {
  1: <Icon.AlertTriangle />,
  2: <Icon.Edit />,
  3: <Icon.Mail />,
  4: <Icon.Lock />,
};

// ── Nav config ─────────────────────────────────────────────────────────────────
const navSections = [
  {
    section: 'GESTIÓN',
    items: [
      { label: 'Dashboard', icon: <Icon.Grid />, badge: null },
      { label: 'Usuarios', icon: <Icon.Users />, badge: '1' },
      { label: 'Gestión Académica', icon: <Icon.Academic />, badge: null },
      { label: 'Asistencia', icon: <Icon.Check />, badge: null },
      { label: 'Mensajería', icon: <Icon.Chat />, badge: '3' },
    ],
  },
  {
    section: 'REPORTES',
    items: [
      { label: 'Informes', icon: <Icon.FileText />, badge: null },
      { label: 'Rendimiento', icon: <Icon.BarChart />, badge: null },
    ],
  },
  {
    section: 'SISTEMA',
    items: [{ label: 'Configuración', icon: <Icon.Settings />, badge: null }],
  },
];

// ── Component ──────────────────────────────────────────────────────────────────
export const DashboardPage: React.FC = () => {
  const [activeNav, setActiveNav] = useState('Dashboard');
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const initials = user?.nombre
    ? user.nombre.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'US';
  const displayName = user?.nombre ?? 'Usuario';
  const displayEmail = user?.email ?? '';
  const displayRole = humanizeRole(user?.rol);

  const handleLogout = () => {
    logout();
    // Limpiar completamente el localStorage y sessionStorage
    localStorage.clear();
    sessionStorage.clear();
    // Limpiar cookies si existen (por si hay cookies de sesión)
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
      {/* ── Sidebar ─────────────────────────────────────────────────────────── */}
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
          {navSections.map(({ section, items }) => (
            <div key={section}>
              <div className="sidebar-section-label">{section}</div>
              {items.map((item) => (
                <button
                  key={item.label}
                  className={`sidebar-nav-item${activeNav === item.label ? ' active' : ''}`}
                  onClick={() => setActiveNav(item.label)}
                >
                  <span className="nav-icon">{item.icon}</span>
                  <span className="nav-label">{item.label}</span>
                  {item.badge && <span className="nav-badge">{item.badge}</span>}
                </button>
              ))}
            </div>
          ))}
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

      {/* ── Main ────────────────────────────────────────────────────────────── */}
      <div className="dashboard-main">
        {/* Header */}
        <header className="dashboard-header">
          <div className="dashboard-header-left">
            <h1 className="dashboard-title">Mi Dashboard</h1>
            <span className="dashboard-subtitle">
              {new Date().toLocaleDateString('es-CL', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })} &mdash; Bienvenido, {user?.nombre ? user.nombre.split(' ')[0] : 'Usuario'}
            </span>
          </div>
          <div className="dashboard-actions">
            <button className="btn-outline">
              <span className="btn-icon"><Icon.Download /></span>
              Exportar reporte
            </button>
          </div>
        </header>

        {/* Body */}
        <div className="dashboard-body">
          {/* Stats */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Total alumnos</span>
                <span className="stat-value">312</span>
                <span className="stat-change positive">
                  <span className="stat-change-icon"><Icon.TrendUp /></span>
                  +3% este mes
                </span>
              </div>
              <div className="stat-icon-wrap stat-icon-blue"><Icon.UserGroup /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Cursos activos</span>
                <span className="stat-value">18</span>
                <span className="stat-change neutral">Activo el día</span>
              </div>
              <div className="stat-icon-wrap stat-icon-green"><Icon.Book /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Asistencia</span>
                <span className="stat-value">88%</span>
                <span className="stat-change positive">
                  <span className="stat-change-icon"><Icon.TrendUp /></span>
                  +2% semana ant.
                </span>
              </div>
              <div className="stat-icon-wrap stat-icon-teal"><Icon.Percent /></div>
            </div>

            <div className="stat-card">
              <div className="stat-card-left">
                <span className="stat-label">Alertas</span>
                <span className="stat-value">7</span>
                <span className="stat-change warning">Sin gestionar</span>
              </div>
              <div className="stat-icon-wrap stat-icon-orange"><Icon.Bell /></div>
            </div>
          </div>

          {/* Content grid */}
          <div className="content-grid">
            {/* Left column */}
            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Gestión de usuarios</h2>
                  <button className="dash-card-link">Ver todos</button>
                </div>
                <table className="users-table">
                  <thead>
                    <tr>
                      <th>NOMBRE</th>
                      <th>ROL</th>
                      <th>ESTADO</th>
                      <th>ÚLTIMO ACCESO</th>
                    </tr>
                  </thead>
                  <tbody>
                    {mockUsers.map((u) => (
                      <tr key={u.id}>
                        <td>
                          <div className="user-cell">
                            <div className="user-initials">{u.name.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
                            <span className="user-name">{u.name}</span>
                          </div>
                        </td>
                        <td><span className={getRoleBadgeClass(u.role)}>{u.role}</span></td>
                        <td><span className={getStatusBadgeClass(u.status)}>{u.status}</span></td>
                        <td><span className="access-time">{u.lastAccess}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Actividad reciente</h2>
                  <button className="dash-card-link">Ver todos</button>
                </div>
                <div className="activity-list">
                  {mockActivity.map((a) => (
                    <div key={a.id} className="activity-item">
                      <div className={`activity-dot dot-${a.type}`}>
                        {activityIconMap[a.type]}
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

            {/* Right column */}
            <div className="content-col">
              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Asistencia por curso</h2>
                  <button className="dash-card-link">Ver detalle</button>
                </div>
                <div className="attendance-list">
                  {mockAttendance.map((a) => (
                    <div key={a.course} className="attendance-item">
                      <div className="attendance-item-top">
                        <span className="attendance-course">{a.course}</span>
                        <span className="attendance-pct">{a.percentage}%</span>
                      </div>
                      <div className="progress-bar-track">
                        <div
                          className={getProgressClass(a.percentage)}
                          style={{ width: `${a.percentage}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="dash-card">
                <div className="dash-card-header">
                  <h2 className="dash-card-title">Alertas sin gestionar</h2>
                  <button className="dash-card-link">Gestionar</button>
                </div>
                <div className="alerts-list">
                  {mockAlerts.map((a) => (
                    <div key={a.id} className="alert-item">
                      <div className={`alert-icon alert-${a.type}`}>
                        {alertIconForId[a.id] ?? alertIconMap[a.type]}
                      </div>
                      <div className="alert-content">
                        <span className="alert-text">{a.text}</span>
                        <span className="alert-detail">{a.detail}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* ── Modal de confirmación de cierre de sesión ── */}
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
                onClick={handleLogoutCancel}
                style={{
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#f0f0f0',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '1rem',
                  fontWeight: '500',
                }}
              >
                No, cancelar
              </button>
              <button
                onClick={handleLogout}
                style={{
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#e74c3c',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '1rem',
                  fontWeight: '500',
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

export default DashboardPage;
