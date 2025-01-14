package com.arbitragebroker.client.strategy.impl;

import com.arbitragebroker.client.service.SubscriptionPackageService;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.model.WalletModel;
import com.arbitragebroker.client.service.TelegramService;
import com.arbitragebroker.client.service.UserService;
import com.arbitragebroker.client.strategy.TransactionStrategy;
import com.arbitragebroker.client.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arbitragebroker.client.util.StringUtils.generateIdKey;

@Service
@RequiredArgsConstructor
public class DepositStrategyImpl implements TransactionStrategy {
    private final SubscriptionPackageService subscriptionPackageService;
    private final TelegramService telegramService;
    private final UserService userService;

    @Override
    public void execute(WalletModel model) {
        var sp = subscriptionPackageService.findMatchedPackageByAmount(model.getAmount());
        if (sp == null)
            throw new BadRequestException("Please deposit at least the amount of the first subscription.");
        model.setStatus(EntityStatusType.Pending);
        var user = userService.findById(model.getUser().getId(),generateIdKey("User", model.getUser().getId()));
        telegramService.sendToRole(user.getRole(), """
                *Pending Transaction*\n
                Date : %s\n
                User : %s\n
                Type : Deposit\n
                Amount : %s""".formatted(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), user.getSelectTitle(), model.getAmount().toString()));
    }
}
