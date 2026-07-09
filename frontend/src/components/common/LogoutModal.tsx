import React, { useEffect } from 'react';
import './LogoutModal.css';

interface LogoutModalProps {
  open: boolean;
  onCancel: () => void;
  onConfirm: () => void;
}

/**
 * Modal de confirmación de cierre de sesión, compartido por los 4 dashboards.
 * Cierra con Escape y usa role="dialog"/aria-modal para lectores de pantalla.
 */
export const LogoutModal: React.FC<LogoutModalProps> = ({ open, onCancel, onConfirm }) => {
  useEffect(() => {
    if (!open) return;

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onCancel();
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [open, onCancel]);

  if (!open) {
    return null;
  }

  return (
    <div className="logout-modal-overlay" onClick={onCancel}>
      <div
        className="logout-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="logout-modal-title"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="logout-modal-title" className="logout-modal-title">¿Cerrar sesión?</h2>
        <p className="logout-modal-text">
          Se cerrará tu sesión y deberás iniciar sesión nuevamente para acceder a la plataforma.
        </p>
        <div className="logout-modal-actions">
          <button type="button" className="logout-modal-btn logout-modal-btn--cancel" onClick={onCancel}>
            No, cancelar
          </button>
          <button type="button" className="logout-modal-btn logout-modal-btn--confirm" onClick={onConfirm} autoFocus>
            Sí, cerrar sesión
          </button>
        </div>
      </div>
    </div>
  );
};

export default LogoutModal;
