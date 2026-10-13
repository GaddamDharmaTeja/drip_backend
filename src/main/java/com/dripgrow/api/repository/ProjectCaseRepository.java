package com.dripgrow.api.repository;

import com.dripgrow.api.model.ProjectCase;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectCaseRepository extends MongoRepository<ProjectCase, String> {
    List<ProjectCase> findByPublishedTrue();
}
