package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.NetworkType;
import com.arbitragebroker.client.filter.RoleDetailFilter;
import com.arbitragebroker.client.model.RoleDetailModel;
import com.arbitragebroker.client.service.RoleDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Role Rest Service v1")
@RequestMapping(value = "/api/v1/role-detail")
public class RoleDetailRestController extends BaseRestControllerImpl<RoleDetailFilter, RoleDetailModel, Long> {

    private RoleDetailService roleDetailService;

    public RoleDetailRestController(RoleDetailService service) {
        super(service);
        this.roleDetailService = service;
    }

    @GetMapping("/{role}/{network}/{currency}")
    ResponseEntity<RoleDetailModel> getWalletAddress(@PathVariable String role,@PathVariable NetworkType network,@PathVariable CurrencyType currency){
        return ResponseEntity.ok(roleDetailService.getWalletAddress(role,network,currency));
    }
}
