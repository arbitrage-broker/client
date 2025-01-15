package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.service.OneTimePasswordService;
import com.arbitragebroker.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("dev")
public class FakeMailServiceImpl extends BaseMailService {

    public FakeMailServiceImpl(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService,
                               ResourceLoader resourceLoader) {
        super(userService, messages, oneTimePasswordService, resourceLoader);
    }

    @Override
    public void send(String to, String subject, String body) {
    }
}
