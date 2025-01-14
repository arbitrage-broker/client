package com.eshop.client.strategy;

import com.eshop.client.enums.TransactionType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.eshop.client.util.StringUtils.getTargetClassName;


@Component
public class TransactionStrategyFactory implements ApplicationContextAware {
    private Map<String, TransactionStrategy> strategyMap;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TransactionStrategy> beans = applicationContext.getBeansOfType(TransactionStrategy.class);
        strategyMap = new HashMap<>();
        for (TransactionStrategy strategy : beans.values()) {
            strategyMap.put(getTargetClassName(strategy), strategy);
        }
    }

    public TransactionStrategy get(TransactionType transactionType) {
        TransactionStrategy strategy = strategyMap.get(transactionType.getImplementationClass().getSimpleName());
        if (strategy == null) {
            throw new RuntimeException("No implementation found for " + transactionType);
        }
        return strategy;
    }
}
