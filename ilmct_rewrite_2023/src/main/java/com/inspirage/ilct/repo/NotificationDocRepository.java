package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.NotificationDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationDocRepository extends MongoRepository<NotificationDoc, String>{

	int countByToUserIdAndShipmentIdAndLocationId(String toUserId, String shipmentId, String locationId);

	List<NotificationDoc> findByToUserIdOrderByIdDesc(String id);

	NotificationDoc findByToUserIdAndId(String id, String notiId);

}
