package com.arbitragebroker.client.strategy.impl;

import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.model.WalletModel;
import com.arbitragebroker.client.repository.WalletRepository;
import com.arbitragebroker.client.service.ParameterService;
import com.arbitragebroker.client.service.SubscriptionService;
import com.arbitragebroker.client.service.TelegramService;
import com.arbitragebroker.client.service.UserService;
import com.arbitragebroker.client.strategy.TransactionStrategy;
import com.arbitragebroker.client.exception.InsufficentBalanceException;
import com.arbitragebroker.client.exception.NotAcceptableException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arbitragebroker.client.util.StringUtils.generateIdKey;

@Service
public class WithdrawalStrategyImpl implements TransactionStrategy {
    private final SubscriptionService subscriptionService;
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final String minWithdrawAmount;
    private final String subUserPercentage;
    private final String userPercentage;
    private final TelegramService telegramService;

    public WithdrawalStrategyImpl(SubscriptionService subscriptionService, WalletRepository walletRepository, UserService userService, ParameterService parameterService, TelegramService telegramService) {
        this.subscriptionService = subscriptionService;
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.minWithdrawAmount = parameterService.findByCode("MIN_WITHDRAW").getValue();
        this.subUserPercentage = parameterService.findByCode("SUB_USER_PERCENTAGE").getValue();
        this.userPercentage = parameterService.findByCode("USER_PERCENTAGE").getValue();
        this.telegramService = telegramService;
    }

    @Override
    public void execute(WalletModel model) {
        model.setActualAmount(model.getAmount());
        model.setCurrency(CurrencyType.USDT);
        var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());
        if (currentSubscription == null)
            throw new InsufficentBalanceException();

        var totalBalance = walletRepository.totalBalance(model.getUser().getId());
        if (totalBalance.compareTo(model.getAmount()) < 0)
            throw new InsufficentBalanceException();

        if(currentSubscription.getSubscriptionPackage().getName().equals("Free"))
            throw new InsufficentBalanceException("The Free plan does not allow withdrawal.");

//        if (model.getAmount().compareTo(currentSubscription.getFinalPrice()) >= 0) {
//            if (currentSubscription.getRemainingWithdrawalPerDay() > 0L)
//                throw new NotAcceptableException(String.format("You can withdraw your funds after %d days.", currentSubscription.getRemainingWithdrawalPerDay()));
//
//            SubscriptionPackageModel currentSubscriptionPackage = currentSubscription.getSubscriptionPackage();
//            if (userService.countAllActiveChild(model.getUser().getId()) < currentSubscriptionPackage.getOrderCount()) {
//                throw new NotAcceptableException(String.format("To withdraw your funds you need to have at least %d referrals.", currentSubscriptionPackage.getOrderCount()));
//            }
//        }
        synchronized (model.getUser().getId().toString().intern()) {
            BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(model.getUser().getId()).multiply(new BigDecimal(subUserPercentage));
            BigDecimal totalDepositOfMyPercentage = totalBalance.multiply(new BigDecimal(userPercentage));
            BigDecimal allowedWithdrawal = totalDepositOfMyPercentage.add(totalDepositOfSubUsersPercentage);
            allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfit(model.getUser().getId()));
            if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                allowedWithdrawal = BigDecimal.ZERO;
            else if (allowedWithdrawal.compareTo(totalBalance) > 0)
                allowedWithdrawal = totalBalance;

            if (allowedWithdrawal.compareTo(model.getAmount()) < 0) {
                throw new NotAcceptableException("""
                            You can withdraw your deposited amount up to : <strong>%d USD</strong>.<br/>
                            To increase your withdrawal amount, please bring more referrals!"""
                        .formatted(allowedWithdrawal.longValue()));
            }
        }
        model.setStatus(EntityStatusType.Pending);

        var user = userService.findById(model.getUser().getId(),generateIdKey("User", model.getUser().getId()));
        telegramService.sendToRole(user.getRole(), """
                *Pending Transaction*\n
                Date : %s\n
                User : %s\n
                Type : Withdraw\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
    }
}
