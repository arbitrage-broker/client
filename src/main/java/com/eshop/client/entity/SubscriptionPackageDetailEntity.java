package com.eshop.client.entity;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "tbl_subscription_package_detail")
@Audited
public class SubscriptionPackageDetailEntity extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_subscription_package_detail")
    @SequenceGenerator(name = "seq_subscription_package_detail", sequenceName = "seq_subscription_package_detail", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subscription_package_id", nullable = false)
    private SubscriptionPackageEntity subscriptionPackage;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "min_profit", nullable = false)
    private BigDecimal minProfit;
    @Column(name = "max_profit", nullable = false)
    private BigDecimal maxProfit;

    @Override
    public String getSelectTitle() {
        return subscriptionPackage.getName().concat(" ").concat(amount.toString().concat(" "));
    }
}