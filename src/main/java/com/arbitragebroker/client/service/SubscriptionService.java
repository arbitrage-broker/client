package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.SubscriptionFilter;
import com.arbitragebroker.client.model.SubscriptionModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubscriptionService extends BaseService<SubscriptionFilter, SubscriptionModel, Long>, LogicalDeletedService<Long>{
    SubscriptionModel findByUserAndActivePackage(UUID userId);
    SubscriptionModel purchase(SubscriptionModel model);
}
