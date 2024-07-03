package com.inspirage.ilct.config;


import com.inspirage.ilct.repo.LoginKeyInfoRepo;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.ConfigurationService;
import com.inspirage.ilct.service.PropertiesService;
import com.inspirage.ilct.service.RoleSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
 TokenUtilService tokenUtilService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LoginKeyInfoRepo loginKeyInfoRepo;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    PropertiesService propertiesService;
    @Autowired
    RoleSettingsService roleSettingsService;
    @Autowired
    ConfigurationService userConfigurationService;


    private final String[] PUBLIC_RESOURCE_AND_URL = {"/",
            "/v1/ui/auth/get_login_key",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/api-docs/**",
            "/v3/otm-post/save-otm-shipment-status",
            "/v3/otm-post/save-otm-shipment-data",
            "/kpi/monitors/get-monitor-by-user",
            "/v3/otm-post/add-shipment-event",
            "/v3/otm-post/get-all-timezones",
            "/manage-rule/user-get-rules",
    "/v3/otm-get/logging",
    "/v3/otm-get/completedShipments",
    "/v3/otm-get/getShipmentStatusCount"};

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(new AuthenticateUserFilter(tokenUtilService, loginKeyInfoRepo, userRepository,bCryptPasswordEncoder, propertiesService,roleSettingsService,userConfigurationService), BasicAuthenticationFilter.class)   //token method
                .addFilterBefore(new CustomCorsFilter(), ChannelProcessingFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true).ignoring().requestMatchers(PUBLIC_RESOURCE_AND_URL);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
