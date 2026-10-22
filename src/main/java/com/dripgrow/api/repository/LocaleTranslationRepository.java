package com.dripgrow.api.repository;

import com.dripgrow.api.model.LocaleTranslation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocaleTranslationRepository extends MongoRepository<LocaleTranslation, String> {
}
