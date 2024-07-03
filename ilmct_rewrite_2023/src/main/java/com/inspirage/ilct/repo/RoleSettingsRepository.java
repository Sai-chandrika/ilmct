package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.enums.RoleType;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
public interface RoleSettingsRepository extends MongoRepository<RoleSettings, String> {
    RoleSettings findOneByRole(RoleType roleType);

}
