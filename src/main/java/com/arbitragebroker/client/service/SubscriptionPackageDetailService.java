package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.SubscriptionPackageDetailFilter;
import com.arbitragebroker.client.model.SubscriptionPackageDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionPackageDetailService extends BaseService<SubscriptionPackageDetailFilter, SubscriptionPackageDetailModel, Long>{
    Page<SubscriptionPackageDetailModel> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable);
}
