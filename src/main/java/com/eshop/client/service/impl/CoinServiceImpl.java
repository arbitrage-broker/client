package com.eshop.client.service.impl;

import com.eshop.client.entity.CoinEntity;
import com.eshop.client.entity.QCoinEntity;
import com.eshop.client.filter.CoinFilter;
import com.eshop.client.mapping.CoinMapper;
import com.eshop.client.model.CoinModel;
import com.eshop.client.repository.CoinRepository;
import com.eshop.client.service.CoinService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CoinServiceImpl extends BaseServiceImpl<CoinFilter, CoinModel, CoinEntity, Long> implements CoinService {

    private CoinRepository repository;
    private CoinMapper mapper;

    @Autowired
    public CoinServiceImpl(CoinRepository repository, CoinMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(CoinFilter filter) {
        QCoinEntity p = QCoinEntity.coinEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));
        filter.getLogo().ifPresent(v->builder.and(p.logo.eq(v)));

        return builder;
    }


    @Override
    public CoinModel findByRandom() {
        return mapper.toModel(repository.findByRandom());
    }
    @Override
    public List<CoinModel> findAllByRandom(int count) {
        return mapper.toModel(repository.findAllByRandom(count));
    }

}
