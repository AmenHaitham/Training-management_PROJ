package com.example.service.impl;

import com.example.dao.TrainingProgramDao;
import com.example.dao.impl.TrainingProgramDaoImpl;
import com.example.model.TrainingProgram;
import com.example.service.TrainingProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TrainingProgramService
 */
public class TrainingProgramServiceImpl implements TrainingProgramService {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingProgramServiceImpl.class);
    
    private final TrainingProgramDao trainingProgramDao;
    
    /**
     * Constructor
     */
    public TrainingProgramServiceImpl() {
        this.trainingProgramDao = new TrainingProgramDaoImpl();
    }
    
    /**
     * Constructor with dependency injection
     * @param trainingProgramDao The TrainingProgramDao implementation to use
     */
    public TrainingProgramServiceImpl(TrainingProgramDao trainingProgramDao) {
        this.trainingProgramDao = trainingProgramDao;
    }

    @Override
    public Optional<TrainingProgram> findById(Long id) {
        logger.debug("Finding training program by ID: {}", id);
        return trainingProgramDao.findById(id);
    }

    @Override
    public List<TrainingProgram> findAll() {
        logger.debug("Finding all training programs");
        return trainingProgramDao.findAll();
    }

    @Override
    public List<TrainingProgram> findByTitle(String title) {
        logger.debug("Finding training programs by title: {}", title);
        return trainingProgramDao.findByTitle(title);
    }

    @Override
    public List<TrainingProgram> findByStatus(String status) {
        logger.debug("Finding training programs by status: {}", status);
        return trainingProgramDao.findByStatus(status);
    }

    @Override
    public List<TrainingProgram> findByCreatedBy(Long userId) {
        logger.debug("Finding training programs by creator: {}", userId);
        return trainingProgramDao.findByCreatedBy(userId);
    }

    @Override
    public List<TrainingProgram> findActivePrograms() {
        logger.debug("Finding active training programs");
        return trainingProgramDao.findActivePrograms();
    }

    @Override
    public TrainingProgram create(TrainingProgram program) {
        logger.debug("Creating new training program: {}", program.getTitle());
        
        // Set default status if not provided
        if (program.getStatus() == null) {
            program.setStatus("DRAFT");
        }
        
        // Ensure the program doesn't have an ID (it's a new program)
        program.setId(null);
        
        return trainingProgramDao.save(program);
    }

    @Override
    public TrainingProgram update(TrainingProgram program) throws IllegalArgumentException {
        logger.debug("Updating training program with ID: {}", program.getId());
        
        // Check if the training program exists
        if (program.getId() == null || !trainingProgramDao.existsById(program.getId())) {
            throw new IllegalArgumentException("Training program does not exist with ID: " + program.getId());
        }
        
        // Retrieve the existing program to preserve certain fields if needed
        Optional<TrainingProgram> existingProgramOpt = trainingProgramDao.findById(program.getId());
        if (existingProgramOpt.isPresent()) {
            TrainingProgram existingProgram = existingProgramOpt.get();
            
            // Preserve created_by if not provided
            if (program.getCreatedBy() == null) {
                program.setCreatedBy(existingProgram.getCreatedBy());
            }
            
            // Preserve status if not provided
            if (program.getStatus() == null) {
                program.setStatus(existingProgram.getStatus());
            }
        }
        
        return trainingProgramDao.save(program);
    }

    @Override
    public boolean deleteById(Long id) {
        logger.debug("Deleting training program with ID: {}", id);
        return trainingProgramDao.deleteById(id);
    }

    @Override
    public TrainingProgram activate(Long id) throws IllegalArgumentException {
        logger.debug("Activating training program with ID: {}", id);
        
        Optional<TrainingProgram> programOpt = trainingProgramDao.findById(id);
        if (!programOpt.isPresent()) {
            throw new IllegalArgumentException("Training program does not exist with ID: " + id);
        }
        
        TrainingProgram program = programOpt.get();
        program.setStatus("ACTIVE");
        
        return trainingProgramDao.save(program);
    }

    @Override
    public TrainingProgram complete(Long id) throws IllegalArgumentException {
        logger.debug("Completing training program with ID: {}", id);
        
        Optional<TrainingProgram> programOpt = trainingProgramDao.findById(id);
        if (!programOpt.isPresent()) {
            throw new IllegalArgumentException("Training program does not exist with ID: " + id);
        }
        
        TrainingProgram program = programOpt.get();
        program.setStatus("COMPLETED");
        
        return trainingProgramDao.save(program);
    }

    @Override
    public TrainingProgram cancel(Long id) throws IllegalArgumentException {
        logger.debug("Cancelling training program with ID: {}", id);
        
        Optional<TrainingProgram> programOpt = trainingProgramDao.findById(id);
        if (!programOpt.isPresent()) {
            throw new IllegalArgumentException("Training program does not exist with ID: " + id);
        }
        
        TrainingProgram program = programOpt.get();
        program.setStatus("CANCELLED");
        
        return trainingProgramDao.save(program);
    }
} 