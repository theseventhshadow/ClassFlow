-- Notification Service - Initial Schema
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_sent ON notifications(sent);
