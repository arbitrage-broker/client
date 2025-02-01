package com.arbitragebroker.client.enums;

import org.springframework.security.core.context.SecurityContextHolder;

public class RoleType {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String SUPER_WISER = "ROLE_SUPER_WISER";
    public static final String MANAGER = "ROLE_MANAGER";
    public static final String USER = "ROLE_USER";

    public static String name(String role){
        return role.replace("ROLE_","");
    }
    public static boolean hasRole(String roleName) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
        }
        return false;
    }

    public static String getSupportUID(String role) {
        switch (role) {
            case SUPER_WISER:
                return "6303b84a-04cf-49e1-8416-632ebd84495e";
            case MANAGER:
                return "97041d85-4cb3-420b-b046-4a27091389ce";
            default:
                return "6303b84a-04cf-49e1-8416-632ebd84495e";
        }
    }
}
