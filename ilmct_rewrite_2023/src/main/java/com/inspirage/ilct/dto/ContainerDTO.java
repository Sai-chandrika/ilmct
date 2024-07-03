package com.inspirage.ilct.dto;

import lombok.Data;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@Data
public class ContainerDTO {
    private String ContainerID;

    private String ContainerNumber;

    private String SealNumber;

    private String ContainerType;

    public ContainerDTO() {
    }
}
