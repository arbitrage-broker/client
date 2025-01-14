package com.eshop.client.model;

import lombok.Data;

import java.util.Objects;

@Data
public class CoinModel extends BaseModel<Long> {
    private String name;
    private String logo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (CoinModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
