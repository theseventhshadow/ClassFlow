-- Notification Service - More Seed Data (V3)
INSERT INTO notifications (user_id, type, subject, content, sent, sent_at, error_message)
VALUES (3, 'EMAIL', 'Recordatorio entrega', 'No olvides entregar la tarea.', FALSE, NULL, NULL),
       (4, 'PUSH', 'Nueva nota', 'Se ha publicado una nueva nota en tu curso.', FALSE, NULL, NULL),
       (5, 'SMS', 'Aviso urgente', 'Aviso importante para tutores.', FALSE, NULL, NULL);
