package com.eshop.client.enums;

import com.eshop.client.config.MessageConfig;
import com.eshop.client.model.Select2Model;
import com.eshop.client.strategy.TransactionStrategy;
import com.eshop.client.strategy.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum TransactionType {
    DEPOSIT("deposit", DepositStrategyImpl.class),
    WITHDRAWAL("withdrawal", WithdrawalStrategyImpl.class),
    WITHDRAWAL_PROFIT("withdrawalProfit", WithdrawalProfitStrategyImpl.class),//both bonus and reward
    BONUS("bonus", BonusStrategyImpl.class), //referrals
    REWARD("reward", RewardStrategyImpl.class), //arbitrage
    REWARD_REFERRAL("referralReward", RewardReferralStrategyImpl.class),//referral reward
    ;


    TransactionType(String title,Class<? extends TransactionStrategy> implementationClass) {
        this.title = title;
        this.implementationClass = implementationClass;
    }
    private MessageConfig messages;
    private String title;
    private Class<? extends TransactionStrategy> implementationClass;
    public MessageConfig getMessages() {
        return messages;
    }

    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return messages.getMessage(title);
    }
    public Class<? extends TransactionStrategy> getImplementationClass() {
        return this.implementationClass;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public static List<Select2Model> getAll() {
        return Arrays.stream(values()).map(m -> new Select2Model(m.name(), m.getTitle())).collect(Collectors.toList());
    }

    @Component
    public static class EnumValuesInjectionService {

        @Autowired
        private MessageConfig messages;

        //Inject into bean through static inner class and assign value to enum.
        @PostConstruct
        public void postConstruct() {

            for (TransactionType type : EnumSet.allOf(TransactionType.class)) {
                type.setMessages(messages);
            }
        }
    }
}