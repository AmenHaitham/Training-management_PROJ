package com.example.service.impl;

import com.example.dao.EnrollmentDao;
import com.example.dao.TrainingProgramDao;
import com.example.dao.UserDao;
import com.example.dao.impl.EnrollmentDaoImpl;
import com.example.dao.impl.TrainingProgramDaoImpl;
import com.example.dao.impl.UserDaoImpl;
import com.example.model.Enrollment;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.service.EnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EnrollmentService
 */
public class EnrollmentServiceImpl implements EnrollmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);
    
    private final EnrollmentDao enrollmentDao;
    private final UserDao userDao;
    private final TrainingProgramDao trainingProgramDao;
    
    /**
     * Constructor
     */
    public EnrollmentServiceImpl() {
        this.enrollmentDao = new EnrollmentDaoImpl();
        this.userDao = new UserDaoImpl();
        this.trainingProgramDao = new TrainingProgramDaoImpl();
    }
    
    /**
     * Constructor with dependency injection
     * @param enrollmentDao The EnrollmentDao implementation to use
     * @param userDao The UserDao implementation to use
     * @param trainingProgramDao The TrainingProgramDao implementation to use
     */
    public EnrollmentServiceImpl(EnrollmentDao enrollmentDao, UserDao userDao, TrainingProgramDao trainingProgramDao) {
        this.enrollmentDao = enrollmentDao;
        this.userDao = userDao;
        this.trainingProgramDao = trainingProgramDao;
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        logger.debug("Finding enrollment by ID: {}", id);
        return enrollmentDao.findById(id);
    }

    @Override
    public List<Enrollment> findAll() {
        logger.debug("Finding all enrollments");
        return enrollmentDao.findAll();
    }

    @Override
    public List<Enrollment> findByTrainee(Long traineeId) {
        logger.debug("Finding enrollments by trainee: {}", traineeId);
        return enrollmentDao.findByTrainee(traineeId);
    }

    @Override
    public List<Enrollment> findByTrainingProgram(Long trainingProgramId) {
        logger.debug("Finding enrollments by training program: {}", trainingProgramId);
        return enrollmentDao.findByTrainingProgram(trainingProgramId);
    }

    @Override
    public List<Enrollment> findByStatus(String status) {
        logger.debug("Finding enrollments by status: {}", status);
        return enrollmentDao.findByStatus(status);
    }

    @Override
    public Enrollment enroll(Long traineeId, Long trainingProgramId) throws IllegalArgumentException {
        logger.debug("Enrolling trainee {} in training program {}", traineeId, trainingProgramId);
        
        // Verify trainee exists
        Optional<User> traineeOpt = userDao.findById(traineeId);
        if (!traineeOpt.isPresent() || !"TRAINEE".equals(traineeOpt.get().getRole())) {
            throw new IllegalArgumentException("Trainee does not exist or user is not a trainee");
        }
        
        // Verify training program exists and is active
        Optional<TrainingProgram> programOpt = trainingProgramDao.findById(trainingProgramId);
        if (!programOpt.isPresent()) {
            throw new IllegalArgumentException("Training program does not exist");
        }
        
        TrainingProgram program = programOpt.get();
        if (!"ACTIVE".equals(program.getStatus())) {
            throw new IllegalArgumentException("Training program is not active and cannot accept enrollments");
        }
        
        // Check if already enrolled
        if (enrollmentDao.isEnrolled(traineeId, trainingProgramId)) {
            throw new IllegalArgumentException("Trainee is already enrolled in this training program");
        }
        
        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setTraineeId(traineeId);
        enrollment.setTrainingProgramId(trainingProgramId);
        enrollment.setStatus("ACTIVE");
        enrollment.setEnrolledAt(LocalDateTime.now());
        
        // Load relationships for return value
        enrollment.setTrainee(traineeOpt.get());
        enrollment.setTrainingProgram(program);
        
        return enrollmentDao.save(enrollment);
    }

    @Override
    public Enrollment updateStatus(Long id, String status) throws IllegalArgumentException {
        logger.debug("Updating enrollment {} status to {}", id, status);
        
        Optional<Enrollment> enrollmentOpt = enrollmentDao.findById(id);
        if (!enrollmentOpt.isPresent()) {
            throw new IllegalArgumentException("Enrollment does not exist");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus(status);
        
        return enrollmentDao.save(enrollment);
    }

    @Override
    public Enrollment complete(Long id) throws IllegalArgumentException {
        logger.debug("Completing enrollment: {}", id);
        Optional<Enrollment> enrollmentOpt = enrollmentDao.findById(id);
        if (!enrollmentOpt.isPresent()) {
            throw new IllegalArgumentException("Enrollment does not exist");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus("COMPLETED");
        enrollment.setCompletedAt(LocalDateTime.now());
        
        return enrollmentDao.save(enrollment);
    }

    @Override
    public Enrollment drop(Long id) throws IllegalArgumentException {
        logger.debug("Dropping enrollment: {}", id);
        Optional<Enrollment> enrollmentOpt = enrollmentDao.findById(id);
        if (!enrollmentOpt.isPresent()) {
            throw new IllegalArgumentException("Enrollment does not exist");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus("DROPPED");
        
        return enrollmentDao.save(enrollment);
    }

    @Override
    public boolean isEnrolled(Long traineeId, Long trainingProgramId) {
        logger.debug("Checking if trainee {} is enrolled in training program {}", traineeId, trainingProgramId);
        return enrollmentDao.isEnrolled(traineeId, trainingProgramId);
    }
} 