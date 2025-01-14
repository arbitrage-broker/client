package com.eshop.client.dto;

import lombok.Data;

@Data
public class TokenResult {
    private String contractAddress;
    private String name;
    private String symbol;
    private String decimals;
}
