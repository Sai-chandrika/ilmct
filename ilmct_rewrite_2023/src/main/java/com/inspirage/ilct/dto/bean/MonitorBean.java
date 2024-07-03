package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MonitorBean {
    private String id;

    @NotBlank(message = "monitorName is mandatory")
    private String monitorName;
    @NotBlank(message = "monitorType is mandatory")
    private String monitorType;
    @NotNull(message = "monitorSequence is mandatory")
    private Integer monitorSequence;
    @NotBlank(message = "monitorQuery is mandatory")
    private String monitorQuery;
    private Boolean isActive;

    private Long count;

    private String monitorCollection;

    private String monitorMaterial;

}
