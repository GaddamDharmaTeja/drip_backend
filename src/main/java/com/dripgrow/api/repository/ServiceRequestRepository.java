package com.dripgrow.api.repository;

import com.dripgrow.api.model.ServiceRequest;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {
    List<ServiceRequest> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
