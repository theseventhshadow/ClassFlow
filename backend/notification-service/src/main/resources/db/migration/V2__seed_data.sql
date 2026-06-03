-- Notification Service - Seed Data
INSERT INTO notifications (user_id, type, subject, content, sent, sent_at, error_message)
VALUES (1, 'EMAIL', 'Prueba', 'Notificación de prueba', FALSE, NULL, NULL),
       (2, 'PUSH', 'Aviso', 'Recordatorio de entrega', FALSE, NULL, NULL);
