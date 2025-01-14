package com.eshop.client.filter;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;

@Setter
@ToString
public class SubscriptionPackageFilter {
    private Long id;
    private String name;
    private Integer duration;
    private Integer orderCount;
    private BigDecimal price;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private BigDecimal maxPrice;
    private BigDecimal maxPriceFrom;
    private BigDecimal maxPriceTo;
    private CurrencyType currency;
    private EntityStatusType status;
    private String description;
    private Float minTradingReward;
    private Float minTradingRewardFrom;
    private Float minTradingRewardTo;
    private Float maxTradingReward;
    private Float maxTradingRewardFrom;
    private Float maxTradingRewardTo;
    private Float parentReferralBonus;
    private Float parentReferralBonusFrom;
    private Float parentReferralBonusTo;
    private Integer withdrawalDurationPerDay;
    private Integer userProfitPercentage;
    private Integer siteProfitPercentage;
    private Boolean active;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getName() {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    public Optional<Integer> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<Integer> getOrderCount() {
        return Optional.ofNullable(orderCount);
    }

    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    public Optional<BigDecimal> getPriceFrom() {
        return Optional.ofNullable(priceFrom);
    }

    public Optional<BigDecimal> getPriceTo() {
        return Optional.ofNullable(priceTo);
    }

    public Optional<CurrencyType> getCurrency() {
        return Optional.ofNullable(currency);
    }

    public Optional<EntityStatusType> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<String> getDescription() {
        if (description == null || description.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(description);
    }

    public Optional<BigDecimal> getMaxPrice() {
        return Optional.ofNullable(maxPrice);
    }

    public Optional<BigDecimal> getMaxPriceFrom() {
        return Optional.ofNullable(maxPriceFrom);
    }

    public Optional<BigDecimal> getMaxPriceTo() {
        return Optional.ofNullable(maxPriceTo);
    }

    public Optional<Float> getMinTradingReward() {
        return Optional.ofNullable(minTradingReward);
    }

    public Optional<Float> getMinTradingRewardFrom() {
        return Optional.ofNullable(minTradingRewardFrom);
    }

    public Optional<Float> getMinTradingRewardTo() {
        return Optional.ofNullable(minTradingRewardTo);
    }

    public Optional<Float> getMaxTradingReward() {
        return Optional.ofNullable(maxTradingReward);
    }

    public Optional<Float> getMaxTradingRewardFrom() {
        return Optional.ofNullable(maxTradingRewardFrom);
    }

    public Optional<Float> getMaxTradingRewardTo() {
        return Optional.ofNullable(maxTradingRewardTo);
    }
    public Optional<Float> getParentReferralBonus() {
        return Optional.ofNullable(parentReferralBonus);
    }

    public Optional<Float> getParentReferralBonusFrom() {
        return Optional.ofNullable(parentReferralBonusFrom);
    }

    public Optional<Float> getParentReferralBonusTo() {
        return Optional.ofNullable(parentReferralBonusTo);
    }

    public Optional<Integer> getWithdrawalDurationPerDay() {
        return Optional.ofNullable(withdrawalDurationPerDay);
    }

    public Optional<Integer> getUserProfitPercentage() {
        return Optional.ofNullable(userProfitPercentage);
    }

    public Optional<Integer> getSiteProfitPercentage() {
        return Optional.ofNullable(siteProfitPercentage);
    }

    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }
}
