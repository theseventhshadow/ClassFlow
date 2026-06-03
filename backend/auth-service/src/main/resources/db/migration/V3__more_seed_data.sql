-- Auth Service - More Seed Data (V3)
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Luis', 'Martín', '10000004', 'luis.martin@example.com', 'password', 'STUDENT', '1º ESO', NULL, TRUE),
('Sofía', 'Ramírez', '10000005', 'sofia.ramirez@example.com', 'password', 'STUDENT', '1º ESO', NULL, TRUE),
('Carlos', 'Díaz', '10000006', 'carlos.diaz@example.com', 'password', 'TEACHER', 'Física', NULL, TRUE),
('Lucía', 'Fernández', '10000007', 'lucia.fernandez@example.com', 'password', 'GUARDIAN', NULL, NULL, TRUE),
('Diego', 'Santos', '10000008', 'diego.santos@example.com', 'password', 'STUDENT', '2º ESO', NULL, TRUE);

-- Añadimos algunos usuarios con roles mixtos para demo
INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Marta', 'Suárez', '10000009', 'marta.suarez@example.com', 'password', 'TEACHER', 'Matemáticas', NULL, TRUE),
('Pablo', 'Ruiz', '10000010', 'pablo.ruiz@example.com', 'password', 'STUDENT', '2º ESO', NULL, TRUE);
