package com.inspirage.ilct.controller;

import com.inspirage.ilct.config.LoginUser;
import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.LoginKeyInfo;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.RuleBean;
import com.inspirage.ilct.dto.bean.UserSettingsBean;
import com.inspirage.ilct.dto.request.UserLoginBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import com.inspirage.ilct.dto.request.UserBean;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.LoginKeyInfoRepo;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.*;
import com.inspirage.ilct.util.Constants;
import com.inspirage.ilct.util.Utility;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ui")
@Validated
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    LoginKeyInfoRepo loginKeyInfoRepo;
    @Autowired
    TokenUtilService jwtService;
    @Autowired
    PropertiesService propertiesService;
    @Autowired
    CommonService commonService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RuleService ruleService;




    @GetMapping(value = "/auth/get_login_key")
    public ApiResponse getLoginKey(HttpServletRequest request) {
        try {
            String ip = Utility.getRequestedFrom(request);
            LoginKeyInfo loginKeyInfo = loginKeyInfoRepo.findUniqueByIpAddress(ip).orElse(new LoginKeyInfo(ip));
            if (loginKeyInfo.getId() != null) {
                loginKeyInfo.setHash(RandomStringUtils.randomAlphanumeric(32));
                loginKeyInfo.setRequestedOn(LocalDateTime.now());
            }
            loginKeyInfoRepo.save(loginKeyInfo);
            return new ApiResponse(HttpStatus.OK, "Success", loginKeyInfo.getHash());
        } catch (Exception e) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "Oops something went wrong!!!");
        }
    }


    @PostMapping("auth/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid UserLoginBean bean, HttpServletRequest request) {
        ApiResponse genericResponse = userService.login(bean, request);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping(value = "getCredentialsAndConfigurations")
    public ApiResponse getCredentialsAndConfigurations(Authentication authentication) {
        User user = authentication.getDetails() != null ? ((LoginUser) authentication.getDetails()).getUser() : null;
        return new ApiResponse(HttpStatus.OK, "Success", propertiesService.getCredentialsAndConfigurations(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ApiResponse apiResponse = userService.logout(request, response, authentication);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUB_ADMIN')")
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse> saveUser(@RequestBody @Valid UserBean userBean, HttpServletRequest request) throws ApplicationSettingsNotFoundException {
        ApiResponse apiResponse = userService.userSave(userBean, request);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("user/getCityAndTimeZone")
    public ResponseEntity getCityAndTimeZone(@RequestParam(value = "countryCode") String countryCode) {
        return ResponseEntity.ok(commonService.getCityAndTimeZone(countryCode));
    }

    // get users /subadmins
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUB_ADMIN')")
    @GetMapping("/user/getUsers")
    public ResponseEntity<ApiResponse> getUsers(@RequestParam(required = false, defaultValue = "0") int pageIndex,
                                                @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE + "") int numberOfRecord,
                                                @RequestParam(required = false) String searchText, HttpServletRequest request) {
        ApiResponse apiResponse = userService.getUsers(pageIndex, numberOfRecord, jwtService.getUserId(request), searchText);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("user/getCountries")
    public ResponseEntity<ApiResponse> getCountries() {
        ApiResponse apiResponse=commonService.getCountries();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



    @GetMapping("user/getLanguages")
    public ResponseEntity getLanguages() {
        return ResponseEntity.ok(commonService.getLanguages());
    }

    @GetMapping("getLocationCountries")
    public ApiResponse getLocationCountry() {
        return this.commonService.getLocationCountry();
    }

    @GetMapping("getAppConstants")
    public ApiResponse getAppConstants() {
        return this.commonService.getAppConstants();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/getSubAdmins")
    public ResponseEntity<ApiResponse> getSubAdmins(@RequestParam(required = false, defaultValue = "0") int pageIndex,
                                                    @RequestParam(required = false, defaultValue = Constants.PAGE_SIZE + "") int numberOfRecord,
                                                    @RequestParam(required = false) String searchText, HttpServletRequest request) {
        ApiResponse apiResponse = userService.getSubAdmins(pageIndex, numberOfRecord, jwtService.getUserId(request), searchText);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
// get users /subadmins


    //user settings
    @GetMapping("user/getSettingsData")
    public ApiResponse getSettingsData(HttpServletRequest request) {
        String id=jwtService.getUserId(request);
        Optional<User> user=userRepository.findOneByUserId(id);
        if(user.isEmpty()) throw new UserNotFoundException("user not found");
        return this.userService.getSettingsData(user.get());
    }
    @PutMapping("user/updateSetting")
    public ApiResponse saveSetting(@RequestBody UserSettingsBean settings, HttpServletRequest request) {
        String id=jwtService.getUserId(request);
        Optional<User> user=userRepository.findOneByUserId(id);
        if(user.isEmpty()) throw new UserNotFoundException("user not found");
        return this.userService.saveSetting(settings, user.get());
    }
    //user settings


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUB_ADMIN')")
    @DeleteMapping("user/deleteUser")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam(value = "userId") String userId, HttpServletRequest request) {
        ApiResponse apiResponse = userService.deletedUser(userId, jwtService.getUserId(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PutMapping("user/saveRules")
    public ResponseEntity saveRules(@RequestBody RuleBean rule) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ruleService.saveRule(user, rule));
    }


    @GetMapping("user/getRules")
    public ResponseEntity getRules() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ruleService.getRules(user));
    }


}
