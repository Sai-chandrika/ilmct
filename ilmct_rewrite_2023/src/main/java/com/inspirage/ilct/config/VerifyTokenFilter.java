package com.inspirage.ilct.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

import static com.inspirage.ilct.util.Constants.INVALID_TOKEN;
import static com.inspirage.ilct.util.Constants.SESSION_EXPIRED;

/*
This filter checks if there is a token in the Request service header and the token is not expired
it is applied to all the routes which are protected
*/
public class VerifyTokenFilter extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(VerifyTokenFilter.class);
    private final TokenUtilService tokenUtilService;

    public VerifyTokenFilter(TokenUtilService tokenUtilService) {
        this.tokenUtilService = tokenUtilService;
    }

    private void setResponse(HttpServletResponse response, String message, int httpStatus, int customCode) throws IOException {
        response.setStatus(httpStatus);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ApiResponse apiResponse = new ApiResponse();
        if (customCode != 0)
            apiResponse.setStatusCode(customCode);
        else
            apiResponse.setStatusCode(httpStatus);
        apiResponse.setMessage(message);
        String jsonRespString = ow.writeValueAsString(apiResponse);
        response.setContentType("application/json");
        response.getWriter().write(jsonRespString);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            Optional<Authentication> authentication = tokenUtilService.verifyToken(httpServletRequest);
            if (authentication.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(authentication.get());
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                logger.error("ExpiredJwtException");
                setResponse(httpServletResponse, "Session Expired", HttpStatus.UNAUTHORIZED.value(), SESSION_EXPIRED);
            }
            if (e instanceof SignatureException) {
                logger.error("SignatureException");
                setResponse(httpServletResponse, "Invalid Token", HttpStatus.UNAUTHORIZED.value(), INVALID_TOKEN);
            }
            if (e instanceof UsernameNotFoundException) {
                logger.error("UsernameNotFoundException");
                setResponse(httpServletResponse, "User Not Found", HttpStatus.UNAUTHORIZED.value(), HttpStatus.NOT_FOUND.value());
            }
            if (e instanceof InvalidUserTokenException) {
                logger.error("InvalidUserTokenException");
                setResponse(httpServletResponse, e.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.value());
            } else {
                e.printStackTrace();
                setResponse(httpServletResponse, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), 0);
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}
