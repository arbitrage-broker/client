package com.arbitragebroker.client.model;

import com.arbitragebroker.client.enums.EntityStatusType;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class SubscriptionModel extends BaseModel<Long> {
    @NotNull
    private UserModel user;
    @NotNull
    private SubscriptionPackageModel subscriptionPackage;
    private LocalDateTime expireDate;
    private Integer discountPercentage;
    private BigDecimal finalPrice;
    private EntityStatusType status = EntityStatusType.Pending;
    public long getRemainingWithdrawalPerDay() {
        if(createdDate == null || subscriptionPackage == null)
            return 0L;
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
        return daysRemaining > 0L ? daysRemaining :  0L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (SubscriptionModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
