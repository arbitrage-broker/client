package com.arbitragebroker.client.enums;

import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.model.Select2Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum EntityStatusType {
    Active("Active"),
    Passive("Passive"),
    Pending("Pending"),
    Expired("Expired"),
    Refunded("Refunded"),
    Invalid("Invalid"),
    ;
    EntityStatusType(String title) {
        this.title = title;
    }
    private MessageConfig messages;
    private String title;
    public MessageConfig getMessages() {
        return messages;
    }

    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return messages.getMessage(title);
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public static List<Select2Model> getAll() {
        return Arrays.stream(values()).map(m -> new Select2Model(m.name(), m.getTitle())).collect(Collectors.toList());
    }

    @Component
    public static class EnumValuesInjectionService {

        @Autowired
        private MessageConfig messages;

        //Inject into bean through static inner class and assign value to enum.
        @PostConstruct
        public void postConstruct() {

            for (EntityStatusType type : EnumSet.allOf(EntityStatusType.class)) {
                type.setMessages(messages);
            }
        }
    }
}