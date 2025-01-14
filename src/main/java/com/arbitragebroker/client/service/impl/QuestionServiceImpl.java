package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.QQuestionEntity;
import com.arbitragebroker.client.entity.QuestionEntity;
import com.arbitragebroker.client.filter.QuestionFilter;
import com.arbitragebroker.client.mapping.QuestionMapper;
import com.arbitragebroker.client.model.QuestionModel;
import com.arbitragebroker.client.repository.QuestionRepository;
import com.arbitragebroker.client.service.QuestionService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QuestionServiceImpl extends BaseServiceImpl<QuestionFilter, QuestionModel, QuestionEntity, Long> implements QuestionService {

    private QuestionRepository repository;
    private QuestionMapper mapper;

    @Autowired
    public QuestionServiceImpl(QuestionRepository repository, QuestionMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(QuestionFilter filter) {
        QQuestionEntity p = QQuestionEntity.questionEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getTitle().ifPresent(v->builder.and(p.title.toLowerCase().contains(v.toLowerCase())));
        filter.getAnswerType().ifPresent(v->builder.and(p.answerType.eq(v)));
        filter.getType().ifPresent(v->builder.and(p.type.eq(v)));
        filter.getActive().ifPresent(v->builder.and(p.active.eq(v)));
        filter.getUserId().ifPresent(v->builder.and(p.user.id.eq(v)));
        filter.getAnswerId().ifPresent(v->builder.and(p.answers.any().id.eq(v)));

        return builder;
    }
}
