package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.LatLng;
import com.inspirage.ilct.documents.ShipmentStatusDoc;
import com.inspirage.ilct.documents.ShipmentV2;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.enums.StatusEnum;
import com.inspirage.ilct.repo.LocationDocRepository;
import com.inspirage.ilct.repo.ShipmentStatusRepository;
import com.inspirage.ilct.repo.ShipmentV2Repository;
import com.inspirage.ilct.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.inspirage.ilct.util.Constants.*;
import static com.inspirage.ilct.util.Constants.TM_VESSEL_CO;


@Service
public class OtmServiceImpl implements OtmService {

    @Autowired
    ShipmentV2Repository shipmentV2Repository;
    @Autowired
    LocationDocRepository locationDocRepository;
    @Autowired
    ShipmentStatusRepository shipmentStatusRepository;


    @Override
    public ApiResponse getCurrentRoute(String loadId) {
        ShipmentV2 shipment = shipmentV2Repository.findOneByLoadID(loadId).orElse(null);
        List<LatLng> bean = new ArrayList<>();
        if (shipment != null) {
            populateEventPoints(shipment, bean);
        } else {
            return new ApiResponse(HttpStatus.NO_CONTENT, "No shipment existed with this load Id");
        }
        return new ApiResponse(HttpStatus.OK,"data fetch successfully", bean);
    }

    public void populateEventPoints(ShipmentV2 shipment, List<LatLng> bean) {
        bean.add(locationDocRepository.findOneBySiteId(shipment.getSource().getSiteId()).orElse(locationDocRepository.findBySiteId(shipment.getSource().getSiteId()).get(0))
                .getLocation());
        if (shipment.getMode().equalsIgnoreCase(TM_TL) || shipment.getMode().equalsIgnoreCase(TM_LTL) ||
                shipment.getMode().equalsIgnoreCase(GROUP_AGE) || shipment.getMode().equalsIgnoreCase(INTER_MODEL) || shipment.getMode().equalsIgnoreCase(TM_TRUCK)) {
            List<ShipmentStatusDoc> shipmentStatusDocs = shipmentStatusRepository.findByLoadIdOrderByEventDateOtmGLogDateAsc(shipment.getLoadID());
            if (!shipmentStatusDocs.isEmpty()) {
                bean.addAll(shipmentStatusDocs.stream().filter(shipmentStatusDoc -> !shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())).map(ShipmentStatusDoc::getLocation).toList());
            } else {
                bean.add(locationDocRepository.findOneBySiteId(shipment.getSource().getSiteId()).orElse(locationDocRepository.findBySiteId(shipment.getSource().getSiteId()).get(0))
                        .getLocation());
            }

        } else if (shipment.getMode().equalsIgnoreCase(TM_VESSEL) || shipment.getMode().equalsIgnoreCase(TM_VESSEL_CO)) {

            List<ShipmentStatusDoc> shipmentStatusDocs = shipmentStatusRepository.findByLoadIdOrderByEventDateOtmGLogDateAsc(shipment.getLoadID());
            if (!shipmentStatusDocs.isEmpty()) {
                bean.addAll(shipmentStatusDocs.stream().filter(shipmentStatusDoc -> !shipmentStatusDoc.getStatusCodeId().equalsIgnoreCase(StatusEnum.INTRANSIT.name())).map(ShipmentStatusDoc::getLocation)
                        .toList());
            } else {
                bean.add(locationDocRepository.findOneBySiteId(shipment.getSource().getSiteId()).orElse(locationDocRepository.findBySiteId(shipment.getSource().getSiteId()).get(0))
                        .getLocation());
            }
        }
    }
}