package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.enums.RoleType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    Optional<User> findOneByUserIdIgnoreCase(String userId);

    Optional<User> findOneByUserId(String userId);

    Optional<User> findOneById(String id);

    long countByRoleIn( List<RoleType> adminRoles);

    List<User> findAllByRoleIn(List<RoleType> adminRoles, PageRequest of);


    List<User> findAllByUserIdAndRoleIn(List<User> forAdmin, String userId, PageRequest of);

    List<User> findByRole(RoleType roleType);

    void deleteOneByUserId(String userId);

    List<User> findByUserIdIgnoreCase(String username);

    long countByCreatedByUserIdAndRoleIn(String loginUser, List<RoleType> adminRoles);
}