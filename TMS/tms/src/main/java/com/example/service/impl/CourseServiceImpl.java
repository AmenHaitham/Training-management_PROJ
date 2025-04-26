package com.example.service.impl;

import com.example.dao.CourseDao;
import com.example.dao.impl.CourseDAOImpl;
import com.example.model.Course;
import com.example.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseServiceImpl implements CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseDao courseDao;

    /**
     * Default constructor that initializes with a new CourseDAOImpl
     */
    public CourseServiceImpl() {
        this.courseDao = new CourseDAOImpl();
    }

    /**
     * Constructor that allows dependency injection of the CourseDao
     * 
     * @param courseDao the CourseDao implementation to use
     */
    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public Optional<Course> findById(Long id) {
        logger.debug("Finding course with ID: {}", id);
        return courseDao.findById(id);
    }

    @Override
    public List<Course> findAll() {
        logger.debug("Finding all courses");
        return courseDao.findAll();
    }

    @Override
    public List<Course> findByProgramId(Long programId) {
        logger.debug("Finding courses for program ID: {}", programId);
        return courseDao.findByProgramId(programId);
    }

    @Override
    public List<Course> findByTrainerId(Long trainerId) {
        logger.debug("Finding courses for trainer ID: {}", trainerId);
        return courseDao.findByTrainerId(trainerId);
    }

    @Override
    public List<Course> findByStatus(String status) {
        logger.debug("Finding courses with status: {}", status);
        return courseDao.findByStatus(status);
    }
    
    @Override
    public List<Course> findByTrainingProgram(Long programId) {
        logger.debug("Finding courses for training program ID: {}", programId);
        return courseDao.findByProgramId(programId);
    }
    
    @Override
    public List<Course> findByTrainer(Long trainerId) {
        logger.debug("Finding courses for trainer ID: {}", trainerId);
        return courseDao.findByTrainerId(trainerId);
    }
    
    @Override
    public List<Course> findByTitle(String title) {
        logger.debug("Finding courses with title containing: {}", title);
        return courseDao.findAll().stream()
                .filter(course -> course.getTitle() != null && 
                        course.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findByCategory(String category) {
        logger.debug("Finding courses with category: {}", category);
        return courseDao.findAll().stream()
                .filter(course -> course.getCategory() != null && 
                        course.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public Course create(Course course) {
        logger.debug("Creating new course: {}", course.getTitle());
        return courseDao.save(course);
    }

    @Override
    public Course save(Course course) {
        logger.debug("Saving new course: {}", course.getTitle());
        return courseDao.save(course);
    }

    @Override
    public Course update(Course course) {
        logger.debug("Updating course with ID: {}", course.getId());
        return courseDao.update(course);
    }

    @Override
    public boolean delete(Long id) {
        logger.debug("Deleting course with ID: {}", id);
        return courseDao.delete(id);
    }

    @Override
    public boolean deleteById(Long id) {
        logger.debug("Deleting course with ID: {}", id);
        return courseDao.delete(id);
    }

    @Override
    public int countByProgramId(Long programId) {
        logger.debug("Counting courses for program ID: {}", programId);
        return courseDao.countByProgramId(programId);
    }
} 