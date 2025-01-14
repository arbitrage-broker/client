package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.QRoleDetailEntity;
import com.arbitragebroker.client.entity.RoleDetailEntity;
import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.NetworkType;
import com.arbitragebroker.client.filter.RoleDetailFilter;
import com.arbitragebroker.client.mapping.RoleDetailMapper;
import com.arbitragebroker.client.model.RoleDetailModel;
import com.arbitragebroker.client.repository.RoleDetailRepository;
import com.arbitragebroker.client.service.RoleDetailService;
import com.arbitragebroker.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleDetailServiceImpl extends BaseServiceImpl<RoleDetailFilter, RoleDetailModel, RoleDetailEntity, Long> implements RoleDetailService {
    private RoleDetailRepository roleDetailRepository;
    private RoleDetailMapper roleDetailMapper;

    @Autowired
    public RoleDetailServiceImpl(RoleDetailRepository repository, RoleDetailMapper mapper) {
        super(repository, mapper);
        this.roleDetailRepository = repository;
        this.roleDetailMapper = mapper;
    }

    @Override
    public Predicate queryBuilder(RoleDetailFilter filter) {
        QRoleDetailEntity p = QRoleDetailEntity.roleDetailEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getNetwork().ifPresent(v->builder.and(p.network.eq(v)));
        filter.getCurrency().ifPresent(v->builder.and(p.currency.eq(v)));
        filter.getRoleId().ifPresent(v->builder.and(p.role.id.eq(v)));
        filter.getDescription().ifPresent(v->builder.and(p.description.contains(v)));

        return builder;
    }

    @Override
    public RoleDetailModel getWalletAddress(String role, NetworkType networkType, CurrencyType currencyType) {
        return mapper.toModel(roleDetailRepository.findByRoleRoleAndNetworkAndCurrency(role, networkType, currencyType).orElseThrow(()->new NotFoundException("RoleDetail not found")));
    }
}
