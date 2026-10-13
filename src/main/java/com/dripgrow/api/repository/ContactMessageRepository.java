package com.dripgrow.api.repository;

import com.dripgrow.api.model.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactMessageRepository extends MongoRepository<ContactMessage, String> {
}
