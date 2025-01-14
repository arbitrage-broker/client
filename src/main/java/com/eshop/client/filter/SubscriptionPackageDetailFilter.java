package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;

@Setter
@ToString
public class SubscriptionPackageDetailFilter {
    private Long id;
    private Long subscriptionPackageId;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal minProfit;
    private BigDecimal maxProfit;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }


    public Optional<Long> getSubscriptionPackageId() {
        return Optional.ofNullable(subscriptionPackageId);
    }

    public Optional<BigDecimal> getAmountFrom() {
        return Optional.ofNullable(amountFrom);
    }

    public Optional<BigDecimal> getAmountTo() {
        return Optional.ofNullable(amountTo);
    }

    public Optional<BigDecimal> getMinProfit() {
        return Optional.ofNullable(minProfit);
    }

    public Optional<BigDecimal> getMaxProfit() {
        return Optional.ofNullable(maxProfit);
    }

}
