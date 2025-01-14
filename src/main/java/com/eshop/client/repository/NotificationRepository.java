package com.eshop.client.repository;

import com.eshop.client.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findAllByRecipientIdOrderByCreatedDateDesc(UUID recipientId, Pageable pageable);
    Page<NotificationEntity> findAllBySenderIdOrderByCreatedDateDesc(UUID senderId, Pageable pageable);
    Page<NotificationEntity> findAllByRecipientIdAndReadIsFalseOrderByCreatedDateDesc(UUID recipientId, Pageable pageable);
}
