package com.eshop.client.strategy.impl;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.model.WalletModel;
import com.eshop.client.repository.WalletRepository;
import com.eshop.client.service.ParameterService;
import com.eshop.client.service.SubscriptionService;
import com.eshop.client.service.TelegramService;
import com.eshop.client.service.UserService;
import com.eshop.client.strategy.TransactionStrategy;
import com.eshop.client.exception.InsufficentBalanceException;
import com.eshop.client.exception.NotAcceptableException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.eshop.client.util.StringUtils.generateIdKey;

@Service
public class WithdrawalProfitStrategyImpl implements TransactionStrategy {
    private final SubscriptionService subscriptionService;
    private final WalletRepository walletRepository;
    private final String minWithdrawAmount;
    private final String subUserPercentage;
    private final String userPercentage;
    private final UserService userService;
    private final TelegramService telegramService;

    public WithdrawalProfitStrategyImpl(SubscriptionService subscriptionService, WalletRepository walletRepository, ParameterService parameterService, UserService userService, TelegramService telegramService) {
        this.subscriptionService = subscriptionService;
        this.walletRepository = walletRepository;
        this.minWithdrawAmount = parameterService.findByCode("MIN_WITHDRAW").getValue();
        this.subUserPercentage = parameterService.findByCode("SUB_USER_PERCENTAGE").getValue();
        this.userPercentage = parameterService.findByCode("USER_PERCENTAGE").getValue();
        this.userService = userService;
        this.telegramService = telegramService;
    }

    @Override
    public void execute(WalletModel model) {
        model.setActualAmount(model.getAmount());
        model.setCurrency(CurrencyType.USDT);
        var totalProfit = walletRepository.totalProfit(model.getUser().getId());
        if(totalProfit.compareTo(model.getAmount())<0)
            throw new InsufficentBalanceException();

        var currentSubscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());
        if (currentSubscription == null)
            throw new InsufficentBalanceException();
        if(currentSubscription.getSubscriptionPackage().getName().equals("Free"))
            throw new InsufficentBalanceException("The Free plan does not allow withdrawal.");
        if (model.getAmount().compareTo(new BigDecimal(minWithdrawAmount)) < 0)
            throw new InsufficentBalanceException(String.format("Your requested amount %s should greater than %s", model.getAmount().toString(), minWithdrawAmount));
        synchronized (model.getUser().getId().toString().intern()) {
            BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(model.getUser().getId()).multiply(new BigDecimal(subUserPercentage));
            BigDecimal totalDepositOfMyPercentage = walletRepository.totalBalance(model.getUser().getId()).multiply(new BigDecimal(userPercentage));
            BigDecimal allowedWithdrawal = totalDepositOfMyPercentage.add(totalDepositOfSubUsersPercentage);
            allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfit(model.getUser().getId()));
            if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                allowedWithdrawal = BigDecimal.ZERO;
            else if (allowedWithdrawal.compareTo(totalProfit) > 0)
                allowedWithdrawal = totalProfit;
            if (allowedWithdrawal.compareTo(model.getAmount()) < 0) {
                throw new NotAcceptableException("""
                            You can withdraw your profit amount up to : <strong>%d USD</strong>.<br/>
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
                Type : Withdraw Profit\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
    }
}
