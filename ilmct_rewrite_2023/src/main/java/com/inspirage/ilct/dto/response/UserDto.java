package com.inspirage.ilct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.documents.Units;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {
    @Indexed
    private String userId;
    @JsonProperty(required = true,value = "first_name")
    private String firstName;
    @JsonProperty(required = true,value = "last_name")
    private String lastName;
    private String nickName;
    private String phone1;
    private String phone2;
    private String email;
    @Indexed
    private String role;
    private String preferredLanguage;
    private String countryCode;
    private String baseMapCountry;
    private String mapLanguageId;
    private Units.TemperatureUnit temperatureUnit;
    private Units.DistanceUnit distanceUnit;
    private String locationCountryCode;
    private String locationProvince;
    private String locationCity;
    private String timeZone;
    private String timeZoneId;
    private List<String> rules = new ArrayList<>();
    private Map<String, String> appliedRules = new HashMap<>();
}
