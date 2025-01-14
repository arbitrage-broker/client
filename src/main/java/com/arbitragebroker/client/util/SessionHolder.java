package com.arbitragebroker.client.util;

import com.arbitragebroker.client.model.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;

import static com.arbitragebroker.client.util.MapperHelper.get;

@Component
@SessionScope
public class SessionHolder {
    private HttpServletRequest request;
    private ObjectMapper objectMapper;

    public SessionHolder(HttpServletRequest request) {
        this.request = request;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public UserModel getCurrentUser() {
        return (UserModel) request.getSession().getAttribute("currentUser");
    }
    public void setCurrentUser(UserModel user) {
        if(get(()->request.getSession()) != null)
            request.getSession().setAttribute("currentUser", user);
    }
    @SneakyThrows
    public String getCurrentUserAsJsonString() {
        return  objectMapper.writeValueAsString(request.getSession().getAttribute("currentUser"));
    }
    public SecurityContextHolderAwareRequestWrapper getRequestWrapper() {
        return new SecurityContextHolderAwareRequestWrapper(request, "");
    }
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
