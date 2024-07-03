package com.inspirage.ilct.service;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.response.UserConfigurationsBean;
import com.inspirage.ilct.exceptions.UserConfigurationNotFoundException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 08-11-2023
 */
public interface ConfigurationService {
    ApiResponse saveUserConfigurations(UserConfigurationsBean userConfigurationsBean, HttpServletRequest request);

    UserConfigurationsBean getUserConfigurationsByRole(String roleName);

    ApiResponse getUserConfigurationsById(String userId, HttpServletRequest request);

    ApiResponse getUser(String userData, HttpServletRequest request);

    ApiResponse getUserConfigurations(String userId, HttpServletRequest request) throws UserConfigurationNotFoundException, UserNotFoundException;

}
