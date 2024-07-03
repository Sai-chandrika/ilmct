package com.inspirage.ilct.dto;

import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlertsBean {
    private String id;
    private List<String> alertName;
    private List<String> alertIconUpload;
    private Boolean isActive = Boolean.TRUE;
    private String alertValue;
}
