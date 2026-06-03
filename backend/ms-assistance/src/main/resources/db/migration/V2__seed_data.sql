-- Assistance Service - Seed Data
INSERT INTO attendances (student_id, course_id, date, present, justification)
VALUES (1, 1, CURRENT_DATE, TRUE, NULL),
       (2, 1, CURRENT_DATE - INTERVAL '1 day', FALSE, 'Enfermedad');

INSERT INTO annotations (student_id, teacher_id, type, description, date, active)
VALUES (1, NULL, 'NOTICE', 'Comportamiento ejemplar', CURRENT_TIMESTAMP, TRUE),
       (2, 2, 'WARNING', 'Retraso en tareas', CURRENT_TIMESTAMP - INTERVAL '2 days', TRUE);
