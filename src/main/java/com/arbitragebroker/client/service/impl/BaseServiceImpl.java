package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.BaseEntity;
import com.arbitragebroker.client.exception.NotFoundException;
import com.arbitragebroker.client.mapping.BaseMapper;
import com.arbitragebroker.client.model.BaseModel;
import com.arbitragebroker.client.model.Select2Model;
import com.arbitragebroker.client.repository.BaseRepository;
import com.arbitragebroker.client.service.BaseService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<F, M extends BaseModel<ID>, E extends BaseEntity<ID>, ID extends Serializable> implements BaseService<F, M, ID> {

    protected final BaseRepository<E, ID> repository;
    protected final BaseMapper<M, E> mapper;

    @Autowired
    protected RedisService redisService;

    public abstract Predicate queryBuilder(F filter);


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
    public Page<M> findAll(F filter, Pageable pageable, String key) {
        return repository.findAll(queryBuilder(filter), pageable).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
    public Page<Select2Model> findAllSelect(F filter, Pageable pageable, String key) {
        return repository.findAll(queryBuilder(filter), pageable)
                .map(m -> new Select2Model(m.getId().toString(), m.getSelectTitle()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
    public Long countAll(F filter, String key) {
        return repository.count(queryBuilder(filter));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
    public boolean exists(F filter, String key) {
        return repository.exists(queryBuilder(filter));
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
    public M findById(ID id, String key) {
        return mapper.toModel(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("id: " + id)));
    }

    @Override
    @CacheEvict(cacheNames = "client", key = "#allKey")
    public M create(M model, String allKey) {
        var saved = repository.save(mapper.toEntity(model));
        clearCache(allKey);
        return mapper.toModel(saved);
    }

    @Override
    @CachePut(cacheNames = "client", key = "#key")
    @CacheEvict(cacheNames = "client", key = "#allKey")
    public M update(M model,String key, String allKey) {
        var entity = repository.findById(model.getId())
                .orElseThrow(() -> new NotFoundException(String.format("%s not found by id %s",
                        model.getClass().getName(), model.getId().toString())));
        clearCache(allKey);
        return mapper.toModel(repository.save(mapper.updateEntity(model, entity)));
    }

    @Override
    @CacheEvict(cacheNames = "client", key = "#allKey")
    public void deleteById(ID id, String allKey) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("id: " + id));
        repository.deleteById(id);
    }

    public void clearCache(String allKey) {
        redisService.clearCache(allKey);
    }
}
