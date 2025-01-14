package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Setter
@ToString
public class AnswerFilter {
    private Long id;
    private String title;
    private Long questionId;
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

    public Optional<Long> getQuestionId() {
        return Optional.ofNullable(questionId);
    }

    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }
}
