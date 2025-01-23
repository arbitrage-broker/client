package com.arbitragebroker.client.service.impl;


import com.arbitragebroker.client.entity.NotificationEntity;
import com.arbitragebroker.client.entity.QNotificationEntity;
import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.filter.NotificationFilter;
import com.arbitragebroker.client.mapping.NotificationMapper;
import com.arbitragebroker.client.model.NotificationModel;
import com.arbitragebroker.client.model.UserModel;
import com.arbitragebroker.client.repository.NotificationRepository;
import com.arbitragebroker.client.repository.UserRepository;
import com.arbitragebroker.client.service.NotificationService;
import com.arbitragebroker.client.service.TelegramService;
import com.arbitragebroker.client.service.UserService;
import com.arbitragebroker.client.util.SessionHolder;
import com.arbitragebroker.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.arbitragebroker.client.enums.RoleType.getSupportUID;
import static com.arbitragebroker.client.util.StringUtils.generateFilterKey;
import static com.arbitragebroker.client.util.StringUtils.generateIdKey;

@Service
public class NotificationServiceImpl extends BaseServiceImpl<NotificationFilter, NotificationModel, NotificationEntity, Long> implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final SessionHolder sessionHolder;
    private final TelegramService telegramService;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository repository, NotificationMapper mapper, SessionHolder sessionHolder, TelegramService telegramService, UserRepository userRepository) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.sessionHolder = sessionHolder;
        this.telegramService = telegramService;
        this.userRepository = userRepository;
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
        clearCache("Notification:findAllBySenderId:%s".formatted(entity.getSender().getId()));
        clearCache("Notification:findAllByRecipientId:%s".formatted(entity.getRecipient().getId()));
        clearCache(generateFilterKey("Notification","findAllByRecipientIdAndNotRead",entity.getRecipient().getId(), PageRequest.of(0,10)));
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
    public NotificationModel createForSupport(NotificationModel model) {
        model.setRecipient(new UserModel().setUserId(UUID.fromString(getSupportUID(sessionHolder.getCurrentUser().getRole()))));
        var result = create(model,"Notification:*");
        var userEntity = userRepository.findById(model.getSender().getId()).orElseThrow(()->new NotFoundException("user not found"));
        result.setSender(new UserModel().setUserId(userEntity.getId()).setFirstName(userEntity.getFirstName()).setLastName(userEntity.getLastName()).setUserName(userEntity.getUserName()));
        telegramService.sendToRole(sessionHolder.getCurrentUser().getRole(), """
            *New Notification*\n
            %s""".formatted(result.toString()));
        return result;
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
