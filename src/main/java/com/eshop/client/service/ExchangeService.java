package com.eshop.client.service;

import com.eshop.client.filter.ExchangeFilter;
import com.eshop.client.model.ExchangeModel;

import java.util.List;

public interface ExchangeService extends BaseService<ExchangeFilter, ExchangeModel, Long> {
    List<ExchangeModel> findByRandom();
    List<ExchangeModel> findAllByRandom(int count);
}
