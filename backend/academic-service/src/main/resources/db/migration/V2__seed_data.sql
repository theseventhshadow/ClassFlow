-- Academic Service - Seed Data
-- Courses and subjects
INSERT INTO courses (name, description, academic_year, active)
VALUES ('1º ESO', 'Curso 1º ESO', 2025, TRUE),
       ('2º ESO', 'Curso 2º ESO', 2025, TRUE);

INSERT INTO subjects (name, description, course_id, active)
VALUES ('Matemáticas', 'Matemáticas básicas', (SELECT id FROM courses WHERE name='1º ESO' LIMIT 1), TRUE),
       ('Física', 'Física introductoria', (SELECT id FROM courses WHERE name='2º ESO' LIMIT 1), TRUE);

-- Evaluations and grades (ejemplos demostrativos)
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Parcial 1', 'Primer parcial', 100.0, 40.0, CURRENT_DATE, (SELECT id FROM subjects WHERE name='Matemáticas' LIMIT 1));

INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (1, 85.5, 'Buen rendimiento', (SELECT id FROM evaluations WHERE name='Parcial 1' LIMIT 1));
