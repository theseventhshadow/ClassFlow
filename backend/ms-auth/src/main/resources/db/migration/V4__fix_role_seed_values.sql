-- Auth Service - Fix seeded role values for existing data
UPDATE users
SET role = 'ADMINISTRATOR'
WHERE role = 'ADMIN';

-- If the roles table has legacy ADMIN rows, normalize them too
UPDATE roles
SET name = 'ADMINISTRATOR'
WHERE name = 'ADMIN';
