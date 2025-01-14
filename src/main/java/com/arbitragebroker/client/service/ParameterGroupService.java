package com.arbitragebroker.client.service;

import com.arbitragebroker.client.entity.ParameterGroupEntity;
import com.arbitragebroker.client.filter.ParameterGroupFilter;
import com.arbitragebroker.client.model.ParameterGroupModel;

public interface ParameterGroupService extends BaseService<ParameterGroupFilter, ParameterGroupModel, Long> , LogicalDeletedService<Long> {
    ParameterGroupEntity findByCode(String code);
}
