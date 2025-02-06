package com.arbitragebroker.client.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoinUsageDTO implements Serializable {
    private String name;
    private Long usageCount;
    private Long usagePercentage;

    public CoinUsageDTO(String name, Long usageCount) {
        this.name = name;
        this.usageCount = usageCount;
    }
}
