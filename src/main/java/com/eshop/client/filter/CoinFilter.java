package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Setter
@ToString
public class CoinFilter {
    private Long id;
    private String name;
    private String logo;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getName() {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    public Optional<String> getLogo() {
        if (logo == null || logo.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(logo);
    }
}
