package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 16-11-2023
 */
@Getter
@Setter
@Document(collection = "language")
public class LanguageDoc extends BaseDoc{
    private String description;
    private String languageCode;
    private Boolean status = Boolean.TRUE;
}
