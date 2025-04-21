package com.training.dao;

import com.training.model.Lesson;
import java.util.List;

public interface LessonDAO {
    Lesson create(Lesson lesson);
    Lesson findById(Long id);
    List<Lesson> findAll();
    List<Lesson> findByModule(Long moduleId);
    Lesson update(Lesson lesson);
    void delete(Long id);
    void reorderLessons(Long moduleId, List<Long> lessonIds);
} 