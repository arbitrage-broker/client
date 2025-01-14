package com.eshop.client.strategy;

import com.eshop.client.dto.TransactionResponse;

import java.math.BigDecimal;

public interface NetworkStrategy {
    BigDecimal getPrice(String token);
    TransactionResponse getTransactionInfo(String hash);
}
