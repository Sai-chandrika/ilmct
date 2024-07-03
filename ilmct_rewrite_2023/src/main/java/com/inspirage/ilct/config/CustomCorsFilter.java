package com.inspirage.ilct.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.inspirage.ilct.config.TokenUtilService.AUTH_HEADER_NAME;

public class CustomCorsFilter extends OncePerRequestFilter {
    private final List<String> allowedOrigins = Arrays.asList("http://localhost", "http://localhost:8001",
            "http://localhost:26", "http://localhost:3000", "http://ci.thrymr.net:47", "http://localhost:10026",
            "http://ci.thrymr.net:10026", " https://otm-42947886.intrapod.oraclecloud.com", "http://localhost:8001", "https://inspvcead05.aws.inspirage.com",
            "https://inspihubcs01.aws.inspirage.com", "http://ci.thrymr.net:8089", "https://inspilmdev01-ext.inspirage.com:8399",
            "https://inspilmdev01-ext.inspirage.com:8399/ilmct/", "https://otm-42947886.intrapod.oraclecloud.com",
            "https://otmgtm-test-a581693.otm.em2.oraclecloud.com:443", "https://otmgtm-test-a581693.otm.em2.oraclecloud.com", "http://13.212.31.37:9063");



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type,X-API-KEY, Accept, Accept-Encoding, Accept-Language, Host, Referer, Connection, User-Agent, authorization," +
                        " sw-useragent, sw-version");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
