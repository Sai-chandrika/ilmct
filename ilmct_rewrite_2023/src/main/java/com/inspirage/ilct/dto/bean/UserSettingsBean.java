package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspirage.ilct.documents.Units;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 20-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserSettingsBean {
    @JsonProperty(required = true)
    private String preferredLanguage;

    @JsonProperty(required = true)
    private String countryCode;
    private String timeZone;

    private String timeZoneId;

    private String mapLanguageId;

    @JsonProperty(required = true)
    private Units.TemperatureUnit temperatureUnit;
    @JsonProperty(required = true)
    private Units.DistanceUnit distanceUnit;
    private String city;
}
