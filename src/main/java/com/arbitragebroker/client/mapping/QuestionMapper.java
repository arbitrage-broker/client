package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.AnswerEntity;
import com.arbitragebroker.client.entity.QuestionEntity;
import com.arbitragebroker.client.model.AnswerModel;
import com.arbitragebroker.client.model.QuestionModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface QuestionMapper extends BaseMapper<QuestionModel, QuestionEntity> {
    @Override
    @Mappings({
            @Mapping(target = "answers", qualifiedByName = "answerEntityToAnswerModel")
    })
    QuestionModel toModel(final QuestionEntity entity);

    @Named("answerEntityToAnswerModel")
    @Mapping(target = "question", ignore = true)
    AnswerModel answerEntityToAnswerModel(AnswerEntity entity);
}
