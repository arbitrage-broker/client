package com.arbitragebroker.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MailJetRequest {
    @JsonProperty("Messages")
    private List<Message> messages;

    @Data
    @Accessors(chain = true)
    public static class Message {
        @JsonProperty("From")
        private EmailAddress from;
        @JsonProperty("To")
        private List<EmailAddress> to;
        @JsonProperty("Subject")
        private String subject;
        @JsonProperty("TextPart")
        private String textPart;
        @JsonProperty("HTMLPart")
        private String htmlPart;
    }

    @Data
    @Accessors(chain = true)
    public static class EmailAddress {
        @JsonProperty("Email")
        private String email;
        @JsonProperty("Name")
        private String name;
    }
}
