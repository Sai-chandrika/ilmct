package com.inspirage.ilct.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 03-11-2023
 */
@Document(collection = "city_time_zone_info")
@Data
@EqualsAndHashCode(callSuper = false)
public class CityTimeZoneInfo extends  BaseDoc{
    private String countryCode;

    private String cityName;

    private String province;

    private String timeZone;
}
