package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.LoginKeyInfo;
import com.inspirage.ilct.documents.RoleSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 02-11-2023
 */
public interface LoginKeyInfoRepo extends MongoRepository<LoginKeyInfo, String> {
    Optional<LoginKeyInfo> findUniqueByIpAddress(String ip);
}
