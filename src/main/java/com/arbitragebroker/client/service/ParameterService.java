package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.ParameterFilter;
import com.arbitragebroker.client.model.ParameterModel;

import java.util.List;

public interface ParameterService extends BaseService<ParameterFilter, ParameterModel, Long> , LogicalDeletedService<Long>{
    ParameterModel findByCode(String code);
    String findValueByCodeOrDefault(String code, String defaultValue);
    List<ParameterModel> findAllByParameterGroupCode(String parameterGroupCode);
}
