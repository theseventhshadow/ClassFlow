-- Academic Service - More Seed Data (V3)
-- Cursos 3° y 4° Medio
INSERT INTO courses (name, description, academic_year, active)
VALUES ('3° Medio', 'Tercer año de Educación Media', 2026, TRUE),
       ('4° Medio', 'Cuarto año de Educación Media', 2026, TRUE);

INSERT INTO subjects (name, description, course_id, active)
VALUES
-- 3° Medio (course_id = 9)
('Lenguaje', 'Lenguaje y Comunicación', 9, TRUE),
('Matemáticas', 'Matemáticas', 9, TRUE),
('Ciencias', 'Ciencias Naturales', 9, TRUE),
('Historia', 'Historia y Geografía', 9, TRUE),
('Inglés', 'Idioma extranjero', 9, TRUE),
-- 4° Medio (course_id = 10)
('Lenguaje', 'Lenguaje y Comunicación', 10, TRUE),
('Matemáticas', 'Matemáticas', 10, TRUE),
('Ciencias', 'Ciencias Naturales', 10, TRUE),
('Historia', 'Historia y Geografía', 10, TRUE),
('Inglés', 'Idioma extranjero', 10, TRUE);

-- ============================================================
-- EVALUACIONES Y NOTAS (student_ids según ms-auth V2)
-- admin=1, teachers=2-9, students=10-29, guardians=30-37
-- ============================================================

-- Evaluaciones 1° Básico
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Lectura', 'Prueba de comprensión lectora', 7.0, 30.0, '2026-04-15', 1),
       ('Prueba Sumas', 'Evaluación de sumas y restas', 7.0, 30.0, '2026-04-20', 2);

-- Evaluaciones 2° Básico
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Multiplicación', 'Tablas de multiplicar', 7.0, 30.0, '2026-04-18', 6),
       ('Prueba Ciencias', 'Los seres vivos', 7.0, 25.0, '2026-04-22', 7);

-- Evaluaciones 5° Básico
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Fracciones', 'Operaciones con fracciones', 7.0, 35.0, '2026-04-25', 20),
       ('Prueba Inglés', 'Vocabulario y gramática', 7.0, 25.0, '2026-04-28', 22);

-- Evaluaciones 1° Medio
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Álgebra', 'Ecuaciones lineales', 7.0, 40.0, '2026-05-05', 32),
       ('Prueba Química', 'Tabla periódica', 7.0, 35.0, '2026-05-10', 33);

-- Evaluaciones 2° Medio
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Geometría', 'Geometría analítica', 7.0, 40.0, '2026-05-12', 38),
       ('Trabajo Historia', 'Investigación histórica', 7.0, 30.0, '2026-05-15', 40);

-- Evaluaciones 3° Medio
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Funciones', 'Funciones y gráficos', 7.0, 40.0, '2026-05-18', 44);

-- Evaluaciones 4° Medio
INSERT INTO evaluations (name, description, max_score, percentage, date, subject_id)
VALUES ('Prueba Integrales', 'Cálculo integral', 7.0, 40.0, '2026-05-22', 49);

-- ============================================================
-- NOTAS (student_id = ms-auth user id)
-- ============================================================

-- Benjamín Araya (id=10) - 1° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (10, 6.5, 'Buena comprensión lectora', 1),
       (10, 6.8, 'Muy buen desempeño en sumas', 2);

-- Antonia Cifuentes (id=11) - 1° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (11, 7.0, 'Excelente lectura', 1),
       (11, 6.2, 'Buen trabajo', 2);

-- Matías Sepúlveda (id=12) - 2° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (12, 5.5, 'Debe practicar tablas', 3),
       (12, 6.0, 'Buena participación', 4);

-- Florencia Valenzuela (id=13) - 2° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (13, 6.8, 'Muy buen rendimiento', 3),
       (13, 6.5, 'Excelente en ciencias', 4);

-- Sebastián Pizarro (id=14) - 3° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (14, 4.5, 'Debe reforzar contenidos', 5),
       (14, 5.0, 'Puede mejorar', 6);

-- Isidora Fuentes (id=15) - 3° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (15, 6.5, 'Muy aplicada', 5),
       (15, 6.8, 'Excelente desempeño', 6);

-- Joaquín Contreras (id=18) - 5° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (18, 5.0, 'Debe practicar fracciones', 7),
       (18, 6.0, 'Buen manejo de vocabulario', 8);

-- Martina Vega (id=19) - 5° Básico
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (19, 6.8, 'Excelente en matemáticas', 7),
       (19, 6.5, 'Muy buen inglés', 8);

-- Maximiliano Figueroa (id=22) - 1° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (22, 5.5, 'Debe repasar ecuaciones', 7),
       (22, 6.0, 'Buena base en química', 8);

-- Josefa Molina (id=23) - 1° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (23, 6.5, 'Muy buen desempeño', 7),
       (23, 6.8, 'Excelente en ciencias', 8);

-- Alonso Ortiz (id=24) - 2° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (24, 4.0, 'Debe reforzar geometría', 9),
       (24, 5.5, 'Buen trabajo de investigación', 10);

-- Trinidad Sandoval (id=25) - 2° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (25, 6.8, 'Excelente en geometría', 9),
       (25, 7.0, 'Trabajo sobresaliente', 10);

-- Nicolás Riquelme (id=26) - 3° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (26, 5.0, 'Debe estudiar funciones', 11);

-- Catalina Parra (id=27) - 3° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (27, 6.2, 'Buen desempeño', 11);

-- Cristóbal Sánchez (id=28) - 4° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (28, 5.8, 'Buen manejo de integrales', 12);

-- Valentina Morales (id=29) - 4° Medio
INSERT INTO grades (student_id, score, observations, evaluation_id)
VALUES (29, 6.5, 'Muy buen rendimiento', 12);
