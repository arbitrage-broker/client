package com.arbitragebroker.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class AnswerModel extends BaseModel<Long> {
    @NotNull
    private QuestionModel question;
    @NotNull
    private String title;
    private Integer displayOrder;
    @NotNull
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AnswerModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
