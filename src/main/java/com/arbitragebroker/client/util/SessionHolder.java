package com.arbitragebroker.client.util;

import com.arbitragebroker.client.dto.UserDetailDto;
import com.arbitragebroker.client.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.beans.Transient;
import java.io.Serializable;

@Component
@SessionScope
public class SessionHolder implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient HttpServletRequest httpServletRequest;
    private UserModel userModel;

    public SessionHolder(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public UserModel getCurrentUser() {
        if (userModel != null)
            return userModel;

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            return (principal instanceof UserDetailDto userDetailDto)
                    ? userDetailDto.toUserModel()
                    : null;
        }
        return null;
    }

    @Transient
    public SecurityContextHolderAwareRequestWrapper getRequestWrapper() {
        return new SecurityContextHolderAwareRequestWrapper(httpServletRequest, "");
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
