-- Notification Service - Seed Data
INSERT INTO notifications (user_id, type, subject, content, sent, sent_at, error_message)
VALUES (10, 'EMAIL', 'Calificación publicada', 'Se ha publicado la nota de Prueba Lectura: 6.5', TRUE, '2026-04-15 14:00:00', NULL),
       (11, 'EMAIL', 'Calificación publicada', 'Se ha publicado la nota de Prueba Lectura: 7.0', TRUE, '2026-04-15 14:00:00', NULL),
       (30, 'EMAIL', 'Informe de progreso', 'Profesora María González ha enviado un informe sobre Benjamín.', TRUE, '2026-04-01 10:00:00', NULL),
       (32, 'EMAIL', 'Inasistencia', 'Su hijo Matías Sepúlveda ha registrado inasistencias sin justificar.', FALSE, NULL, NULL),
       (2, 'PUSH', 'Recordatorio reunión', 'Tiene reunión de apoderados el 15 de abril a las 19:00', FALSE, NULL, NULL),
       (22, 'PUSH', 'Prueba próxima', 'Recuerde que tiene prueba de Álgebra el 5 de mayo', FALSE, NULL, NULL);
