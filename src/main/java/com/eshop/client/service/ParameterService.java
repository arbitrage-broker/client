package com.eshop.client.service;

import com.eshop.client.filter.ParameterFilter;
import com.eshop.client.model.ParameterModel;

import java.util.List;

public interface ParameterService extends BaseService<ParameterFilter, ParameterModel, Long> , LogicalDeletedService<Long>{
    ParameterModel findByCode(String code);
    String findValueByCodeOrDefault(String code, String defaultValue);
    List<ParameterModel> findAllByParameterGroupCode(String parameterGroupCode);
}
