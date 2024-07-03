package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.Container;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContainerRepository extends MongoRepository<Container, String> {
	Container findByContainerNumberAndShipContainerId(String containerId,String shipContainerId);

    List<Container> findByShipmentsV2LoadIDAndShipContainerId(String loadID, String id);
}
