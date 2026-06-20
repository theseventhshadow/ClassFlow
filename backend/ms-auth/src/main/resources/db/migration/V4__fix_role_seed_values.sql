-- Auth Service - Fix seeded role values for existing data
UPDATE users
SET role = 'ADMINISTRATOR'
WHERE role = 'ADMIN';

UPDATE roles
SET name = 'ADMINISTRATOR'
WHERE name = 'ADMIN';
