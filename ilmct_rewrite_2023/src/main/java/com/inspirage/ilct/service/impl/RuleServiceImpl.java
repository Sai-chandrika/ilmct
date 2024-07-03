package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.RuleDoc;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.RuleBean;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.exceptions.NullPointerException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.RuleRepository;
import com.inspirage.ilct.service.MessageByLocaleService;
import com.inspirage.ilct.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 09-11-2023
 */
@Service
public class RuleServiceImpl implements RuleService {
    @Autowired
    RuleRepository ruleRepository;
    @Override
    public ApiResponse saveRule(User user, RuleBean ruleBean) {
        if (ruleBean == null || !ruleBean.validate()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST,"data is not validated");
        } else {
            if (user == null) {
                      throw new NullPointerException("user is not found");
            } else {
                RuleDoc doc = ruleRepository.findOneByUserId(user.getUserId()).orElse(new RuleDoc());
                doc.setShortDelayMinutes(ruleBean.getShortDelayMinutes());
                doc.setAvgDelayMinutes(ruleBean.getAvgDelayMinutes());
                doc.setLongDelayMinutes(ruleBean.getLongDelayMinutes());
                doc.setTemperatureAlert(ruleBean.getTemperatureAlert());
                doc.setSpeedAlert(ruleBean.getSpeedAlert());
                doc.setFuelAlert(ruleBean.getFuelAlert());
                doc.setUserId(user.getUserId());
                doc.setInBoundDelayInHours(ruleBean.getInBoundDelayInHours());
                doc.setInBoundRiskDemurageInDays(ruleBean.getInBoundRiskDemurageInDays());
                doc.setOutBoundInTransitDelayInHours(ruleBean.getOutBoundInTransitDelayInHours());
                doc.setOutBoundRiskDemurageInHours(ruleBean.getOutBoundRiskDemurageInHours());
                if (user.getRole().equals(RoleType.ADMIN) && !ObjectUtils.isEmpty(ruleBean.getLastSeenHours())) {
                    doc.setLastSeenHours(ruleBean.getLastSeenHours());
                }
                ruleRepository.save(doc);
                return new ApiResponse(HttpStatus.OK.value(), "rule save successfully", doc);
            }
        }

    }

    @Override
    public ApiResponse getRules(User user) {
        if (user == null) {
            throw new NullPointerException("user details are null");
        }
        RuleDoc doc = ruleRepository.findOneByUserId(user.getUserId()).orElse(new RuleDoc());
        return new ApiResponse(HttpStatus.OK.value(), doc);
    }

    @Autowired
    MessageByLocaleService localeService;

    @Override
    public ApiResponse getRuleConfiguration(User loginUser, String userId) {
        ApiResponse.ApiResponseBuilder builder = new ApiResponse.ApiResponseBuilder();
        if (loginUser == null) {
            builder.setStatus(HttpStatus.NOT_FOUND);
            builder.setMessage(localeService.getMessage("error.login.user_not_exist"));
        } else {
            RuleDoc doc = ruleRepository.findOneByUserId(loginUser.getUserId()).orElse(new RuleDoc());
            builder.setData(doc);
            builder.setStatus(HttpStatus.OK);
            builder.setMessage(localeService.getMessage("msg.success"));
        }
        return builder.build();
    }
}
