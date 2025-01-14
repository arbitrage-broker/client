package com.eshop.client.dto;

import lombok.Data;

@Data
public class TransactionReceiptResult {
    private String status; // "0" for failure and "1" for success
    private String transactionHash;
    private String from;
    private String to;
    private String blockNumber;
}
