package com.eshop.client.mapping;

import com.eshop.client.entity.AnswerEntity;
import com.eshop.client.entity.QuestionEntity;
import com.eshop.client.model.AnswerModel;
import com.eshop.client.model.QuestionModel;
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
