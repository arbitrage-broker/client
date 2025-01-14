package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Setter
@ToString
public class ParameterFilter {
    private Long id;
    private String code;
    private String title;
    private String value;
    private ParameterGroupFilter parameterGroup;


    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getCode() {
        if (code == null || code.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(code);
    }

    public Optional<String> getTitle() {
        if (title == null || title.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(title);
    }

    public Optional<String> getValue() {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public Optional<ParameterGroupFilter> getParameterGroup() {
        return Optional.ofNullable(parameterGroup);
    }
}
