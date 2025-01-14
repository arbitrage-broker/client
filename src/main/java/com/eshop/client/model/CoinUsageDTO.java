package com.eshop.client.model;

import lombok.Data;

@Data
public class CoinUsageDTO {
    private String name;
    private Long usageCount;
    private Long usagePercentage;

    public CoinUsageDTO(String name, Long usageCount) {
        this.name = name;
        this.usageCount = usageCount;
    }
}
