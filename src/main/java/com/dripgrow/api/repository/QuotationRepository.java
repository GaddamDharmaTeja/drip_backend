package com.dripgrow.api.repository;

import com.dripgrow.api.model.Quotation;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {
    List<Quotation> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
