package com.inspirage.ilct.dto.bean.rewrite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 18-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CityTimeZoneDtO {
    private  String id;
    private  String countryCode;
    private  String cityName;
    private  String province;
    private  String timeZone;
    private String offset;
}
