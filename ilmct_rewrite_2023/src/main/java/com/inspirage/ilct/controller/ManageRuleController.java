package com.inspirage.ilct.controller;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage-rule/")
public class ManageRuleController {
    @Autowired
    RuleService ruleService;

    @Autowired
    RoleSettingsService roleSettingsService;

    @GetMapping("/user-get-rules")
    public ResponseEntity getRules() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ruleService.getRules(user));
    }

    @GetMapping("get-role-settings")
    public ResponseEntity<ApiResponse> getRoleSettings(@RequestParam String roleName, HttpServletRequest request) {
        return ResponseEntity.ok(roleSettingsService.getRoleSettings(roleName, request));
    }
}
