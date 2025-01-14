package com.eshop.client.service.impl;

import com.eshop.client.entity.AnswerEntity;
import com.eshop.client.entity.QAnswerEntity;
import com.eshop.client.filter.AnswerFilter;
import com.eshop.client.mapping.AnswerMapper;
import com.eshop.client.model.AnswerModel;
import com.eshop.client.repository.AnswerRepository;
import com.eshop.client.service.AnswerService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AnswerServiceImpl extends BaseServiceImpl<AnswerFilter, AnswerModel, AnswerEntity, Long> implements AnswerService {

    private AnswerRepository repository;
    private AnswerMapper mapper;

    @Autowired
    public AnswerServiceImpl(AnswerRepository repository, AnswerMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(AnswerFilter filter) {
        QAnswerEntity p = QAnswerEntity.answerEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getTitle().ifPresent(v->builder.and(p.title.toLowerCase().contains(v.toLowerCase())));
        filter.getQuestionId().ifPresent(v->builder.and(p.question.id.eq(v)));
        filter.getActive().ifPresent(v->builder.and(p.active.eq(v)));

        return builder;
    }
}
