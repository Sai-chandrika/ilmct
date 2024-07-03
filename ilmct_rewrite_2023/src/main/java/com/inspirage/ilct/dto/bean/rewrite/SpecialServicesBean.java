package com.inspirage.ilct.dto.bean.rewrite;

import lombok.Data;

@Data
public class SpecialServicesBean {

    private String id;

    private String specialServiceName;

    private String specialServiceIconUpload;

    private Boolean isActive = Boolean.TRUE;
}
