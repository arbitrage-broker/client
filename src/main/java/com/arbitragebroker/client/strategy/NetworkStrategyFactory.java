package com.arbitragebroker.client.strategy;

import com.arbitragebroker.client.enums.NetworkType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.arbitragebroker.client.util.StringUtils.getTargetClassName;

@Component
public class NetworkStrategyFactory implements ApplicationContextAware {
    private Map<String, NetworkStrategy> strategyMap;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, NetworkStrategy> beans = applicationContext.getBeansOfType(NetworkStrategy.class);
        strategyMap = new HashMap<>();
        for (NetworkStrategy strategy : beans.values()) {
            strategyMap.put(getTargetClassName(strategy), strategy);
        }
    }

    public NetworkStrategy get(NetworkType networkType) {
        NetworkStrategy strategy = strategyMap.get(networkType.getImplementationClass().getSimpleName());
        if (strategy == null) {
            throw new RuntimeException("No implementation found for " + networkType);
        }
        return strategy;
    }
}
