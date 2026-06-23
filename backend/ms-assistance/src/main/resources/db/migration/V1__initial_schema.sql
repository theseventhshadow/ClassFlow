-- Assistance Service - Initial Schema
CREATE TABLE IF NOT EXISTS attendances (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    date DATE NOT NULL,
    present BOOLEAN,
    justification TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS annotations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT,
    type VARCHAR(50),
    description TEXT,
    date TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_attendance_student_id ON attendances(student_id);
CREATE INDEX idx_attendance_course_id ON attendances(course_id);
CREATE INDEX idx_attendance_date ON attendances(date);
CREATE INDEX idx_annotation_student_id ON annotations(student_id);
CREATE INDEX idx_annotation_teacher_id ON annotations(teacher_id);
