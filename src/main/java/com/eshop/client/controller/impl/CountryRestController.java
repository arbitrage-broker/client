package com.eshop.client.controller.impl;

import com.eshop.client.filter.CountryFilter;
import com.eshop.client.model.CountryModel;
import com.eshop.client.service.CountryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Country Rest Service v1")
@RequestMapping(value = "/api/v1/country")
public class CountryRestController extends BaseRestControllerImpl<CountryFilter, CountryModel, Long>  {

    private CountryService countryService;

    public CountryRestController(CountryService service) {
        super(service);
        this.countryService = service;
    }
}
