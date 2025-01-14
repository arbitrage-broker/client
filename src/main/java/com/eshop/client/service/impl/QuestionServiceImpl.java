package com.eshop.client.service.impl;

import com.eshop.client.entity.QQuestionEntity;
import com.eshop.client.entity.QuestionEntity;
import com.eshop.client.filter.QuestionFilter;
import com.eshop.client.mapping.QuestionMapper;
import com.eshop.client.model.QuestionModel;
import com.eshop.client.repository.QuestionRepository;
import com.eshop.client.service.QuestionService;
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
