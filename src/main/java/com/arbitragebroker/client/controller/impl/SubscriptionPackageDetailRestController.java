package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.filter.SubscriptionPackageDetailFilter;
import com.arbitragebroker.client.model.SubscriptionPackageDetailModel;
import com.arbitragebroker.client.service.SubscriptionPackageDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Subscription package detail Rest Service v1")
@RequestMapping(value = "/api/v1/subscription-package-detail")
public class SubscriptionPackageDetailRestController extends BaseRestControllerImpl<SubscriptionPackageDetailFilter, SubscriptionPackageDetailModel, Long> {

    private SubscriptionPackageDetailService service;

    public SubscriptionPackageDetailRestController(SubscriptionPackageDetailService service) {
        super(service);
        this.service = service;
    }
}
