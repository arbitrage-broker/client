package com.eshop.client.service.impl;

import com.eshop.client.entity.ParameterEntity;
import com.eshop.client.entity.QParameterEntity;
import com.eshop.client.filter.ParameterFilter;
import com.eshop.client.filter.ParameterGroupFilter;
import com.eshop.client.mapping.ParameterMapper;
import com.eshop.client.model.ParameterModel;
import com.eshop.client.repository.ParameterRepository;
import com.eshop.client.service.ParameterService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.eshop.client.util.StringUtils.generateFilterKey;


@Service
public class ParameterServiceImpl extends BaseServiceImpl<ParameterFilter,ParameterModel, ParameterEntity, Long> implements ParameterService {
    private ParameterRepository repository;
    private ParameterMapper mapper;

    @Autowired
    public ParameterServiceImpl(ParameterRepository repository, ParameterMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Predicate queryBuilder(ParameterFilter filter) {
        QParameterEntity p = QParameterEntity.parameterEntity;
        BooleanBuilder builder = new BooleanBuilder();
        filter.getId().ifPresent(v -> builder.and(p.id.eq(v)));
        filter.getCode().ifPresent(v -> builder.and(p.code.eq(v)));
        filter.getTitle().ifPresent(v -> builder.and(p.title.contains(v)));
        filter.getValue().ifPresent(v -> builder.and(p.value.contains(v)));
        filter.getParameterGroup().ifPresent(v -> v.getId().ifPresent(id -> builder.and(p.parameterGroup.id.eq(id))));
        filter.getParameterGroup().ifPresent(v -> v.getCode().ifPresent(code -> builder.and(p.parameterGroup.code.eq(code))));

        return builder;
    }
    @Cacheable(cacheNames = "client", key = "'Parameter:' + #code + ':findByCode'")
    public ParameterModel findByCode(String code) {
        Optional<ParameterEntity> optional = repository.findByCode(code);
        if (optional.isPresent())
            return mapper.toModel(optional.get());
        return new ParameterModel();
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Parameter:' + #code + ':findByCodeOrDefault'")
    public String findValueByCodeOrDefault(String code, String defaultValue) {
        var model = findByCode(code);
        if(model == null || !StringUtils.hasLength(model.getValue()))
            return defaultValue;
        return model.getValue();
    }

    @Cacheable(cacheNames = "client", key = "'Parameter:' + #parameterGroupCode + ':findAllByParameterGroupCode'")
    public List<ParameterModel> findAllByParameterGroupCode(String parameterGroupCode) {
        ParameterFilter filter = new ParameterFilter(){{setParameterGroup(new ParameterGroupFilter(){{setCode(parameterGroupCode);}});}};
        return super.findAll(filter, PageRequest.ofSize(1000), generateFilterKey("Parameter","findAllByParameterGroupCode",filter,PageRequest.ofSize(1000))).getContent();
    }

    @Override
    public JpaRepository<ParameterEntity,Long> getRepository() {
        return repository;
    }
}
