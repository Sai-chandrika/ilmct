package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "driver_rest_time_doc")
public class DriverRestTimeDoc extends BaseDoc {
	private CountryDoc country;
	private Integer driveHrs;
	private Integer driveMins;
	private Integer restHrs;
	private Integer restMins;
	private Boolean status = Boolean.TRUE;
}
