package com.eshop.client.controller.impl;

import com.eshop.client.filter.CoinFilter;
import com.eshop.client.model.CoinModel;
import com.eshop.client.service.CoinService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Coin Rest Service v1")
@RequestMapping(value = "/api/v1/coin")
public class CoinRestController extends BaseRestControllerImpl<CoinFilter, CoinModel, Long>  {

    private CoinService coinService;

    public CoinRestController(CoinService service) {
        super(service);
        this.coinService = service;
    }
    @GetMapping("buy/{userId}")
    public ResponseEntity<CoinModel> buy(@PathVariable UUID userId){
        return ResponseEntity.ok(coinService.findByRandom());
    }
}
