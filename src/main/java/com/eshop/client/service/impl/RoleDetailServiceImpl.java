package com.eshop.client.service.impl;

import com.eshop.client.entity.QRoleDetailEntity;
import com.eshop.client.entity.RoleDetailEntity;
import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.filter.RoleDetailFilter;
import com.eshop.client.mapping.RoleDetailMapper;
import com.eshop.client.model.RoleDetailModel;
import com.eshop.client.repository.RoleDetailRepository;
import com.eshop.client.service.RoleDetailService;
import com.eshop.client.exception.NotFoundException;
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
