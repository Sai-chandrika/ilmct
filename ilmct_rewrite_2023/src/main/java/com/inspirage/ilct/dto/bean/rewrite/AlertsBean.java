package com.inspirage.ilct.dto.bean.rewrite;

import lombok.Data;

@Data
public class AlertsBean {

    private String id;

    private String alertName;

    private String alertIconUpload;
    private Boolean isActive = Boolean.TRUE;

    private String alertValue;

}
