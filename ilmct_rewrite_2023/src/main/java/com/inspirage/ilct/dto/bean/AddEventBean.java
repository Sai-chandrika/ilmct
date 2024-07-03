package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AddEventBean {

    private String loadId;
    private int truckSequence;
    private String eventType;
    private Double longitude;
    private Double latitude;
    private Date dateAndTime;
    private String timezone;
    private Date otmFlexDateAndTime;
    private String otmFlexTimezone;
    private String eventDescription;
    private String description;
    private String address;
    private String remarks;
}
