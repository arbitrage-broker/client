package com.eshop.client.controller.impl;

import com.eshop.client.filter.SubscriptionPackageFilter;
import com.eshop.client.model.SubscriptionPackageModel;
import com.eshop.client.service.SubscriptionPackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@Tag(name = "Subscription Package Rest Service v1")
@RequestMapping(value = "/api/v1/subscription-package")
public class SubscriptionPackageRestController extends BaseRestControllerImpl<SubscriptionPackageFilter, SubscriptionPackageModel, Long> {

    private SubscriptionPackageService subscriptionPackageService;

    public SubscriptionPackageRestController(SubscriptionPackageService service) {
        super(service);
        this.subscriptionPackageService = service;
    }

    @GetMapping("find-matched-plan/{userId}/{amount}")
    public ResponseEntity<String> findByByAmount(@PathVariable UUID userId, @PathVariable BigDecimal amount){
        return ResponseEntity.ok(subscriptionPackageService.findMatchedPackageByUserIdAndAmount(userId,amount));
    }
}
