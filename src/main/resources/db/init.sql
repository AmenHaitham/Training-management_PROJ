-- USERS
CREATE TABLE IF NOT EXISTS Users (
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
CREATE TABLE IF NOT EXISTS Rooms (
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
CREATE TABLE IF NOT EXISTS Trainings (
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
CREATE TABLE IF NOT EXISTS Courses (
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
CREATE TABLE IF NOT EXISTS Training_Courses (
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
CREATE TABLE IF NOT EXISTS Sessions (
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
CREATE TABLE IF NOT EXISTS Materials (
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
CREATE TABLE IF NOT EXISTS Enrollments (
    id SERIAL PRIMARY KEY,
    trainee_id INT NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    training_id INT NOT NULL REFERENCES Trainings(id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT NOW(),
    status TEXT DEFAULT 'ACTIVE' 
        CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED')),
    UNIQUE (trainee_id, training_id)
);

-- FEEDBACKS
CREATE TABLE IF NOT EXISTS Feedbacks (
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
CREATE TABLE IF NOT EXISTS Certificates (
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
CREATE TABLE IF NOT EXISTS Attendance (
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

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_name_search ON Users (lower(first_name), lower(last_name));
CREATE INDEX IF NOT EXISTS idx_users_role_status ON Users (role, status) WHERE status = TRUE;
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone_email_unique ON Users (phone_number, email);
CREATE INDEX IF NOT EXISTS idx_users_created_updated ON Users (created_at, updated_at);

CREATE INDEX IF NOT EXISTS idx_rooms_status_capacity ON Rooms (status, capacity) WHERE status = 'AVAILABLE';
CREATE INDEX IF NOT EXISTS idx_rooms_location_search ON Rooms (lower(location));

CREATE INDEX IF NOT EXISTS idx_trainings_room_id ON Trainings (room_id);
CREATE INDEX IF NOT EXISTS idx_trainings_dates ON Trainings (start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_trainings_status ON Trainings (status);
CREATE INDEX IF NOT EXISTS idx_trainings_title_search ON Trainings (lower(title));
CREATE INDEX IF NOT EXISTS idx_trainings_created_updated ON Trainings (created_at, updated_at);

CREATE INDEX IF NOT EXISTS idx_courses_trainer_id ON Courses (trainer_id);
CREATE INDEX IF NOT EXISTS idx_courses_title_search ON Courses (lower(title));
CREATE INDEX IF NOT EXISTS idx_courses_created_updated ON Courses (created_at, updated_at);

CREATE INDEX IF NOT EXISTS idx_training_courses_composite ON Training_Courses (training_id, course_id);
CREATE INDEX IF NOT EXISTS idx_training_courses_status ON Training_Courses (status);
CREATE INDEX IF NOT EXISTS idx_training_courses_dates ON Training_Courses (start_date, end_date);

-- Insert sample data
INSERT INTO Users (first_name, last_name, phone_number, email, password, gender, address, role)
VALUES 
('John', 'Doe', '1234567890', 'john@example.com', 'password123', 'MALE', '123 Main St', 'ADMIN'),
('Alice', 'Smith', '2345678901', 'alice@example.com', 'password123', 'FEMALE', '456 Park Ave', 'TRAINER'),
('Bob', 'Brown', '3456789012', 'bob@example.com', 'password123', 'MALE', '789 Broadway', 'TRAINEE'),
('Carol', 'Johnson', '4567890123', 'carol@example.com', 'password123', 'FEMALE', '321 Oak St', 'TRAINER'),
('Dave', 'Williams', '5678901234', 'dave@example.com', 'password123', 'MALE', '654 Pine St', 'TRAINEE')
ON CONFLICT (phone_number, email) DO NOTHING;

INSERT INTO Rooms (capacity, location)
VALUES 
(30, 'Building 1, Floor 2'),
(50, 'Building 1, Floor 3'),
(20, 'Building 2, Floor 1')
ON CONFLICT DO NOTHING; 