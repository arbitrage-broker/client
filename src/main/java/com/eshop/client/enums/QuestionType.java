package com.eshop.client.enums;

import com.eshop.client.config.MessageConfig;
import com.eshop.client.model.Select2Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionType {
    TICKET("Ticket"),
    GENERAL("General");
    QuestionType(String title) {
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

            for (QuestionType type : EnumSet.allOf(QuestionType.class)) {
                type.setMessages(messages);
            }
        }
    }
}
