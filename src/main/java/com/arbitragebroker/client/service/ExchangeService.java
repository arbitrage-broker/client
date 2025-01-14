package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.ExchangeFilter;
import com.arbitragebroker.client.model.ExchangeModel;

import java.util.List;

public interface ExchangeService extends BaseService<ExchangeFilter, ExchangeModel, Long> {
    List<ExchangeModel> findByRandom();
    List<ExchangeModel> findAllByRandom(int count);
}
