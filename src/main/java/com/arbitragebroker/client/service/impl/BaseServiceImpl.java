package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.dto.PagedResponse;
import com.arbitragebroker.client.entity.BaseEntity;
import com.arbitragebroker.client.exception.NotFoundException;
import com.arbitragebroker.client.mapping.BaseMapper;
import com.arbitragebroker.client.model.BaseModel;
import com.arbitragebroker.client.model.Select2Model;
import com.arbitragebroker.client.repository.BaseRepository;
import com.arbitragebroker.client.service.BaseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<F, M extends BaseModel<ID>, E extends BaseEntity<ID>, ID extends Serializable> implements BaseService<F, M, ID> {

    protected final BaseRepository<E, ID> repository;
    protected final BaseMapper<M, E> mapper;

    @Autowired
    protected RedisService redisService;

    public abstract Predicate queryBuilder(F filter);

    @Override
    @Transactional(readOnly = true)
    public Page<M> findAll(F filter, Pageable pageable, String key) {
        TypeReference<PagedResponse<M>> typeRef = new TypeReference<>() {};
        var page = redisService.getPage(key, typeRef);
        if(page.isEmpty()){
            page = repository.findAll(queryBuilder(filter), pageable).map(mapper::toModel);
            redisService.savePage(key, page);
        }
        return page;
    }

//    @Override
//    @Transactional(readOnly = true)
//    @Cacheable(cacheNames = "client", unless = "#result == null", key = "#key")
//    public PageDto<M> findAllTable(F filter, Pageable pageable, String key) {
//        Predicate predicate = queryBuilder(filter);
//        var page = repository.findAll(predicate, pageable);
//        return new PageDto<>(repository.count(), page.getTotalElements(), mapper.toModel(page.getContent()));
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<Select2Model> findAllSelect(F filter, Pageable pageable, String key) {
        var page = redisService.getPage(key, Select2Model.class);
        if(page.isEmpty()) {
            page = repository.findAll(queryBuilder(filter), pageable)
                    .map(m -> new Select2Model(m.getId().toString(), m.getSelectTitle()));
            redisService.savePage(key, page);
        }
        return page;
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
