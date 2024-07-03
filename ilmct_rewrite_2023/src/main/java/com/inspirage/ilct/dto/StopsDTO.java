package com.inspirage.ilct.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class StopsDTO {
    private List<StopDTO> Stop = new ArrayList<StopDTO>();
}
