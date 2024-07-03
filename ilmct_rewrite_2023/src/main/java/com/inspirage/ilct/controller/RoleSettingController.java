package com.inspirage.ilct.controller;

import com.inspirage.ilct.documents.RoleSettings;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.RoleSettingBean;
import com.inspirage.ilct.dto.bean.RuleBean;
import com.inspirage.ilct.service.RoleSettingsService;
import com.inspirage.ilct.service.RuleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
@RestController
@RequestMapping("/v3/role-settings/")
public class RoleSettingController {

    @Autowired
    RoleSettingsService roleSettingsService;
    @Autowired
    RuleService ruleService;
    @PostMapping("save-role-settings")
    public ResponseEntity<ApiResponse> saveRoleSettings(@RequestBody RoleSettings RoleSettings, HttpServletRequest request) {
        return ResponseEntity.ok(roleSettingsService.saveRoleSettings(RoleSettings, request));
    }
//    @GetMapping("get-role-settings")
//    public ResponseEntity<ApiResponse> getRoleSettings(@RequestParam String roleName, HttpServletRequest request) {
//        return ResponseEntity.ok(roleSettingsService.getRoleSettings(roleName, request));
//    }
// user rules
    @PutMapping("user/saveRules")
    public ResponseEntity saveRules(@RequestBody RuleBean rule) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user+"************************");
        return ResponseEntity.ok(ruleService.saveRule(user, rule));
    }


//    @GetMapping("user/getRules")
//    public ResponseEntity getRules() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(user+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//        return ResponseEntity.ok(ruleService.getRules(user));
//    }

    @GetMapping("get-role-settings")
    public ResponseEntity<ApiResponse> getRoleSettings(@RequestParam String roleName, HttpServletRequest request) {
        return ResponseEntity.ok(roleSettingsService.getRoleSettings(roleName, request));
    }
}
