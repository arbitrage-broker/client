package com.eshop.client.service;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.filter.RoleDetailFilter;
import com.eshop.client.model.RoleDetailModel;

public interface RoleDetailService extends BaseService<RoleDetailFilter, RoleDetailModel, Long> {
    RoleDetailModel getWalletAddress(String role,NetworkType networkType, CurrencyType currencyType);
}
