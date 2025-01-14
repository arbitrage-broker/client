package com.arbitragebroker.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.StringJoiner;

@Data
@Accessors(chain = true)
public class MailJetResponse {

    @JsonProperty("Messages")
    private List<Message> messages;

    @Override
    public String toString() {
        return new StringJoiner(", ", MailJetResponse.class.getSimpleName() + "[", "]")
                .add("messages=" + messages)
                .toString();
    }

    @Data
    @Accessors(chain = true)
    public static class Message {
        @JsonProperty("Status")
        private String status;

        @JsonProperty("CustomID")
        private String customID;

        @JsonProperty("To")
        private List<Recipient> to;

        @JsonProperty("Cc")
        private List<Recipient> cc;

        @JsonProperty("Bcc")
        private List<Recipient> bcc;

        @Override
        public String toString() {
            return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                    .add("status='" + status + "'")
                    .add("customID='" + customID + "'")
                    .add("to=" + to)
                    .add("cc=" + cc)
                    .add("bcc=" + bcc)
                    .toString();
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Recipient {
        @JsonProperty("Email")
        private String email;

        @JsonProperty("MessageUUID")
        private String messageUUID;

        @JsonProperty("MessageID")
        private long messageID;

        @JsonProperty("MessageHref")
        private String messageHref;

        @Override
        public String toString() {
            return new StringJoiner(", ", Recipient.class.getSimpleName() + "[", "]")
                    .add("email='" + email + "'")
                    .add("messageUUID='" + messageUUID + "'")
                    .add("messageID=" + messageID)
                    .add("messageHref='" + messageHref + "'")
                    .toString();
        }
    }
}
