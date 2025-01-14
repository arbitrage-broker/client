package com.eshop.client.entity;

import com.eshop.client.enums.EntityStatusType;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.eshop.client.util.MapperHelper.getOrDefault;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "tbl_subscription")
@Where(clause = "deleted=false")
@Audited
public class SubscriptionEntity extends BaseEntity<Long> implements LogicalDeleted {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_subscription")
    @SequenceGenerator(name = "seq_subscription", sequenceName = "seq_subscription", allocationSize = 1, initialValue = 1)
    @Column(name = "id")
    private Long id;
    @OneToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subscription_package_id", nullable = false)
    private SubscriptionPackageEntity subscriptionPackage;
    @Column(name = "expire_date")
    private LocalDateTime expireDate;
    @Column(name = "discount_percentage")
    private Integer discountPercentage;
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;
    @Column(nullable = false)
    private boolean deleted;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private EntityStatusType status;
    private String role;

    @Override
    public String getSelectTitle() {
        return getOrDefault(()->user.getSelectTitle(),"") + " | " + getOrDefault(()->subscriptionPackage.getSelectTitle(),"");
    }
}