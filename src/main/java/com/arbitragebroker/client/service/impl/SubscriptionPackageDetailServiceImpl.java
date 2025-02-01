package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.dto.PagedResponse;
import com.arbitragebroker.client.entity.QSubscriptionPackageDetailEntity;
import com.arbitragebroker.client.entity.SubscriptionPackageDetailEntity;
import com.arbitragebroker.client.filter.SubscriptionPackageDetailFilter;
import com.arbitragebroker.client.mapping.SubscriptionPackageDetailMapper;
import com.arbitragebroker.client.model.SubscriptionPackageDetailModel;
import com.arbitragebroker.client.repository.SubscriptionPackageDetailRepository;
import com.arbitragebroker.client.service.SubscriptionPackageDetailService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
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
    public PagedResponse<SubscriptionPackageDetailModel> findBySubscriptionPackageId(long subscriptionPackageId, Pageable pageable) {
        var page = repository.findBySubscriptionPackageId(subscriptionPackageId, pageable).map(mapper::toModel);
        return PagedResponse.fromPage(page);
    }
}
