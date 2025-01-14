package com.eshop.client.model;

import lombok.Data;

import java.util.List;
@Data
public class CoinExchangeModel {
    private CoinModel coin;
    private int index;
    private List<ExchangeModel> exchanges;
}
