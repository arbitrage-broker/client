package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Setter
@ToString
public class CountryFilter {
    private Long id;
    private String name;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getName() {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }
}
