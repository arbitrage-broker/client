package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.CoinFilter;
import com.arbitragebroker.client.model.CoinModel;

import java.util.List;

public interface CoinService extends BaseService<CoinFilter, CoinModel, Long> {
    CoinModel findByRandom();
    List<CoinModel> findAllByRandom(int count);
}
