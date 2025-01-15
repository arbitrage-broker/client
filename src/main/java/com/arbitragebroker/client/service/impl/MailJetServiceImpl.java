package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.dto.MailJetRequest;
import com.arbitragebroker.client.service.OneTimePasswordService;
import com.arbitragebroker.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.arbitragebroker.client.util.MapperHelper.getOrDefault;
import static org.apache.logging.log4j.util.LambdaUtil.get;

@Service
@Slf4j
@Profile("prod")
public class MailJetServiceImpl extends BaseMailService {
    private final RestTemplate restTemplate;
    @Value("${site.url}")
    String siteUrl;
    @Value("${site.name}")
    String siteName;
    @Value("${mailjet.token}")
    String token;
    @Value("${mailjet.url}")
    String url;

    public MailJetServiceImpl(UserService userService, MessageConfig messages, OneTimePasswordService oneTimePasswordService,
                              ResourceLoader resourceLoader, RestTemplate restTemplate) {
        super(userService, messages, oneTimePasswordService, resourceLoader);
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(String to, String subject, String body) {
        try {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + token);
            // Create HTTP entity
            var emailMessage = new MailJetRequest().setMessages(List.of(new MailJetRequest.Message()
                    .setSubject(subject)
                    .setHtmlPart(body)
                    .setFrom(new MailJetRequest.EmailAddress().setEmail("noreply@" + siteUrl).setName(siteName))
                    .setTo(List.of(new MailJetRequest.EmailAddress().setEmail(to).setName("Me")))
            ));
            HttpEntity<MailJetRequest> requestEntity = new HttpEntity<>(emailMessage, headers);

            var response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if(!response.getStatusCode().is2xxSuccessful() ||
                    getOrDefault(()-> !response.getBody().contains("\"Status\":\"success\""),false)){
                log.error("failed to send email. {}", get(()->response.getBody().toString()));
                throw new IllegalStateException("failed to send email." + get(()->response.getBody().toString()));
            }
        } catch (Exception e) {
            log.error("failed to send email. {}", e.getMessage());
            throw new IllegalStateException("failed to send email." + e.getMessage());
        }
    }
}
