package com.arbitragebroker.client.strategy.impl;

import com.arbitragebroker.client.model.WalletModel;
import com.arbitragebroker.client.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BonusStrategyImpl implements TransactionStrategy {
    @Override
    public void execute(WalletModel model) {

    }
}
