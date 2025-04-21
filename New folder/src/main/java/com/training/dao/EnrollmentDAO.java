package com.training.dao;

import com.training.model.Enrollment;
import java.util.List;

public interface EnrollmentDAO {
    Enrollment create(Enrollment enrollment);
    Enrollment findById(Long id);
    List<Enrollment> findByTraineeId(Long traineeId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByTrainingProgramId(Long programId);
    List<Enrollment> findAll();
    Enrollment update(Enrollment enrollment);
    void delete(Long id);
} 