package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.documents.UserConfigurations;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.ApplicationStatus;
import com.inspirage.ilct.dto.bean.VisibilityBean;
import com.inspirage.ilct.dto.request.UserBean;
import com.inspirage.ilct.dto.response.UserConfigurationsBean;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.exceptions.NullPointerException;
import com.inspirage.ilct.exceptions.UserConfigurationNotFoundException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.UserConfigurationsRepo;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.ConfigurationService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 08-11-2023
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    @Autowired
    UserConfigurationsRepo userConfigurationsRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public UserConfigurationsBean getUserConfigurationsByRole(String roleName) {
        UserConfigurationsBean configurationsBean ;
        UserConfigurations userConfigurations = userConfigurationsRepo.findByRoleTypeAndUserIdIsNull(Utility.getRoleType(roleName));
        if (userConfigurations != null) {
            configurationsBean = userConfigurationToBean(userConfigurations);
        } else {
            UserConfigurations newUserConfig = getNewUserConfig() ;
            newUserConfig.setId(null);
            newUserConfig.setRoleType(Utility.getRoleType(roleName));
            newUserConfig = userConfigurationsRepo.save(newUserConfig);
            configurationsBean = userConfigurationToBean(newUserConfig);
        }
        if (configurationsBean.getApplicationStatus() == null)
            configurationsBean.setApplicationStatus(new ApplicationStatus());
        return  configurationsBean;
    }

    @Override
    public ApiResponse getUserConfigurationsById(String userId, HttpServletRequest request) {
        if (StringUtils.isEmpty(userId)) throw new NullPointerException("Invalid User Id ");
        Optional<User> userOptional = userRepository.findOneByUserId(userId);
        if (userOptional.isEmpty()){
            throw new UserNotFoundException("User not found with given user id");
        }
        User user = userOptional.get();
        UserConfigurationsBean configurationsBean ;
        UserConfigurations userConfigurations = userConfigurationsRepo.findByUserId(userId);
        if (userConfigurations != null) {
            configurationsBean = userConfigurationToBean(userConfigurations);
            if (configurationsBean.getApplicationStatus() == null)
                configurationsBean.setApplicationStatus(new ApplicationStatus());
        } else {
            configurationsBean = getUserConfigurationsByRole(user.getRole().name());
            configurationsBean.setRoleType(user.getRole());
            configurationsBean.setUserId(userId);
            UserConfigurations newUserConfig =parseUserConfigurationBean(configurationsBean, getNewUserConfig());
            newUserConfig.setId(null);
            newUserConfig = userConfigurationsRepo.save(newUserConfig);
            configurationsBean = userConfigurationToBean(newUserConfig);
        }
        return new ApiResponse(HttpStatus.OK.value(),configurationsBean);
    }

    @Override
    public ApiResponse getUser(String userData, HttpServletRequest request) {
        Query query = new Query(Criteria.where("role").in(Arrays.asList(RoleType.SUB_ADMIN,RoleType.CUSTOMER,RoleType.PLANNER))
                .orOperator(
                        Criteria.where("userId").regex(userData, "i")
                ));
        List<User> usersList = mongoTemplate.find(query, User.class);
        return new ApiResponse(HttpStatus.OK.value(), usersList.stream().map(this::userToUserBean).toList());
    }

    @Override
    public ApiResponse saveUserConfigurations(UserConfigurationsBean userConfigurationsBean, HttpServletRequest request) {
        if (Utility.isEmpty(userConfigurationsBean.getUserId()) && Utility.checkRole(userConfigurationsBean.getRoleType())) {
            throw new NullPointerException("Invalid User Configurations , Please provide user id or role type");
        } else if (!Utility.isEmpty(userConfigurationsBean.getUserId())) {
            return saveUserConfigurationForUserId(userConfigurationsBean);
        } else {
            return saveUserConfigurationForRole(userConfigurationsBean);
        }
    }

    private ApiResponse saveUserConfigurationForRole(UserConfigurationsBean userConfigurationsBean) {
        UserConfigurations dbConfigurations;
        try {
            dbConfigurations = userConfigurationsRepo.findByRoleType(userConfigurationsBean.getRoleType());
        } catch (
                IncorrectResultSizeDataAccessException e) {
            List<UserConfigurations> configurationsList = userConfigurationsRepo.findAllByRoleType(userConfigurationsBean.getRoleType());
            dbConfigurations = configurationsList.get(0);
        }
        if (dbConfigurations == null) {
            dbConfigurations = parseUserConfigurationBean(userConfigurationsBean, new UserConfigurations());
        } else {
            dbConfigurations =parseUserConfigurationBean(userConfigurationsBean, dbConfigurations);
        }
        dbConfigurations = userConfigurationsRepo.save(dbConfigurations);
        return new ApiResponse(HttpStatus.OK.value(), userConfigurationToBean(dbConfigurations));
    }


    private ApiResponse saveUserConfigurationForUserId(UserConfigurationsBean userConfigurationsBean) {
        UserConfigurations configurations ;
        try{
            configurations= userConfigurationsRepo.findByUserId(userConfigurationsBean.getUserId());
        }catch(IncorrectResultSizeDataAccessException e){
            List<UserConfigurations> userConfigurations= userConfigurationsRepo.findAllByUserId(userConfigurationsBean.getUserId());
            configurations=userConfigurations.get(0);
        }
        if (configurations != null) {
            RoleType dbrole = configurations.getRoleType();
            configurations = parseUserConfigurationBean(userConfigurationsBean, configurations);
            configurations.setRoleType(dbrole);
        }else{
             configurations=parseUserConfigurationBean(userConfigurationsBean, new UserConfigurations());
        }
        configurations= userConfigurationsRepo.save(configurations);
        return new ApiResponse(HttpStatus.OK.value(), configurations);
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
    private UserConfigurations getNewUserConfig() {
        UserConfigurations configurations = new UserConfigurations();
        configurations.setApplicationStatus(new ApplicationStatus());
        configurations.setMonitorSettings(new ArrayList<>());
        configurations.setCompletedShipmentsTable(new VisibilityBean());
        configurations.setVehicleVisibilityTable(new VisibilityBean());
        configurations.setVehicleVisibilityMap(new VisibilityBean());
        configurations.setOrderVisibilityTable(new VisibilityBean());
        configurations.setItemVisibilityMap(new VisibilityBean());
        configurations.setContainerVisibilityTable(new VisibilityBean());
        configurations.setContainerVisibilityMap(new VisibilityBean());
        configurations.setParcelVisibilityTable(new VisibilityBean());
        return configurations;
    }

    public UserConfigurationsBean userConfigurationToBean(UserConfigurations userConfigurations) {
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

    public UserBean userToUserBean(User user) {
        UserBean userBean=new UserBean();
        userBean.setId(user.getId());
        userBean.setUserId(user.getUserId());
        userBean.setFirstName(user.getFirstName());
        userBean.setPhone1(user.getPhone1());
        userBean.setEmail(user.getEmail());
        userBean.setRole(String.valueOf(user.getRole()));
        userBean.setGrantorUserRole(user.getGrantorUserRole());
        userBean.setMapLanguageId(user.getMapLanguageId());
        userBean.setPreferredLanguage(user.getPreferredLanguage());
        userBean.setCountryCode(user.getCountryCode());
        userBean.setBaseMapCountry(user.getBaseMapCountry());
        userBean.setTimeZone(user.getTimeZone());
        userBean.setTimeZoneId(user.getTimeZoneId());
        userBean.setTemperatureUnit(user.getTemperatureUnit());
        userBean.setDistanceUnit(user.getDistanceUnit());
        return userBean;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());


    @Override
    @Transactional(readOnly = true)
    public ApiResponse getUserConfigurations(String userId, HttpServletRequest request) throws UserConfigurationNotFoundException, UserNotFoundException {
        if (StringUtils.isEmpty(userId)) throw new UserConfigurationNotFoundException("Invalid User Id ");
        Optional<User> userOp = userRepository.findOneByUserId(userId);
        if (!userOp.isPresent()){
            throw new UserNotFoundException("User not found with given user id");
        }
        User user = userOp.get();
        UserConfigurationsBean configurationsBean = new UserConfigurationsBean();
        UserConfigurations userConfigurations = userConfigurationsRepo.findByUserId(userId);
        if (userConfigurations != null) {
            configurationsBean = configurationsBean.parseUserConfiguration(userConfigurations);
            if (configurationsBean.getApplicationStatus() == null)
                configurationsBean.setApplicationStatus(new ApplicationStatus());
        } else {
            configurationsBean = getUserConfigurationsByRole(user.getRole().name());
            configurationsBean.setRoleType(user.getRole());
            configurationsBean.setUserId(userId);
            UserConfigurations newUserConfig = configurationsBean.parseUserConfigurationBean(configurationsBean, getNewUserConfig());
            newUserConfig.setId(null);
            newUserConfig = userConfigurationsRepo.save(newUserConfig);
            configurationsBean = configurationsBean.parseUserConfiguration(newUserConfig);
        }

        logger.info("Returning User Configurations with user Id " + userId);
        return new ApiResponse.ApiResponseBuilder().setStatus(HttpStatus.OK).setData(configurationsBean).build();
    }

}
