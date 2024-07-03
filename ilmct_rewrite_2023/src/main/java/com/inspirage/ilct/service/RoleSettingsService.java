package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.RoleSettingBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
public interface RoleSettingsService {

    ApiResponse saveRoleSettings(RoleSettings roleSettings, HttpServletRequest request);

    ApiResponse getRoleSettings(String roleName, HttpServletRequest request);

    RoleSettings getRoleSettingsWithApiResponse(String name, HttpServletRequest request);
}
