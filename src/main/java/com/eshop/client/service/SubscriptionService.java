package com.eshop.client.service;

import com.eshop.client.filter.SubscriptionFilter;
import com.eshop.client.model.SubscriptionModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubscriptionService extends BaseService<SubscriptionFilter, SubscriptionModel, Long>, LogicalDeletedService<Long>{
    SubscriptionModel findByUserAndActivePackage(UUID userId);
    SubscriptionModel purchase(SubscriptionModel model);
}
