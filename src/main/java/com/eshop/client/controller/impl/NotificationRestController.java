package com.eshop.client.controller.impl;

import com.eshop.client.controller.LogicalDeletedRestController;
import com.eshop.client.filter.NotificationFilter;
import com.eshop.client.model.NotificationModel;
import com.eshop.client.model.UserModel;
import com.eshop.client.service.LogicalDeletedService;
import com.eshop.client.service.NotificationService;
import com.eshop.client.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.eshop.client.util.StringUtils.generateFilterKey;

@RestController
@Tag(name = "Notification Rest Service v1")
@RequestMapping(value = "/api/v1/notification")
@Slf4j
public class NotificationRestController extends BaseRestControllerImpl<NotificationFilter, NotificationModel, Long> implements LogicalDeletedRestController<Long> {

    private NotificationService notificationService;

    public NotificationRestController(NotificationService service) {
        super(service);
        this.notificationService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return notificationService;
    }

    @GetMapping("findAll-by-recipientId/{recipientId}")
    public ResponseEntity<Page<NotificationModel>> findAllByRecipientId(@PathVariable UUID recipientId, @PageableDefault Pageable pageable) {
        String key = generateFilterKey(getClassName(),"findAllByRecipientId",recipientId,pageable);
        return ResponseEntity.ok(notificationService.findAllByRecipientId(recipientId,pageable, key));
    }
    @GetMapping("findAll-by-senderId/{senderId}")
    public ResponseEntity<Page<NotificationModel>> findAllBySenderId(@PathVariable UUID senderId, @PageableDefault Pageable pageable) {
        String key = generateFilterKey(getClassName(),"findAllBySenderId",senderId,pageable);
        return ResponseEntity.ok(notificationService.findAllBySenderId(senderId,pageable,key));
    }
    @PostMapping("/support")
    @ResponseBody
    @Operation(summary = "${api.baseRest.create}", description = "${api.baseRest.create.desc}")
    public ResponseEntity<NotificationModel> createForSupport(NotificationModel model) {
        log.debug("call BaseRestImpl.save for {}", model);
        if (model.getId() != null) {
            throw new BadRequestException("The id must not be provided when creating a new record");
        }
        model.setRecipient(new UserModel().setUserId(UUID.fromString("6303b84a-04cf-49e1-8416-632ebd84495e")));
        return ResponseEntity.ok(service.create(model, String.format("%s:*", getClassName())));
    }
}
