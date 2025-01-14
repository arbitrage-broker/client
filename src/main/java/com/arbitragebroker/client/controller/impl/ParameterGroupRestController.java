package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.controller.LogicalDeletedRestController;
import com.arbitragebroker.client.filter.ParameterGroupFilter;
import com.arbitragebroker.client.model.ParameterGroupModel;
import com.arbitragebroker.client.service.LogicalDeletedService;
import com.arbitragebroker.client.service.ParameterGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "ParameterGroup Rest Service v1")
@RequestMapping(value = "/api/v1/parameterGroup")
public class ParameterGroupRestController extends BaseRestControllerImpl<ParameterGroupFilter, ParameterGroupModel, Long> implements LogicalDeletedRestController<Long> {

    private ParameterGroupService parameterGroupService;

    public ParameterGroupRestController(ParameterGroupService service) {
        super(service);
        this.parameterGroupService = service;
    }
    @Override
    public LogicalDeletedService<Long> getService() {
        return parameterGroupService;
    }
}
