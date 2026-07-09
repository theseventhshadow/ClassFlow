/**
 * Utilidades de cálculo sobre calificaciones.
 */

interface ScoredItem {
  score?: number | null;
}

/**
 * Promedio de las notas ya calificadas, excluyendo evaluaciones pendientes (score null/undefined).
 * Devuelve null si no hay ninguna nota calificada todavía.
 */
export function calculateAverageScore(grades: ScoredItem[]): number | null {
  const scored = grades.filter((grade): grade is { score: number } => grade.score !== null && grade.score !== undefined);
  if (scored.length === 0) {
    return null;
  }
  return scored.reduce((sum, grade) => sum + grade.score, 0) / scored.length;
}

/** Formatea un promedio para mostrar, o "—" si aún no hay notas calificadas. */
export function formatAverageScore(average: number | null): string {
  return average === null ? '—' : average.toFixed(1);
}
