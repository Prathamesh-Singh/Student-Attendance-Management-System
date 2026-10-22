CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  institution_id VARCHAR(64) NOT NULL,
  full_name VARCHAR(160) NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(16) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  failed_login_attempts INT NOT NULL DEFAULT 0,
  locked_until DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  CONSTRAINT uk_users_institution_id UNIQUE (institution_id),
  CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE course_sections (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  course_code VARCHAR(32) NOT NULL,
  course_name VARCHAR(160) NOT NULL,
  semester VARCHAR(24) NOT NULL,
  section_name VARCHAR(24) NOT NULL,
  academic_year VARCHAR(16) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_course_section_identity UNIQUE (course_code, semester, section_name, academic_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE course_faculty (
  course_section_id BIGINT NOT NULL,
  faculty_user_id BIGINT NOT NULL,
  CONSTRAINT fk_course_faculty_section FOREIGN KEY (course_section_id) REFERENCES course_sections(id),
  CONSTRAINT fk_course_faculty_user FOREIGN KEY (faculty_user_id) REFERENCES users(id),
  CONSTRAINT uk_course_faculty UNIQUE (course_section_id, faculty_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE enrolments (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  course_section_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_enrolment_section FOREIGN KEY (course_section_id) REFERENCES course_sections(id),
  CONSTRAINT fk_enrolment_student FOREIGN KEY (student_user_id) REFERENCES users(id),
  CONSTRAINT uk_enrolment_section_student UNIQUE (course_section_id, student_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE class_sessions (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  course_section_id BIGINT NOT NULL,
  session_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  session_type VARCHAR(16) NOT NULL,
  created_by_user_id BIGINT NOT NULL,
  submitted BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_session_section FOREIGN KEY (course_section_id) REFERENCES course_sections(id),
  CONSTRAINT fk_session_creator FOREIGN KEY (created_by_user_id) REFERENCES users(id),
  INDEX idx_session_section_date (course_section_id, session_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attendance_records (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL,
  marked_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES class_sessions(id),
  CONSTRAINT fk_attendance_student FOREIGN KEY (student_user_id) REFERENCES users(id),
  CONSTRAINT uk_attendance_session_student UNIQUE (session_id, student_user_id),
  INDEX idx_attendance_student (student_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE correction_requests (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  attendance_record_id BIGINT NOT NULL,
  student_user_id BIGINT NOT NULL,
  reason VARCHAR(250) NOT NULL,
  requested_status VARCHAR(16) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
  requested_at DATETIME(6) NOT NULL,
  decided_at DATETIME(6) NULL,
  decided_by_user_id BIGINT NULL,
  decision_note VARCHAR(250) NULL,
  CONSTRAINT fk_correction_attendance FOREIGN KEY (attendance_record_id) REFERENCES attendance_records(id),
  CONSTRAINT fk_correction_student FOREIGN KEY (student_user_id) REFERENCES users(id),
  CONSTRAINT fk_correction_decider FOREIGN KEY (decided_by_user_id) REFERENCES users(id),
  INDEX idx_correction_status (status, requested_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE audit_log (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  actor_email VARCHAR(190) NULL,
  action VARCHAR(80) NOT NULL,
  target_type VARCHAR(80) NULL,
  target_id VARCHAR(100) NULL,
  old_value TEXT NULL,
  new_value TEXT NULL,
  reason VARCHAR(250) NULL,
  source_ip VARCHAR(64) NULL,
  created_at DATETIME(6) NOT NULL,
  INDEX idx_audit_created (created_at),
  INDEX idx_audit_actor (actor_email),
  INDEX idx_audit_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE password_reset_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  used_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  CONSTRAINT fk_reset_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT uk_reset_token_hash UNIQUE (token_hash),
  INDEX idx_reset_token_hash (token_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE revoked_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  jti VARCHAR(64) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  CONSTRAINT uk_revoked_jti UNIQUE (jti),
  INDEX idx_revoked_expiry (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notification_outbox (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  recipient_email VARCHAR(190) NOT NULL,
  subject VARCHAR(200) NOT NULL,
  body_text TEXT NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
  attempts INT NOT NULL DEFAULT 0,
  next_attempt_at DATETIME(6) NOT NULL,
  last_error VARCHAR(500) NULL,
  attachment_name VARCHAR(200) NULL,
  attachment_content_type VARCHAR(120) NULL,
  attachment_content LONGBLOB NULL,
  created_at DATETIME(6) NOT NULL,
  delivered_at DATETIME(6) NULL,
  INDEX idx_outbox_status_next (status, next_attempt_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE report_log (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  report_type VARCHAR(40) NOT NULL,
  recipient_email VARCHAR(190) NULL,
  status VARCHAR(16) NOT NULL,
  details VARCHAR(500) NULL,
  created_at DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE app_settings (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  setting_key VARCHAR(80) NOT NULL,
  setting_value VARCHAR(250) NOT NULL,
  CONSTRAINT uk_app_setting_key UNIQUE (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
