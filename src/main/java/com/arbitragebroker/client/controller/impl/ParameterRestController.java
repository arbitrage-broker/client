package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.controller.LogicalDeletedRestController;
import com.arbitragebroker.client.filter.ParameterFilter;
import com.arbitragebroker.client.model.ParameterModel;
import com.arbitragebroker.client.service.LogicalDeletedService;
import com.arbitragebroker.client.service.ParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Parameter Rest Service v1")
@RequestMapping(value = "/api/v1/parameter")
public class ParameterRestController extends BaseRestControllerImpl<ParameterFilter, ParameterModel, Long> implements LogicalDeletedRestController<Long> {

    private final ParameterService parameterService;

    public ParameterRestController(ParameterService service) {
        super(service);
        this.parameterService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return parameterService;
    }

    @GetMapping("find-by-code/{code}")
    public ResponseEntity<ParameterModel> findByCode(@PathVariable String code){
        return ResponseEntity.ok(parameterService.findByCode(code));
    }
    @GetMapping("find-by-group-code/{code}")
    public ResponseEntity<List<ParameterModel>> findAllByParameterGroupCode(@PathVariable String code){
        return ResponseEntity.ok(parameterService.findAllByParameterGroupCode(code));
    }
}
