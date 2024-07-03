package com.inspirage.ilct.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "gid")
public class Gid extends BaseDoc {
    private String domainName;
    private String xid;
}
