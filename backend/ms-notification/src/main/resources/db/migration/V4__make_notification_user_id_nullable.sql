-- Los correos directos (POST /api/notifications/email) no tienen un usuario destinatario
-- en el dominio de negocio. El tipo se sigue exigiendo: se usa NotificationType.EMAIL para ellos.
ALTER TABLE notifications ALTER COLUMN user_id DROP NOT NULL;
