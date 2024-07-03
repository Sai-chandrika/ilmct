package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "filters")
@Setter
@Getter
public class FilterDoc extends BaseDoc {
    private String userId;
    private String criteria;
    private String name;
}
