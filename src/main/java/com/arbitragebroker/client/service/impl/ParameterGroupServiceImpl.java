package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.ParameterGroupEntity;
import com.arbitragebroker.client.entity.QParameterGroupEntity;
import com.arbitragebroker.client.filter.ParameterGroupFilter;
import com.arbitragebroker.client.mapping.ParameterGroupMapper;
import com.arbitragebroker.client.model.ParameterGroupModel;
import com.arbitragebroker.client.repository.ParameterGroupRepository;
import com.arbitragebroker.client.service.ParameterGroupService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


@Service
public class ParameterGroupServiceImpl extends BaseServiceImpl<ParameterGroupFilter, ParameterGroupModel, ParameterGroupEntity, Long> implements ParameterGroupService {

    private ParameterGroupRepository repository;
    private ParameterGroupMapper mapper;

    @Autowired
    public ParameterGroupServiceImpl(ParameterGroupRepository repository, ParameterGroupMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(ParameterGroupFilter filter) {
        QParameterGroupEntity p = QParameterGroupEntity.parameterGroupEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getCode().ifPresent(v->builder.and(p.code.eq(v)));
        filter.getTitle().ifPresent(v->builder.and(p.title.eq(v)));

        return builder;
    }

    public ParameterGroupEntity findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public JpaRepository<ParameterGroupEntity,Long> getRepository() {
        return repository;
    }

}
