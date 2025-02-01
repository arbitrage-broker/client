package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.service.OneTimePasswordService;
import com.arbitragebroker.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
@Profile("test")
public class MailServiceImpl extends BaseMailService {
    private final JavaMailSender mailSender;
    @Value("${site.url}")
    String siteUrl;

    public MailServiceImpl(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService,
                           ResourceLoader resourceLoader, JavaMailSender mailSender) {
        super(userService, messages, oneTimePasswordService, resourceLoader);
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("admin@" + siteUrl, "Support");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("failed to send email. {}", e.getMessage());
            throw new IllegalStateException("failed to send email." + e.getMessage());
        }
    }
}
