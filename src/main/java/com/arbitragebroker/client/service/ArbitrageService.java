package com.arbitragebroker.client.service;

import com.arbitragebroker.client.filter.ArbitrageFilter;
import com.arbitragebroker.client.model.ArbitrageModel;
import com.arbitragebroker.client.model.CoinUsageDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ArbitrageService extends BaseService<ArbitrageFilter, ArbitrageModel, Long> {
    long countAllByUserId(UUID userId);
    long countByUserIdAndDate(UUID userId, LocalDateTime date);
    String purchaseLimit(UUID userId);
    Page<CoinUsageDTO> findMostUsedCoins(int pageSize);
}
