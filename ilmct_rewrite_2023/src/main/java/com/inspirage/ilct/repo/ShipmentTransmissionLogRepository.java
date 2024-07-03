package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.ShipmentTransmissionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

/**
 * @Author : Deepak Sagar Behera
 * @Created On :  24-May-2018 20:13
 */
public interface ShipmentTransmissionLogRepository extends MongoRepository<ShipmentTransmissionLog, String> {

    void deleteByCreatedDateLessThan(LocalDateTime localDateTime);
    long countByCreatedDateLessThan(LocalDateTime localDateTime);
}
