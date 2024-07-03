package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.ShipmentStatusDoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface ShipmentStatusRepository extends MongoRepository<ShipmentStatusDoc, String> {


    Page<ShipmentStatusDoc> findByLoadIdAndStatusCodeIdNotOrderByCreatedDateDesc(String loadId, String name, Pageable pageable);

    List<ShipmentStatusDoc> findByLoadIdAndStatusCodeIdNotOrderByCreatedDateDesc(String loadId, String name);

    Page<ShipmentStatusDoc> findByLoadIdAndStatusCodeIdNotAndStopSequenceOrderByCreatedDateDesc(String loadId, String name, Integer seqNo, Pageable pageable);

    List<ShipmentStatusDoc> findByLoadIdAndStopSequenceOrderByCreatedDateAsc(String loadId, Integer seqNo);

    List<ShipmentStatusDoc> findByLoadIdOrderByEventDateOtmGLogDateAsc(String loadId);

    int countByLoadId(String loadID);

    Optional<ShipmentStatusDoc> findTopByLoadIdAndStatusCodeIdNotOrderByEventDateOtmGLogDateAsc(String loadID, String name);

    List<ShipmentStatusDoc> findOneByLoadId(String loadId, PageRequest lastUpdated);
    List<ShipmentStatusDoc> findByLoadIdIn(List<String> loadIdList, Sort lastUpdated);

    List<ShipmentStatusDoc> countByLoadIdIn(List<String> loadIds);

    void deleteByLoadId(String loadID);

    List<ShipmentStatusDoc> findByLoadIdInAndStatusCodeIdInOrderByCreatedDateDesc(List<String> collect, List<String> gvitStatusList);
}