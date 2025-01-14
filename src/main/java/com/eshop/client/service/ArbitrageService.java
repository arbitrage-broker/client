package com.eshop.client.service;

import com.eshop.client.filter.ArbitrageFilter;
import com.eshop.client.model.ArbitrageModel;
import com.eshop.client.model.CoinUsageDTO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.UUID;

public interface ArbitrageService extends BaseService<ArbitrageFilter, ArbitrageModel, Long> {
    long countAllByUserId(UUID userId);
    long countByUserIdAndDate(UUID userId, Date date);
    String purchaseLimit(UUID userId);
    Page<CoinUsageDTO> findMostUsedCoins(int pageSize);
}
