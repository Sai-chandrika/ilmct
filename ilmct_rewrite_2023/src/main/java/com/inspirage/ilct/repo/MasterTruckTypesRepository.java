package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.MasterTruckType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MasterTruckTypesRepository extends MongoRepository<MasterTruckType,String> {
    List<MasterTruckType> findByTypeIn(Set<String> collect);

    Optional<MasterTruckType> findByIsDefault(boolean b);


    MasterTruckType findByGroup(String truckType);
}
