package com.inspirage.ilct.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inspirage.ilct.documents.LoginKeyInfo;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.request.UserBean;
import com.inspirage.ilct.dto.response.UserConfigurationsBean;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.LoginKeyInfoRepo;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.ConfigurationService;
import com.inspirage.ilct.service.PropertiesService;
import com.inspirage.ilct.service.RoleSettingsService;
import com.inspirage.ilct.util.Utility;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.inspirage.ilct.config.TokenUtilService.AUTH_HEADER_NAME;
import static com.inspirage.ilct.util.Constants.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticateUserFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateUserFilter.class);
    private final TokenUtilService tokenUtilService;
    private final UserRepository userRepository;
    private final LoginKeyInfoRepo loginKeyInfoRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PropertiesService propertiesService;
    private final RoleSettingsService roleSettingsService;
    private final ConfigurationService userConfigurationService;
    private static final String[] EXCLUDED_URLS = {};

    public AuthenticateUserFilter(TokenUtilService tokenUtilService, LoginKeyInfoRepo loginKeyInfoRepo,
                                  UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                                  PropertiesService propertiesService, RoleSettingsService roleSettingsService,
                                  ConfigurationService userConfigurationService) {

        this.tokenUtilService = tokenUtilService;
        this.userRepository = userRepository;
        this.loginKeyInfoRepo = loginKeyInfoRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.propertiesService = propertiesService;
        this.roleSettingsService = roleSettingsService;
        this.userConfigurationService = userConfigurationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<LoginKeyInfo> loginKeyInfos=loginKeyInfoRepo.findAll().parallelStream().toList();
        String dataString, username = null, password = null;
        try {
            if (propertiesService.getEnableSecurity()) {
                String str = request.getHeader(AUTH_HEADER_NAME);
                LoginKeyInfo loginKeyInfo=loginKeyInfos.parallelStream().filter(a->a.getIpAddress().contains(Utility.getRequestedFrom(request))).findFirst().orElse(null);
                try {
                    String decryptedPassword = new String(java.util.Base64.getDecoder().decode(str));
                    AesUtil aesUtil = new AesUtil(128, 1000);
                    dataString = aesUtil.decrypt(decryptedPassword.split("::")[1], decryptedPassword.split("::")[0], loginKeyInfo.getHash(), decryptedPassword.split("::")[2]);
                    String[] decoadedData = dataString.split("&Password=");
                    username = decoadedData[0].replace("UserId=", "");
                    password = decoadedData[1];
                } catch (Exception e) {
                    try {
                        User loginUser = tokenUtilService.parseUserFromToken(str);
                        username = loginUser.getUserId();
                        password = loginUser.getPassword();
                    } catch (MalformedJwtException | io.jsonwebtoken.io.DecodingException exception) {
                        String tokenWithoutBearer = extractAuthToken(str);
                        User loginUser = tokenUtilService.parseUserFromToken(tokenWithoutBearer);
                        username = loginUser.getUserId();
                        password = loginUser.getPassword();
                    } catch (InvalidUserTokenException w) {
                        setErrorResponse(response, "Session Expired", HttpStatus.UNAUTHORIZED.value(), SESSION_EXPIRED);
                    }
                }
                loginKeyInfo.setHash(null);
                loginKeyInfoRepo.save(loginKeyInfo);
            } else {
                String jsonString = Utility.toString(request.getInputStream(), "UTF-8");
                JSONObject userJSON = new JSONObject(jsonString);
                username = userJSON.getString("userId");
                password = userJSON.getString("password");
            }
            if (username == null && password == null)
                setErrorResponse(response, USER_PASSWORD_REQUIRED, HttpStatus.BAD_REQUEST.value(), 0);
            User user = new User();
            if (username != null) {
                try {
                    user = userRepository.findOneByUserIdIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
                } catch (IncorrectResultSizeDataAccessException e) {
                    user = userRepository.findByUserIdIgnoreCase(username).get(0);//orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
                }
            }
            if (request.getRequestURI().contains("/v1/ui/auth/login")) {
                if (password != null && !bCryptPasswordEncoder.matches(password, user.getPassword())) {
                    setErrorResponse(response, INVALID_CREDENTIALS_MESSAGE, HttpStatus.UNAUTHORIZED.value(), 0);
                    return;
                }
            }
            if (!user.isActive())
                setErrorResponse(response, ACCOUNT_ACTIVATE_MESSAGE, HttpStatus.UNAUTHORIZED.value(), ACCOUNT_LOCKED);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    List.of(new SimpleGrantedAuthority(user.getRole().name())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (request.getRequestURI().contains("/v1/ui/auth/login")) {
                User loginUser = (User) authentication.getPrincipal();
                String tokenString = this.tokenUtilService.createTokenForUser(new LoginUser(loginUser), Utility.getRequestedFrom(request));
                setLoginResponse(response, new LoginUser(loginUser), tokenString, request);
            } else
                filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            setErrorResponse(response, INVALID_CREDENTIALS_MESSAGE, HttpStatus.UNAUTHORIZED.value(), 0);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        for (String pattern : EXCLUDED_URLS) {
            if (new AntPathMatcher().match(pattern, requestURI)) {
                return true;
            }
        }
        return false;
    }

    private void setLoginResponse(HttpServletResponse res, LoginUser tokenUser, String tokenString, HttpServletRequest request) throws IOException, UserNotFoundException {
        UserBean resp = new UserBean();
        resp.setFirstName(tokenUser.getUser().getFirstName());
        resp.setLastName(tokenUser.getUser().getLastName());
        resp.setUserId(tokenUser.getUser().getUserId());
        resp.setEmail(tokenUser.getUser().getEmail());
        resp.setJwtToken(tokenString);
        resp.setRole(tokenUser.getRole());
        resp.setBaseMapCountry(tokenUser.getUser().getBaseMapCountry());
        resp.setPreferredLanguage(tokenUser.getLangauge());
        resp.setMapLanguageId(tokenUser.getUser().getMapLanguageId());
        UserConfigurationsBean configurationsBean = (UserConfigurationsBean) userConfigurationService.getUserConfigurationsById(resp.getUserId(), null).getData();
        resp.setUserConfigurationsBean(configurationsBean);
        resp.setRoleSettings(roleSettingsService.getRoleSettingsWithApiResponse(tokenUser.getUser().getRole().name(), request));
        resp.setWatchList(tokenUser.getUser().getWatchList());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(HttpServletResponse.SC_OK);
        apiResponse.setMessage("successfully login");
        apiResponse.setData(resp);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonRespString = ow.writeValueAsString(apiResponse);
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");
        res.getWriter().write(jsonRespString);
        res.getWriter().flush();
        res.getWriter().close();
    }

    private void setErrorResponse(HttpServletResponse response, String message, int httpStatus, int customCode) throws IOException {
        response.setStatus(httpStatus);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ApiResponse apiResponse = new ApiResponse();
        if (customCode != 0) apiResponse.setStatusCode(customCode);
        else apiResponse.setStatusCode(httpStatus);
        apiResponse.setMessage(message);
        String jsonRespString = ow.writeValueAsString(apiResponse);
        response.setContentType("application/json");
        response.getWriter().write(jsonRespString);
        response.getWriter().flush();
        response.getWriter().close();
    }

    private String extractAuthToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            return authorizationHeader.substring(7); // Will be useful with Swagger
        }
        return authorizationHeader; // Token not found or header value format is incorrect
    }
}
