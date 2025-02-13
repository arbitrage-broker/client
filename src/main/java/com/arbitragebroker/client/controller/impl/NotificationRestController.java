package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.controller.LogicalDeletedRestController;
import com.arbitragebroker.client.exception.BadRequestException;
import com.arbitragebroker.client.filter.NotificationFilter;
import com.arbitragebroker.client.model.NotificationModel;
import com.arbitragebroker.client.service.LogicalDeletedService;
import com.arbitragebroker.client.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        String key = "Notification:%s:findAllByRecipientId".formatted(recipientId);
        return ResponseEntity.ok(notificationService.findAllByRecipientId(recipientId, pageable, key));
    }
    @GetMapping("findAll-by-senderId/{senderId}")
    public ResponseEntity<Page<NotificationModel>> findAllBySenderId(@PathVariable UUID senderId, @PageableDefault Pageable pageable) {
        String key = "Notification:%s:findAllBySenderId".formatted(senderId);
        return ResponseEntity.ok(notificationService.findAllBySenderId(senderId,pageable,key));
    }
    @PostMapping("/support")
    @ResponseBody
    @Operation(summary = "${api.baseRest.create}", description = "${api.baseRest.create.desc}")
    public ResponseEntity<NotificationModel> createForSupport(@RequestBody @Validated NotificationModel model) {
        log.debug("call NotificationRestController.createForSupport for {}", model);
        if (model.getId() != null) {
            throw new BadRequestException("The id must not be provided when creating a new record");
        }
        return ResponseEntity.ok(notificationService.createForSupport(model));
    }
}
