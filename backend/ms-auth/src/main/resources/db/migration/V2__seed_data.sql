-- Auth Service - Seed Data
INSERT INTO roles (name) VALUES ('ADMIN'), ('TEACHER'), ('STUDENT'), ('GUARDIAN');

-- ============================================================
-- ADMINISTRADOR
-- ============================================================
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Administrador', 'Sistema', '11111111', 'admin@classflow.cl', 'password', 'ADMIN', NULL, NULL, TRUE);

-- ============================================================
-- DOCENTES (8)
-- ============================================================
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('María', 'González Muñoz', '20123456', 'maria.gonzalez@classflow.cl', 'password', 'TEACHER', 'Matemáticas', NULL, TRUE),
('Carlos', 'Muñoz Rojas', '15123456', 'carlos.munoz@classflow.cl', 'password', 'TEACHER', 'Lenguaje', NULL, TRUE),
('Paola', 'Soto Mardones', '16123456', 'paola.soto@classflow.cl', 'password', 'TEACHER', 'Ciencias', NULL, TRUE),
('Rodrigo', 'Álvarez Vera', '17123456', 'rodrigo.alvarez@classflow.cl', 'password', 'TEACHER', 'Historia', NULL, TRUE),
('Daniela', 'Torres Reyes', '18123456', 'daniela.torres@classflow.cl', 'password', 'TEACHER', 'Inglés', NULL, TRUE),
('Felipe', 'Castro Olmos', '19123456', 'felipe.castro@classflow.cl', 'password', 'TEACHER', 'Educación Física', NULL, TRUE),
('Claudia', 'Hernández Pino', '21123456', 'claudia.hernandez@classflow.cl', 'password', 'TEACHER', 'Artes', NULL, TRUE),
('Jorge', 'Díaz Contreras', '22123456', 'jorge.diaz@classflow.cl', 'password', 'TEACHER', 'Música', NULL, TRUE);

-- ============================================================
-- ESTUDIANTES (20)
-- ============================================================
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Benjamín', 'Araya Vega', '30123456', 'benjamin.araya@classflow.cl', 'password', 'STUDENT', '1° Básico', NULL, TRUE),
('Antonia', 'Cifuentes Ruiz', '30123457', 'antonia.cifuentes@classflow.cl', 'password', 'STUDENT', '1° Básico', NULL, TRUE),
('Matías', 'Sepúlveda Lagos', '30123458', 'matias.sepulveda@classflow.cl', 'password', 'STUDENT', '2° Básico', NULL, TRUE),
('Florencia', 'Valenzuela Mora', '30123459', 'florencia.valenzuela@classflow.cl', 'password', 'STUDENT', '2° Básico', NULL, TRUE),
('Sebastián', 'Pizarro Peña', '30123460', 'sebastian.pizarro@classflow.cl', 'password', 'STUDENT', '3° Básico', NULL, TRUE),
('Isidora', 'Fuentes Cárdenas', '30123461', 'isidora.fuentes@classflow.cl', 'password', 'STUDENT', '3° Básico', NULL, TRUE),
('Vicente', 'Tapia Navarro', '30123462', 'vicente.tapia@classflow.cl', 'password', 'STUDENT', '4° Básico', NULL, TRUE),
('Emilia', 'Gutiérrez Espinoza', '30123463', 'emilia.gutierrez@classflow.cl', 'password', 'STUDENT', '4° Básico', NULL, TRUE),
('Joaquín', 'Contreras Silva', '30123464', 'joaquin.contreras@classflow.cl', 'password', 'STUDENT', '5° Básico', NULL, TRUE),
('Martina', 'Vega Salinas', '30123465', 'martina.vega@classflow.cl', 'password', 'STUDENT', '5° Básico', NULL, TRUE),
('Gaspar', 'Bravo Guzmán', '30123466', 'gaspar.bravo@classflow.cl', 'password', 'STUDENT', '6° Básico', NULL, TRUE),
('Amanda', 'Rivas Pereira', '30123467', 'amanda.rivas@classflow.cl', 'password', 'STUDENT', '6° Básico', NULL, TRUE),
('Maximiliano', 'Figueroa Coronado', '30123468', 'maximiliano.figueroa@classflow.cl', 'password', 'STUDENT', '1° Medio', NULL, TRUE),
('Josefa', 'Molina Cáceres', '30123469', 'josefa.molina@classflow.cl', 'password', 'STUDENT', '1° Medio', NULL, TRUE),
('Alonso', 'Ortiz Delgado', '30123470', 'alonso.ortiz@classflow.cl', 'password', 'STUDENT', '2° Medio', NULL, TRUE),
('Trinidad', 'Sandoval Farías', '30123471', 'trinidad.sandoval@classflow.cl', 'password', 'STUDENT', '2° Medio', NULL, TRUE),
('Nicolás', 'Riquelme Cuevas', '30123472', 'nicolas.riquelme@classflow.cl', 'password', 'STUDENT', '3° Medio', NULL, TRUE),
('Catalina', 'Parra Muñoz', '30123473', 'catalina.parra@classflow.cl', 'password', 'STUDENT', '3° Medio', NULL, TRUE),
('Cristóbal', 'Sánchez Bustos', '30123474', 'cristobal.sanchez@classflow.cl', 'password', 'STUDENT', '4° Medio', NULL, TRUE),
('Valentina', 'Morales Leiva', '30123475', 'valentina.morales@classflow.cl', 'password', 'STUDENT', '4° Medio', NULL, TRUE);

-- ============================================================
-- APODERADOS (8)
-- ============================================================
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Patricia', 'Vega Paredes', '40123456', 'patricia.vega@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Héctor', 'Araya Muñoz', '40123457', 'hector.araya@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Marcela', 'Ruiz Soto', '40123458', 'marcela.ruiz@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Raúl', 'Sepúlveda Ahumada', '40123459', 'raul.sepulveda@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Carolina', 'Mora Peñaloza', '40123460', 'carolina.mora@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Pablo', 'Pizarro Zamora', '40123461', 'pablo.pizarro@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Angélica', 'Cárdenas Vega', '40123462', 'angelica.cardenas@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Ricardo', 'Lagos Pino', '40123463', 'ricardo.lagos@classflow.cl', 'password', 'GUARDIAN', NULL, NULL, TRUE);

