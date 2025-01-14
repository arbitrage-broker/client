package com.eshop.client.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransactionResponse {
    private boolean isSuccess;
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private String symbol;

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "isSuccess='" + isSuccess + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", symbol='" + symbol + '\'' +
                ", amount=" + amount +
                '}';
    }
}
