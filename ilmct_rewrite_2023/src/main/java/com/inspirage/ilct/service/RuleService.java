package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.RuleBean;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
public interface RuleService {
    ApiResponse saveRule(User user, RuleBean rule);
    ApiResponse getRules(User user);

    ApiResponse getRuleConfiguration(User loginUser, String userId);

}
