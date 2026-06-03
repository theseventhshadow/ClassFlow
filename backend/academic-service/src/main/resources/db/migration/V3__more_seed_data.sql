-- Academic Service - More Seed Data (V3)
-- Añadir más cursos y asignaturas
INSERT INTO courses (name, description, academic_year, active)
VALUES ('3º ESO', 'Curso 3º ESO', 2025, TRUE),
       ('4º ESO', 'Curso 4º ESO', 2025, TRUE),
       ('Bachillerato', 'Curso Bachillerato', 2025, TRUE);

INSERT INTO subjects (name, description, course_id, active)
VALUES ('Química', 'Química básica', (SELECT id FROM courses WHERE name='3º ESO' LIMIT 1), TRUE),
       ('Historia', 'Historia universal', (SELECT id FROM courses WHERE name='4º ESO' LIMIT 1), TRUE),
       ('Filosofía', 'Filosofía', (SELECT id FROM courses WHERE name='Bachillerato' LIMIT 1), TRUE);

-- Evaluaciones adicionales
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Parcial 2', 'Segundo parcial', 100.0, 40.0, CURRENT_DATE - INTERVAL '10 days', (SELECT id FROM subjects WHERE name='Química' LIMIT 1)),
       ('Recuperación', 'Recuperación final', 100.0, 20.0, CURRENT_DATE - INTERVAL '2 days', (SELECT id FROM subjects WHERE name='Historia' LIMIT 1));

-- Notas ejemplo para varios estudiantes
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (2, 72.0, 'Necesita repasar', (SELECT id FROM evaluations WHERE name='Parcial 2' LIMIT 1)),
       (3, 95.0, 'Excelente', (SELECT id FROM evaluations WHERE name='Recuperación' LIMIT 1)),
       (4, 66.5, 'Mejorable', (SELECT id FROM evaluations WHERE name='Parcial 2' LIMIT 1));
