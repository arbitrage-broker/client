package com.eshop.client.entity;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.util.NumberUtil;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "tbl_subscription_package")
@Audited
public class SubscriptionPackageEntity extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_subscription_package")
    @SequenceGenerator(name = "seq_subscription_package", sequenceName = "seq_subscription_package", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(nullable = false)
    private Integer duration;//days
    @Column(name = "order_count")
    private Integer orderCount;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "max_price",nullable = false)
    private BigDecimal maxPrice;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CurrencyType currency;
    @Column(nullable = false)
    private String description;
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EntityStatusType status;
    @Column(name = "min_trading_reward", nullable = false)
    private Float minTradingReward;
    @Column(name = "max_trading_reward", nullable = false)
    private Float maxTradingReward;
    @Column(name = "parent_referral_bonus", nullable = false)
    private Float parentReferralBonus;
    @Column(name = "withdrawal_duration_per_day", nullable = false)
    private int withdrawalDurationPerDay;
    @Column(name = "user_profit_percentage", nullable = false)
    private int userProfitPercentage;
    @Column(name = "site_profit_percentage", nullable = false)
    private int siteProfitPercentage;
    @OneToMany(mappedBy = "subscriptionPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionPackageDetailEntity> subscriptionPackageDetails = new ArrayList<>();
    @Transient
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

    @Override
    public String getSelectTitle() {
        if(name == null) return null;
        return name.concat(" ").concat(" ").concat(price.toString()).concat(" ").concat(currency.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}