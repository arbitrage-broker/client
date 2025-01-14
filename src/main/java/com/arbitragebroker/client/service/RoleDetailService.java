package com.arbitragebroker.client.service;

import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.NetworkType;
import com.arbitragebroker.client.filter.RoleDetailFilter;
import com.arbitragebroker.client.model.RoleDetailModel;

public interface RoleDetailService extends BaseService<RoleDetailFilter, RoleDetailModel, Long> {
    RoleDetailModel getWalletAddress(String role,NetworkType networkType, CurrencyType currencyType);
}
