package com.inspirage.ilct.documents;

import com.inspirage.ilct.enums.MonitorCollection;
import com.inspirage.ilct.enums.MonitorMaterial;
import com.inspirage.ilct.enums.ShipmentClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
@Getter
@Setter
@Document(collection = "monitors")
public class Monitor extends BaseDoc{
    private String monitorName;
    @Enumerated(value = EnumType.STRING)
    private ShipmentClassification monitorType;
    private Integer monitorSequence;
    private String monitorQuery;
    @Enumerated(value = EnumType.STRING)
    private MonitorCollection monitorCollection;
    @Enumerated(value = EnumType.STRING)
    private MonitorMaterial monitorMaterial;
    private Boolean isActive;
}
