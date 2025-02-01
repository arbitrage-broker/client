package com.arbitragebroker.client.util;

import com.arbitragebroker.client.dto.UserDetailDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.beans.Transient;
import java.io.Serializable;
import java.util.UUID;

@Component
@SessionScope
public class SessionHolder implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient HttpServletRequest httpServletRequest;
    private UUID userId;

    public SessionHolder(HttpServletRequest request) {
        this.httpServletRequest = request;
    }

    public UserDetailDto getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            return (UserDetailDto) authentication.getPrincipal();
        }
        return null;
    }

    @Transient
    public SecurityContextHolderAwareRequestWrapper getRequestWrapper() {
        return new SecurityContextHolderAwareRequestWrapper(httpServletRequest, "");
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
