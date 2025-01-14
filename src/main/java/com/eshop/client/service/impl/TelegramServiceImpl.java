package com.eshop.client.service.impl;

import com.eshop.client.entity.TelegramEntity;
import com.eshop.client.repository.TelegramRepository;
import com.eshop.client.service.ParameterService;
import com.eshop.client.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {

    private final TelegramRepository repository;
    private final String apiUrl;
    private final RestTemplate restTemplate;

    public TelegramServiceImpl(TelegramRepository repository, RestTemplate restTemplate, ParameterService parameterService) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        String botToken = parameterService.findByCode("TELEGRAM_BOT_TOKEN").getValue();
        this.apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }

    @Override
    public void sendMessage(String chatId, String text) {
        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", text);
        params.put("parse_mode", "Markdown");
        try {
            restTemplate.postForObject(apiUrl, params, String.class);
        } catch (Exception e) {
            log.error("Failed to send message.", e);
        }
    }
    @Override
    public void sendToRole(String role, String text) {
        List<TelegramEntity> list = repository.findAllByRole(role);
        if(!CollectionUtils.isEmpty(list)) {
            for (TelegramEntity entity : list) {
                sendMessage(entity.getChatId(),text);
            }
        }
    }
}
