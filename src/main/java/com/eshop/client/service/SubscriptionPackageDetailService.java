package com.eshop.client.service;

import com.eshop.client.filter.SubscriptionPackageDetailFilter;
import com.eshop.client.model.SubscriptionPackageDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionPackageDetailService extends BaseService<SubscriptionPackageDetailFilter, SubscriptionPackageDetailModel, Long>{
    Page<SubscriptionPackageDetailModel> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable);
}
