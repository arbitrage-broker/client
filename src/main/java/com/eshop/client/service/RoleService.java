package com.eshop.client.service;

import com.eshop.client.filter.RoleFilter;
import com.eshop.client.model.RoleModel;

public interface RoleService extends BaseService<RoleFilter,RoleModel, Long> {
    RoleModel findByRole(String role);
}
