package com.eshop.client.service;

import com.eshop.client.filter.CoinFilter;
import com.eshop.client.model.CoinModel;

import java.util.List;

public interface CoinService extends BaseService<CoinFilter, CoinModel, Long> {
    CoinModel findByRandom();
    List<CoinModel> findAllByRandom(int count);
}
