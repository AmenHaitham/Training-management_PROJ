CREATE DATABASE training_mange;

CREATE TABLE "User" (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('Admin', 'Trainer', 'Trainee')) NOT NULL
);

CREATE TABLE Admin (
    admin_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES "User"(user_id) ON DELETE CASCADE
);

CREATE TABLE Trainer (
    trainer_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES "User"(user_id) ON DELETE CASCADE
);

CREATE TABLE Trainee (
    trainee_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES "User"(user_id) ON DELETE CASCADE
);

CREATE TABLE Training (
    training_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    admin_id INT REFERENCES Admin(admin_id) ON DELETE SET NULL
);

CREATE TABLE Course (
    course_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20),
    training_id INT REFERENCES Training(training_id) ON DELETE CASCADE,
    trainer_id INT REFERENCES Trainer(trainer_id) ON DELETE SET NULL
);

CREATE TABLE Session (
    session_id SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20),
    course_id INT REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE TrainingMaterial (
    material_id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    file_url TEXT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trainer_id INT REFERENCES Trainer(trainer_id) ON DELETE SET NULL,
    course_id INT REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE Feedback (
    feedback_id SERIAL PRIMARY KEY,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    trainee_id INT REFERENCES Trainee(trainee_id) ON DELETE CASCADE,
    course_id INT REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE Certificate (
    certificate_id SERIAL PRIMARY KEY,
    file TEXT,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trainee_id INT REFERENCES Trainee(trainee_id) ON DELETE CASCADE,
    course_id INT REFERENCES Course(course_id) ON DELETE CASCADE
);

CREATE TABLE Attendance (
    attendance_id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    session_id INT REFERENCES Session(session_id) ON DELETE CASCADE,
    trainee_id INT REFERENCES Trainee(trainee_id) ON DELETE CASCADE
);

-- Many-to-many relation between Course and Trainee
CREATE TABLE CourseEnrollment (
    course_id INT REFERENCES Course(course_id) ON DELETE CASCADE,
    trainee_id INT REFERENCES Trainee(trainee_id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, trainee_id)
);
