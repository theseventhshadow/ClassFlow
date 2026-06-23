-- Auth Service - More Seed Data (V3)
-- Vincular apoderados con estudiantes (guardian_id)
UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'patricia.vega@classflow.cl')
WHERE email IN ('benjamin.araya@classflow.cl', 'antonia.cifuentes@classflow.cl');

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'hector.araya@classflow.cl')
WHERE email = 'matias.sepulveda@classflow.cl';

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'marcela.ruiz@classflow.cl')
WHERE email IN ('florencia.valenzuela@classflow.cl', 'sebastian.pizarro@classflow.cl');

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'raul.sepulveda@classflow.cl')
WHERE email IN ('isidora.fuentes@classflow.cl', 'vicente.tapia@classflow.cl');

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'carolina.mora@classflow.cl')
WHERE email = 'emilia.gutierrez@classflow.cl';

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'pablo.pizarro@classflow.cl')
WHERE email IN ('joaquin.contreras@classflow.cl', 'martina.vega@classflow.cl');

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'angelica.cardenas@classflow.cl')
WHERE email = 'gaspar.bravo@classflow.cl';

UPDATE users SET guardian_id = (SELECT id FROM users WHERE email = 'ricardo.lagos@classflow.cl')
WHERE email IN ('amanda.rivas@classflow.cl', 'maximiliano.figueroa@classflow.cl');
