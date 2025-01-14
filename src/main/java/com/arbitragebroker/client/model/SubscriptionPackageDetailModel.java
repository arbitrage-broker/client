package com.arbitragebroker.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class SubscriptionPackageDetailModel extends BaseModel<Long> {
    @NotNull
    private SubscriptionPackageModel subscriptionPackage;
    private BigDecimal amount;
    private BigDecimal minProfit;
    private BigDecimal maxProfit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (SubscriptionPackageDetailModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
