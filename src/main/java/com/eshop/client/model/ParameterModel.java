package com.eshop.client.model;

import lombok.Data;

import java.util.Objects;

@Data
public class ParameterModel extends BaseModel<Long> {
    private String code;
    private String title;
    private String value;
    private ParameterGroupModel parameterGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ParameterModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
