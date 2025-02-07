package com.arbitragebroker.client.config;

import com.arbitragebroker.client.service.HCaptchaService;
import com.maxmind.geoip2.DatabaseReader;
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
import java.util.UUID;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final String honeypotFieldName;
    private DatabaseReader dbReader;
    private final HCaptchaService hCaptchaService;

    @SneakyThrows
    public AuthenticationFilter(String honeypotFieldName, HCaptchaService hCaptchaService, ResourceLoader resourceLoader) {
        this.honeypotFieldName = honeypotFieldName;
        this.hCaptchaService = hCaptchaService;

        Resource databaseResource = resourceLoader.getResource("classpath:GeoLite2-Country.mmdb");
        try (InputStream databaseStream = databaseResource.getInputStream()) {
            dbReader = new DatabaseReader.Builder(databaseStream).build();
        }

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
        MDC.put("traceId", UUID.randomUUID().toString());
        String ipAddress = getClientIp(request);
        MDC.put("clientIp", ipAddress);
        boolean isLocalIp = false;
        var profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1") || profile.equals("dev"))
            isLocalIp = true;

        if(!isLocalIp) {
            // Get country information
            InetAddress ip = InetAddress.getByName(ipAddress);
            CountryResponse countryResponse = dbReader.country(ip);
            String countryCode = countryResponse.getCountry().getIsoCode();
            // Block specific countries
            if ("CN".equals(countryCode) || "RU".equals(countryCode) || "IR".equals(countryCode) || "KP".equals(countryCode)) {
                response.sendRedirect("/region_denied");
                return;
            }
        }
        if (isLoginRequest(request)) {
            String honeypotValue = request.getParameter(honeypotFieldName);
            if (honeypotValue != null && !honeypotValue.isEmpty()) {
                // Bot detected - redirect to login page with error
                response.sendRedirect("/login?errorMsg=botDetected");
                return;
            }
            if(profile.equals("prod")) {
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
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getMethod().equals("POST") &&
                request.getServletPath().equals("/login");
    }
}