package com.eshop.client.service.impl;

import com.eshop.client.entity.QSubscriptionPackageDetailEntity;
import com.eshop.client.entity.SubscriptionPackageDetailEntity;
import com.eshop.client.filter.SubscriptionPackageDetailFilter;
import com.eshop.client.mapping.SubscriptionPackageDetailMapper;
import com.eshop.client.model.SubscriptionPackageDetailModel;
import com.eshop.client.repository.SubscriptionPackageDetailRepository;
import com.eshop.client.service.SubscriptionPackageDetailService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPackageDetailServiceImpl extends BaseServiceImpl<SubscriptionPackageDetailFilter, SubscriptionPackageDetailModel, SubscriptionPackageDetailEntity, Long> implements SubscriptionPackageDetailService {

    private final SubscriptionPackageDetailRepository repository;

    public SubscriptionPackageDetailServiceImpl(SubscriptionPackageDetailRepository repository, SubscriptionPackageDetailMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
    }

    @Override
    public Predicate queryBuilder(SubscriptionPackageDetailFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSubscriptionPackageDetailEntity path = QSubscriptionPackageDetailEntity.subscriptionPackageDetailEntity;

        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getSubscriptionPackageId().ifPresent(v -> builder.and(path.subscriptionPackage.id.eq(v)));
        filter.getAmountFrom().ifPresent(v -> builder.and(path.amount.goe(v)));
        filter.getAmountTo().ifPresent(v -> builder.and(path.amount.loe(v)));
        filter.getMinProfit().ifPresent(v -> builder.and(path.minProfit.eq(v)));
        filter.getMaxProfit().ifPresent(v -> builder.and(path.maxProfit.eq(v)));

        return builder;
    }

    @Override
    public Page<SubscriptionPackageDetailModel> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable) {
        return repository.findBySubscriptionPackageId(subscriptionPackageId, pageable).map(mapper::toModel);
    }
}
