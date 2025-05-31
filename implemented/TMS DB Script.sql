-- USERS
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL CHECK (length(trim(first_name)) > 0),
    last_name TEXT NOT NULL CHECK (length(trim(last_name)) > 0),
    phone_number TEXT NOT NULL 
        CHECK (phone_number ~ '^[0-9]{10,15}$'),
    email TEXT NOT NULL 
        CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
    password TEXT NOT NULL 
        CHECK (length(password) >= 8),
    status BOOLEAN DEFAULT TRUE,
    photo BYTEA,
    gender TEXT NOT NULL 
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    address TEXT NOT NULL 
        CHECK (length(trim(address)) > 0),
    role TEXT NOT NULL 
        CHECK (role IN ('ADMIN', 'TRAINER', 'TRAINEE')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- ROOMS
CREATE TABLE Rooms (
    id SERIAL PRIMARY KEY,
    capacity INT NOT NULL 
        CHECK (capacity > 0 AND capacity <= 1000),
    location TEXT NOT NULL 
        CHECK (length(trim(location)) > 0),
    status TEXT NOT NULL DEFAULT 'AVAILABLE' 
        CHECK (status IN ('AVAILABLE', 'UNAVAILABLE', 'MAINTENANCE')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- TRAININGS
CREATE TABLE Trainings (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL 
        CHECK (length(trim(title)) > 0),
    description TEXT,
    room_id INT REFERENCES Rooms(id) ON DELETE SET NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status TEXT NOT NULL DEFAULT 'AVAILABLE' 
        CHECK (status IN ('AVAILABLE', 'UNAVAILABLE', 'CANCELED', 'COMPLETED', 'LIVE')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CHECK (end_date >= start_date),
    CHECK (end_date >= created_at)
);

-- COURSES
CREATE TABLE Courses (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL 
        CHECK (length(trim(title)) > 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    trainer_id INT NOT NULL REFERENCES Users(id) ON DELETE SET NULL,
    CHECK (created_at <= updated_at)
);

-- TRAINING_COURSES
CREATE TABLE Training_Courses (
    id SERIAL PRIMARY KEY,
    training_id INT NOT NULL REFERENCES Trainings(id) ON DELETE CASCADE,
    course_id INT NOT NULL REFERENCES Courses(id) ON DELETE CASCADE,
    total_sessions INT NOT NULL 
        CHECK (total_sessions > 0 AND total_sessions <= 100),
    current_sessions INT 
        CHECK (current_sessions BETWEEN 0 AND total_sessions),
    completed_sessions INT DEFAULT 0 
        CHECK (completed_sessions BETWEEN 0 AND total_sessions),
    cancelled_sessions INT DEFAULT 0 
        CHECK (cancelled_sessions BETWEEN 0 AND total_sessions),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'COMING' 
        CHECK (status IN ('COMPLETED', 'CANCELLED', 'COMING', 'LIVE')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CHECK (end_date >= start_date),
    CHECK (completed_sessions + cancelled_sessions <= total_sessions),
    CHECK (created_at <= updated_at),
    UNIQUE (training_id, course_id)
);

-- SESSIONS
CREATE TABLE Sessions (
    id SERIAL PRIMARY KEY,
    training_course_id INT NOT NULL REFERENCES Training_Courses(id) ON DELETE CASCADE,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(10) DEFAULT 'COMING' NOT NULL 
        CHECK (status IN ('COMPLETED', 'CANCELLED', 'COMING')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CHECK (end_time > start_time),
    CHECK (created_at <= updated_at)
);

-- MATERIALS
CREATE TABLE Materials (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL 
        CHECK (length(trim(title)) > 0),
    description TEXT,
    session_id INT NOT NULL REFERENCES Sessions(id) ON DELETE CASCADE,
    file_data BYTEA NOT NULL,
    file_type TEXT NOT NULL 
        CHECK (file_type IN ('PDF', 'PPT', 'DOC', 'VIDEO', 'IMAGE', 'OTHER')),
    file_size BIGINT NOT NULL 
        CHECK (file_size > 0 AND file_size <= 104857600),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CHECK (created_at <= updated_at)
);

-- ENROLLMENTS
CREATE TABLE Enrollments (
    id SERIAL PRIMARY KEY,
    trainee_id INT NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    training_id INT NOT NULL REFERENCES Trainings(id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT NOW(),
    status TEXT DEFAULT 'ACTIVE' 
        CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED')),
    UNIQUE (trainee_id, training_id)
);

-- FEEDBACKS
CREATE TABLE Feedbacks (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES Sessions(id) ON DELETE CASCADE,
    trainee_id INT NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    rating INT NOT NULL 
        CHECK (rating BETWEEN 1 AND 5),
    comment TEXT NOT NULL 
        CHECK (length(trim(comment)) > 0),
    submitted_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (session_id, trainee_id),
    CHECK (submitted_at <= NOW())
);

-- CERTIFICATES
CREATE TABLE Certificates (
    id SERIAL PRIMARY KEY,
    trainee_id INT NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    training_id INT NOT NULL REFERENCES Trainings(id) ON DELETE CASCADE,
    issued_at TIMESTAMP DEFAULT NOW(),
    certificate_file BYTEA NOT NULL,
    certificate_number TEXT NOT NULL 
        CHECK (length(trim(certificate_number)) > 0),
    UNIQUE (trainee_id, training_id),
    UNIQUE (certificate_number),
    CHECK (issued_at <= NOW())
);

-- ATTENDANCE
CREATE TABLE Attendance (
    id SERIAL PRIMARY KEY,
    trainee_id INT NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    session_id INT NOT NULL REFERENCES Sessions(id) ON DELETE CASCADE,
    status TEXT NOT NULL 
        CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
    recorded_at TIMESTAMP DEFAULT NOW(),
    notes TEXT,
    UNIQUE (trainee_id, session_id),
    CHECK (recorded_at <= NOW())
);

-- =============================================
-- INDEXES
-- =============================================

-- Users indexes
CREATE INDEX idx_users_name_search ON Users (lower(first_name), lower(last_name));
CREATE INDEX idx_users_role_status ON Users (role, status) WHERE status = TRUE;
CREATE UNIQUE INDEX idx_users_phone_email_unique ON Users (phone_number, email);
CREATE INDEX idx_users_created_updated ON Users (created_at, updated_at);

-- Rooms indexes
CREATE INDEX idx_rooms_status_capacity ON Rooms (status, capacity) WHERE status = 'AVAILABLE';
CREATE INDEX idx_rooms_location_search ON Rooms (lower(location));

-- Trainings indexes
CREATE INDEX idx_trainings_room_id ON Trainings (room_id);
CREATE INDEX idx_trainings_dates ON Trainings (start_date, end_date);
CREATE INDEX idx_trainings_status ON Trainings (status);
CREATE INDEX idx_trainings_title_search ON Trainings (lower(title));
CREATE INDEX idx_trainings_created_updated ON Trainings (created_at, updated_at);

-- Courses indexes
CREATE INDEX idx_courses_trainer_id ON Courses (trainer_id);
CREATE INDEX idx_courses_title_search ON Courses (lower(title));
CREATE INDEX idx_courses_created_updated ON Courses (created_at, updated_at);

-- Training_Courses indexes
CREATE INDEX idx_training_courses_composite ON Training_Courses (training_id, course_id);
CREATE INDEX idx_training_courses_status ON Training_Courses (status);
CREATE INDEX idx_training_courses_dates ON Training_Courses (start_date, end_date);
CREATE INDEX idx_training_courses_created_updated ON Training_Courses (created_at, updated_at);

-- Sessions indexes
CREATE INDEX idx_sessions_training_course_id ON Sessions (training_course_id);
CREATE INDEX idx_sessions_date_time ON Sessions (session_date, start_time);
CREATE INDEX idx_sessions_status ON Sessions (status) WHERE status IN ('COMPLETED', 'CANCELLED');
CREATE INDEX idx_sessions_created_updated ON Sessions (created_at, updated_at);

-- Materials indexes
CREATE INDEX idx_materials_session_id ON Materials (session_id);
CREATE INDEX idx_materials_file_type ON Materials (file_type);
CREATE INDEX idx_materials_created_updated ON Materials (created_at, updated_at);

-- Enrollments indexes
CREATE INDEX idx_enrollments_trainee_id ON Enrollments (trainee_id);
CREATE INDEX idx_enrollments_training_id ON Enrollments (training_id);
CREATE INDEX idx_enrollments_date ON Enrollments (enrollment_date);
CREATE INDEX idx_enrollments_status ON Enrollments (status);

-- Feedbacks indexes
CREATE INDEX idx_feedbacks_session_id ON Feedbacks (session_id);
CREATE INDEX idx_feedbacks_trainee_id ON Feedbacks (trainee_id);
CREATE INDEX idx_feedbacks_submitted_at ON Feedbacks (submitted_at);
CREATE INDEX idx_feedbacks_rating ON Feedbacks (rating);

-- Certificates indexes
CREATE INDEX idx_certificates_trainee_id ON Certificates (trainee_id);
CREATE INDEX idx_certificates_training_id ON Certificates (training_id);
CREATE INDEX idx_certificates_issued_at ON Certificates (issued_at);
CREATE INDEX idx_certificates_number ON Certificates (certificate_number);

-- Attendance indexes
CREATE INDEX idx_attendance_session_id ON Attendance (session_id);
CREATE INDEX idx_attendance_status ON Attendance (status);
CREATE INDEX idx_attendance_recorded_at ON Attendance (recorded_at);
CREATE INDEX idx_attendance_trainee_id ON Attendance (trainee_id);

-- =============================================
-- TRIGGERS
-- =============================================

-- 1. ENROLLMENT CAPACITY TRIGGER
CREATE OR REPLACE FUNCTION check_training_capacity()
RETURNS TRIGGER AS $$
DECLARE
    current_enrollments INT;
    room_capacity INT;
    training_status TEXT;
BEGIN
    SELECT 
        COUNT(e.id), 
        r.capacity,
        t.status
    INTO 
        current_enrollments, 
        room_capacity,
        training_status
    FROM 
        Trainings t
        LEFT JOIN Rooms r ON t.room_id = r.id
        LEFT JOIN Enrollments e ON e.training_id = t.id AND e.status = 'ACTIVE'
    WHERE 
        t.id = NEW.training_id
    GROUP BY 
        r.capacity, t.status;

    IF room_capacity IS NOT NULL THEN
        IF current_enrollments >= room_capacity AND training_status <> 'UNAVAILABLE' THEN
            UPDATE Trainings
            SET status = 'UNAVAILABLE'
            WHERE id = NEW.training_id;
            
            RAISE NOTICE 'Training % marked as UNAVAILABLE (reached capacity of %)', 
                         NEW.training_id, room_capacity;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_training_capacity ON Enrollments;
CREATE TRIGGER trg_check_training_capacity
AFTER INSERT OR UPDATE OF status ON Enrollments
FOR EACH ROW
EXECUTE FUNCTION check_training_capacity();

-- 2. TRAINING COURSE SESSION MANAGEMENT TRIGGER
CREATE OR REPLACE FUNCTION manage_training_course_sessions()
RETURNS TRIGGER AS $$
BEGIN
    NEW.current_sessions := COALESCE(NEW.current_sessions, NEW.total_sessions);
    
    IF NEW.current_sessions > NEW.total_sessions THEN
        NEW.current_sessions := NEW.total_sessions;
        RAISE NOTICE 'Adjusted current_sessions to match total_sessions (%)', NEW.total_sessions;
    END IF;
    
    IF NEW.completed_sessions + NEW.cancelled_sessions > NEW.total_sessions THEN
        RAISE EXCEPTION 'Sum of completed and cancelled sessions cannot exceed total sessions';
    END IF;
    
    IF NEW.completed_sessions = NEW.total_sessions THEN
        NEW.status := 'COMPLETED';
    ELSIF NEW.cancelled_sessions = NEW.total_sessions THEN
        NEW.status := 'CANCELLED';
    ELSIF (NEW.completed_sessions + NEW.cancelled_sessions) > 0 THEN
        NEW.status := 'LIVE';
    ELSE
        NEW.status := 'COMING';
    END IF;
    
    NEW.updated_at := NOW();
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_manage_training_course_sessions ON Training_Courses;
CREATE TRIGGER trg_manage_training_course_sessions
BEFORE INSERT OR UPDATE ON Training_Courses
FOR EACH ROW
EXECUTE FUNCTION manage_training_course_sessions();

-- 3. SESSION STATUS TRIGGER
CREATE OR REPLACE FUNCTION update_training_course_from_session()
RETURNS TRIGGER AS $$
DECLARE
    session_counts RECORD;
    tc_id INT;
BEGIN
    tc_id := COALESCE(NEW.training_course_id, OLD.training_course_id);
    
    SELECT 
        COUNT(*) AS total,
        COUNT(*) FILTER (WHERE status = 'COMPLETED') AS completed,
        COUNT(*) FILTER (WHERE status = 'CANCELLED') AS cancelled
    INTO session_counts
    FROM Sessions
    WHERE training_course_id = tc_id;

    UPDATE Training_Courses
    SET 
        total_sessions = session_counts.total,
        completed_sessions = session_counts.completed,
        cancelled_sessions = session_counts.cancelled,
        current_sessions = GREATEST(0, session_counts.total - (session_counts.completed + session_counts.cancelled)),
        status = CASE 
            WHEN session_counts.total = session_counts.completed THEN 'COMPLETED'
            WHEN session_counts.total = session_counts.cancelled THEN 'CANCELLED'
            WHEN (session_counts.completed + session_counts.cancelled) > 0 THEN 'LIVE'
            ELSE 'COMING'
        END,
        updated_at = NOW()
    WHERE id = tc_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_training_course_from_session ON Sessions;
CREATE TRIGGER trg_update_training_course_from_session
AFTER INSERT OR UPDATE OF status OR DELETE ON Sessions
FOR EACH ROW
EXECUTE FUNCTION update_training_course_from_session();

-- 4. TRAINING DATE VALIDATION TRIGGER
CREATE OR REPLACE FUNCTION validate_training_dates()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        RAISE EXCEPTION 'Training end date cannot be before start date';
    END IF;
    
    IF TG_OP = 'UPDATE' THEN
        PERFORM 1 FROM Sessions s
        JOIN Training_Courses tc ON s.training_course_id = tc.id
        WHERE tc.training_id = NEW.id
        AND (
            s.session_date < NEW.start_date::DATE OR 
            s.session_date > NEW.end_date::DATE
        )
        LIMIT 1;
        
        IF FOUND THEN
            RAISE EXCEPTION 'Cannot update training dates - existing sessions fall outside new date range';
        END IF;
    END IF;
    
    IF TG_OP = 'UPDATE' THEN
        NEW.updated_at := NOW();
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_training_dates ON Trainings;
CREATE TRIGGER trg_validate_training_dates
BEFORE INSERT OR UPDATE OF start_date, end_date ON Trainings
FOR EACH ROW
EXECUTE FUNCTION validate_training_dates();

-- 5. ROOM AVAILABILITY TRIGGER
CREATE OR REPLACE FUNCTION check_room_availability()
RETURNS TRIGGER AS $$
DECLARE
    conflicting_trainings INT;
    room_status TEXT;
BEGIN
    IF NEW.room_id IS NULL OR (TG_OP = 'UPDATE' AND NEW.room_id = OLD.room_id) THEN
        RETURN NEW;
    END IF;
    
    SELECT status INTO room_status FROM Rooms WHERE id = NEW.room_id;
    
    IF room_status <> 'AVAILABLE' THEN
        RAISE EXCEPTION 'Room % is not available (status: %)', NEW.room_id, room_status;
    END IF;
    
    SELECT COUNT(*) INTO conflicting_trainings
    FROM Trainings t
    WHERE t.room_id = NEW.room_id
    AND t.id <> NEW.id
    AND t.status IN ('AVAILABLE', 'LIVE')
    AND (
        (NEW.start_date BETWEEN t.start_date AND t.end_date) OR
        (NEW.end_date BETWEEN t.start_date AND t.end_date) OR
        (t.start_date BETWEEN NEW.start_date AND NEW.end_date) OR
        (t.end_date BETWEEN NEW.start_date AND NEW.end_date)
    );
    
    IF conflicting_trainings > 0 THEN
        RAISE EXCEPTION 'Room % is already booked for another training during this period', NEW.room_id;
    END IF;
    
    IF TG_OP = 'UPDATE' THEN
        NEW.updated_at := NOW();
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_room_availability ON Trainings;
CREATE TRIGGER trg_check_room_availability
BEFORE INSERT OR UPDATE OF room_id, start_date, end_date ON Trainings
FOR EACH ROW
EXECUTE FUNCTION check_room_availability();

-- 6. TRAINER VALIDATION TRIGGER
CREATE OR REPLACE FUNCTION validate_trainer_role()
RETURNS TRIGGER AS $$
DECLARE
    user_role TEXT;
    user_status BOOLEAN;
BEGIN
    SELECT role, status INTO user_role, user_status
    FROM Users
    WHERE id = NEW.trainer_id;
    
    IF user_role <> 'TRAINER' THEN
        RAISE EXCEPTION 'User % is not a trainer (role: %)', NEW.trainer_id, user_role;
    END IF;
    
    IF NOT user_status THEN
        RAISE EXCEPTION 'Trainer % is not active', NEW.trainer_id;
    END IF;
    
    IF TG_OP = 'UPDATE' THEN
        NEW.updated_at := NOW();
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_trainer_role ON Courses;
CREATE TRIGGER trg_validate_trainer_role
BEFORE INSERT OR UPDATE OF trainer_id ON Courses
FOR EACH ROW
EXECUTE FUNCTION validate_trainer_role();

-- 7. ATTENDANCE VALIDATION TRIGGER (FIXED)
CREATE OR REPLACE FUNCTION validate_attendance()
RETURNS TRIGGER AS $$
DECLARE
    is_enrolled BOOLEAN;
    session_rec RECORD;
BEGIN
    -- Get session details
    SELECT s.status, s.session_date, s.start_time INTO session_rec
    FROM Sessions s WHERE s.id = NEW.session_id;
    
    -- Prevent attendance for completed/cancelled sessions
    IF session_rec.status IN ('COMPLETED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Cannot record attendance for a % session', session_rec.status;
    END IF;
    
    -- Check if trainee is enrolled in the training
    SELECT EXISTS (
        SELECT 1 FROM Enrollments e
        JOIN Training_Courses tc ON e.training_id = tc.training_id
        JOIN Sessions s ON tc.id = s.training_course_id
        WHERE e.trainee_id = NEW.trainee_id
        AND s.id = NEW.session_id
        AND e.status = 'ACTIVE'
    ) INTO is_enrolled;
    
    IF NOT is_enrolled THEN
        RAISE EXCEPTION 'Trainee % is not enrolled in the training for session %', 
                        NEW.trainee_id, NEW.session_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_attendance ON Attendance;
CREATE TRIGGER trg_validate_attendance
BEFORE INSERT OR UPDATE ON Attendance
FOR EACH ROW
EXECUTE FUNCTION validate_attendance();


-- 8. SESSION TIME VALIDATION TRIGGER
CREATE OR REPLACE FUNCTION validate_session_times()
RETURNS TRIGGER AS $$
DECLARE
    training_start DATE;
    training_end DATE;
BEGIN
    SELECT t.start_date::DATE, t.end_date::DATE INTO training_start, training_end
    FROM Trainings t
    JOIN Training_Courses tc ON t.id = tc.training_id
    WHERE tc.id = NEW.training_course_id;
    
    IF NEW.session_date < training_start OR NEW.session_date > training_end THEN
        RAISE EXCEPTION 'Session date must be within the training period (% to %)', 
                        training_start, training_end;
    END IF;
    
    IF TG_OP = 'UPDATE' THEN
        NEW.updated_at := NOW();
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_session_times ON Sessions;
CREATE TRIGGER trg_validate_session_times
BEFORE INSERT OR UPDATE ON Sessions
FOR EACH ROW
EXECUTE FUNCTION validate_session_times();

-- 9. TRAINING STATUS UPDATE TRIGGER
CREATE OR REPLACE FUNCTION update_training_status()
RETURNS TRIGGER AS $$
DECLARE
    all_completed BOOLEAN;
    any_live BOOLEAN;
BEGIN
    IF NEW.status = 'COMPLETED' OR (TG_OP = 'UPDATE' AND OLD.status <> 'COMPLETED' AND NEW.status = 'COMPLETED') THEN
        SELECT 
            NOT EXISTS (
                SELECT 1 FROM Training_Courses 
                WHERE training_id = NEW.training_id 
                AND status <> 'COMPLETED'
            ),
            EXISTS (
                SELECT 1 FROM Training_Courses
                WHERE training_id = NEW.training_id
                AND status = 'LIVE'
            )
        INTO all_completed, any_live;
        
        IF all_completed THEN
            UPDATE Trainings SET status = 'COMPLETED', updated_at = NOW()
            WHERE id = NEW.training_id;
        ELSIF any_live THEN
            UPDATE Trainings SET status = 'LIVE', updated_at = NOW()
            WHERE id = NEW.training_id;
        END IF;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_training_status ON Training_Courses;
CREATE TRIGGER trg_update_training_status
AFTER INSERT OR UPDATE OF status ON Training_Courses
FOR EACH ROW
EXECUTE FUNCTION update_training_status();

-- 10. USER UPDATE TIMESTAMP TRIGGER
CREATE OR REPLACE FUNCTION update_user_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_user_timestamp ON Users;
CREATE TRIGGER trg_update_user_timestamp
BEFORE UPDATE ON Users
FOR EACH ROW
EXECUTE FUNCTION update_user_timestamp();

-- 11. ROOM UPDATE TIMESTAMP TRIGGER
CREATE OR REPLACE FUNCTION update_room_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_room_timestamp ON Rooms;
CREATE TRIGGER trg_update_room_timestamp
BEFORE UPDATE ON Rooms
FOR EACH ROW
EXECUTE FUNCTION update_room_timestamp();

-- 12. MATERIAL UPDATE TIMESTAMP TRIGGER
CREATE OR REPLACE FUNCTION update_material_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_material_timestamp ON Materials;
CREATE TRIGGER trg_update_material_timestamp
BEFORE UPDATE ON Materials
FOR EACH ROW
EXECUTE FUNCTION update_material_timestamp();

-- =============================================
-- SAMPLE DATA
-- =============================================

-- USERS
INSERT INTO Users (first_name, last_name, phone_number, email, password, gender, address, role)
VALUES 
('John', 'Doe', '1234567890', 'john@example.com', 'password123', 'MALE', '123 Main St', 'ADMIN'),
('Alice', 'Smith', '2345678901', 'alice@example.com', 'password123', 'FEMALE', '456 Park Ave', 'TRAINER'),
('Bob', 'Brown', '3456789012', 'bob@example.com', 'password123', 'MALE', '789 Broadway', 'TRAINEE'),
('Carol', 'Johnson', '4567890123', 'carol@example.com', 'password123', 'FEMALE', '321 Oak St', 'TRAINER'),
('Dave', 'Williams', '5678901234', 'dave@example.com', 'password123', 'MALE', '654 Pine St', 'TRAINEE');

-- ROOMS
INSERT INTO Rooms ( capacity, location)
VALUES 
( 30, 'Building 1, Floor 2'),
( 50, 'Building 1, Floor 3'),
( 20, 'Building 2, Floor 1');

-- TRAININGS
INSERT INTO Trainings (title, description, room_id, start_date, end_date)
VALUES 
('Java Basics', 'Introductory Java training', 1, '2025-06-01 09:00:00', '2025-06-10 17:00:00'),
('Web Development', 'Full-stack web training', 2, '2025-07-01 09:00:00', '2025-07-15 17:00:00'),
('Database Design', 'Relational database concepts', 3, '2025-08-01 09:00:00', '2025-08-05 17:00:00');

-- COURSES
INSERT INTO Courses (title, description, trainer_id)
VALUES 
('Java Fundamentals', 'Covers OOP, syntax, and Java basics', 2),
('HTML & CSS', 'Web design essentials', 2),
('Advanced SQL', 'Database optimization techniques', 4),
('Spring Framework', 'Enterprise Java development', 4);

-- TRAINING_COURSES
INSERT INTO Training_Courses (training_id, course_id, total_sessions, start_date, end_date)
VALUES 
(1, 1, 5, '2025-06-01 09:00:00', '2025-06-10 17:00:00'),
(2, 2, 6, '2025-07-01 09:00:00', '2025-07-15 17:00:00'),
(3, 3, 4, '2025-08-01 09:00:00', '2025-08-05 17:00:00');

-- SESSIONS
INSERT INTO Sessions (training_course_id, session_date, start_time, end_time)
VALUES 
(1, '2025-06-01', '09:00', '11:00'),
(1, '2025-06-02', '09:00', '11:00'),
(2, '2025-07-01', '10:00', '12:00'),
(2, '2025-07-02', '10:00', '12:00'),
(3, '2025-08-01', '13:00', '15:00'),
(3, '2025-08-02', '13:00', '15:00');

-- MATERIALS
INSERT INTO Materials (title, description, session_id, file_data, file_type, file_size)
VALUES 
('Intro Slides', 'Slides for first session', 1, decode('DEADBEEF', 'hex'), 'PDF', 1024),
('CSS Basics', 'Stylesheet explanation', 3, decode('CAFEBABE', 'hex'), 'PDF', 2048),
('SQL Cheat Sheet', 'Quick reference guide', 5, decode('12345678', 'hex'), 'PDF', 1536);

-- ENROLLMENTS
INSERT INTO Enrollments (trainee_id, training_id)
VALUES 
(3, 1),
(3, 2),
(5, 1),
(5, 3);

-- FEEDBACKS
INSERT INTO Feedbacks (session_id, trainee_id, rating, comment)
VALUES 
(1, 3, 5, 'Great session!'),
(3, 3, 4, 'Very informative.'),
(5, 5, 5, 'Excellent content');

-- CERTIFICATES
INSERT INTO Certificates (trainee_id, training_id, certificate_file, certificate_number)
VALUES 
(3, 2, decode('12345678', 'hex'), 'CERT-2025-0001'),
(5, 3, decode('87654321', 'hex'), 'CERT-2025-0002');

-- ATTENDANCE
INSERT INTO Attendance (trainee_id, session_id, status, notes)
VALUES 
(3, 1, 'PRESENT', 'On time'),
(3, 3, 'LATE', 'Arrived 10 minutes late'),
(5, 5, 'PRESENT', 'Participated actively');

