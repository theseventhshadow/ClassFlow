import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@context';
import { Loading, Error as ErrorState } from '@components/common';
import { useDashboardData } from '@hooks';
import { UserRole, authService, courseService } from '@services';
import { humanizeRole } from '@utils';
import './AdminDashboard.css';

type TableUser = {
  name: string;
  rol: string;
  rolClass: string;
  estado: string;
  estadoClass: string;
  acceso: string;
};

const ROL_LABEL: Record<UserRole, string> = {
  ADMINISTRATOR: 'Administrador',
  TEACHER: 'Docente',
  GUARDIAN: 'Apoderado',
  STUDENT: 'Estudiante',
};

const ROL_CLASS: Record<UserRole, string> = {
  ADMINISTRATOR: 'badge--administrador',
  TEACHER: 'badge--docente',
  GUARDIAN: 'badge--apoderado',
  STUDENT: 'badge--estudiante',
};

const navSections = [
  {
    label: 'PRINCIPAL',
    items: [{ icon: '▣', label: 'Dashboard', active: true, badge: null as number | null }],
  },
  {
    label: 'GESTIÓN',
    items: [
      { icon: '👥', label: 'Usuarios', active: false, badge: 4 as number | null },
      { icon: '📚', label: 'Gestión Académica', active: false, badge: null as number | null },
      { icon: '✔', label: 'Asistencia', active: false, badge: null as number | null },
      { icon: '💬', label: 'Mensajería', active: false, badge: 7 as number | null },
    ],
  },
  {
    label: 'REPORTES',
    items: [
      { icon: '📄', label: 'Informes', active: false, badge: null as number | null },
      { icon: '📈', label: 'Rendimiento', active: false, badge: null as number | null },
    ],
  },
  {
    label: 'SISTEMA',
    items: [{ icon: '⚙', label: 'Configuración', active: false, badge: null as number | null }],
  },
];

type FormState = {
  nombres: string;
  apellidos: string;
  email: string;
  password: string;
  rol: UserRole;
  idNumber: string;
};

const EMPTY_FORM: FormState = {
  nombres: '',
  apellidos: '',
  email: '',
  password: '',
  rol: 'TEACHER',
  idNumber: '',
};

