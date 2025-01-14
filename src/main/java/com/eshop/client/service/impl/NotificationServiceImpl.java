package com.eshop.client.service.impl;


import com.eshop.client.entity.NotificationEntity;
import com.eshop.client.entity.QNotificationEntity;
import com.eshop.client.filter.NotificationFilter;
import com.eshop.client.mapping.NotificationMapper;
import com.eshop.client.model.NotificationModel;
import com.eshop.client.repository.NotificationRepository;
import com.eshop.client.service.NotificationService;
import com.eshop.client.service.UserService;
import com.eshop.client.util.SessionHolder;
import com.eshop.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.eshop.client.util.StringUtils.generateIdKey;

@Service
public class NotificationServiceImpl extends BaseServiceImpl<NotificationFilter, NotificationModel, NotificationEntity, Long> implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final SessionHolder sessionHolder;

    public NotificationServiceImpl(NotificationRepository repository, NotificationMapper mapper, SessionHolder sessionHolder) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.sessionHolder = sessionHolder;
    }

    @Override
    public JpaRepository<NotificationEntity, Long> getRepository() {
        return repository;
    }

    @Override
    public NotificationModel findById(Long id, String key) {
        var entity = repository.findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        if(entity.getRecipient().getId().equals(sessionHolder.getCurrentUser().getId()))
            entity.setRead(true);
        repository.save(entity);
        clearCache(key);
        return mapper.toModel(entity);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public Page<NotificationModel> findAllByRecipientId(UUID recipientId, Pageable pageable, String key) {
        return repository.findAllByRecipientIdOrderByCreatedDateDesc(recipientId, pageable).map(mapper::toModel);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "#key")
    public Page<NotificationModel> findAllBySenderId(UUID senderId, Pageable pageable, String key) {
        return repository.findAllBySenderIdOrderByCreatedDateDesc(senderId, pageable).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "#key")
    public Page<NotificationModel> findAllByRecipientIdAndNotRead(UUID recipientId, Pageable pageable, String key) {
        return repository.findAllByRecipientIdAndReadIsFalseOrderByCreatedDateDesc(recipientId, pageable).map(mapper::toModel);
    }

    @Override
    public Predicate queryBuilder(NotificationFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QNotificationEntity path = QNotificationEntity.notificationEntity;

        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getSenderId().ifPresent(v -> builder.and(path.sender.id.eq(v)));
        filter.getRecipientId().ifPresent(v -> builder.and(path.recipient.id.eq(v)));
        filter.getSubject().ifPresent(v -> builder.and(path.subject.toLowerCase().contains(v.toLowerCase())));
        filter.getBody().ifPresent(v -> builder.and(path.body.toLowerCase().contains(v.toLowerCase())));
        filter.getRead().ifPresent(v -> builder.and(path.read.eq(v)));
        
        return builder;
    }

    @Override
    public NotificationModel create(NotificationModel model, String allKey) {
        model.setRole(sessionHolder.getCurrentUser().getRole());
        return super.create(model, allKey);
    }
}
