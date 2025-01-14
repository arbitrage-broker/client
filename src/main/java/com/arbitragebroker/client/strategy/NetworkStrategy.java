package com.arbitragebroker.client.strategy;

import com.arbitragebroker.client.dto.TransactionResponse;

import java.math.BigDecimal;

public interface NetworkStrategy {
    BigDecimal getPrice(String token);
    TransactionResponse getTransactionInfo(String hash);
}
