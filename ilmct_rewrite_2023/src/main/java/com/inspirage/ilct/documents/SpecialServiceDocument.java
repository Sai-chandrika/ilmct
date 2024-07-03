package com.inspirage.ilct.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 06-12-2023
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "special_services")
public class SpecialServiceDocument extends BaseDoc{
    private String specialServiceName;
    private String specialServiceIconUpload;
    private Boolean isActive = Boolean.TRUE;
}
