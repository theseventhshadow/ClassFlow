-- Message Service - More Seed Data (V3)
INSERT INTO messages (sender_id, receiver_id, subject, body, read, sent_at)
VALUES
((SELECT id FROM messages LIMIT 1), 3, 'Recordatorio', 'Recuerda entregar el proyecto.', FALSE, CURRENT_TIMESTAMP - INTERVAL '2 days'),
 (2, 4, 'Consulta', 'Consulta sobre la clase de hoy.', FALSE, CURRENT_TIMESTAMP - INTERVAL '3 days');

INSERT INTO announcements (title, content, course_id, sender_id, published_at, active)
VALUES ('Examen final', 'El examen final será el próximo viernes', 1, 2, CURRENT_TIMESTAMP - INTERVAL '1 day', TRUE),
       ('Cambio de aula', 'La clase de Matemáticas cambia de aula', 1, 3, CURRENT_TIMESTAMP - INTERVAL '5 hours', TRUE);
