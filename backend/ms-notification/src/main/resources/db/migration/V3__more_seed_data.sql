-- Notification Service - More Seed Data (V3)
INSERT INTO notifications (user_id, type, subject, content, sent, sent_at, error_message)
VALUES (12, 'EMAIL', 'Calificación publicada', 'Se ha publicado la nota de Prueba Multiplicación: 5.5', TRUE, '2026-04-18 15:00:00', NULL),
       (13, 'EMAIL', 'Calificación publicada', 'Se ha publicado la nota de Prueba Multiplicación: 6.8', TRUE, '2026-04-18 15:00:00', NULL),
       (18, 'EMAIL', 'Calificación publicada', 'Se ha publicado la nota de Prueba Fracciones: 5.0', TRUE, '2026-04-25 16:00:00', NULL),
       (24, 'EMAIL', 'Anotación negativa', 'Alonso Ortiz ha registrado una anotación por no realizar la tarea de Historia.', FALSE, NULL, NULL),
       (25, 'EMAIL', 'Anotación positiva', 'Trinidad Sandoval ha recibido una anotación positiva por su presentación oral.', TRUE, '2026-04-02 10:30:00', NULL),
       (26, 'PUSH', 'Anotación grave', 'Nicolás Riquelme ha sido sorprendido fumando en el baño.', FALSE, NULL, NULL),
       (34, 'EMAIL', 'Entrevista personal', 'Profesor Rodrigo Álvarez cita a entrevista el 12 de abril a las 15:00.', FALSE, NULL, NULL),
       (36, 'PUSH', 'Taller de Inglés', 'Se ha abierto taller de Inglés los sábados para nivelación.', FALSE, NULL, NULL),
       (1, 'EMAIL', 'Reporte mensual', 'Reporte de rendimiento mensual disponible.', TRUE, '2026-04-30 08:00:00', NULL),
       (2, 'PUSH', 'Capacitación docente', 'Capacitación obligatoria el 18 de abril a las 14:00.', FALSE, NULL, NULL);
