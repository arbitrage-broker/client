package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.AnswerEntity;
import com.arbitragebroker.client.entity.QuestionEntity;
import com.arbitragebroker.client.model.AnswerModel;
import com.arbitragebroker.client.model.QuestionModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AnswerMapper extends BaseMapper<AnswerModel, AnswerEntity> {
    @Override
    @Mappings({
            @Mapping(target = "question", qualifiedByName = "questionEntityToQuestionModel")
    })
    AnswerModel toModel(final AnswerEntity entity);

    @Named("questionEntityToQuestionModel")
    @Mapping(target = "answers", ignore = true)
    QuestionModel questionEntityToQuestionModel(QuestionEntity entity);
}
