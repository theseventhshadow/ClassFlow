-- Message Service - Seed Data
INSERT INTO messages (sender_id, receiver_id, subject, body, read, sent_at)
VALUES (2, 1, 'Prueba de mensaje', 'Este es un mensaje de prueba para demo.', FALSE, CURRENT_TIMESTAMP),
       (1, 2, 'Respuesta', 'Gracias por el aviso.', FALSE, CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO announcements (title, content, course_id, sender_id, published_at, active)
VALUES ('Bienvenida', 'Bienvenidos al curso 1º ESO', 1, 2, CURRENT_TIMESTAMP, TRUE);
