package com.arbitragebroker.client.config;

import com.arbitragebroker.client.dto.UserDetailDto;
import com.arbitragebroker.client.enums.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuccessLoginConfig implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy;

    public SuccessLoginConfig() {
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailDto user = (UserDetailDto) authentication.getCredentials();
        MDC.put("userId", user.getId().toString());

        SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "");
        String targetUrl = "/access-denied";
        if (requestWrapper.isUserInRole(RoleType.ADMIN) || requestWrapper.isUserInRole(RoleType.SUPER_WISER) ||requestWrapper.isUserInRole(RoleType.MANAGER) || requestWrapper.isUserInRole(RoleType.USER))
            targetUrl = "/dashboard";
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}