export const AdminDashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { stats, users, courseAttendance, activity, alerts, loading, error, refetch } = useDashboardData();

  const [showModal, setShowModal] = useState(false);
  const [showCourseModal, setShowCourseModal] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [localUsers, setLocalUsers] = useState<TableUser[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isCreatingCourse, setIsCreatingCourse] = useState(false);

  const [courseForm, setCourseForm] = useState<{ name: string; description: string; academicYear: string }>({
    name: '',
    description: '',
    academicYear: '',
  });
  const [courseFormError, setCourseFormError] = useState<string | null>(null);

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

  const initials = user?.nombre
    ? user.nombre
        .split(' ')
        .map((name: string) => name[0])
        .join('')
        .slice(0, 2)
        .toUpperCase()
    : 'AD';

  const openModal = () => {
    setForm(EMPTY_FORM);
    setFormError(null);
    setShowModal(true);
  };

  const openCourseModal = () => {
    setCourseForm({ name: '', description: '', academicYear: '' });
    setCourseFormError(null);
    setShowCourseModal(true);
  };

  const closeModal = () => setShowModal(false);

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value } as FormState));
  };

  const handleCourseFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target as HTMLInputElement;
    setCourseForm((prev) => ({ ...prev, [name]: value }));
  };
  const mapRoleToBackend = (role: UserRole): string => role;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.nombres.trim() || !form.apellidos.trim() || !form.email.trim() || !form.password.trim() || !form.idNumber.trim()) {
      setFormError('Todos los campos son obligatorios.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(form.email)) {
      setFormError('Ingresa un correo electrónico válido.');
      return;
    }

    if (form.password.length < 6) {
      setFormError('La contraseña debe tener al menos 6 caracteres.');
      return;
    }

    const rutRegex = /^\d{1,2}\.\d{3}\.\d{3}-[0-9kK]$/;
    if (!rutRegex.test(form.idNumber)) {
      setFormError('El RUT debe tener formato: 12.345.678-9');
      return;
    }

    setIsSubmitting(true);
    setFormError(null);

    try {
      const backendRole = mapRoleToBackend(form.rol);

      await authService.register({
        firstName: form.nombres.trim(),
        lastName: form.apellidos.trim(),
        idNumber: form.idNumber.trim(),
        email: form.email.trim(),
        password: form.password,
        role: backendRole,
      });

      const newUser: TableUser = {
        name: `${form.nombres.trim()} ${form.apellidos.trim()}`,
        rol: ROL_LABEL[form.rol],
        rolClass: ROL_CLASS[form.rol],
        estado: 'Activo',
        estadoClass: 'badge--activo',
        acceso: 'Ahora',
      };

      setLocalUsers((prev) => [newUser, ...prev]);
      closeModal();
      await refetch();
    } catch (err: any) {
      const msg = err?.details?.message ?? err?.message ?? 'Error al crear el usuario';
      setFormError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCreateCourse = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!courseForm.name.trim() || !courseForm.academicYear.trim()) {
      setCourseFormError('Nombre y año académico son obligatorios.');
      return;
    }

    const year = Number(courseForm.academicYear);
    if (!Number.isInteger(year) || year < 2000 || year > 2100) {
      setCourseFormError('Año académico inválido.');
      return;
    }

    setIsCreatingCourse(true);
    setCourseFormError(null);

    try {
      await courseService.createCourse({
        name: courseForm.name.trim(),
        description: courseForm.description.trim() || undefined,
        academicYear: year,
      });

      setShowCourseModal(false);
      await refetch();
    } catch (err: any) {
      const msg = err?.response?.data?.message ?? err?.message ?? 'Error al crear el curso';
      setCourseFormError(msg);
    } finally {
      setIsCreatingCourse(false);
    }
  };

  const mergedUsers = [...localUsers, ...users];

  return (
    <div className="admin-page">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-brand">
          <span className="admin-sidebar-logo-box">CF</span>
          <div>
            <div className="admin-brand-name">ClassFlow</div>
            <div className="admin-brand-sub">Panel Administrativo</div>
          </div>
        </div>

        <nav className="admin-nav">
          {navSections.map((section) => (
            <div key={section.label} className="admin-nav-section">
              <span className="admin-nav-section-label">{section.label}</span>
              {section.items.map((item) => (
                <a
                  key={item.label}
                  href="#"
                  className={`admin-nav-item${item.active ? ' admin-nav-item--active' : ''}`}
                >
                  <span className="admin-nav-icon">{item.icon}</span>
                  <span>{item.label}</span>
                  {item.badge !== null && <span className="admin-nav-badge">{item.badge}</span>}
                </a>
              ))}
            </div>
          ))}
        </nav>

        <div className="admin-sidebar-footer">
          <div className="admin-sidebar-user">
            <div className="admin-sidebar-avatar">{initials}</div>
            <div>
              <div className="admin-sidebar-user-name">{user?.nombre ?? 'Usuario'}</div>
              <div className="admin-sidebar-user-role">{humanizeRole(user?.rol) || 'Usuario'}</div>
            </div>
          </div>
          <button className="admin-logout-btn" onClick={() => setShowLogoutModal(true)} title="Cerrar sesión">
            ⏻
          </button>
        </div>
      </aside>

      <main className="admin-main">
        <header className="admin-header">
          <div>
            <h1 className="admin-title">Panel de Administración</h1>
            <p className="admin-subtitle">
              {new Date().toLocaleDateString('es-CL', {
                weekday: 'long',
                day: 'numeric',
                month: 'long',
                year: 'numeric',
              })}
            </p>
          </div>
          <div className="admin-header-actions">
            <button className="admin-btn admin-btn--secondary">⬇ Exportar reporte</button>
            <button className="admin-btn admin-btn--secondary" onClick={openCourseModal}>
              + Nuevo curso
            </button>
            <button className="admin-btn admin-btn--primary" onClick={openModal}>
              + Nuevo usuario
            </button>
          </div>
        </header>

        {error && <ErrorState message={error} onRetry={refetch} />}

        {loading ? (
          <Loading size="lg" message="Cargando datos reales del BFF..." />
        ) : (
          <>
            <section className="admin-stats">
              {stats.map((card) => (
                <div key={card.label} className="admin-stat-card">
                  <p className="admin-stat-value">{card.value}</p>
                  <p className="admin-stat-label">{card.label}</p>
                  <p
                    className={`admin-stat-trend ${
                      card.trendUp === true
                        ? 'trend--up'
                        : card.trendUp === false
                          ? 'trend--down'
                          : 'trend--neutral'
                    }`}
                  >
                    {card.trend}
                  </p>
                </div>
              ))}
            </section>

            <div className="admin-mid-row">
              <section className="admin-card">
                <div className="admin-card-header">
                  <h2 className="admin-card-title">Gestión de usuarios</h2>
                  <a href="#" className="admin-card-link">
                    Ver todos →
                  </a>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Rol</th>
                      <th>Estado</th>
                      <th>Último acceso</th>
                    </tr>
                  </thead>
                  <tbody>
                    {mergedUsers.length > 0 ? (
                      mergedUsers.map((u, index) => (
                        <tr key={`${u.name}-${index}`}>
                          <td>{u.name}</td>
                          <td>
                            <span className={`admin-badge ${u.rolClass}`}>{u.rol}</span>
                          </td>
                          <td>
                            <span className={`admin-badge ${u.estadoClass}`}>{u.estado}</span>
                            {u.estado === 'Pendiente' && <button className="admin-approve-btn">Aprobar</button>}
                          </td>
                          <td className="admin-table-muted">{u.acceso}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={4} className="admin-table-muted">
                          No hay usuarios recientes para mostrar.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </section>

              <section className="admin-card">
                <div className="admin-card-header">
                  <h2 className="admin-card-title">Asistencia por curso</h2>
                  <a href="#" className="admin-card-link">
                    Detalles →
                  </a>
                </div>
                <div className="admin-attendance-list">
                  {courseAttendance.length > 0 ? (
                    courseAttendance.map((course) => (
                      <div key={course.name} className="admin-attendance-row">
                        <span className="admin-attendance-name">{course.name}</span>
                        <div className="admin-progress-bar">
                          <div
                            className={`admin-progress-fill ${
                              course.pct >= 80 ? 'fill--good' : course.pct >= 70 ? 'fill--warn' : 'fill--bad'
                            }`}
                            style={{ width: `${Math.max(0, Math.min(100, course.pct))}%` }}
                          />
                        </div>
                        <span className="admin-attendance-pct">{course.pct}%</span>
                      </div>
                    ))
                  ) : (
                    <p className="admin-table-muted">No hay asistencia suficiente para mostrar cursos.</p>
                  )}
                </div>
              </section>
            </div>

            <div className="admin-bottom-row">
              <section className="admin-card">
                <div className="admin-card-header">
                  <h2 className="admin-card-title">Actividad reciente</h2>
                  <a href="#" className="admin-card-link">
                    Ver todos →
                  </a>
                </div>
                <ul className="admin-activity-list">
                  {activity.length > 0 ? (
                    activity.map((item, index) => (
                      <li key={`${item.text}-${index}`} className="admin-activity-item">
                        <span className={`admin-activity-dot dot--${item.type}`} />
                        <div className="admin-activity-body">
                          <p className="admin-activity-text">{item.text}</p>
                          <p className="admin-activity-time">{item.time}</p>
                        </div>
                      </li>
                    ))
                  ) : (
                    <li className="admin-activity-item">
                      <div className="admin-activity-body">
                        <p className="admin-activity-text">No hay actividad reciente registrada.</p>
                      </div>
                    </li>
                  )}
                </ul>
              </section>

              <section className="admin-card">
                <div className="admin-card-header">
                  <h2 className="admin-card-title">Alertas de gestionar</h2>
                  <a href="#" className="admin-card-link">
                    Gestionar →
                  </a>
                </div>
                <ul className="admin-alert-list">
                  {alerts.length > 0 ? (
                    alerts.map((alert) => (
                      <li key={alert.id} className="admin-alert-item">
                        <span className="admin-alert-icon">⚠</span>
                        <p className="admin-alert-text">{alert.text}</p>
                        <a href="#" className="admin-alert-link">
                          Ver
                        </a>
                      </li>
                    ))
                  ) : (
                    <li className="admin-alert-item">
                      <span className="admin-alert-icon">✓</span>
                      <p className="admin-alert-text">No hay alertas pendientes.</p>
                    </li>
                  )}
                </ul>
              </section>
            </div>
          </>
        )}
      </main>

      {showModal && (
        <div className="admin-modal-overlay" onClick={closeModal}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <div className="admin-modal-header">
              <h2 className="admin-modal-title">Nuevo usuario</h2>
              <button className="admin-modal-close" onClick={closeModal}>
                ✕
              </button>
            </div>

            <form className="admin-modal-form" onSubmit={handleSubmit} noValidate>
              <div className="admin-form-row">
                <div className="admin-form-group">
                  <label className="admin-form-label" htmlFor="nombres">
                    Nombres
                  </label>
                  <input
                    id="nombres"
                    name="nombres"
                    type="text"
                    className="admin-form-input"
                    placeholder="Ej: Juan"
                    value={form.nombres}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                    autoComplete="off"
                  />
                </div>
                <div className="admin-form-group">
                  <label className="admin-form-label" htmlFor="apellidos">
                    Apellidos
                  </label>
                  <input
                    id="apellidos"
                    name="apellidos"
                    type="text"
                    className="admin-form-input"
                    placeholder="Ej: Pérez"
                    value={form.apellidos}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                    autoComplete="off"
                  />
                </div>
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="idNumber">
                  RUT
                </label>
                <input
                  id="idNumber"
                  name="idNumber"
                  type="text"
                  className="admin-form-input"
                  placeholder="12.345.678-9"
                  value={form.idNumber}
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                  autoComplete="off"
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="email">
                  Correo electrónico
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  className="admin-form-input"
                  placeholder="correo@classflow.cl"
                  value={form.email}
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                  autoComplete="off"
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="password">
                  Contraseña
                </label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  className="admin-form-input"
                  placeholder="Mínimo 6 caracteres"
                  value={form.password}
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                  autoComplete="new-password"
                />
              </div>

              <div className="admin-form-row">
                <div className="admin-form-group">
                  <label className="admin-form-label" htmlFor="rol">
                    Rol
                  </label>
                  <select
                    id="rol"
                    name="rol"
                    className="admin-form-select"
                    value={form.rol}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  >
                    <option value="ADMINISTRATOR">Administrador</option>
                    <option value="TEACHER">Docente</option>
                    <option value="GUARDIAN">Apoderado</option>
                    <option value="STUDENT">Estudiante</option>
                  </select>
                </div>
              </div>

              {formError && <p className="admin-form-error">{formError}</p>}

              <div className="admin-modal-actions">
                <button type="button" className="admin-btn admin-btn--secondary" onClick={closeModal}>
                  Cancelar
                </button>
                <button type="submit" className="admin-btn admin-btn--primary" disabled={isSubmitting}>
                  {isSubmitting ? 'Creando...' : 'Crear usuario'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showCourseModal && (
        <div className="admin-modal-overlay" onClick={() => setShowCourseModal(false)}>
          <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
            <div className="admin-modal-header">
              <h2 className="admin-modal-title">Nuevo curso</h2>
              <button className="admin-modal-close" onClick={() => setShowCourseModal(false)}>
                ✕
              </button>
            </div>

            <form className="admin-modal-form" onSubmit={handleCreateCourse} noValidate>
              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="courseName">
                  Nombre del curso
                </label>
                <input
                  id="courseName"
                  name="name"
                  type="text"
                  className="admin-form-input"
                  placeholder="Ej: Matemáticas 1° Básico"
                  value={courseForm.name}
                  onChange={handleCourseFormChange}
                  disabled={isCreatingCourse}
                  autoComplete="off"
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="courseDescription">
                  Descripción
                </label>
                <textarea
                  id="courseDescription"
                  name="description"
                  className="admin-form-input"
                  placeholder="Descripción opcional"
                  value={courseForm.description}
                  onChange={handleCourseFormChange}
                  disabled={isCreatingCourse}
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label" htmlFor="academicYear">
                  Año académico
                </label>
                <input
                  id="academicYear"
                  name="academicYear"
                  type="number"
                  className="admin-form-input"
                  placeholder="Ej: 2026"
                  value={courseForm.academicYear}
                  onChange={handleCourseFormChange}
                  disabled={isCreatingCourse}
                  autoComplete="off"
                />
              </div>

              {courseFormError && <p className="admin-form-error">{courseFormError}</p>}

              <div className="admin-modal-actions">
                <button type="button" className="admin-btn admin-btn--secondary" onClick={() => setShowCourseModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="admin-btn admin-btn--primary" disabled={isCreatingCourse}>
                  {isCreatingCourse ? 'Creando...' : 'Crear curso'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

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
