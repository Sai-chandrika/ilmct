package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RoleSettingBean {
    private String id;
    private String role;
    private Boolean users;
    private Boolean manageRules;
    private Boolean savedFilters;
    private Boolean savedQueries;
    private Boolean userSettings;
    private Boolean createGeoFence;
    private Boolean monitorAndExecutiveDashboards;
    private Boolean analytics;
    public RoleSettingBean(){
        this.manageRules = Boolean.FALSE;
        this.savedFilters = Boolean.FALSE;
        this.savedQueries = Boolean.FALSE;
        this.userSettings = Boolean.FALSE;
        this.createGeoFence = Boolean.FALSE;
        this.monitorAndExecutiveDashboards = Boolean.FALSE;
        this.analytics = Boolean.FALSE;
    }

}
