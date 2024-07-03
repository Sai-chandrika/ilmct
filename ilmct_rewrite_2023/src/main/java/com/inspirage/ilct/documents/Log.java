package com.inspirage.ilct.documents;
import com.inspirage.ilct.enums.ActionEnum;
import com.inspirage.ilct.enums.MessageTypeEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Data
@Document(collection = "logDocument")
public class Log {
    private MessageTypeEnum type;
    private String message;
    @Indexed
    private LocalDateTime localDateTime;
    private String loadId;
    private String user;
    private ActionEnum actionEnum;
}
