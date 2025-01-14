package com.eshop.client.strategy.impl;

import com.eshop.client.model.WalletModel;
import com.eshop.client.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BonusStrategyImpl implements TransactionStrategy {
    @Override
    public void execute(WalletModel model) {

    }
}
