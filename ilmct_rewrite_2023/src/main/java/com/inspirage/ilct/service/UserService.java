package com.inspirage.ilct.service;

import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.UserSettingsBean;
import com.inspirage.ilct.dto.request.UserLoginBean;
import com.inspirage.ilct.dto.request.UserBean;
import com.inspirage.ilct.exceptions.ApplicationSettingsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;

public interface UserService {
    ApiResponse login(UserLoginBean userLoginBean, HttpServletRequest request);

    ApiResponse logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    ApiResponse userSave(UserBean userBean, HttpServletRequest request) throws ApplicationSettingsNotFoundException;

    ApiResponse getUsers(int pageIndex, int numberOfRecord, String loginUser, String searchText);

    ApiResponse getSubAdmins(int pageIndex, int numberOfRecord, String userId, String searchText);

    ApiResponse deletedUser(String userId, String request);

    ApiResponse saveSetting(UserSettingsBean settings, User user);

    ApiResponse getSettingsData(User user);
}
