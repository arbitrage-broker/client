package com.arbitragebroker.client.strategy;


import com.arbitragebroker.client.model.WalletModel;

public interface TransactionStrategy {
    void execute(WalletModel model);
}
