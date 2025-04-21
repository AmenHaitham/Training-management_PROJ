-- Insert admin user
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('admin', 'admin@training.com', 'admin123', 'Admin', 'User', 'ADMIN');

-- Insert trainer users
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES 
('trainer1', 'trainer1@training.com', 'trainer123', 'John', 'Smith', 'TRAINER'),
('trainer2', 'trainer2@training.com', 'trainer123', 'Sarah', 'Johnson', 'TRAINER');

-- Insert trainee users
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES 
('trainee1', 'trainee1@training.com', 'trainee123', 'Michael', 'Brown', 'TRAINEE'),
('trainee2', 'trainee2@training.com', 'trainee123', 'Emily', 'Davis', 'TRAINEE'),
('trainee3', 'trainee3@training.com', 'trainee123', 'David', 'Wilson', 'TRAINEE');

-- Insert training programs
INSERT INTO training_programs (name, description, start_date, end_date, status, created_by)
VALUES 
('Java Development Bootcamp', 'Intensive Java programming course', '2024-03-01', '2024-06-30', 'PLANNED', 1),
('Web Development Fundamentals', 'Introduction to web development', '2024-04-01', '2024-07-31', 'PLANNED', 1);

-- Insert courses for Java Development Bootcamp
INSERT INTO courses (training_program_id, name, description, duration, created_by)
VALUES 
(1, 'Java Core', 'Core Java programming concepts', 40, 1),
(1, 'Spring Framework', 'Spring Boot and Spring MVC', 30, 1),
(1, 'Database Programming', 'JDBC and JPA', 20, 1);

-- Insert courses for Web Development Fundamentals
INSERT INTO courses (training_program_id, name, description, duration, created_by)
VALUES 
(2, 'HTML & CSS', 'Web page structure and styling', 20, 1),
(2, 'JavaScript Basics', 'Introduction to JavaScript', 30, 1),
(2, 'React.js', 'Modern frontend development', 40, 1);

-- Insert sessions for Java Core course
INSERT INTO sessions (course_id, name, description, start_time, end_time, location, trainer_id)
VALUES 
(1, 'Introduction to Java', 'Basic Java concepts', '2024-03-01 09:00:00', '2024-03-01 13:00:00', 'Room 101', 2),
(1, 'Object-Oriented Programming', 'OOP principles in Java', '2024-03-02 09:00:00', '2024-03-02 13:00:00', 'Room 101', 2);

-- Insert sessions for Spring Framework course
INSERT INTO sessions (course_id, name, description, start_time, end_time, location, trainer_id)
VALUES 
(2, 'Spring Boot Basics', 'Introduction to Spring Boot', '2024-03-15 09:00:00', '2024-03-15 13:00:00', 'Room 102', 3),
(2, 'Spring MVC', 'Web application development', '2024-03-16 09:00:00', '2024-03-16 13:00:00', 'Room 102', 3);

-- Insert enrollments
INSERT INTO enrollments (training_program_id, user_id, status)
VALUES 
(1, 4, 'APPROVED'),
(1, 5, 'APPROVED'),
(2, 6, 'APPROVED');

-- Insert attendance records
INSERT INTO attendance (session_id, user_id, status)
VALUES 
(1, 4, 'PRESENT'),
(1, 5, 'PRESENT'),
(2, 4, 'PRESENT'),
(2, 5, 'LATE');

-- Insert assessments
INSERT INTO assessments (course_id, name, description, total_marks, passing_marks, created_by)
VALUES 
(1, 'Java Basics Quiz', 'Test on basic Java concepts', 100, 60, 2),
(2, 'Spring Boot Assignment', 'Practical Spring Boot project', 100, 70, 3);

-- Insert assessment results
INSERT INTO assessment_results (assessment_id, user_id, marks_obtained, status)
VALUES 
(1, 4, 85, 'PASS'),
(1, 5, 75, 'PASS'),
(2, 4, 90, 'PASS'); 