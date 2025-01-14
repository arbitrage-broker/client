package com.eshop.client.dto;

import lombok.Data;

@Data
public class TransactionReceiptResponse {
    private String status;
    private String message;
    private TransactionReceiptResult result;
}
