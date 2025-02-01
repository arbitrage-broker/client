package com.arbitragebroker.client.service;

import com.arbitragebroker.client.dto.PagedResponse;
import com.arbitragebroker.client.model.BaseModel;
import com.arbitragebroker.client.dto.PageDto;
import com.arbitragebroker.client.model.Select2Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public interface BaseService<F, M extends BaseModel<ID>, ID extends Serializable> {
    PagedResponse<M> findAll(F filter, Pageable pageable, String key);
    PageDto<M> findAllTable(F filter, Pageable pageable, String key);
    PagedResponse<Select2Model> findAllSelect(F filter, Pageable pageable, String key);
    Long countAll(F filter, String key);
    boolean exists(F filter, String key);
    M findById(ID id, String key);
    M create(M model, String allKey);
    M update(M model, String key, String allKey);
    void deleteById(ID id, String allKey);
    void clearCache(String allKey);
}