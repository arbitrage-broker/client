package com.eshop.client.strategy.impl;

import com.eshop.client.entity.WalletEntity;
import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.model.ParameterModel;
import com.eshop.client.model.WalletModel;
import com.eshop.client.repository.WalletRepository;
import com.eshop.client.service.ParameterService;
import com.eshop.client.service.UserService;
import com.eshop.client.strategy.TransactionStrategy;
import com.eshop.client.exception.NotAcceptableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardReferralStrategyImpl implements TransactionStrategy {
    private final WalletRepository walletRepository;
        private final List<ParameterModel> referralRewardParameters;
    private final UserService userService;

    public RewardReferralStrategyImpl(WalletRepository walletRepository, ParameterService parameterService, UserService userService) {
        this.walletRepository = walletRepository;
        this.referralRewardParameters = parameterService.findAllByParameterGroupCode("REFERRAL_REWARD");
        this.userService = userService;
    }


    @Override
    public void execute(WalletModel model) {
        var rewardReferrals = walletRepository.findAllReferralRewardByUserId(model.getUser().getId());
        long countAllActiveChild = userService.countAllActiveChild(model.getUser().getId());
        Map<Integer, WalletEntity> walletEntityMap = new HashMap<>();
        rewardReferrals.forEach(w->{
            Integer key = referralRewardParameters.stream()
                    .filter(f -> Integer.valueOf(f.getValue()) <= w.getAmount().intValue())
                    .mapToInt(m -> Integer.valueOf(m.getTitle()))
                    .max()
                    .orElse(0);
            walletEntityMap.put(key, w);
        });
        var allowedReferralCount = countAllActiveChild - walletEntityMap.keySet().stream().mapToInt(Integer::valueOf).sum();
        if(allowedReferralCount <= 0)
            throw new NotAcceptableException("You have already claimed this referrals reward.");

        var allowedAmount = referralRewardParameters.stream()
                .filter(f -> Long.valueOf(f.getTitle()) <= allowedReferralCount)
                .mapToLong(m -> Long.valueOf(m.getValue()))
                .max()
                .orElse(0L);
        if(BigDecimal.valueOf(allowedAmount).compareTo(model.getAmount()) < 0)
            throw new NotAcceptableException(String.format("Invalid requested, Amount is greater than the allowed amount (%d USD).", allowedAmount));
        model.setActualAmount(model.getAmount());
        model.setCurrency(CurrencyType.USDT);
        model.setStatus(EntityStatusType.Active);
    }
}
