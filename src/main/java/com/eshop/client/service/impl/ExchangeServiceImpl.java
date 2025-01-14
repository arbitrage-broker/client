package com.eshop.client.service.impl;

import com.eshop.client.entity.ExchangeEntity;
import com.eshop.client.entity.QExchangeEntity;
import com.eshop.client.filter.ExchangeFilter;
import com.eshop.client.mapping.ExchangeMapper;
import com.eshop.client.model.ExchangeModel;
import com.eshop.client.repository.ExchangeRepository;
import com.eshop.client.service.ExchangeService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExchangeServiceImpl extends BaseServiceImpl<ExchangeFilter, ExchangeModel, ExchangeEntity, Long> implements ExchangeService {

    private ExchangeRepository repository;
    private ExchangeMapper mapper;

    @Autowired
    public ExchangeServiceImpl(ExchangeRepository repository, ExchangeMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(ExchangeFilter filter) {
        QExchangeEntity p = QExchangeEntity.exchangeEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));
        filter.getLogo().ifPresent(v->builder.and(p.logo.eq(v)));

        return builder;
    }

    @Override
    public List<ExchangeModel> findByRandom() {
        return mapper.toModel(repository.findByRandom());
    }
    @Override
    public List<ExchangeModel> findAllByRandom(int count) {
        return mapper.toModel(repository.findAllByRandom(count));
    }
}
