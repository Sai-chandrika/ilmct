package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.CityTimeZoneInfo;
import com.inspirage.ilct.documents.LocationDoc;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.documents.UserConfigurations;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.UserSettingsBean;
import com.inspirage.ilct.dto.request.UserLoginBean;
import com.inspirage.ilct.dto.request.UserBean;
import com.inspirage.ilct.dto.response.PageableResponse;
import com.inspirage.ilct.dto.response.UserDto;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.exceptions.*;
import com.inspirage.ilct.repo.CityTimeZoneInfoRepo;
import com.inspirage.ilct.repo.LocationDocRepository;
import com.inspirage.ilct.repo.UserConfigurationsRepo;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.RoleSettingsService;
import com.inspirage.ilct.service.UserService;
import com.inspirage.ilct.util.Constants;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.Cacheable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenUtilService jwtService;

    @Autowired
    StringRedisTemplate redisTemplate;
    //@Value("${spring.redis.timeout}")
    Long expirationTime;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    CityTimeZoneInfoRepo cityTimeZoneInfoRepo;
    @Autowired
    UserConfigurationsRepo userConfigurationsRepo;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    LocationDocRepository locationDocRepository;
    @Autowired
    RoleSettingsService roleSettingsService;


    @Override
    public ApiResponse login(UserLoginBean userLoginBean, HttpServletRequest request) {
        System.out.println(userLoginBean.getUserId());
        System.out.println(userLoginBean.getPassword());
        Optional<User> user = userRepository.findOneByUserId(userLoginBean.getUserId());
        if (user.isPresent() && bCryptPasswordEncoder.matches(userLoginBean.getPassword(), user.get().getPassword())) {
            UserBean userBean = new UserBean();
            String jwtToken = jwtService.generateJWTToken(user.get());
            userBean.setRoleSettings(roleSettingsService.getRoleSettingsWithApiResponse(user.get().getRole().name(), request));
            redisTemplate.opsForValue().set(userLoginBean.getUserId(), jwtToken, expirationTime, TimeUnit.MILLISECONDS);
            return new ApiResponse(HttpStatus.OK, "Login successful", userBean);
        }
        return new ApiResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @Override
    public ApiResponse logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String token;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "Token Not existed");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        token = redisTemplate.opsForValue().get(user.getUserId());
        if (token != null) {
            redisTemplate.delete(user.getUserId());
            return new ApiResponse(HttpStatus.OK, "Logout successfully");
        } else {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "Token is expired");
        }
    }


    @Override
    public ApiResponse userSave(UserBean userBean, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        User user = userRepository.findOneByUserId(userBean.getUserId().toLowerCase()).orElse(null);
        User loginUser = userRepository.findOneByUserId(jwtService.getUserId(request)).orElse(null);
        RoleType roleType = RoleType.valueOf(userBean.getRole().toUpperCase());
        User userDetail;
        if (loginUser == null) {
            throw new UserNotFoundException("user not found given this id");
        } else if (roleType == RoleType.ADMIN) {
            throw new InvalidUserTokenException("Admin not save the another admin details");
        } else if (roleType == RoleType.SUB_ADMIN && loginUser.getRole() != RoleType.ADMIN) {
            throw new InvalidUserTokenException("Un Authorized access");
        } else if ((roleType != RoleType.SUB_ADMIN)
                && (Objects.requireNonNull(loginUser).getRole() != RoleType.ADMIN && loginUser.getRole() != RoleType.SUB_ADMIN)) {
            throw new InvalidUserTokenException("Un Authorized access");
        } else if (!validateUser(userBean)) {
            throw new InvalidUserTokenException("Some mandatory fields are missing");
        } else if (!validateEmail(userBean.getEmail())) {
            throw new InvalidDataException("Invalid Email");
        } else if (userBean.getBaseMapCountry() == null) {
            throw new ApplicationSettingsNotFoundException("Base Map country code is missing");
        }
        if (userBean.getId() != null) {
            Optional<User> optional = userRepository.findOneById(userBean.getId());
            if (optional.isPresent()) {
                userDetail = optional.get();
                update(userBean, request, userDetail);
                if (userBean.getRules() != null) {
                    userDetail.setRules(userBean.getRules());
                } else userDetail.setRules(userDetail.getRules());
                userRepository.save(userDetail);
                return new ApiResponse("user updated successfully", userToUserBean(userDetail));
            } else throw new NotFoundException("user not found");
        } else {
            if (user != null) {
                throw new DuplicateRequestException("User already existed with userId");
            }
            userDetail = new User();
            userDetail.setUserId(userBean.getUserId().toLowerCase());
            userDetail.setFirstName(userBean.getFirstName());
            userDetail.setLastName(userBean.getLastName());
            if (userBean.getMiddleName() != null) {
                userDetail.setMiddleName(userBean.getMiddleName());
            }
            userDetail.setNickName(userBean.getNickName());
            userDetail.setPassword(bCryptPasswordEncoder.encode(userBean.getPassword()));
            userDetail.setPhone1(userBean.getPhone1());
            userDetail.setPhone2(userBean.getPhone2());
            userDetail.setDefaultUserRole(userBean.getRole());
            if (userBean.getDataProfile() != null) {
                userDetail.setDataProfile(userBean.getDataProfile());
            }
            if (userBean.getGrantorUserRole() != null) {
                userDetail.setGrantorUserRole(userBean.getGrantorUserRole());
            }
            userDetail.setEmail(userBean.getEmail());
            userDetail.setCreatedByUserId(loginUser.getId());
            userDetail.setPreferredLanguage(userBean.getPreferredLanguage());
            if (userBean.getRole() != null) {
                userDetail.setRole(RoleType.valueOf(userBean.getRole().toUpperCase()));
            }
            if (userBean.getCountryCode() != null) {
                userDetail.setCountryCode(userBean.getCountryCode());
            }
            if (userBean.getTimeZoneId() != null) {
                List<CityTimeZoneInfo> byTimeZoneList = cityTimeZoneInfoRepo.findByTimeZoneOrderByCreatedDateAsc(userBean.getTimeZoneId());
                CityTimeZoneInfo cityTimeZoneInfo = byTimeZoneList.stream().findFirst().orElse(null);
                userDetail.setTimeZone(cityTimeZoneInfo != null ? cityTimeZoneInfo.getTimeZone() : null);
                userDetail.setTimeZoneId(cityTimeZoneInfo != null ? cityTimeZoneInfo.getId() : null);
            }
            userDetail.setTemperatureUnit(userBean.getTemperatureUnit());
            userDetail.setDistanceUnit(userBean.getDistanceUnit());
            if (userBean.getLocationCountryCode() != null) {
                userDetail.setLocationCountryCode(userBean.getLocationCountryCode());
            }
            if (userBean.getLocationProvince() != null) {
                userDetail.setLocationProvince(userBean.getLocationProvince());
            }
            if (userBean.getLocationCity() != null) {
                userDetail.setLocationCity(userBean.getLocationCity());
            }
            userDetail.setBaseMapCountry(userBean.getBaseMapCountry());
            if (userBean.getContactId() != null) {
                userDetail.setContactId(userBean.getContactId());
            }
            if (userBean.getMapLanguageId() != null) {
                userDetail.setMapLanguageId(userBean.getMapLanguageId());
            }
            userDetail.setRules(userBean.getRules());
            UserConfigurations configurations = null;
            try {
                configurations = userConfigurationsRepo.findByRoleType(userDetail.getRole());
            } catch (IncorrectResultSizeDataAccessException e) {
                List<UserConfigurations> configurationsList = userConfigurationsRepo.findAllByRoleType(userDetail.getRole());
                configurations = configurationsList.get(0);
            }
            configurations.setUserId(userDetail.getUserId());
            configurations.setId(null);
            userConfigurationsRepo.save(configurations);
            userRepository.save(userDetail);
            return new ApiResponse("user save successfully", userToUserBean(userDetail));
        }
    }

    @Override
    public ApiResponse getUsers(int pageIndex, int numberOfRecord, String loginUser, String searchText) {
        Optional<User> user = userRepository.findOneByUserId(loginUser);
        if (user.isEmpty()) {
            throw new InvalidUserTokenException("user not found");
        } else {
            List<RoleType> adminRoles = Arrays
                    .asList(RoleType.CUSTOMER, RoleType.CARRIER, RoleType.PLANNER);
            List<User> forAdmin = userRepository.findAllByRoleIn(adminRoles, PageRequest.of(pageIndex,numberOfRecord));
            if (user.get().getRole().equals(RoleType.ADMIN)) {
                if (Utility.isEmpty(searchText)) {
                    return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                            .withMessage("Success")
                            .withData(usersToUserBeans(forAdmin))
                            .withPageInfo(pageIndex, numberOfRecord,  userRepository.countByRoleIn(adminRoles))
                            .build();
                } else {
                    Query query = new Query(Criteria.where("role").in(Arrays.asList(RoleType.PLANNER, RoleType.CUSTOMER, RoleType.CARRIER))
                            .orOperator(
                                    Criteria.where("userId").regex(searchText, "i"),
                                    Criteria.where("firstName").regex(searchText, "i"),
                                    Criteria.where("preferredLanguage").regex(searchText, "i"),
                                    Criteria.where("phone1").regex(searchText, "i"),
                                    Criteria.where("temperatureUnit").regex(searchText, "i")
                            ));
                    List<User> users = mongoTemplate.find(query, User.class);
                    return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                              .withMessage("Success")
                            .withData(usersToUserBeans(users))
                            .withPageInfo(pageIndex, numberOfRecord,  userRepository.countByRoleIn(adminRoles))
                            .build();
                }
            } else if (user.get().getRole().equals(RoleType.SUB_ADMIN)) {
                List<User> forSubAdmin = userRepository.findAllByUserIdAndRoleIn(forAdmin, user.get().getUserId(), PageRequest.of(pageIndex, numberOfRecord));
                if (Utility.isEmpty(searchText)) {
                    return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                            .withMessage("Success")
                            .withData(usersToUserBeans(forSubAdmin))
                            .withPageInfo(pageIndex, numberOfRecord, userRepository.countByCreatedByUserIdAndRoleIn(loginUser, adminRoles))
                            .build();
                } else {
                    Query query = new Query(Criteria.where("role").in(Arrays.asList(RoleType.PLANNER, RoleType.CUSTOMER, RoleType.CARRIER))
                            .orOperator(
                                    Criteria.where("userId").regex(searchText, "i"),
                                    Criteria.where("firstName").regex(searchText, "i"),
                                    Criteria.where("preferredLanguage").regex(searchText, "i"),
                                    Criteria.where("phone1").regex(searchText, "i"),
                                    Criteria.where("temperatureUnit").regex(searchText, "i")
                            ));
                    List<User> users = mongoTemplate.find(query, User.class);
                    return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                            .withMessage("Success")
                            .withData(usersToUserBeans(users))
                            .withPageInfo(pageIndex, numberOfRecord, userRepository.countByCreatedByUserIdAndRoleIn(loginUser, adminRoles))
                            .build();
                }
            } else throw new InvalidUserTokenException("un Authorized");
        }
    }


    @Override
    public ApiResponse getSubAdmins(int pageIndex, int numberOfRecord, String loginUser, String searchText) {
        Optional<User> user = userRepository.findOneByUserId(loginUser);
        if (user.isEmpty()) throw new InvalidUserTokenException("USER_NOT_FOUND_MESSAGE");
        if (user.get().getRole().equals(RoleType.ADMIN)) {
            List<RoleType> adminRoles = Arrays
                    .asList(RoleType.SUB_ADMIN);
            List<User> forSubAdmin = userRepository.findAllByRoleIn(adminRoles, PageRequest.of(pageIndex, numberOfRecord)).stream().toList();
            if (Utility.isEmpty(searchText)) {
                List<User> users = forSubAdmin.stream().toList();
                return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                        .withMessage("Success")
                        .withData(usersToUserBeans(users))
                        .withPageInfo(pageIndex, numberOfRecord, userRepository.countByRoleIn(Arrays.asList(RoleType.SUB_ADMIN)))
                        .build();
            } else {
                Query query = new Query(Criteria.where("role").in(Arrays.asList(RoleType.SUB_ADMIN))
                        .orOperator(
                                Criteria.where("userId").regex(searchText, "i"),
                                Criteria.where("firstName").regex(searchText, "i"),
                                Criteria.where("preferredLanguage").regex(searchText, "i"),
                                Criteria.where("phone1").regex(searchText, "i"),
                                Criteria.where("temperatureUnit").regex(searchText, "i")
                        ));
                List<User> users = mongoTemplate.find(query, User.class);
                return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                        .withMessage("Success")
                        .withData(usersToUserBeans(users))
                        .withPageInfo(pageIndex, numberOfRecord, userRepository.countByRoleIn(Arrays.asList(RoleType.SUB_ADMIN)))
                        .build();
            }
        } else throw new InvalidUserTokenException("un Authorized");
    }


    @Override
    public ApiResponse deletedUser(String userId, String loginUser) {
        Optional<User> user = userRepository.findOneByUserId(loginUser);
        if (user.isEmpty()) throw new InvalidUserTokenException("USER_NOT_FOUND_MESSAGE");
        if (user.get().getRole().equals(RoleType.ADMIN)) {
            userRepository.deleteOneByUserId(userId);
            return new ApiResponse(HttpStatus.OK.value(), "user deleted successfully");
        } else if (user.get().getRole().equals(RoleType.SUB_ADMIN)) {
            Optional<User> userOptional = userRepository.findOneByUserId(userId);
            if (userOptional.isPresent() && userOptional.get().getCreatedByUserId().equals(user.get().getId())) {
                userRepository.deleteOneByUserId(userId);
                return new ApiResponse(HttpStatus.OK.value(), "user deleted successfully");
            }
            return new ApiResponse(HttpStatus.OK.value(), "Users can only be deleted by the sub admin who created them");
        } else throw new InvalidUserTokenException("un Authorized");

    }
    //user settings

    @Override
    public ApiResponse saveSetting(UserSettingsBean userSettingsBean, User loginUser) {
        try {
            loginUser.setCountryCode(userSettingsBean.getCountryCode());
            if (!StringUtils.isEmpty(userSettingsBean.getTimeZoneId())) {
                List<CityTimeZoneInfo> byTimeZoneList = cityTimeZoneInfoRepo.findByTimeZoneOrderByCreatedDateAsc(userSettingsBean.getTimeZoneId());
                if (byTimeZoneList != null && byTimeZoneList.size() > 0) {
                    CityTimeZoneInfo cityTimeZoneInfo = byTimeZoneList.get(0);
                    loginUser.setTimeZone(cityTimeZoneInfo.getTimeZone());
                    loginUser.setTimeZoneId(cityTimeZoneInfo.getId());
                }
            }
            loginUser.setTemperatureUnit(userSettingsBean.getTemperatureUnit());
            loginUser.setDistanceUnit(userSettingsBean.getDistanceUnit());
            loginUser.setPreferredLanguage(userSettingsBean.getPreferredLanguage());
            loginUser.setMapLanguageId(userSettingsBean.getMapLanguageId());
            loginUser.setLocationCity(userSettingsBean.getCity());
            userRepository.save(loginUser);

            return new ApiResponse(HttpStatus.OK, "Success", userToUserBean(loginUser));
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
        }
    }

    @Override
    public ApiResponse getSettingsData(User user) {
        UserSettingsBean settingsBean = new UserSettingsBean();
        settingsBean.setCountryCode(user.getCountryCode());
        settingsBean.setCity(user.getLocationCity());
        settingsBean.setTimeZone(user.getTimeZone());
        settingsBean.setDistanceUnit(user.getDistanceUnit());
        settingsBean.setTemperatureUnit(user.getTemperatureUnit());
        settingsBean.setPreferredLanguage(user.getPreferredLanguage());
        return new ApiResponse(HttpStatus.OK, "user settings", settingsBean);
    }
    //user settings



    public Map<String, String> fetchLocationsBySiteIds(List<User> user) {
        List<String> siteIds = new ArrayList<>();
        for (User u : user) {
            siteIds.addAll(u.getRules());
        }
        List<LocationDoc> allLocations = locationDocRepository.findBySiteIdIn(siteIds);
        Map<String, String> locationMap = new HashMap<>();
        for (LocationDoc locationDoc : allLocations) {
            if (locationDoc.getSiteId() != null) {
                locationMap.put(locationDoc.getSiteId(), locationDoc.getSiteName());
            }
        }
        return locationMap;
    }


    public void update(UserBean userBean, HttpServletRequest request, User user) {
        User loginUser = userRepository.findOneByUserId(jwtService.getUserId(request)).orElse(null);
        User userDetail = user;
        if (userBean.getUserId() != null) {
            if (Boolean.FALSE.equals(uniqueEmail(userBean))) {
                userDetail.setUserId(userBean.getUserId().toLowerCase());
            } else throw new DuplicateRequestException("User already existed with userId");
        }
        if (userBean.getFirstName() != null) {
            userDetail.setFirstName(userBean.getFirstName());
        }
        userDetail.setLastName(userBean.getLastName());
        if (userBean.getMiddleName() != null) {
            userDetail.setMiddleName(userBean.getMiddleName());
        }
        if (userBean.getNickName() != null) {
            userDetail.setNickName(userBean.getNickName());
        }
        if (userBean.getPassword() != null) {
            userDetail.setPassword(bCryptPasswordEncoder.encode(userBean.getPassword()));
        }
        if (userBean.getPhone1() != null) {
            userDetail.setPhone1(userBean.getPhone1());
        }
        if (userBean.getPhone2() != null) {
            userDetail.setPhone2(userBean.getPhone2());
        }
        if (userBean.getRole() != null) {
            userDetail.setDefaultUserRole(userBean.getRole());
        }
        if (userBean.getDataProfile() != null) {
            userDetail.setDataProfile(userBean.getDataProfile());
        }
        if (userBean.getGrantorUserRole() != null) {
            userDetail.setGrantorUserRole(userBean.getGrantorUserRole());
        }
        if (userBean.getEmail() != null) {
            userDetail.setEmail(userBean.getEmail());
        }
        assert loginUser != null;
        if (loginUser.getId() != null) {
            userDetail.setCreatedByUserId(loginUser.getId());
        }
        if (userBean.getPreferredLanguage() != null) {
            userDetail.setPreferredLanguage(userBean.getPreferredLanguage());
        }
        if (userBean.getRole() != null) {
            userDetail.setRole(RoleType.valueOf(userBean.getRole().toUpperCase()));
        }
        if (userBean.getCountryCode() != null) {
            userDetail.setCountryCode(userBean.getCountryCode());
        }
        if (userBean.getTimeZoneId() != null) {
            List<CityTimeZoneInfo> byTimeZoneList = cityTimeZoneInfoRepo.findByTimeZoneOrderByCreatedDateAsc(userBean.getTimeZoneId());
            CityTimeZoneInfo cityTimeZoneInfo = byTimeZoneList.stream().findFirst().orElse(null);
            userDetail.setTimeZone(cityTimeZoneInfo != null ? cityTimeZoneInfo.getTimeZone() : null);
            userDetail.setTimeZoneId(cityTimeZoneInfo != null ? cityTimeZoneInfo.getId() : null);
        }
        if (userBean.getTemperatureUnit() != null) {
            userDetail.setTemperatureUnit(userBean.getTemperatureUnit());
        }
        if (userBean.getDistanceUnit() != null) {
            userDetail.setDistanceUnit(userBean.getDistanceUnit());
        }
        if (userBean.getLocationCountryCode() != null) {
            userDetail.setLocationCountryCode(userBean.getLocationCountryCode());
        }
        if (userBean.getLocationProvince() != null) {
            userDetail.setLocationProvince(userBean.getLocationProvince());
        }
        if (userBean.getLocationCity() != null) {
            userDetail.setLocationCity(userBean.getLocationCity());
        }
        if (userBean.getBaseMapCountry() != null) {
            userDetail.setBaseMapCountry(userBean.getBaseMapCountry());
        }
        if (userBean.getContactId() != null) {
            userDetail.setContactId(userBean.getContactId());
        }
        if (userBean.getMapLanguageId() != null) {
            userDetail.setMapLanguageId(userBean.getMapLanguageId());
        }
    }


    public Boolean uniqueEmail(UserBean dto) {
        Optional<User> optional = userRepository.findOneByUserId(dto.getUserId());
        return optional.filter(user -> dto.getId() == null || !dto.getId().equals(user.getId())).isPresent();
    }

    public boolean validateUser(UserBean userBean) {
        return !Utility.isEmpty(userBean.getUserId()) && !Utility.isEmpty(userBean.getFirstName()) && !Utility.isEmpty(userBean.getLastName()) && !Utility.isEmpty(userBean.getPassword()) && !Utility.isEmpty(userBean.getPhone1()) && !Utility.isEmpty(userBean.getRole()) && !Utility.isEmpty(userBean.getEmail());
    }

    public boolean validateEmail(String email) {
        return Utility.validateEmail(Constants.REGEX_EMAIL_VALIDATION, email);
    }

    public UserBean userToUserBean(User user) {
        UserBean userBean = new UserBean();
        userBean.setUserId(user.getUserId());
        userBean.setFirstName(user.getFirstName());
        userBean.setLastName(user.getLastName());
        userBean.setNickName(user.getNickName());
        userBean.setPhone1(user.getPhone1());
        userBean.setPhone2(user.getPhone2());
        userBean.setEmail(user.getEmail());
        userBean.setRole(String.valueOf(user.getRole()));
        userBean.setMapLanguageId(user.getMapLanguageId());
        userBean.setPreferredLanguage(user.getPreferredLanguage());
        userBean.setCountryCode(user.getCountryCode());
        userBean.setBaseMapCountry(user.getBaseMapCountry());
        userBean.setTimeZone(user.getTimeZone());
        userBean.setTimeZoneId(user.getTimeZoneId());
        userBean.setTemperatureUnit(user.getTemperatureUnit());
        userBean.setDistanceUnit(user.getDistanceUnit());
        userBean.setLocationCountryCode(user.getLocationCountryCode());
        userBean.setLocationProvince(user.getLocationProvince());
        userBean.setLocationCity(user.getLocationCity());
        userBean.setRules(user.getRules());
        Map<String, String> appliedRules = fetchLocationsBySiteIds(Collections.singletonList(user));
        userBean.setAppliedRules(appliedRules);
        return userBean;
    }

    public List<UserDto> usersToUserBeans(List<User> users) {
        Map<String, String> allAppliedRules = fetchLocationsBySiteIds(users);
      return   users.parallelStream().map(user -> {
          UserDto userBean = new UserDto();
            userBean.setUserId(user.getUserId());
            userBean.setFirstName(user.getFirstName());
            userBean.setLastName(user.getLastName());
            userBean.setNickName(user.getNickName());
            userBean.setPhone1(user.getPhone1());
            userBean.setPhone2(user.getPhone2());
            userBean.setEmail(user.getEmail());
            userBean.setRole(String.valueOf(user.getRole()));
            userBean.setMapLanguageId(user.getMapLanguageId());
            userBean.setPreferredLanguage(user.getPreferredLanguage());
            userBean.setCountryCode(user.getCountryCode());
            userBean.setBaseMapCountry(user.getBaseMapCountry());
            userBean.setTimeZone(user.getTimeZone());
            userBean.setTimeZoneId(user.getTimeZoneId());
            userBean.setTemperatureUnit(user.getTemperatureUnit());
            userBean.setDistanceUnit(user.getDistanceUnit());
            userBean.setLocationCountryCode(user.getLocationCountryCode());
            userBean.setLocationProvince(user.getLocationProvince());
            userBean.setLocationCity(user.getLocationCity());
            userBean.setRules(user.getRules());
            Map<String, String> appliedRules = allAppliedRules.entrySet().stream()
                    .filter(entry -> user.getRules().contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            userBean.setAppliedRules(appliedRules);
            return userBean;
        }).toList();
    }



}

