package com.dripgrow.api.repository;

import com.dripgrow.api.model.AppSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppSettingsRepository extends MongoRepository<AppSettings, String> {
}
