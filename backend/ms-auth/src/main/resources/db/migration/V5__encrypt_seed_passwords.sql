-- Auth Service - Encrypt seed passwords with BCrypt
-- Hash for "password" with strength 10: $2a$10$x6PM8Civ0UeHrBjAsdkUiuc7tJlnRpoxj1fR7Mhvpp7Jz/StTevrO

UPDATE users
SET password = '$2a$10$x6PM8Civ0UeHrBjAsdkUiuc7tJlnRpoxj1fR7Mhvpp7Jz/StTevrO'
WHERE email IN (
    'admin@example.com',
    'maria.gomez@example.com',
    'juan.perez@example.com',
    'ana.lopez@example.com',
    'luis.martin@example.com',
    'sofia.ramirez@example.com',
    'carlos.diaz@example.com',
    'lucia.fernandez@example.com',
    'diego.santos@example.com',
    'marta.suarez@example.com',
    'pablo.ruiz@example.com'
);
