package com.arbitragebroker.client.model;

import com.arbitragebroker.client.enums.AnswerType;
import com.arbitragebroker.client.enums.QuestionType;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class QuestionModel extends BaseModel<Long> {
    @NotNull
    private String title;
    private Integer displayOrder;
    @NotNull
    private QuestionType type;
    @NotNull
    private AnswerType answerType;
    private UserModel user;
    private List<AnswerModel> answers = new ArrayList<>();;
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (QuestionModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
