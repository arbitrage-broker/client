package com.eshop.client.controller.impl;

import com.eshop.client.filter.ExchangeFilter;
import com.eshop.client.model.ExchangeModel;
import com.eshop.client.model.SubscriptionModel;
import com.eshop.client.service.ExchangeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Exchange Rest Service v1")
@RequestMapping(value = "/api/v1/exchange")
public class ExchangeRestController extends BaseRestControllerImpl<ExchangeFilter, ExchangeModel, Long>  {

    private ExchangeService exchangeService;

    public ExchangeRestController(ExchangeService service) {
        super(service);
        this.exchangeService = service;
    }
    @GetMapping("buy/{userId}")
    public ResponseEntity<List<ExchangeModel>> buy(@PathVariable UUID userId){
        return ResponseEntity.ok(exchangeService.findByRandom());
    }
}
