package com.inspirage.ilct.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inspirage.ilct.enums.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "users")
public class User extends BaseDoc  {
    @Indexed
    private String userId;
    @TextIndexed
    private String firstName;
    @TextIndexed
    private String middleName;
    @TextIndexed
    private String lastName;
    @TextIndexed
    private String nickName;
    private String password; 
    private String temporaryPassword;
    @TextIndexed
    private String phone1;
    @TextIndexed
    private String phone2;
    @TextIndexed
    private String email;
    @TextIndexed
    private String defaultUserRole;
    @TextIndexed
    private String dataProfile;
    private String grantorUserRole;
    private String lastLoginDate;
    private String accountLockout;
    private String accountPolicy;
    private String mapLanguageId;
    private int unsuccessfulLoginAttempts = 0;
    @JsonIgnore
    private LocalDateTime temporaryPasswordExpiryDate;
    private boolean isActive = true;
    @Indexed
    @Enumerated(value = EnumType.STRING)
    private RoleType role = RoleType.CUSTOMER;
    private String createdByUserId;
    @Indexed
    private String preferredLanguage;
    private String countryCode;
    private String baseMapCountry;
    private String timeZone;
    private String timeZoneId;
    @Enumerated(value = EnumType.STRING)
    private Units.TemperatureUnit temperatureUnit;
    @Enumerated(value = EnumType.STRING)
    private Units.DistanceUnit distanceUnit;
    private String locationCountryCode;
    private String locationProvince;
    private String locationCity;
    private List<String> rules = new ArrayList<>();
    private String contactId;
    private UserConfigurations userConfigurations;
    private List<String> watchList = new ArrayList<>();

}
