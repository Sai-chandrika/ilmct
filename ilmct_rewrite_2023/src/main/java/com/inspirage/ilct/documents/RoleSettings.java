package com.inspirage.ilct.documents;

import com.inspirage.ilct.enums.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
@Getter
@Setter
@ToString
@Document(collection = "roleSettings")
public class RoleSettings extends BaseDoc{
    @Enumerated(EnumType.STRING)
    private RoleType role;
    private Boolean users;
    private Boolean manageRules;
    private Boolean savedFilters;
    private Boolean savedQueries;
    private Boolean userSettings;
    private Boolean createGeoFence;
    private Boolean monitorAndExecutiveDashboards;
    private Boolean analytics;

    public RoleSettings(){
        this.manageRules = Boolean.FALSE;
        this.savedFilters = Boolean.FALSE;
        this.savedQueries = Boolean.FALSE;
        this.userSettings = Boolean.FALSE;
        this.createGeoFence = Boolean.FALSE;
        this.monitorAndExecutiveDashboards = Boolean.FALSE;
        this.analytics = Boolean.FALSE;
    }
}
