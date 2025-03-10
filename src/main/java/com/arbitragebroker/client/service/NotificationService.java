package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.NotificationFilter;
import com.arbitragebroker.client.model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService extends BaseService<NotificationFilter, NotificationModel, Long>, LogicalDeletedService<Long>{
    Page<NotificationModel> findAllByRecipientId(UUID recipientId, Pageable pageable, String key);
    Page<NotificationModel> findAllBySenderId(UUID senderId, Pageable pageable, String key);
    Page<NotificationModel> findAllByRecipientIdAndNotRead(UUID recipientId, Pageable pageable, String key);
    NotificationModel createForSupport(NotificationModel model);
}
