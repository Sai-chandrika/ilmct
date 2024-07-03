package com.inspirage.ilct.dto.bean.rewrite;

import lombok.Data;

@Data
public class StatusCountBean {
    public Long onTime;
    public Long late;
    public Long arrivingEarly;
    public Long untracked;
}
