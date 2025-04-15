CREATE TABLE Users ( 
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    first_last VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    role VARCHAR(10) CHECK (role IN ('Admin', 'Trainer', 'Trainee')) NOT NULL
);

CREATE TABLE Trainings(
    training_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) Check (status in ('Completed' , 'OnGoing' , 'Cancelled' , 'Archived' )),
    admin_id INT REFERENCES User(user_id) ON DELETE SET NULL
);

CREATE TABLE Courses (
    course_id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) CHECK (status IN ('Completed', 'OnGoing', 'Cancelled')),
    trainer_id INT REFERENCES User(user_id) ON DELETE SET NULL
);


CREATE TABLE Sessions (
    session_id SERIAL PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    notes VARCHAR(200) NOT NULL,
    course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE,
    scheduled_datetime DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Completed', 'OnGoing', 'Cancelled'))
);


CREATE TABLE Materials (
    material_id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    file BYTEA NOT NULL,   -- Use BYTEA for binary data (files)
    file_type VARCHAR(20) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by INT,
    session_id INT,
    FOREIGN KEY (uploaded_by) REFERENCES "User"(user_id) ON DELETE SET NULL,
    FOREIGN KEY (session_id) REFERENCES public.Sessions(session_id) ON DELETE SET NULL  -- Ensure this points to the correct schema
);


CREATE TABLE Feedbacks (
    feedback_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES User(user_id) ON DELETE SET NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Certificates(
    certificate_id SERIAL PRIMARY KEY,
    file BYTEA,  -- Using BYTEA for binary data (e.g., file content)
    trainee_id INT REFERENCES User(user_id) ON DELETE CASCADE,
    training_id INT REFERENCES Trainings(training_id) ON DELETE CASCADE,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE Attendances (
    attendance_id SERIAL PRIMARY KEY,
    session_id INT REFERENCES Sessions(session_id) ON DELETE CASCADE,
    trainee_id INT REFERENCES User(user_id) ON DELETE CASCADE,
    attended BOOLEAN NOT NULL
);

CREATE TABLE Enrollments (
    enrollment_id SERIAL PRIMARY KEY,  -- Unique identifier for the enrollment
    trainee_id INT REFERENCES User(user_id) ON DELETE CASCADE,
    course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) CHECK (status IN ('Approved', 'Rejected', 'Pending'))
);


CREATE TABLE Training_Courses (
    training_id INT REFERENCES Trainings(training_id) ON DELETE CASCADE,
    course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE,
    PRIMARY KEY (training_id, course_id)
);

CREATE TABLE Assessments (
    assessment_id SERIAL PRIMARY KEY,
    title VARCHAR(50),
    description VARCHAR(300),
    total_marks INT,
    course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE,
    create_by INT REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES User(user_id) ON DELETE CASCADE,
    message VARCHAR(100) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE
);
