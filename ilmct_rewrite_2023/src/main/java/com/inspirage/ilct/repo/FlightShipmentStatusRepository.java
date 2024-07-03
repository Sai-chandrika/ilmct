package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.FlightShipmentStatusDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface FlightShipmentStatusRepository extends MongoRepository<FlightShipmentStatusDoc, String> {

    List<FlightShipmentStatusDoc> findListByLoadId(String loadId);
    //un used
    //List<FlightShipmentStatusDoc> findByLoadId(String loadId, Pageable pageable);

    void deleteByLoadId(String loadId);
    //un used
    //long countByLoadId(String loadId);


    Map<String, Long> countByLoadIdIn(List<String> shipmentV2loadIds);
}
