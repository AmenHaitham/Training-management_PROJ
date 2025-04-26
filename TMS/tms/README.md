# Training Management System (TMS)

A Java-based web application for managing training programs, courses, and enrollments. Built with JSP, Servlets, and JDBC.

## Features

- **User Management**: Registration, authentication, and role-based access control (Admin, Trainer, Trainee)
- **Training Programs Management**: Create, update, activate, complete, and cancel training programs
- **Course Management**: Manage courses within training programs, assign trainers
- **Enrollment Management**: Enroll trainees in training programs, track enrollment status
- **Session Management**: Schedule and manage training sessions
- **Attendance Tracking**: Record and track trainee attendance
- **Assessment Management**: Create assessments and record results

## Technologies

- Java 17
- JDBC (Java Database Connectivity)
- PostgreSQL
- HikariCP (Connection Pooling)
- Servlets & JSP
- JSTL (JavaServer Pages Standard Tag Library)
- BCrypt (Password Hashing)
- Maven (Build Tool)
- SLF4J (Logging)
- Bootstrap 5 (Frontend)

## Database Schema

The application uses the following main tables:
- `users`: Store user information with roles (Admin, Trainer, Trainee)
- `training_programs`: Training program details
- `courses`: Courses within training programs
- `enrollments`: Trainee enrollments in training programs
- `sessions`: Training sessions for courses
- `materials`: Training materials for sessions
- `attendance`: Attendance records for sessions
- `assessments`: Assessments for courses
- `assessment_results`: Trainee results for assessments

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL database
- Apache Tomcat 9 or compatible servlet container
- Maven

### Configuration

Database connection settings are in `src/main/resources/db.properties`:

```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://hostname:port/database
db.username=username
db.password=password
db.poolSize=10
```

### Building the Application

```bash
mvn clean package
```

This will create a WAR file in the `target` directory.

### Deployment

Deploy the generated WAR file to a servlet container:

1. Copy the WAR file to Tomcat's `webapps` directory
2. Start Tomcat
3. Access the application at `http://localhost:8080/tms`

For development, you can use the Maven Tomcat plugin:

```bash
mvn tomcat7:run
```

This will start the application at `http://localhost:8080/tms`

### Default Users

The application creates the following default users on first run:

- Admin: username=`admin`, password=`admin123`
- Trainer: username=`trainer`, password=`trainer123`
- Trainee: username=`trainee`, password=`trainee123`

## Project Structure

```
tms/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── dao/              # Data Access Objects
│   │   │           ├── model/            # Entity models
│   │   │           ├── service/          # Business logic services
│   │   │           ├── util/             # Utility classes
│   │   │           └── web/              # Web components
│   │   │               ├── filter/       # Servlet filters
│   │   │               ├── listener/     # Servlet listeners
│   │   │               └── servlet/      # Servlet controllers
│   │   ├── resources/
│   │   │   ├── db.properties            # Database configuration
│   │   │   └── db_schema.sql            # Database schema
│   │   └── webapp/
│   │       ├── assets/                  # Static resources (CSS, JS)
│   │       ├── WEB-INF/
│   │       │   ├── includes/            # JSP includes (header, footer)
│   │       │   ├── views/               # JSP views
│   │       │   └── web.xml              # Web application configuration
│   │       └── index.jsp                # Home page
│   └── test/                            # Unit tests
└── pom.xml                              # Maven configuration
```

## Architecture

The application follows a layered architecture:

1. **Model Layer**: Entity classes representing database tables
2. **DAO Layer**: Data Access Objects for database operations
3. **Service Layer**: Business logic and transaction management
4. **Controller Layer**: Servlets for handling HTTP requests
5. **View Layer**: JSP pages for rendering HTML

### Web Application Flow

1. Client makes HTTP request
2. Web filters check authentication and authorization
3. Servlet processes the request and interacts with services
4. Service layer implements business logic using DAOs
5. DAOs interact with the database
6. Servlet forwards to JSP view for rendering
7. JSP view generates HTML response using EL expressions and JSTL tags

## Role-Based Access

- **Admin**: Full access to all features
- **Trainer**: Create and manage training programs and courses
- **Trainee**: View and enroll in training programs, view materials 