package com.dripgrow.api.repository;

import com.dripgrow.api.model.IrrigationPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IrrigationPlanRepository extends MongoRepository<IrrigationPlan, String> {
    Optional<IrrigationPlan> findBySlug(String slug);
    List<IrrigationPlan> findByActiveTrue();
}
