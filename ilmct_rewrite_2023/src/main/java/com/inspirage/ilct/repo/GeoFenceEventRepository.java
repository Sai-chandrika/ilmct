package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.GeoFenceEventDoc;
import com.inspirage.ilct.enums.FencingEventType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GeoFenceEventRepository extends MongoRepository<GeoFenceEventDoc, String> {

	List<GeoFenceEventDoc> findAllByShipmentIdAndShipmentStopV2StopNumberAndFenceEventType(String shipmentId, Integer stopSequence, FencingEventType fencingEventType);

	GeoFenceEventDoc findTopByOrderByCreatedDateDesc();
}
