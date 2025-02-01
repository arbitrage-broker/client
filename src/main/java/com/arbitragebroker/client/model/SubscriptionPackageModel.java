package com.arbitragebroker.client.model;

import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.util.NumberUtil;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class SubscriptionPackageModel extends BaseModel<Long> {
    @NotEmpty
    private String name;
    private Integer duration;//days
    @NotNull
    private Integer orderCount;
    @NotNull
    private BigDecimal price;
    private BigDecimal maxPrice;
    @NotNull
    private CurrencyType currency;
    private String description;
    private EntityStatusType status;
    private Float minTradingReward;
    private Float maxTradingReward;
    private Float parentReferralBonus;
    private int withdrawalDurationPerDay;
    private int userProfitPercentage;
    private int siteProfitPercentage;
    private List<SubscriptionPackageDetailModel> subscriptionPackageDetails = new ArrayList<>();

    public BigDecimal getReward(BigDecimal balance) {
        var list = subscriptionPackageDetails.stream().map(x->x.getAmount()).collect(Collectors.toList());
        list.sort(Comparator.naturalOrder());
        BigDecimal closestAmount = null;
        for (BigDecimal amount : list) {
            if (amount.compareTo(balance) <= 0) {
                closestAmount = amount;
            } else {
                break;
            }
        }
        if(closestAmount == null)
            return BigDecimal.ZERO;
        final BigDecimal amount = closestAmount;
        var spd = subscriptionPackageDetails.stream().filter(x->x.getAmount().compareTo(amount) == 0).findAny().get();
        return NumberUtil.getRandom(spd.getMinProfit(),spd.getMaxProfit());
    }

    public SubscriptionPackageModel setSubscriptionPackageId(Long id){
        setId(id);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (SubscriptionPackageModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
