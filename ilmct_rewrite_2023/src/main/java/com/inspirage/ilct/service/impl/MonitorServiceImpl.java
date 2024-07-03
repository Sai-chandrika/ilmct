package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.MonitorBean;
import com.inspirage.ilct.enums.MonitorCollection;
import com.inspirage.ilct.enums.MonitorMaterial;
import com.inspirage.ilct.enums.ShipmentClassification;
import com.inspirage.ilct.exceptions.NotFoundException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.MonitorRepository;
import com.inspirage.ilct.repo.UserConfigurationsRepo;
import com.inspirage.ilct.service.MonitorService;

import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class MonitorServiceImpl implements MonitorService {
    @Autowired
    MonitorRepository monitorRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserConfigurationsRepo configurationsRepository;
    private static final String COLLECTION_SHIPMENT = "ShipmentV2";

    private static final String COLLECTION_CONTAINER = "container";
    @Override
    public ApiResponse saveMonitor(MonitorBean monitorBean, HttpServletRequest request) {
        String validate = validateMonitor(monitorBean);
        if (!Utility.isEmpty(validate)) {
            return new ApiResponse(HttpStatus.OK.value(),validate);
        }
        Monitor monitor = new Monitor();
        monitor.setIsActive(Boolean.TRUE);
        monitor.setMonitorName(monitorBean.getMonitorName());
        monitor.setMonitorSequence(monitorBean.getMonitorSequence());
        monitor.setMonitorType(ShipmentClassification.valueOf(monitorBean.getMonitorType()));
        monitor.setMonitorCollection(MonitorCollection.valueOf(monitorBean.getMonitorCollection()));
        monitor.setMonitorMaterial(MonitorMaterial.valueOf(monitorBean.getMonitorMaterial()));
        monitor.setMonitorQuery(monitorBean.getMonitorQuery());
        monitor = monitorRepository.save(monitor);
        monitorBean.setId(monitor.getId());
        monitorBean.setIsActive(monitor.getIsActive());
        return new ApiResponse(HttpStatus.OK,"saved monitor", monitorBean);
    }

    @Override
    public ApiResponse updateMonitor(MonitorBean monitorBean, HttpServletRequest request) {
        String validate = validateMonitor(monitorBean);
        if (!Utility.isEmpty(validate) || Utility.isEmpty(monitorBean.getId())) {
            return new ApiResponse(HttpStatus.OK.value(), validate);
        }
        Optional<Monitor> monitorOptional = monitorRepository.findById(monitorBean.getId());
        if (monitorOptional.isPresent()) {
            Monitor monitor = monitorOptional.get();
            monitor.setMonitorName(monitorBean.getMonitorName());
            monitor.setMonitorSequence(monitorBean.getMonitorSequence());
            monitor.setMonitorType(ShipmentClassification.valueOf(monitorBean.getMonitorType()));
            monitor.setMonitorCollection(MonitorCollection.valueOf(monitorBean.getMonitorCollection()));
            monitor.setMonitorMaterial(MonitorMaterial.valueOf(monitorBean.getMonitorMaterial()));
            monitor.setMonitorQuery(monitorBean.getMonitorQuery());
            monitorRepository.save(monitor);
            return new ApiResponse(HttpStatus.OK,"updated successfully", monitor);
        } else {
            throw new NotFoundException("No monitor found !");
        }
    }

    @Override
    public ApiResponse deleteMonitor(String monitorId, HttpServletRequest request) {
        if (StringUtils.isEmpty(monitorId)) {
            throw new NotFoundException("Invalid Monitor Id ");
        }
        Optional<Monitor> monitorOptional = monitorRepository.findById(monitorId);
        if (monitorOptional.isPresent()  && monitorOptional.get().getIsActive().equals(true)) {
            Monitor monitor=monitorOptional.get();
            monitor.setIsActive(Boolean.FALSE);
            monitorRepository.save(monitor);
            return new ApiResponse(HttpStatus.OK.value(), "monitor deleted successfully");
        } else {
            throw new NotFoundException("No monitor found with given id ");
        }
    }


    private String validateMonitor(MonitorBean monitorBean) {
        if (Utility.isEmpty(monitorBean.getMonitorName())) {
            return "Please provide valid monitor name";
        }
        if (Utility.isEmpty(monitorBean.getMonitorType())) {
            return "Please provide valid monitor type";
        }
        if (Utility.isEmpty(monitorBean.getMonitorSequence())) {
            return "Please provide valid monitor sequence";
        }
        if (Utility.isEmpty(monitorBean.getMonitorQuery())) {
            return "Please provide valid monitor query";
        }
        try {
            if (monitorBean.getMonitorCollection().equalsIgnoreCase(MonitorCollection.SHIPMENT.name())) {
                mongoTemplate.count(transformMonitorQuery(monitorBean), ShipmentV2.class, COLLECTION_SHIPMENT);
            } else if (monitorBean.getMonitorCollection().equalsIgnoreCase(MonitorCollection.CONTAINER.name())) {
                mongoTemplate.count(transformMonitorQuery(monitorBean), Container.class, COLLECTION_CONTAINER);
            }
        } catch (Exception e) {
            return "Invalid query";
        }
        return null;
    }


    private Query transformMonitorQuery(MonitorBean monitor) {
        BasicQuery query = new BasicQuery(monitor.getMonitorQuery());
        Criteria criteria = new Criteria();
        if (monitor.getMonitorCollection().equalsIgnoreCase(MonitorCollection.SHIPMENT.name())) {
            if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.BM.name())) {
                criteria.andOperator(Criteria.where("loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("loadReferences.content").is("BM"));
            } else if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.PM.name())) {
                criteria.andOperator(Criteria.where("loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("loadReferences.content").is("PM"));
            } else if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.AM.name())) {
                criteria.andOperator(Criteria.where("loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("loadReferences.content").is("AM"));
            }
        } else if (monitor.getMonitorCollection().equalsIgnoreCase(MonitorCollection.CONTAINER.name())) {
            if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.BM.name())) {
                criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("shipmentsV2.loadReferences.content").is("BM"));
            } else if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.PM.name())) {
                criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("shipmentsV2.loadReferences.content").is("PM"));
            } else if (monitor.getMonitorMaterial().equalsIgnoreCase(MonitorMaterial.AM.name())) {
                criteria.andOperator(Criteria.where("shipmentsV2.loadReferences.loadReferenceType")
                                .is("SHIPMENT_MATERIAL"),
                        Criteria.where("shipmentsV2.loadReferences.content").is("AM"));
            }
        }
        query.addCriteria(criteria);
        return query;
    }




    @Override
    public ApiResponse getMonitorsBasedOnUser(String userId,HttpServletRequest request){
        if (Utility.isEmpty(userId)) {
            throw new UserNotFoundException("No User found with given user id");
        }
        UserConfigurations userConfigurations = configurationsRepository.findByUserId(userId);
        List<MonitorBean> monitors = new LinkedList<>();
        if (userConfigurations != null) {
            List<String> monitorList = userConfigurations.getMonitorSettings().stream().map(Monitor::getId).collect(Collectors.toList());
            monitors = monitorRepository.findByIsActiveTrueAndIdIn(monitorList).stream().map(this::monitorToBean).collect(Collectors.toList());
            try {
                monitors.forEach(monitor -> {
                    if (monitor.getMonitorCollection().equalsIgnoreCase(MonitorCollection.SHIPMENT.name())) {
                        long shipmentCount = mongoTemplate.count(transformMonitorQuery(monitor), ShipmentV2.class, COLLECTION_SHIPMENT);
                        monitor.setCount(shipmentCount);
                    } else if (monitor.getMonitorCollection().equalsIgnoreCase(MonitorCollection.CONTAINER.name())) {
                        long containerCount = mongoTemplate.count(transformMonitorQuery(monitor), Container.class, COLLECTION_CONTAINER);
                        monitor.setCount(containerCount);
                    }
                });
            } catch (Exception e) {
                return new ApiResponse(HttpStatus.BAD_REQUEST,"something went wrong",monitors);
            }
        }
        return new ApiResponse(HttpStatus.OK,monitors);
    }

    @Override
    public ApiResponse getAllMonitors( Integer pageIndex, Integer numberOfRecord, HttpServletRequest request) {
        ApiResponse apiResponse=new ApiResponse();
        List<Monitor> monitorList=monitorRepository.findAll();
        List<Monitor> monitors;
            if (pageIndex != null && numberOfRecord != null) {
                monitors = monitorRepository.findByIsActiveTrue(PageRequest.of(pageIndex, numberOfRecord, Sort.by(new Sort.Order(Sort.Direction.DESC, "monitorSequence"))));
                return apiResponse.withData(monitorsToBeans(monitors)).message("success")
                        .pageInfo(pageIndex, numberOfRecord, monitorList.size());
            }
        else{
            monitors=monitorList.stream().filter(a->a.getIsActive().equals(true)).toList();
            return apiResponse.withData(monitorsToBeans(monitors)).message("success")
                    .pageInfo(0,monitors.size(), monitorList.size());
        }
    }


    private static List<MonitorBean> monitorsToBeans(List<Monitor> monitors) {
        List<MonitorBean> monitorBeans = new ArrayList<>();
        for (Monitor monitor : monitors) {
            MonitorBean monitorBean = new MonitorBean();
            monitorBean.setMonitorName(monitor.getMonitorName());
            monitorBean.setMonitorQuery(monitor.getMonitorQuery());
            monitorBean.setMonitorSequence(monitor.getMonitorSequence());
            monitorBean.setMonitorType(monitor.getMonitorType().name());
            monitorBean.setMonitorCollection(monitor.getMonitorCollection().name());
            monitorBean.setMonitorMaterial(monitor.getMonitorMaterial().name());
            monitorBean.setIsActive(monitor.getIsActive());
            monitorBean.setId(monitor.getId());
            monitorBeans.add(monitorBean);
        }
        return monitorBeans;
    }


    private  MonitorBean monitorToBean(Monitor monitor) {
        MonitorBean monitorBean = new MonitorBean();
        monitorBean.setMonitorName(monitor.getMonitorName());
        monitorBean.setMonitorQuery(monitor.getMonitorQuery());
        monitorBean.setMonitorSequence(monitor.getMonitorSequence());
        monitorBean.setMonitorType(monitor.getMonitorType().name());
        monitorBean.setMonitorCollection(monitor.getMonitorCollection().name());
        monitorBean.setMonitorMaterial(monitor.getMonitorMaterial().name());
        monitorBean.setIsActive(monitor.getIsActive());
        monitorBean.setId(monitor.getId());
        return monitorBean;
    }
}
