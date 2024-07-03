package com.inspirage.ilct.repo;

import com.inspirage.ilct.documents.Log;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoggingRepository extends MongoRepository <Log, String>{

    List<Log> findByLocalDateTimeBetween(LocalDate fromDate, LocalDate toDate, Sort sort);


}
