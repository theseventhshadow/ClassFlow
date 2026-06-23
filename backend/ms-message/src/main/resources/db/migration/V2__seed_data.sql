-- Message Service - Seed Data
-- Mensajes entre docentes y apoderados
INSERT INTO messages (sender_id, receiver_id, subject, body, read, sent_at)
VALUES (2, 30, 'Informe de progreso', 'Estimada apoderada, le informo que Benjamín ha tenido un excelente rendimiento este mes. Saludos cordiales, María González.', TRUE, '2026-04-01 10:00:00'),
       (30, 2, 'RE: Informe de progreso', 'Estimada profesora, muchas gracias por la información. Saludos, Patricia Vega.', TRUE, '2026-04-01 14:30:00'),
       (3, 32, 'Inasistencia de Matías', 'Estimados apoderados, informo que Matías ha faltado a clases sin justificación. Favor justificar.', FALSE, '2026-04-02 09:00:00'),
       (4, 34, 'Anotación positiva', 'Estimada apoderada, quería comentarle que Isidora tuvo una excelente participación en clases hoy.', TRUE, '2026-04-03 11:15:00'),
       (34, 4, 'RE: Anotación positiva', 'Profesor, muchas gracias por el aviso. Nos alegramos mucho.', TRUE, '2026-04-03 16:00:00'),
       (2, 35, 'Bajo rendimiento', 'Estimada apoderada, Joaquín ha tenido un bajo rendimiento en Matemáticas. Sugiero reforzar en casa.', FALSE, '2026-04-04 08:30:00'),
       (5, 37, 'Prueba de Inglés', 'Estimada apoderada, le recuerdo que la próxima semana tenemos prueba de Inglés.', FALSE, '2026-04-04 10:00:00'),
       (30, 2, 'Consulta sobre tareas', 'Profesora, Benjamín me dice que no entiende las tareas de Matemáticas. ¿Podría sugerir ejercicios extras?', FALSE, '2026-04-05 18:00:00');

-- Anuncios
INSERT INTO announcements (title, content, course_id, sender_id, published_at, active)
VALUES ('Bienvenida año escolar 2026', 'Bienvenidos al nuevo año escolar. Esperamos que tengan un excelente año académico.', 1, 1, '2026-03-01 08:00:00', TRUE),
       ('Reunión de apoderados', 'Se convoca a reunión de apoderados el día 15 de abril a las 19:00 hrs.', 1, 2, '2026-04-01 09:00:00', TRUE),
       ('Prueba de Matemáticas', 'Se informa que la prueba de Matemáticas será el día 25 de abril.', 2, 3, '2026-04-02 10:30:00', TRUE),
       ('Día del alumno', 'El día 11 de mayo se celebrará el Día del Alumno. Actividades culturales y recreativas.', NULL, 1, '2026-04-03 08:00:00', TRUE),
       ('feriado 1 de mayo', 'Se recuerda que el día 1 de mayo es feriado. No hay clases.', NULL, 1, '2026-04-04 07:00:00', TRUE),
       ('Semana de la ciencia', 'Del 20 al 24 de abril se realizará la Semana de la Ciencia. Todos los cursos participarán.', NULL, 1, '2026-04-05 14:00:00', TRUE);
