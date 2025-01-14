package com.eshop.client.repository;

import com.eshop.client.entity.SubscriptionEntity;
import com.eshop.client.enums.EntityStatusType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends BaseRepository<SubscriptionEntity, Long> {
    SubscriptionEntity findByUserIdAndStatus(UUID userId, EntityStatusType status);
}
