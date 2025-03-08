package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService, Serializable {

    private final transient UserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userService.findByUserNameOrEmail(login);
    }
}
