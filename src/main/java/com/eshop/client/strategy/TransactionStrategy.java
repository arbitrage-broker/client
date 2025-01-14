package com.eshop.client.strategy;


import com.eshop.client.model.WalletModel;

public interface TransactionStrategy {
    void execute(WalletModel model);
}
