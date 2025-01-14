package com.arbitragebroker.client.service;

public interface TelegramService {
    void sendMessage(String chatId, String text);
    void sendToRole(String role, String text);
}
