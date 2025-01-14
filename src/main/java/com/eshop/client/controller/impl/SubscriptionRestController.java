package com.eshop.client.controller.impl;

import com.eshop.client.controller.LogicalDeletedRestController;
import com.eshop.client.filter.SubscriptionFilter;
import com.eshop.client.model.SubscriptionModel;
import com.eshop.client.service.LogicalDeletedService;
import com.eshop.client.service.SubscriptionService;
import com.eshop.client.util.SessionHolder;
import com.eshop.client.exception.NotAcceptableException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Subscription Rest Service v1")
@RequestMapping(value = "/api/v1/subscription")
public class SubscriptionRestController extends BaseRestControllerImpl<SubscriptionFilter, SubscriptionModel, Long> implements LogicalDeletedRestController<Long> {

    private SubscriptionService subscriptionService;
    private SessionHolder sessionHolder;

    public SubscriptionRestController(SubscriptionService service, SessionHolder sessionHolder) {
        super(service);
        this.subscriptionService = service;
        this.sessionHolder = sessionHolder;
    }

    @Override
    public LogicalDeletedService<Long> getService() {
        return subscriptionService;
    }

    @GetMapping("find-active-by-user/{userId}")
    public ResponseEntity<SubscriptionModel> findAll(@PathVariable UUID userId){
        return ResponseEntity.ok(subscriptionService.findByUserAndActivePackage(userId));
    }
    @PostMapping("purchase")
    public ResponseEntity<SubscriptionModel> purchase(@RequestBody @Validated SubscriptionModel model){
        if(!sessionHolder.getCurrentUser().equals(model.getUser()))
            throw new NotAcceptableException("Other user can not purchase!");

        return ResponseEntity.ok(subscriptionService.purchase(model));
    }
}
