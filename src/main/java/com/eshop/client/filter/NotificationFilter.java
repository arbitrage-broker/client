package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
public class NotificationFilter {
    private Long id;
    private UUID senderId;
    private UUID recipientId;
    private String subject;
    private String body;
    private Boolean read;


    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<UUID> getSenderId() {
        return Optional.ofNullable(senderId);
    }

    public Optional<UUID> getRecipientId() {
        return Optional.ofNullable(recipientId);
    }

    public Optional<String> getSubject() {
        if (subject == null || subject.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(subject);
    }

    public Optional<String> getBody() {
        if (body == null || body.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(body);
    }

    public Optional<Boolean> getRead() {
        return Optional.ofNullable(read);
    }
}
