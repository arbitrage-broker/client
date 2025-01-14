package com.eshop.client.controller.impl;

import com.eshop.client.strategy.NetworkStrategy;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.strategy.NetworkStrategyFactory;
import com.eshop.client.model.Select2Model;
import com.eshop.client.util.ReflectionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Tag(name = "Common Rest Service v1")
@RequestMapping(value = "/api/v1/common")
@RequiredArgsConstructor
public class CommonRestController {
    private final NetworkStrategyFactory networkStrategyFactory;

    @GetMapping("/getEnum/{name}")
    public ResponseEntity<List<Select2Model>> getEnum(@PathVariable String name) {
        List<Select2Model> list = (List<Select2Model>) ReflectionUtil.invokeMethod("com.eshop.client.enums.".concat(name), "getAll");
        return ResponseEntity.ok(list);
    }
    @GetMapping("/price/{network}/{currency}")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable NetworkType network, @PathVariable String currency) {
        NetworkStrategy networkStrategy = networkStrategyFactory.get(network);
        return ResponseEntity.ok(networkStrategy.getPrice(currency));
    }
}
