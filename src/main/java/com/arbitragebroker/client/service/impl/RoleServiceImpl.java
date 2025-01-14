package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.QRoleEntity;
import com.arbitragebroker.client.entity.RoleEntity;
import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.filter.RoleFilter;
import com.arbitragebroker.client.mapping.RoleMapper;
import com.arbitragebroker.client.model.RoleModel;
import com.arbitragebroker.client.repository.RoleRepository;
import com.arbitragebroker.client.service.RoleService;
import com.arbitragebroker.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleFilter,RoleModel, RoleEntity, Long> implements RoleService {

    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
    }

    @Override
    public Predicate queryBuilder(RoleFilter filter) {
        QRoleEntity path = QRoleEntity.roleEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.role.ne(RoleType.ADMIN));
        }

        filter.getId().ifPresent(value -> builder.and(path.id.eq(value)));
        filter.getRole().ifPresent(value -> builder.and(path.role.eq(value)));
        filter.getTitle().ifPresent(value -> builder.and(path.title.eq(value)));

        return builder;
    }

    @Override
    public RoleModel findByRole(String role) {
        return mapper.toModel(repository.findByRole(role).orElseThrow(() -> new NotFoundException("role: " + role)));
    }
}