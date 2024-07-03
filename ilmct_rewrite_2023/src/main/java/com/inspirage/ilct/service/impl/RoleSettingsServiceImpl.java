package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.dto.ApiResponse;

import com.inspirage.ilct.dto.bean.RoleSettingBean;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.repo.RoleSettingsRepository;
import com.inspirage.ilct.service.RoleSettingsService;

import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



@Service
public class RoleSettingsServiceImpl implements RoleSettingsService {
    @Autowired
    private RoleSettingsRepository roleSettingsRepository;


    @Override
    public ApiResponse saveRoleSettings(RoleSettings roleSettings, HttpServletRequest request) {
        if(!Utility.isRoleExist(roleSettings.getRole())) {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"failed");
        }
        RoleSettings dbRoleSettings = roleSettingsRepository.findOneByRole(roleSettings.getRole());
        if(dbRoleSettings!=null){
            roleSettings.setId(dbRoleSettings.getId());
        }
        roleSettings = roleSettingsRepository.save(roleSettings);
        return new ApiResponse(HttpStatus.OK,"saved role details", roleSettings);
    }

    @Override
    public ApiResponse getRoleSettings(String roleName, HttpServletRequest request) {
        if(!Utility.isRoleExist(roleName.toUpperCase())) {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"something went wrong");
        }
        RoleSettings dbRoleSettings = roleSettingsRepository.findOneByRole(Utility.getRoleType(roleName.toUpperCase()));
        if(dbRoleSettings!=null){
            return new ApiResponse(HttpStatus.OK,"data set successfully",dbRoleSettings);
        }else{
            RoleSettings roleSettings = new RoleSettings();
            roleSettings.setRole(Utility.getRoleType(roleName));
            return new ApiResponse(HttpStatus.OK,"set role type",roleSettings);
        }
    }

    @Override
    public RoleSettings getRoleSettingsWithApiResponse(String roleName, HttpServletRequest request) {
        RoleSettings dbRoleSettings = roleSettingsRepository.findOneByRole(Utility.getRoleType(roleName.toUpperCase()));
        if(dbRoleSettings!=null){
            return dbRoleSettings;
        }else{ RoleSettings roleSettings = new RoleSettings();
            roleSettings.setRole(Utility.getRoleType(roleName));
            return roleSettings;
        }
    }

    public ApiResponse save(RoleSettingBean roleSettingBean, HttpServletRequest request){
        RoleSettings roleSettings=new RoleSettings();
        if(!Utility.isRoleExist(roleSettingBean.getRole())) {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"please provide valid role",roleSettingBean);
        }
        RoleSettings dbRoleSettings = roleSettingsRepository.findOneByRole(RoleType.valueOf(roleSettingBean.getRole()));
       if(dbRoleSettings!=null){
           roleSettings.setId(dbRoleSettings.getId());
           beanToRoleSettings(roleSettingBean);
       }else{
           roleSettings= beanToRoleSettings(roleSettingBean);
       }
       roleSettingsRepository.save(roleSettings);
        return new ApiResponse(HttpStatus.OK.value(),"role setting save successfully", roleSettingToBean(roleSettings));
    }

    public RoleSettings beanToRoleSettings(RoleSettingBean roleSettingBean){
        RoleSettings roleSettings=new RoleSettings();
        roleSettings.setUserSettings(roleSettingBean.getUserSettings());
        roleSettings.setUsers(roleSettingBean.getUsers());
        roleSettings.setSavedFilters(roleSettingBean.getSavedFilters());
        roleSettings.setSavedQueries(roleSettingBean.getSavedQueries());
        roleSettings.setAnalytics(roleSettingBean.getAnalytics());
        roleSettings.setManageRules(roleSettingBean.getManageRules());
        roleSettings.setMonitorAndExecutiveDashboards(roleSettingBean.getMonitorAndExecutiveDashboards());
        roleSettings.setCreateGeoFence(roleSettingBean.getCreateGeoFence());
        return roleSettings;
    }

    public RoleSettingBean roleSettingToBean(RoleSettings roleSettingBean){
        RoleSettingBean roleSettings=new RoleSettingBean();
        roleSettings.setId(roleSettingBean.getId());
        roleSettings.setUserSettings(roleSettingBean.getUserSettings());
        roleSettings.setUsers(roleSettingBean.getUsers());
        roleSettings.setSavedFilters(roleSettingBean.getSavedFilters());
        roleSettings.setSavedQueries(roleSettingBean.getSavedQueries());
        roleSettings.setAnalytics(roleSettingBean.getAnalytics());
        roleSettings.setManageRules(roleSettingBean.getManageRules());
        roleSettings.setMonitorAndExecutiveDashboards(roleSettingBean.getMonitorAndExecutiveDashboards());
        roleSettings.setCreateGeoFence(roleSettingBean.getCreateGeoFence());
        return roleSettings;
    }

}
