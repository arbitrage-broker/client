package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.AnswerEntity;
import com.arbitragebroker.client.entity.QAnswerEntity;
import com.arbitragebroker.client.filter.AnswerFilter;
import com.arbitragebroker.client.mapping.AnswerMapper;
import com.arbitragebroker.client.model.AnswerModel;
import com.arbitragebroker.client.repository.AnswerRepository;
import com.arbitragebroker.client.service.AnswerService;
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
