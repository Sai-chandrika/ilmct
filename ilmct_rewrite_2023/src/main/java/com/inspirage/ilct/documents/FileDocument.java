package com.inspirage.ilct.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "fileDocument")
public class FileDocument extends BaseDoc{
    private String fileId;
    private String fileName;
    @JsonIgnore
    private InputStream file;
    @JsonIgnore
    private String fileType;
    private String comment;
    private String uploadedBy;
}
