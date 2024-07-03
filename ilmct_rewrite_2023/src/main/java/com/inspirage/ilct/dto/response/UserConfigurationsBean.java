package com.inspirage.ilct.dto.response;

import com.inspirage.ilct.documents.Monitor;
import com.inspirage.ilct.documents.UserConfigurations;
import com.inspirage.ilct.dto.bean.ApplicationStatus;
import com.inspirage.ilct.dto.bean.VisibilityBean;
import com.inspirage.ilct.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

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
public class UserConfigurationsBean {
    private String id;

    private String userId;

    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    private List<Monitor> monitorSettings = new ArrayList<>();

    private Integer autoRefreshTime;

    private ApplicationStatus applicationStatus;

    private VisibilityBean vehicleVisibilityMap;

    private VisibilityBean itemVisibilityMap;

    private VisibilityBean containerVisibilityMap;

    private VisibilityBean vehicleVisibilityTable;

    private VisibilityBean containerVisibilityTable;

    private VisibilityBean completedShipmentsTable;

    private VisibilityBean orderVisibilityTable;

    private VisibilityBean parcelVisibilityTable;

    public UserConfigurationsBean parseUserConfiguration(UserConfigurations userConfigurations) {
        UserConfigurationsBean configurationsBean = new UserConfigurationsBean();
        configurationsBean.setMonitorSettings(userConfigurations.getMonitorSettings());
        configurationsBean.setParcelVisibilityTable(userConfigurations.getParcelVisibilityTable());
        configurationsBean.setCompletedShipmentsTable(userConfigurations.getCompletedShipmentsTable());
        configurationsBean.setContainerVisibilityMap(userConfigurations.getContainerVisibilityMap());
        configurationsBean.setContainerVisibilityTable(userConfigurations.getContainerVisibilityTable());
        configurationsBean.setItemVisibilityMap(userConfigurations.getItemVisibilityMap());
        configurationsBean.setOrderVisibilityTable(userConfigurations.getOrderVisibilityTable());
        configurationsBean.setVehicleVisibilityMap(userConfigurations.getVehicleVisibilityMap());
        configurationsBean.setVehicleVisibilityTable(userConfigurations.getVehicleVisibilityTable());
        configurationsBean.setRoleType(userConfigurations.getRoleType());
        configurationsBean.setUserId(userConfigurations.getUserId());
        configurationsBean.setAutoRefreshTime(userConfigurations.getAutoRefreshTime());
        configurationsBean.setApplicationStatus(userConfigurations.getApplicationStatus());
        configurationsBean.setId(userConfigurations.getId());
        return configurationsBean;
    }


    public UserConfigurations parseUserConfigurationBean(UserConfigurationsBean configurationsBean, UserConfigurations configurations) {
        configurations.setMonitorSettings(configurationsBean.getMonitorSettings());
        configurations.setCompletedShipmentsTable(configurationsBean.getCompletedShipmentsTable());
        configurations.setParcelVisibilityTable(configurationsBean.getParcelVisibilityTable());
        configurations.setContainerVisibilityMap(configurationsBean.getContainerVisibilityMap());
        configurations.setContainerVisibilityTable(configurationsBean.getContainerVisibilityTable());
        configurations.setItemVisibilityMap(configurationsBean.getItemVisibilityMap());
        configurations.setOrderVisibilityTable(configurationsBean.getOrderVisibilityTable());
        configurations.setVehicleVisibilityMap(configurationsBean.getVehicleVisibilityMap());
        configurations.setVehicleVisibilityTable(configurationsBean.getVehicleVisibilityTable());
        configurations.setRoleType(configurationsBean.getRoleType());
        configurations.setUserId(configurationsBean.getUserId());
        configurations.setAutoRefreshTime(configurationsBean.getAutoRefreshTime());
        configurations.setApplicationStatus(configurationsBean.getApplicationStatus());
        return configurations;
    }
}
