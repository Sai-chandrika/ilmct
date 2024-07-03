package com.inspirage.ilct.repo;
import com.inspirage.ilct.documents.UserConfigurations;
import com.inspirage.ilct.enums.RoleType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 03-11-2023
 */
public interface UserConfigurationsRepo extends MongoRepository<UserConfigurations, String> {
    UserConfigurations findByRoleType(RoleType role);

    List<UserConfigurations> findAllByRoleType(RoleType role);

    UserConfigurations findByUserId(String userId);

    UserConfigurations findByRoleTypeAndUserIdIsNull(RoleType roleType);

    List<UserConfigurations> findAllByUserId(String userId);
}
