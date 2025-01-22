package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.service.HCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
        Map<String, String> body = new HashMap<>();
        body.put("secret", secret);
        body.put("response", token);

        Map<String, Object> response = restTemplate.postForObject(HCAPTCHA_VERIFY_URL, body, Map.class);

        return response != null && (Boolean) response.get("success");
    }
}
