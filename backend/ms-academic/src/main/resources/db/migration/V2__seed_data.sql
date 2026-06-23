-- Academic Service - Seed Data
-- Cursos al sistema educativo chileno
INSERT INTO courses (name, description, academic_year, active)
VALUES ('1° Básico', 'Primer año de Educación Básica', 2026, TRUE),
       ('2° Básico', 'Segundo año de Educación Básica', 2026, TRUE),
       ('3° Básico', 'Tercer año de Educación Básica', 2026, TRUE),
       ('4° Básico', 'Cuarto año de Educación Básica', 2026, TRUE),
       ('5° Básico', 'Quinto año de Educación Básica', 2026, TRUE),
       ('6° Básico', 'Sexto año de Educación Básica', 2026, TRUE),
       ('1° Medio', 'Primer año de Educación Media', 2026, TRUE),
       ('2° Medio', 'Segundo año de Educación Media', 2026, TRUE);

INSERT INTO subjects (name, description, course_id, active)
VALUES
-- 1° Básico (course_id = 1)
('Lenguaje', 'Lenguaje y Comunicación', 1, TRUE),
('Matemáticas', 'Matemáticas básicas', 1, TRUE),
('Ciencias', 'Ciencias Naturales', 1, TRUE),
('Historia', 'Historia y Geografía', 1, TRUE),
-- 2° Básico (course_id = 2)
('Lenguaje', 'Lenguaje y Comunicación', 2, TRUE),
('Matemáticas', 'Matemáticas', 2, TRUE),
('Ciencias', 'Ciencias Naturales', 2, TRUE),
('Historia', 'Historia y Geografía', 2, TRUE),
-- 3° Básico (course_id = 3)
('Lenguaje', 'Lenguaje y Comunicación', 3, TRUE),
('Matemáticas', 'Matemáticas', 3, TRUE),
('Ciencias', 'Ciencias Naturales', 3, TRUE),
('Historia', 'Historia y Geografía', 3, TRUE),
('Inglés', 'Idioma extranjero', 3, TRUE),
-- 4° Básico (course_id = 4)
('Lenguaje', 'Lenguaje y Comunicación', 4, TRUE),
('Matemáticas', 'Matemáticas', 4, TRUE),
('Ciencias', 'Ciencias Naturales', 4, TRUE),
('Historia', 'Historia y Geografía', 4, TRUE),
('Inglés', 'Idioma extranjero', 4, TRUE),
-- 5° Básico (course_id = 5)
('Lenguaje', 'Lenguaje y Comunicación', 5, TRUE),
('Matemáticas', 'Matemáticas', 5, TRUE),
('Ciencias', 'Ciencias Naturales', 5, TRUE),
('Historia', 'Historia y Geografía', 5, TRUE),
('Inglés', 'Idioma extranjero', 5, TRUE),
-- 6° Básico (course_id = 6)
('Lenguaje', 'Lenguaje y Comunicación', 6, TRUE),
('Matemáticas', 'Matemáticas', 6, TRUE),
('Ciencias', 'Ciencias Naturales', 6, TRUE),
('Historia', 'Historia y Geografía', 6, TRUE),
('Inglés', 'Idioma extranjero', 6, TRUE),
-- 1° Medio (course_id = 7)
('Lenguaje', 'Lenguaje y Comunicación', 7, TRUE),
('Matemáticas', 'Matemáticas', 7, TRUE),
('Ciencias', 'Ciencias Naturales', 7, TRUE),
('Historia', 'Historia y Geografía', 7, TRUE),
('Inglés', 'Idioma extranjero', 7, TRUE),
('Educación Física', 'Educación Física y Salud', 7, TRUE),
-- 2° Medio (course_id = 8)
('Lenguaje', 'Lenguaje y Comunicación', 8, TRUE),
('Matemáticas', 'Matemáticas', 8, TRUE),
('Ciencias', 'Ciencias Naturales', 8, TRUE),
('Historia', 'Historia y Geografía', 8, TRUE),
('Inglés', 'Idioma extranjero', 8, TRUE),
('Educación Física', 'Educación Física y Salud', 8, TRUE);
