-- Auth Service - Encrypt seed passwords with BCrypt
-- Hash for "password" with strength 10: $2a$10$x6PM8Civ0UeHrBjAsdkUiuc7tJlnRpoxj1fR7Mhvpp7Jz/StTevrO

UPDATE users
SET password = '$2a$10$x6PM8Civ0UeHrBjAsdkUiuc7tJlnRpoxj1fR7Mhvpp7Jz/StTevrO'
WHERE email LIKE '%@classflow.cl';
