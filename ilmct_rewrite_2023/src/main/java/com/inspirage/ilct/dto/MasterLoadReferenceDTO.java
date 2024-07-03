package com.inspirage.ilct.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hari
 * @ProjectName ilmct-backend
 * @since 31-10-2023
 */
@Data
public class MasterLoadReferenceDTO {
    private List<LoadReferenceDTO> LoadReference = new ArrayList<LoadReferenceDTO>();
}
