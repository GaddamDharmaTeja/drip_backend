package com.dripgrow.api.repository;

import com.dripgrow.api.model.SiteContact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SiteContactRepository extends MongoRepository<SiteContact, String> {
}
