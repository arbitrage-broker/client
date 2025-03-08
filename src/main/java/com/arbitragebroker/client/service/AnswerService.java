package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.AnswerFilter;
import com.arbitragebroker.client.model.AnswerModel;

public interface AnswerService extends BaseService<AnswerFilter, AnswerModel, Long> {
    void receiveMessage(String msg);
}
