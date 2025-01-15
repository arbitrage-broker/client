package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.service.OneTimePasswordService;
import com.arbitragebroker.client.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public abstract class BaseMailService {
    private final UserService userService;
    private final MessageConfig messages;
    private final OneTimePasswordService oneTimePasswordService;
    private final ResourceLoader resourceLoader;
    @Value("${site.url}")
    String siteUrl;

    public BaseMailService(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService,
                           ResourceLoader resourceLoader) {
        this.userService = userService;
        this.messages = messages;
        this.oneTimePasswordService = oneTimePasswordService;
        this.resourceLoader = resourceLoader;
    }

    public abstract void send(String to, String subject, String body);

    @SneakyThrows
    public void sendOTP(String to, String subject) {
        var entity = userService.findByEmail(to);
        var otp = oneTimePasswordService.create(entity.getId());
        String appName = messages.getMessage("siteName");

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/otp-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        emailContent = emailContent.replace("[user_first_name]", entity.getFirstName());
        emailContent = emailContent.replace("[YourAppName]", appName);
        emailContent = emailContent.replace("[otp_code]", otp);

        send(to, subject, emailContent);
    }

    @SneakyThrows
    public void sendVerification(String to, String subject) {
        var user = userService.findByEmail(to);
        var otp = oneTimePasswordService.create(user.getId());
        String appName = messages.getMessage("siteName");
        String link = String.format("https://%s/api/v1/user/verify-email/%s/%s",siteUrl,user.getId().toString(), otp);

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/verification-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        emailContent = emailContent.replace("[user_first_name]", user.getFirstName());
        emailContent = emailContent.replace("[YourAppName]", appName);
        emailContent = emailContent.replace("[verification_code]", link);
        emailContent = emailContent.replace("[verification_link]", link);

        send(to, subject, emailContent);
    }
}
