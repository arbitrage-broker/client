package com.eshop.client.filter;

import com.eshop.client.enums.AnswerType;
import com.eshop.client.enums.QuestionType;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
public class QuestionFilter {
    private Long id;
    private String title;
    private Long answerId;
    private UUID userId;
    private QuestionType type;
    private AnswerType answerType;
    private Boolean active;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getTitle() {
        if (title == null || title.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(title);
    }

    public Optional<Long> getAnswerId() {
        return Optional.ofNullable(answerId);
    }

    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<QuestionType> getType() {
        return Optional.ofNullable(type);
    }

    public Optional<AnswerType> getAnswerType() {
        return Optional.ofNullable(answerType);
    }

    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }
}
