package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.enums.StatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ShipmentV2Repository extends MongoRepository<ShipmentV2,String> {
    List<ShipmentV2> findByLoadID(String loadID);

    Optional<ShipmentV2> findOneByLoadID(String loadId);

    List<ShipmentV2> findByOrdersInAndStatusIn(List<String> collect, List<String> lastMileDeliveryStatus);

    List<ShipmentV2> findShipmentByLoadID(String loadId);

    List<ShipmentV2> findLoadIdByModeInAndStatusInAndCreatedDateLessThan(List<String> modes, List<Object> list, LocalDateTime shipmentStatusClearDate);

    long countByStatusInAndCreatedDateLessThan(List<StatusEnum> list, LocalDateTime shipmentClearDate);

    List<ShipmentV2> findLoadIdByModeAndStatusInAndCreatedDateLessThan(String tmAir, List<StatusEnum> list, LocalDateTime flightStatusClearDate);

    List<ShipmentV2> findLoadIdByStatusInAndCreatedDateLessThan(List<StatusEnum> statuses, LocalDateTime createdDate);

    Long countByStatusAndModeIn(String name, List<String> modes);
}
