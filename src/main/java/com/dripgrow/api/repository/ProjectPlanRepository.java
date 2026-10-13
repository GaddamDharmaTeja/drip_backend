package com.dripgrow.api.repository;

import com.dripgrow.api.model.ProjectPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectPlanRepository extends MongoRepository<ProjectPlan, String> {
    Optional<ProjectPlan> findBySlug(String slug);
    List<ProjectPlan> findByActiveTrue();
}
