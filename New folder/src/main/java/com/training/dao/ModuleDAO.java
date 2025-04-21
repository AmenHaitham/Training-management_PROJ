package com.training.dao;

import com.training.model.Module;
import java.util.List;

public interface ModuleDAO {
    Module create(Module module);
    Module findById(Long id);
    List<Module> findAll();
    List<Module> findByCourse(Long courseId);
    Module update(Module module);
    void delete(Long id);
    void reorderModules(Long courseId, List<Long> moduleIds);
} 