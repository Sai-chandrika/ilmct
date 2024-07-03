package com.inspirage.ilct.documents;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "alerts")
public class Alerts extends BaseDoc{
    private String alertName;
    private String alertIconUpload;
    private Boolean isActive = Boolean.TRUE;
}
