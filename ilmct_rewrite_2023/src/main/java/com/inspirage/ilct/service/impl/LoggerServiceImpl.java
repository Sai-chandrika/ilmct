package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.Log;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.repo.LoggingRepository;
import com.inspirage.ilct.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoggerServiceImpl implements LoggerService {

    private LoggingRepository loggingRepository;

    private TokenUtilService tokenUtilService;

    private MongoTemplate mongoTemplate;

    @Override
    public void saveLog(Log log, HttpServletRequest request) {
        log.setUser(tokenUtilService.getUserId(request));
        loggingRepository.save(log);
    }

    @Override
    public void saveLog(Log log, String userId) {
        log.setUser(userId);
        loggingRepository.save(log);
    }

    @Override
    public void saveLog(Log log) {
        loggingRepository.save(log);
    }

    @Override
    public ApiResponse fetchLog(Optional<LocalDate> fromDate, Optional<LocalDate> toDate, Integer index, Integer numberOfRecords, Optional<String> search) {
        if ((fromDate.isPresent() && toDate.isPresent()) || search.isPresent()) {
            Pageable pageable = PageRequest.of(index, numberOfRecords);
            Query query = generateQuery(toDate, fromDate, pageable, search);
            query.with(Sort.by(Sort.Direction.DESC, "localDateTime"));
            List<Log> logs = mongoTemplate.find(query, Log.class);
            return new ApiResponse.ApiResponseBuilder().setData(PageableExecutionUtils.getPage(logs, pageable, () -> mongoTemplate.count(query, Log.class))).setStatus(HttpStatus.OK).build();
        } else return fetchAllLog();
    }

    private ApiResponse fetchAllLog() {
        List<Log> logs = loggingRepository.findAll(Sort.by(new Sort.Order(Sort.Direction.DESC, "localDateTime")));
        return new ApiResponse(HttpStatus.OK, logs);
    }

    private ApiResponse fetchOneWeekLog() {
        LocalDate fromDateTime = LocalDate.now();
        LocalDate oneWeekDateTime = LocalDate.now().minusDays(3);
        List<Log> logs = loggingRepository.findByLocalDateTimeBetween(oneWeekDateTime, fromDateTime, Sort.by(new Sort.Order(Sort.Direction.DESC, "localDateTime")));
        return new ApiResponse(HttpStatus.OK, logs);
    }

    private Query generateQuery(Optional<LocalDate> toDate, Optional<LocalDate> fromDate, Pageable pageable, Optional<String> search) {
        if (toDate.isPresent() && fromDate.isPresent() && search.isPresent()) {
            LocalDate fromDateTime = fromDate.get();
            LocalDate toDateTime = toDate.get();
            return new Query().addCriteria(new Criteria().andOperator(Criteria.where("localDateTime").lte(fromDateTime).gte(toDateTime), new Criteria().orOperator(Criteria.where("message").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("loadId").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("user").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("type").regex(search.get(), "i"), Criteria.where("actionEnum").regex(search.get(), "i"))))))).with(pageable);
        } else if (toDate.isPresent() && fromDate.isPresent()) {
            LocalDate fromDateTime = fromDate.get();
            LocalDate toDateTime = toDate.get();
            return new Query().addCriteria(Criteria.where("localDateTime").lte(fromDateTime).gte(toDateTime)).with(pageable);
        } else {
            return new Query().addCriteria(new Criteria().orOperator(Criteria.where("message").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("loadId").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("user").regex(search.get(), "i"), new Criteria().orOperator(Criteria.where("type").regex(search.get(), "i"), Criteria.where("actionEnum").regex(search.get(), "i")))))).with(pageable);
        }
    }
}
