package com.eshop.client.controller.impl;

import com.eshop.client.filter.QuestionFilter;
import com.eshop.client.model.QuestionModel;
import com.eshop.client.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Question Rest Service v1")
@RequestMapping(value = "/api/v1/question")
public class QuestionRestController extends BaseRestControllerImpl<QuestionFilter, QuestionModel, Long> {

    private QuestionService questionService;

    public QuestionRestController(QuestionService service) {
        super(service);
        this.questionService = service;
    }
}
