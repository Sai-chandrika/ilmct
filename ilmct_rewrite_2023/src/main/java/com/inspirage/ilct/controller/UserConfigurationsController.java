package com.inspirage.ilct.controller;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.response.UserConfigurationsBean;
import com.inspirage.ilct.exceptions.UserConfigurationNotFoundException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.service.ConfigurationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 08-11-2023
 */
@RestController
@RequestMapping("/v3/userConfigurations/")
public class UserConfigurationsController {
    @Autowired
    ConfigurationService configurationService;

    @PostMapping("save-user-configurations")
    public ResponseEntity<ApiResponse> saveUserConfiguration(@RequestBody UserConfigurationsBean userConfigurationsBean, HttpServletRequest request)  {
        return ResponseEntity.ok(configurationService.saveUserConfigurations(userConfigurationsBean,request));
    }

    @GetMapping("get-user-configuration-by-role")
    public ResponseEntity<UserConfigurationsBean> getUserConfigurationsByRole(@RequestParam String roleName)  {
        return ResponseEntity.ok(configurationService.getUserConfigurationsByRole(roleName));
    }
//    @GetMapping("get-user-configuration-by-user-id")
//    public ResponseEntity<ApiResponse> getUserConfigurationsById(@RequestParam String userId, HttpServletRequest request) {
//        return ResponseEntity.ok(configurationService.getUserConfigurationsById(userId,request));
//    }

    @GetMapping("get-user")
    public ResponseEntity<ApiResponse> getUser(@RequestParam String userData, HttpServletRequest request)  {
        return ResponseEntity.ok(configurationService.getUser(userData,request));
    }

    @GetMapping("get-user-configuration")
    public ResponseEntity<ApiResponse> getOneUserConfiguration(@RequestParam String userId, HttpServletRequest request) throws UserNotFoundException, UserConfigurationNotFoundException {
        return ResponseEntity.ok(configurationService.getUserConfigurations(userId,request));
    }
}
