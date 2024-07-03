package com.inspirage.ilct.repo;
import com.inspirage.ilct.documents.OrderV2Doc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersV2Repository extends MongoRepository<OrderV2Doc, String> {

    List<OrderV2Doc> findAllByShipmentIdIn(List<String> collect);

    Optional<OrderV2Doc> findByOrderId(String order);

    List<OrderV2Doc> findByShipmentId(String loadID);

}