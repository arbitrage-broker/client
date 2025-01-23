package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.service.HCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HCaptchaServiceImpl implements HCaptchaService {

    private final RestTemplate restTemplate;
    private static final String HCAPTCHA_VERIFY_URL = "https://hcaptcha.com/siteverify";
    @Value("${hcaptcha.secret}")
    private String secret;


    @Override
    public boolean verifyCaptcha(String token) {
        if (!StringUtils.hasLength(token))
            return false;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secret);
        body.add("response", token);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(HCAPTCHA_VERIFY_URL, request, Map.class);


        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                return Boolean.TRUE.equals(responseBody.get("success"));
            }
        }
        return false;
    }
}
