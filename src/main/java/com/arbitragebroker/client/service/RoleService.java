package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.RoleFilter;
import com.arbitragebroker.client.model.RoleModel;

public interface RoleService extends BaseService<RoleFilter,RoleModel, Long> {
    RoleModel findByRole(String role);
}
