package com.inspirage.ilct.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="Notification")
@EqualsAndHashCode(callSuper=false)
public class NotificationDoc extends BaseDoc {

	private String toUserId;

	private String locationId;

	private String shipmentId;

	private String message;

	private Boolean isRead = Boolean.FALSE;

}
