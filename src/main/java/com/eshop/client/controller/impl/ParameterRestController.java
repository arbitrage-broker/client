package com.eshop.client.controller.impl;

import com.eshop.client.controller.LogicalDeletedRestController;
import com.eshop.client.filter.ParameterFilter;
import com.eshop.client.model.ParameterModel;
import com.eshop.client.service.LogicalDeletedService;
import com.eshop.client.service.ParameterService;
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
