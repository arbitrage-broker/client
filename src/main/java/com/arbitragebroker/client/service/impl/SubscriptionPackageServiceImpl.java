package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.QSubscriptionPackageEntity;
import com.arbitragebroker.client.entity.SubscriptionPackageEntity;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.filter.SubscriptionPackageFilter;
import com.arbitragebroker.client.mapping.SubscriptionPackageMapper;
import com.arbitragebroker.client.model.SubscriptionPackageModel;
import com.arbitragebroker.client.repository.SubscriptionPackageRepository;
import com.arbitragebroker.client.repository.SubscriptionRepository;
import com.arbitragebroker.client.repository.WalletRepository;
import com.arbitragebroker.client.service.SubscriptionPackageService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SubscriptionPackageServiceImpl extends BaseServiceImpl<SubscriptionPackageFilter,SubscriptionPackageModel, SubscriptionPackageEntity, Long> implements SubscriptionPackageService {

    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final WalletRepository walletRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionPackageServiceImpl(SubscriptionPackageRepository repository, SubscriptionPackageMapper mapper, WalletRepository walletRepository, SubscriptionRepository subscriptionRepository) {
        super(repository, mapper);
        this.subscriptionPackageRepository = repository;
        this.walletRepository = walletRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Predicate queryBuilder(SubscriptionPackageFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSubscriptionPackageEntity path = QSubscriptionPackageEntity.subscriptionPackageEntity;

        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getName().ifPresent(v -> builder.and(path.name.toLowerCase().contains(v.toLowerCase())));
        filter.getDuration().ifPresent(v -> builder.and(path.duration.eq(v)));
        filter.getOrderCount().ifPresent(v -> builder.and(path.orderCount.eq(v)));
        filter.getPrice().ifPresent(v -> builder.and(path.price.eq(v)));
        filter.getPriceFrom().ifPresent(v -> builder.and(path.price.goe(v)));
        filter.getPriceTo().ifPresent(v -> builder.and(path.price.loe(v)));
        filter.getMaxPrice().ifPresent(v -> builder.and(path.maxPrice.eq(v)));
        filter.getMaxPriceFrom().ifPresent(v -> builder.and(path.maxPrice.goe(v)));
        filter.getMaxPriceTo().ifPresent(v -> builder.and(path.maxPrice.loe(v)));
        filter.getCurrency().ifPresent(v -> builder.and(path.currency.eq(v)));
        filter.getStatus().ifPresent(v -> builder.and(path.status.eq(v)));
        filter.getDescription().ifPresent(v -> builder.and(path.description.toLowerCase().contains(v.toLowerCase())));
        filter.getMinTradingReward().ifPresent(v -> builder.and(path.minTradingReward.eq(v)));
        filter.getMinTradingRewardFrom().ifPresent(v -> builder.and(path.minTradingReward.goe(v)));
        filter.getMinTradingRewardTo().ifPresent(v -> builder.and(path.minTradingReward.loe(v)));
        filter.getMaxTradingReward().ifPresent(v -> builder.and(path.maxTradingReward.eq(v)));
        filter.getMaxTradingRewardFrom().ifPresent(v -> builder.and(path.maxTradingReward.goe(v)));
        filter.getMaxTradingRewardTo().ifPresent(v -> builder.and(path.maxTradingReward.loe(v)));
        filter.getParentReferralBonus().ifPresent(v -> builder.and(path.parentReferralBonus.eq(v)));
        filter.getParentReferralBonusFrom().ifPresent(v -> builder.and(path.parentReferralBonus.goe(v)));
        filter.getParentReferralBonusTo().ifPresent(v -> builder.and(path.parentReferralBonus.loe(v)));
        filter.getWithdrawalDurationPerDay().ifPresent(v -> builder.and(path.withdrawalDurationPerDay.eq(v)));
        filter.getUserProfitPercentage().ifPresent(v -> builder.and(path.userProfitPercentage.eq(v)));
        filter.getSiteProfitPercentage().ifPresent(v -> builder.and(path.siteProfitPercentage.eq(v)));

        return builder;
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'SubscriptionPackage:findMatchedPackageByAmount:amount:' + #amount.toString()")
    public SubscriptionPackageModel findMatchedPackageByAmount(BigDecimal amount) {
        var result = subscriptionPackageRepository.findMatchedPackageByAmount(amount);
        if(result.isEmpty()) {
            var lastPackage = subscriptionPackageRepository.findTopByOrderByMaxPriceDesc();
            if(amount.compareTo(lastPackage.getMaxPrice()) >= 0)
                return mapper.toModel(lastPackage);
            return null;
        }
        return mapper.toModel(result.get());
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'SubscriptionPackage:findMatchedPackageByUserIdAndAmount:'+ #userId +':amount:' + #amount.toString()")
    @Transactional
    public String findMatchedPackageByUserIdAndAmount(UUID userId, BigDecimal amount) {//will check from withdrawal page
        var balance = walletRepository.calculateUserBalance(userId);
        var result = findMatchedPackageByAmount(balance.subtract(amount));

        if(result == null)
            return null;
        var currentSubscription = subscriptionRepository.findByUserIdAndStatus(userId, EntityStatusType.Active);
        if(currentSubscription == null || currentSubscription.getSubscriptionPackage().getId().equals(result.getId()))
            return "noChange";
        return result.getName();
    }
}
