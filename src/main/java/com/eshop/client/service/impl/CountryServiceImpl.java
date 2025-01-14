package com.eshop.client.service.impl;

import com.eshop.client.entity.CountryEntity;
import com.eshop.client.entity.QCountryEntity;
import com.eshop.client.filter.CountryFilter;
import com.eshop.client.mapping.CountryMapper;
import com.eshop.client.model.CountryModel;
import com.eshop.client.repository.CountryRepository;
import com.eshop.client.service.CountryService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CountryServiceImpl extends BaseServiceImpl<CountryFilter, CountryModel, CountryEntity, Long> implements CountryService {

    private CountryRepository repository;
    private CountryMapper mapper;

    @Autowired
    public CountryServiceImpl(CountryRepository repository, CountryMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(CountryFilter filter) {
        QCountryEntity p = QCountryEntity.countryEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getName().ifPresent(v->builder.and(p.name.toLowerCase().contains(v.toLowerCase())));

        return builder;
    }
}
