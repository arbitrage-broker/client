package com.eshop.client.dto;

import lombok.Data;

@Data
public class TransactionResult {
    private String blockHash;
    private String blockNumber;
    private String from;
    private String gas;
    private String gasPrice;
    private String input;
    private String hash;
    private String nonce;
    private String to;
    private String transactionIndex;
    private String value;
    private String v;
    private String r;
    private String s;
}
