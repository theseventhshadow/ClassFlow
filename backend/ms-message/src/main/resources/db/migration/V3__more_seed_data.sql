-- Message Service - More Seed Data (V3)
INSERT INTO messages (sender_id, receiver_id, subject, body, read, sent_at)
VALUES (3, 33, 'Entrega de trabajos', 'Estimada apoderada, le recuerdo que Florencia debe entregar el trabajo de Ciencias este viernes.', FALSE, '2026-04-03 08:00:00'),
       (33, 3, 'RE: Entrega de trabajos', 'Gracias profesor, lo tendré presente.', TRUE, '2026-04-03 18:30:00'),
       (2, 30, 'Salida pedagógica', 'Estimada apoderada, informamos que el día 20 de abril los estudiantes de 1° Básico asistirán al Museo Nacional.', FALSE, '2026-04-04 09:00:00'),
       (4, 34, 'Entrevista personal', 'Estimada apoderada, lo cito a entrevista el día 12 de abril a las 15:00 hrs para hablar sobre el rendimiento de su hija.', TRUE, '2026-04-05 10:00:00'),
       (5, 36, 'Taller de Inglés', 'Estimado apoderado, informamos que se abrirá un taller de Inglés los días sábado para nivelación.', FALSE, '2026-04-06 11:00:00'),
       (1, 2, 'Capacitación docente', 'Estimada colega, se informa que el día 18 de abril hay capacitación obligatoria a las 14:00 hrs.', TRUE, '2026-04-02 15:00:00'),
       (2, 1, 'Confirmación capacitación', 'Estimada administración, confirmo asistencia a la capacitación del día 18.', TRUE, '2026-04-02 16:30:00');

INSERT INTO announcements (title, content, course_id, sender_id, published_at, active)
VALUES ('Suspensión de clases', 'Se suspenden las clases el día 30 de abril por mantenimiento del establecimiento.', NULL, 1, '2026-04-10 07:00:00', TRUE),
       ('Resultados SIMCE', 'Se informa que los resultados SIMCE estarán disponibles a partir del 15 de mayo.', NULL, 1, '2026-04-12 08:00:00', TRUE),
       ('Lista de útiles 2027', 'Se publicó la lista de útiles escolares para el próximo año en la página del colegio.', NULL, 1, '2026-04-15 09:00:00', TRUE);
