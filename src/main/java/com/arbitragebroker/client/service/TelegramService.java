package com.arbitragebroker.client.service;

import java.io.Serializable;

public interface TelegramService extends Serializable {
    void sendMessage(String chatId, String text);
    void sendToRole(String role, String text);
}
