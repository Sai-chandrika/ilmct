package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.bean.rewrite.D1ProcessedEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "d1_processed_doc")
public class D1ProcessedDoc extends BaseDoc {
    private D1ProcessedEvent d1ProcessedBean;
    private String request;
    private String d1ProcessedResponse;
}
