-- Auth Service - Seed Data
INSERT INTO roles (name) VALUES ('ADMIN'), ('TEACHER'), ('STUDENT'), ('GUARDIAN');

INSERT INTO users (first_name, last_name, id_number, email, password, role, course, guardian_id, active)
VALUES
('Administrador', 'Root', '00000000', 'admin@example.com', 'password', 'ADMIN', NULL, NULL, TRUE),
('María', 'Gómez', '10000001', 'maria.gomez@example.com', 'password', 'TEACHER', 'Matemáticas', NULL, TRUE),
('Juan', 'Pérez', '10000002', 'juan.perez@example.com', 'password', 'STUDENT', '1º ESO', NULL, TRUE),
('Ana', 'López', '10000003', 'ana.lopez@example.com', 'password', 'GUARDIAN', NULL, NULL, TRUE);

