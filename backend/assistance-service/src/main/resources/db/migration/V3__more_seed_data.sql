-- Assistance Service - More Seed Data (V3)
-- Asistencias adicionales
INSERT INTO attendances (student_id, course_id, date, present, justification)
VALUES (3, 1, CURRENT_DATE - INTERVAL '3 days', TRUE, NULL),
       (4, 2, CURRENT_DATE - INTERVAL '4 days', FALSE, 'Cita médica'),
       (5, 1, CURRENT_DATE - INTERVAL '5 days', TRUE, NULL),
       (6, 2, CURRENT_DATE - INTERVAL '6 days', TRUE, NULL);

-- Anotaciones adicionales
INSERT INTO annotations (student_id, teacher_id, type, description, date, active)
VALUES (3, 2, 'NOTICE', 'Participa activamente en clase', CURRENT_TIMESTAMP - INTERVAL '1 day', TRUE),
       (5, 6, 'WARNING', 'Falta de entrega', CURRENT_TIMESTAMP - INTERVAL '7 days', TRUE);
