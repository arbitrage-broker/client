package com.eshop.client.entity;

import com.eshop.client.util.DateUtil;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@Entity
@Where(clause = "deleted=false")
@Table(name = "tbl_one_time_password", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "password"})})
public class OneTimePasswordEntity extends BaseEntity<Long> implements LogicalDeleted {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_one_time_password")
    @SequenceGenerator(name = "seq_one_time_password", sequenceName = "seq_one_time_password", allocationSize = 1, initialValue = 1)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private String password;
    private boolean consumed;
    @Column(nullable = false)
    private boolean deleted;

    @Override
    public String getSelectTitle() {
        return user.getSelectTitle();
    }

    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;   // 5 minutes
    public boolean isExpired() {

        long currentTimeInMillis = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        long otpRequestedTimeInMillis = DateUtil.toEpoch(this.getCreatedDate());

        if (otpRequestedTimeInMillis + OTP_VALID_DURATION < currentTimeInMillis) {
            // OTP expired
            return true;
        }
        return false;
    }
}