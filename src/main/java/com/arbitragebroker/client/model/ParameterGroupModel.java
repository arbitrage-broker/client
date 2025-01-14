package com.arbitragebroker.client.model;

import lombok.Data;

import java.util.Objects;

@Data
public class ParameterGroupModel extends BaseModel<Long> {
    private String code;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ParameterGroupModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
