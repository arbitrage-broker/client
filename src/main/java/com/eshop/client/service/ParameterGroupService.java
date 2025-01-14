package com.eshop.client.service;

import com.eshop.client.entity.ParameterGroupEntity;
import com.eshop.client.filter.ParameterGroupFilter;
import com.eshop.client.model.ParameterGroupModel;

public interface ParameterGroupService extends BaseService<ParameterGroupFilter, ParameterGroupModel, Long> , LogicalDeletedService<Long> {
    ParameterGroupEntity findByCode(String code);
}
