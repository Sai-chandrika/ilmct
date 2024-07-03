package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.Monitor;
import io.lettuce.core.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
public interface MonitorRepository extends MongoRepository<Monitor, String> {
    List<Monitor> findByIsActiveTrue(PageRequest monitorSequence);

    List<Monitor> findByIsActiveTrue();

    List<Monitor> findByIsActiveTrueAndIdIn(List<String> monitorList);
}

