package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.filter.ArbitrageFilter;
import com.arbitragebroker.client.model.ArbitrageModel;
import com.arbitragebroker.client.model.CoinUsageDTO;
import com.arbitragebroker.client.service.ArbitrageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.arbitragebroker.client.util.DateUtil.toLocalDateTime;
import static com.arbitragebroker.client.util.DateUtil.truncate;

@RestController
@Tag(name = "Arbitrage Rest Service v1")
@RequestMapping(value = "/api/v1/arbitrage")
public class ArbitrageRestController extends BaseRestControllerImpl<ArbitrageFilter, ArbitrageModel, Long>  {

    private ArbitrageService arbitrageService;

    public ArbitrageRestController(ArbitrageService service) {
        super(service);
        this.arbitrageService = service;
    }
    @GetMapping("count-all-by-user/{userId}")
    public ResponseEntity<Long> countAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(arbitrageService.countAllByUserId(userId));
    }
    @GetMapping("count-by-user-and-date/{userId}/{epochDate}")
    public ResponseEntity<Long> countByUserIdAndDate(@PathVariable UUID userId, @PathVariable Long epochDate) {
        return ResponseEntity.ok(arbitrageService.countByUserIdAndDate(userId, truncate(toLocalDateTime(epochDate))));
    }
    @GetMapping("count-today-by-user/{userId}")
    public ResponseEntity<Long> countByUserIdAndDate(@PathVariable UUID userId) {
        return ResponseEntity.ok(arbitrageService.countByUserIdAndDate(userId, truncate(LocalDateTime.now())));
    }
    @GetMapping("find-top-coins/{pageSize}")
    public ResponseEntity<Page<CoinUsageDTO>> findMostUsedCoins(@PathVariable int pageSize) {
        return ResponseEntity.ok(arbitrageService.findMostUsedCoins(pageSize));
    }
    @GetMapping("purchase-limit/{userId}")
    public ResponseEntity<String> purchaseLimit(@PathVariable UUID userId) {
        return ResponseEntity.ok(arbitrageService.purchaseLimit(userId));
    }
}
