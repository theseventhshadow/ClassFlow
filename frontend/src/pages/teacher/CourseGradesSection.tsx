import React, { useState } from 'react';
import { DashboardSubject, DashboardEvaluation, DashboardGrade, gradeService } from '@services';
import { CourseRosterEntry, getErrorMessage } from '@utils';

interface Props {
  subjects: DashboardSubject[];
  evaluationsBySubjectId: Map<number, DashboardEvaluation[]>;
  gradesByEvaluationId: Map<number, DashboardGrade[]>;
  courseEvaluations: DashboardEvaluation[];
  roster: CourseRosterEntry[];
  refetch: () => void;
}

interface FormState {
  evaluationId: string;
  studentId: string;
  score: string;
  observations: string;
}

const EMPTY_FORM: FormState = { evaluationId: '', studentId: '', score: '', observations: '' };

export const CourseGradesSection: React.FC<Props> = ({
  subjects,
  evaluationsBySubjectId,
  gradesByEvaluationId,
  courseEvaluations,
  roster,
  refetch,
}) => {
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const hasEvaluations = courseEvaluations.length > 0;
  const hasRoster = roster.length > 0;
  const formDisabled = !hasEvaluations || !hasRoster;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.evaluationId || !form.studentId || !form.score.trim()) {
      setFormError('Evaluación, alumno y nota son obligatorios.');
      return;
    }
    const score = Number(form.score);
    if (Number.isNaN(score) || score < 0) {
      setFormError('La nota debe ser un número válido.');
      return;
    }

    setIsSubmitting(true);
    setFormError(null);

    try {
      await gradeService.createGrade({
        evaluationId: Number(form.evaluationId),
        studentId: Number(form.studentId),
        score,
        observations: form.observations.trim() || undefined,
      });
      setForm(EMPTY_FORM);
      refetch();
    } catch (err) {
      setFormError(getErrorMessage(err, 'Error al registrar la calificación'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="tp-card">
      <div className="tp-card-header">
        <h3>Calificaciones</h3>
      </div>

      <div className="tp-list">
        {courseEvaluations.length > 0 ? (
          courseEvaluations.map((ev) => {
            const subject = subjects.find((s) => s.id === ev.subjectId);
            const grades = gradesByEvaluationId.get(ev.id) ?? [];
            return (
              <div key={ev.id} className="tp-grade-eval-group">
                <div className="tp-grade-eval-header">
                  <span className="tp-grade-eval-name">{subject?.name ?? 'Asignatura'} — {ev.name}</span>
                  <span className="tp-grade-eval-date">{ev.date ? new Date(ev.date).toLocaleDateString('es-CL') : ''}</span>
                </div>
                {grades.length > 0 ? (
                  grades.map((g) => {
                    const student = roster.find((r) => r.studentId === g.studentId);
                    const name = g.studentName ?? student?.studentName ?? `Estudiante #${g.studentId}`;
                    return (
                      <div key={g.id} className="tp-list-item">
                        <span className="tp-list-name">{name}</span>
                        <span className="tp-badge tp-badge--active">{g.score ?? '—'}</span>
                        {g.observations && <span className="tp-annotation-note">{g.observations}</span>}
                      </div>
                    );
                  })
                ) : (
                  <p className="tp-empty-hint">Sin notas registradas para esta evaluación.</p>
                )}
              </div>
            );
          })
        ) : (
          <p className="tp-empty-hint">Aún no hay evaluaciones registradas en las asignaturas de este curso.</p>
        )}
      </div>

      <form className="tp-form" onSubmit={handleSubmit} noValidate>
        <h4>Agregar calificación</h4>

        {!hasRoster && (
          <p className="tp-empty-hint">No hay alumnos conocidos para este curso todavía. Se necesita al menos un registro de asistencia previo.</p>
        )}
        {hasRoster && !hasEvaluations && (
          <p className="tp-empty-hint">No se puede agregar una nota porque este curso no tiene evaluaciones registradas.</p>
        )}

        {formError && <p className="tp-form-error">{formError}</p>}

        <div className="tp-field">
          <label htmlFor="grade-evaluation">Evaluación</label>
          <select
            id="grade-evaluation"
            className="tp-select"
            value={form.evaluationId}
            disabled={formDisabled}
            onChange={(e) => setForm({ ...form, evaluationId: e.target.value })}
          >
            <option value="">Seleccionar evaluación…</option>
            {subjects.map((subject) => {
              const evals = evaluationsBySubjectId.get(subject.id) ?? [];
              if (evals.length === 0) return null;
              return (
                <optgroup key={subject.id} label={subject.name}>
                  {evals.map((ev) => (
                    <option key={ev.id} value={ev.id}>
                      {ev.name}
                    </option>
                  ))}
                </optgroup>
              );
            })}
          </select>
        </div>

        <div className="tp-field">
          <label htmlFor="grade-student">Alumno</label>
          <select
            id="grade-student"
            className="tp-select"
            value={form.studentId}
            disabled={formDisabled}
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
          <label htmlFor="grade-score">Nota</label>
          <input
            id="grade-score"
            type="number"
            step="0.1"
            min={0}
            className="tp-select"
            value={form.score}
            disabled={formDisabled}
            onChange={(e) => setForm({ ...form, score: e.target.value })}
          />
        </div>

        <div className="tp-field">
          <label htmlFor="grade-observations">Observaciones</label>
          <input
            id="grade-observations"
            type="text"
            className="tp-select"
            value={form.observations}
            disabled={formDisabled}
            onChange={(e) => setForm({ ...form, observations: e.target.value })}
          />
        </div>

        <button type="submit" className="tp-btn tp-btn--primary" disabled={formDisabled || isSubmitting}>
          {isSubmitting ? 'Guardando…' : 'Agregar calificación'}
        </button>
      </form>
    </div>
  );
};

export default CourseGradesSection;
