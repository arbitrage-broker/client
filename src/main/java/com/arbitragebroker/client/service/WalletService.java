package com.arbitragebroker.client.service;

import com.arbitragebroker.client.enums.TransactionType;
import com.arbitragebroker.client.filter.WalletFilter;
import com.arbitragebroker.client.model.WalletModel;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface WalletService extends BaseService<WalletFilter, WalletModel, Long> {
    BigDecimal totalBalanceByUserId(UUID userId);
    BigDecimal totalDeposit(UUID userId);
    BigDecimal totalWithdrawal(UUID userId);
    BigDecimal totalBonus(UUID userId);
    BigDecimal totalReward(UUID userId);
    BigDecimal totalProfit(UUID userId);
    BigDecimal dailyProfit(UUID userId);
    Map<Long, BigDecimal> findAllWithinDateRange(long startDate, long endDate, TransactionType transactionType);
    BigDecimal allowedWithdrawalBalance(UUID userId, TransactionType transactionType);
    WalletModel claimReferralReward(UUID userId, Integer userCount);
    Integer getClaimedReferrals(UUID userId);
}
