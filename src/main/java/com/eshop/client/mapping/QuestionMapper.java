package com.eshop.client.mapping;

import com.eshop.client.entity.AnswerEntity;
import com.eshop.client.entity.QuestionEntity;
import com.eshop.client.model.AnswerModel;
import com.eshop.client.model.QuestionModel;
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
