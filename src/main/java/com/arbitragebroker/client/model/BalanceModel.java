package com.arbitragebroker.client.model;

import com.arbitragebroker.client.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class BalanceModel {
    private CurrencyType currency;
    private BigDecimal totalAmount;
}
