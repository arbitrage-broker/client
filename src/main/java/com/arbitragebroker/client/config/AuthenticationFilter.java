package com.arbitragebroker.client.config;

import com.arbitragebroker.client.service.HCaptchaService;
import com.arbitragebroker.client.util.SessionHolder;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final String honeypotFieldName;
    private DatabaseReader dbReader;
    private final HCaptchaService hCaptchaService;
    private final SessionHolder sessionHolder;

    @SneakyThrows
    public AuthenticationFilter(String honeypotFieldName, HCaptchaService hCaptchaService, ResourceLoader resourceLoader, SessionHolder sessionHolder) {
        this.honeypotFieldName = honeypotFieldName;
        this.hCaptchaService = hCaptchaService;
        this.sessionHolder = sessionHolder;
//        Resource databaseResource = resourceLoader.getResource("classpath:GeoLite2-Country.mmdb");
//        try (InputStream databaseStream = databaseResource.getInputStream()) {
//            dbReader = new DatabaseReader.Builder(databaseStream).build();
//        }

    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/page_403") ||
                path.equals("/page_404") ||
                path.equals("/region_denied");
    }
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ipAddress = getClientIp(request);
        MDC.put("clientIp", ipAddress);

        var user = sessionHolder.getCurrentUser();
        if(user != null)
            MDC.put("userId", user.getId().toString());

        boolean isLocalIp = false;
        var profile = Arrays.asList(getEnvironment().getActiveProfiles());

        if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1") || profile.contains("dev"))
            isLocalIp = true;

//        if(!isLocalIp) {
//            // Get country information
//            InetAddress ip = InetAddress.getByName(ipAddress);
//            CountryResponse countryResponse = dbReader.country(ip);
//            String countryCode = countryResponse.getCountry().getIsoCode();
//            // Block specific countries
//            if ("CN".equals(countryCode) || "RU".equals(countryCode) || "IR".equals(countryCode) || "KP".equals(countryCode)) {
//                response.sendRedirect("/region_denied");
//                return;
//            }
//        }
        if (isLoginRequest(request)) {
            String honeypotValue = request.getParameter(honeypotFieldName);
            if (honeypotValue != null && !honeypotValue.isEmpty()) {
                // Bot detected - redirect to login page with error
                response.sendRedirect("/login?errorMsg=botDetected");
                return;
            }
            if(profile.contains("prod")) {
                String captchaResponse = request.getParameter("h-captcha-response");
                if (!hCaptchaService.verifyCaptcha(captchaResponse)) {
                    // Handle failed captcha verification
                    response.sendRedirect("/login?errorMsg=captchaVerificationFailed");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getMethod().equals("POST") &&
                request.getServletPath().equals("/login");
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs in X-Forwarded-For, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}