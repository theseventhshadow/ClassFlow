import React, { useState } from 'react';
import { DashboardAnnotation, AnnotationType, annotationService } from '@services';
import { CourseRosterEntry, getErrorMessage } from '@utils';

interface Props {
  roster: CourseRosterEntry[];
  annotations: DashboardAnnotation[];
  teacherId: number;
  refetch: () => void;
}

interface FormState {
  studentId: string;
  type: AnnotationType;
  description: string;
}

const EMPTY_FORM: FormState = { studentId: '', type: 'POSITIVE', description: '' };

export const CourseAnnotationsSection: React.FC<Props> = ({ roster, annotations, teacherId, refetch }) => {
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const hasRoster = roster.length > 0;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.studentId || !form.description.trim()) {
      setFormError('Alumno y descripción son obligatorios.');
      return;
    }

    setIsSubmitting(true);
    setFormError(null);

    try {
      await annotationService.createAnnotation({
        studentId: Number(form.studentId),
        teacherId,
        type: form.type,
        description: form.description.trim(),
      });
      setForm(EMPTY_FORM);
      refetch();
    } catch (err) {
      setFormError(getErrorMessage(err, 'Error al registrar la anotación'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="tp-card">
      <div className="tp-card-header">
        <h3>Anotaciones</h3>
      </div>

      <div className="tp-list">
        {annotations.length > 0 ? (
          annotations.map((ann) => {
            const student = roster.find((r) => r.studentId === ann.studentId);
            const name = ann.studentName ?? student?.studentName ?? `Estudiante #${ann.studentId}`;
            return (
              <div key={ann.id} className="tp-list-item tp-list-item--annotation">
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
          })
        ) : (
          <p className="tp-empty-hint">Sin anotaciones registradas para este curso.</p>
        )}
      </div>

      <form className="tp-form" onSubmit={handleSubmit} noValidate>
        <h4>Agregar anotación</h4>

        {!hasRoster && (
          <p className="tp-empty-hint">No hay alumnos conocidos para este curso todavía. Se necesita al menos un registro de asistencia previo.</p>
        )}

        {formError && <p className="tp-form-error">{formError}</p>}

        <div className="tp-field">
          <label htmlFor="annotation-student">Alumno</label>
          <select
            id="annotation-student"
            className="tp-select"
            value={form.studentId}
            disabled={!hasRoster}
            onChange={(e) => setForm({ ...form, studentId: e.target.value })}
          >
            <option value="">Seleccionar alumno…</option>
            {roster.map((r) => (
              <option key={r.studentId} value={r.studentId}>
                {r.studentName}
              </option>
            ))}
          </select>
        </div>

        <div className="tp-field">
          <label htmlFor="annotation-type">Tipo</label>
          <select
            id="annotation-type"
            className="tp-select"
            value={form.type}
            disabled={!hasRoster}
            onChange={(e) => setForm({ ...form, type: e.target.value as AnnotationType })}
          >
            <option value="POSITIVE">Positiva</option>
            <option value="NEGATIVE">Negativa</option>
          </select>
        </div>

        <div className="tp-field">
          <label htmlFor="annotation-description">Descripción</label>
          <textarea
            id="annotation-description"
            className="tp-select"
            rows={3}
            value={form.description}
            disabled={!hasRoster}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </div>

        <button type="submit" className="tp-btn tp-btn--primary" disabled={!hasRoster || isSubmitting}>
          {isSubmitting ? 'Guardando…' : 'Agregar anotación'}
        </button>
      </form>
    </div>
  );
};

export default CourseAnnotationsSection;
