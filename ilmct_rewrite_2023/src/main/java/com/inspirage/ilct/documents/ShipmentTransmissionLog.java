package com.inspirage.ilct.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author : Deepak Sagar Behera
 * @Created On :  24-May-2018 20:08
 */
@Document(collection = "shipment_transmission_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class ShipmentTransmissionLog extends BaseDoc {

    private String requestURI;
    private String requestedFrom;
    private String requestedBy;
    private String requestData;
    private Integer responseStatusCode;
}
