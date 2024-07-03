package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.ApplicationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationSettingsRepository extends MongoRepository<ApplicationSettings, String> {
}
