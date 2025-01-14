package com.arbitragebroker.client.service;

import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.filter.SubscriptionPackageFilter;
import com.arbitragebroker.client.model.SubscriptionPackageModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubscriptionPackageService extends BaseService<SubscriptionPackageFilter, SubscriptionPackageModel, Long> {
    SubscriptionPackageModel findMatchedPackageByAmount(BigDecimal amount);
    String findMatchedPackageByUserIdAndAmount(UUID userId, BigDecimal amount);
}
