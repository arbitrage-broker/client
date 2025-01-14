package com.eshop.client.service;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.filter.SubscriptionPackageFilter;
import com.eshop.client.model.SubscriptionPackageModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubscriptionPackageService extends BaseService<SubscriptionPackageFilter, SubscriptionPackageModel, Long> {
    SubscriptionPackageModel findMatchedPackageByAmount(BigDecimal amount);
    String findMatchedPackageByUserIdAndAmount(UUID userId, BigDecimal amount);
}
