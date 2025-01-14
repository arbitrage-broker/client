package com.eshop.client.filter;

import com.eshop.client.enums.EntityStatusType;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
public class SubscriptionFilter {
    private Long id;
    private UUID userId;
    private Long subscriptionPackageId;
    private Long expireDateFrom;
    private Long expireDateTo;
    private Integer discountPercentage;
    private BigDecimal finalPriceFrom;
    private BigDecimal finalPriceTo;
    private EntityStatusType status;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<Long> getSubscriptionPackageId() {
        return Optional.ofNullable(subscriptionPackageId);
    }

    public Optional<Long> getExpireDateFrom() {
        return Optional.ofNullable(expireDateFrom);
    }

    public Optional<Long> getExpireDateTo() {
        return Optional.ofNullable(expireDateTo);
    }

    public Optional<Integer> getDiscountPercentage() {
        return Optional.ofNullable(discountPercentage);
    }

    public Optional<BigDecimal> getFinalPriceFrom() {
        return Optional.ofNullable(finalPriceFrom);
    }

    public Optional<BigDecimal> getFinalPriceTo() {
        return Optional.ofNullable(finalPriceTo);
    }

    public Optional<EntityStatusType> getStatus() {
        return Optional.ofNullable(status);
    }
}
