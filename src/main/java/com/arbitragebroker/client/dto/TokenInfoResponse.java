package com.arbitragebroker.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenInfoResponse {
    private List<TokenResult> result;
}