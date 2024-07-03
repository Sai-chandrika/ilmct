package com.inspirage.ilct.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.documents.Units;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.response.UserConfigurationsBean;
import com.inspirage.ilct.service.impl.UserServiceImpl;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserBean {
    private String id;
    @Indexed
    @JsonProperty(required = true)
    private String userId;
    @NotBlank(message = "firstName is mandatory")
    @JsonProperty(required = true,value = "first_name")
    private String firstName;

    private String middleName;
    @JsonProperty(required = true,value = "last_name")
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    private String nickName;
    @JsonProperty(required = true)
    @NotBlank(message = "password is mandatory")
    private String password;
    @NotBlank(message = "phone1 is mandatory")
    @JsonProperty(required = true)
    private String phone1;
    @NotBlank(message = "phone2 is mandatory")
    private String phone2;
    @JsonProperty(required = true)
    @NotBlank(message = "email is mandatory")
    private String email;

    private String dataProfile;
    @JsonProperty(required = true)
    private String grantorUserRole;

    private String jwtToken;
    @Indexed
    @NotBlank(message = "role is mandatory")
    private String role;

    private String mapLanguageId;
    private String preferredLanguage;
    private String countryCode;
    private String baseMapCountry;
    private String timeZone;
    private String timeZoneId;

    @JsonProperty(required = true)
    private Units.TemperatureUnit temperatureUnit;
    @JsonProperty(required = true)
    private Units.DistanceUnit distanceUnit;

    private String locationCountryCode;
    private String locationProvince;
    private String locationCity;
    private List<String> rules = new ArrayList<>();

    private Map<String, String> appliedRules = new HashMap<>();

    private String contactId;

    private UserConfigurationsBean userConfigurationsBean;

    private RoleSettings roleSettings;

    private List<String> watchList = new ArrayList<>();

}
