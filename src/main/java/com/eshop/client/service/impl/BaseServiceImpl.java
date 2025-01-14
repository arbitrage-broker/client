package com.eshop.client.service.impl;

import com.eshop.client.entity.BaseEntity;
import com.eshop.client.mapping.BaseMapper;
import com.eshop.client.model.BaseModel;
import com.eshop.client.model.PageModel;
import com.eshop.client.model.Select2Model;
import com.eshop.client.repository.BaseRepository;
import com.eshop.client.service.BaseService;
import com.eshop.client.exception.NotFoundException;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<F, M extends BaseModel<ID>, E extends BaseEntity<ID>, ID extends Serializable> implements BaseService<F, M, ID> {

    protected final BaseRepository<E, ID> repository;
    protected final BaseMapper<M, E> mapper;
    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    protected CacheManager cacheManager;

    public abstract Predicate queryBuilder(F filter);

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public Page<M> findAll(F filter, Pageable pageable, String key) {
        return repository.findAll(queryBuilder(filter), pageable).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public PageModel<M> findAllTable(F filter, Pageable pageable, String key) {
        Predicate predicate = queryBuilder(filter);
        var page = repository.findAll(predicate, pageable);
        return new PageModel<>(repository.count(), page.getTotalElements(), mapper.toModel(page.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public Page<Select2Model> findAllSelect(F filter, Pageable pageable, String key) {
        return repository.findAll(queryBuilder(filter), pageable)
                .map(m -> new Select2Model(m.getId().toString(), m.getSelectTitle()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public Long countAll(F filter, String key) {
        return repository.count(queryBuilder(filter));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public boolean exists(F filter, String key) {
        return repository.exists(queryBuilder(filter));
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "client", key = "#key")
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

    @CacheEvict(cacheNames = "client", key = "#allKey")
    public void clearCache(String allKey) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("client");
        if(cache != null) {
            cache.getNativeCache().asMap().keySet().removeIf(key -> key.toString().startsWith(allKey.replace("*","")));
        }
    }
}
