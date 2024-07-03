package com.inspirage.ilct.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSummaryBean {
    private String eventCode;
    private String eventDescription;
    private String coordinates;
    private String eventDate;
    private String address;
    private Integer stopNo;
    private String remarks;
}
