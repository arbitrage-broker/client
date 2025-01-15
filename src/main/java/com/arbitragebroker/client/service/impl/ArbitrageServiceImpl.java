package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.ArbitrageEntity;
import com.arbitragebroker.client.entity.QArbitrageEntity;
import com.arbitragebroker.client.entity.WalletEntity;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.enums.TransactionType;
import com.arbitragebroker.client.exception.NotAcceptableException;
import com.arbitragebroker.client.exception.NotFoundException;
import com.arbitragebroker.client.filter.ArbitrageFilter;
import com.arbitragebroker.client.mapping.ArbitrageMapper;
import com.arbitragebroker.client.model.ArbitrageModel;
import com.arbitragebroker.client.model.CoinUsageDTO;
import com.arbitragebroker.client.model.SubscriptionModel;
import com.arbitragebroker.client.model.SubscriptionPackageModel;
import com.arbitragebroker.client.repository.ArbitrageRepository;
import com.arbitragebroker.client.repository.SubscriptionPackageRepository;
import com.arbitragebroker.client.repository.UserRepository;
import com.arbitragebroker.client.repository.WalletRepository;
import com.arbitragebroker.client.service.ArbitrageService;
import com.arbitragebroker.client.service.SubscriptionService;
import com.arbitragebroker.client.util.DateUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static com.arbitragebroker.client.util.MapperHelper.get;


@Service
public class ArbitrageServiceImpl extends BaseServiceImpl<ArbitrageFilter, ArbitrageModel, ArbitrageEntity, Long> implements ArbitrageService {

    private final ArbitrageRepository arbitrageRepository;
    private ArbitrageRepository repository;
    private ArbitrageMapper mapper;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final SubscriptionService subscriptionService;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;
    private final BaseMailService mailService;

    @Autowired
    public ArbitrageServiceImpl(ArbitrageRepository repository, ArbitrageMapper mapper, SubscriptionPackageRepository subscriptionPackageRepository,
                                SubscriptionService subscriptionService, WalletRepository walletRepository, UserRepository userRepository,
                                ArbitrageRepository arbitrageRepository, JPAQueryFactory queryFactory, BaseMailService mailService) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.subscriptionService = subscriptionService;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.arbitrageRepository = arbitrageRepository;
        this.queryFactory = queryFactory;
        this.mailService = mailService;
    }

    @Override
    public Predicate queryBuilder(ArbitrageFilter filter) {
        QArbitrageEntity p = QArbitrageEntity.arbitrageEntity;
        BooleanBuilder builder = new BooleanBuilder();
        DateTemplate<LocalDateTime> truncatedDate = Expressions.dateTemplate(LocalDateTime.class, "date_trunc('day', {0})", p.createdDate);

        filter.getId().ifPresent(v->builder.and(p.id.eq(v)));
        filter.getCreatedDate().ifPresent(v-> builder.and(truncatedDate.eq(v)));
        filter.getCreatedDateFrom().ifPresent(v-> builder.and(p.createdDate.goe(v)));
        filter.getCreatedDateTo().ifPresent(v-> builder.and(p.createdDate.loe(v)));
        filter.getUserId().ifPresent(v-> builder.and(p.user.id.eq(v)));
        filter.getExchangeId().ifPresent(v-> builder.and(p.exchange.id.eq(v)));
        filter.getCoinId().ifPresent(v-> builder.and(p.coin.id.eq(v)));
        filter.getSubscriptionId().ifPresent(v-> builder.and(p.subscription.id.eq(v)));
        filter.getRewardFrom().ifPresent(v-> builder.and(p.reward.goe(v)));
        filter.getRewardTo().ifPresent(v-> builder.and(p.reward.loe(v)));
        filter.getCurrency().ifPresent(v-> builder.and(p.currency.eq(v)));

        return builder;
    }

    @Override
    @Transactional
    public ArbitrageModel create(ArbitrageModel model, String allKey) {
        var purchaseLimitResponse = purchaseLimit(model.getUser().getId());
        if(purchaseLimitResponse != null)
            throw new NotAcceptableException("You have reached purchase limitation, please try " + purchaseLimitResponse);

        var subscription = subscriptionService.findByUserAndActivePackage(model.getUser().getId());
        var balance = walletRepository.calculateUserBalance(model.getUser().getId());
        var user = userRepository.findById(model.getUser().getId()).orElseThrow(()->new NotFoundException("user not found"));
//        if(!user.isEmailVerified()) {
//            mailService.sendVerification(user.getEmail(),"Email verification link");
//            throw new ExpectationException("Please verify your email before make this transaction.");
//        }
        var reward = subscription.getSubscriptionPackage().getReward(balance);

        if(reward.compareTo(BigDecimal.ZERO) == 1) {
            WalletEntity buyReward = new WalletEntity();
            buyReward.setStatus(EntityStatusType.Active);
            buyReward.setAmount(reward);
            buyReward.setActualAmount(reward);
            buyReward.setUser(user);
            buyReward.setRole(user.getRole());
            buyReward.setTransactionType(TransactionType.REWARD);
            walletRepository.save(buyReward);
            model.setReward(buyReward.getAmount());
            model.setCurrency(subscription.getSubscriptionPackage().getCurrency());

            if(!subscription.getSubscriptionPackage().getName().equals("Free")) {
                //parent reward
                if (user.getParent() != null) {
                    WalletEntity buyRewardParent = new WalletEntity();
                    buyRewardParent.setStatus(EntityStatusType.Active);
                    buyRewardParent.setAmount(reward.multiply(new BigDecimal("0.18")).setScale(6, RoundingMode.HALF_UP));
                    buyRewardParent.setActualAmount(reward.multiply(new BigDecimal("0.18")).setScale(6, RoundingMode.HALF_UP));
                    buyRewardParent.setUser(user.getParent());
                    buyRewardParent.setRole(user.getRole());
                    buyRewardParent.setCurrency(subscription.getSubscriptionPackage().getCurrency());
                    buyRewardParent.setTransactionType(TransactionType.BONUS);
                    walletRepository.save(buyRewardParent);
                }

                //grand parent reward
                if (get(() -> user.getParent().getParent()) != null) {
                    WalletEntity buyRewardGrandParent = new WalletEntity();
                    buyRewardGrandParent.setStatus(EntityStatusType.Active);
                    buyRewardGrandParent.setRole(user.getRole());
                    buyRewardGrandParent.setAmount(reward.multiply(new BigDecimal("0.08")).setScale(6, RoundingMode.HALF_UP));
                    buyRewardGrandParent.setActualAmount(reward.multiply(new BigDecimal("0.08")).setScale(6, RoundingMode.HALF_UP));
                    buyRewardGrandParent.setUser(user.getParent().getParent());
                    buyRewardGrandParent.setCurrency(subscription.getSubscriptionPackage().getCurrency());
                    buyRewardGrandParent.setTransactionType(TransactionType.BONUS);
                    walletRepository.save(buyRewardGrandParent);
                }
                clearCache("Wallet:" + user.getId().toString());
                model.setRole(user.getRole());

                var nextSubscriptionPackage = subscriptionPackageRepository.findMatchedPackageByAmount(balance).orElse(null);
                if (nextSubscriptionPackage != null && !nextSubscriptionPackage.getId().equals(subscription.getSubscriptionPackage().getId())) {
                    subscriptionService.create(new SubscriptionModel().setSubscriptionPackage(new SubscriptionPackageModel().setSubscriptionPackageId(nextSubscriptionPackage.getId())).setUser(model.getUser()).setStatus(EntityStatusType.Active), "Subscription");
                }
            }
            return super.create(model, allKey);
        }
        throw new NotAcceptableException("Your profit is equal to zero, please contact support.");
    }

    @Override
    public long countAllByUserId(UUID userId) {
        return repository.countAllByUserId(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Arbitrage:' + #userId.toString() + ':' + #date.getTime() + ':countByUserIdAndDate'")
    public long countByUserIdAndDate(UUID userId, Date date) {
        QArbitrageEntity path = QArbitrageEntity.arbitrageEntity;
        DateTemplate<Date> truncatedDate = Expressions.dateTemplate(Date.class, "date_trunc('day', {0})", path.createdDate);
        return queryFactory.from(path)
                .where(truncatedDate.eq(DateUtil.truncate(date)))
                .where(path.user.id.eq(userId))
                .fetchCount();
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Arbitrage:findMostUsedCoins:' + #pageSize")
    public Page<CoinUsageDTO> findMostUsedCoins(int pageSize) {
        long count = repository.countSince(LocalDateTime.now().minusDays(1));
        return repository.findMostUsedCoins(LocalDateTime.now().minusDays(1),PageRequest.ofSize(pageSize)).map(m->{
            m.setUsagePercentage(m.getUsageCount()*100L/count);
            return m;
        });
    }
    @Override
    @Transactional(readOnly = true)
//    @Cacheable(cacheNames = "client", key = "'Arbitrage:' + #userId.toString() + ':purchaseLimit'")
    public String purchaseLimit(UUID userId) {
        var subscription = subscriptionService.findByUserAndActivePackage(userId);
        if(subscription == null)
            return null;
        var todayArbitrages = arbitrageRepository.findByUserIdAndSubscriptionIdAndCreatedDateOrderByCreatedDateDesc(userId, subscription.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        if(CollectionUtils.isEmpty(todayArbitrages))
            return null;
        var allowedDate = todayArbitrages.get(0).getCreatedDate().plusMinutes(20L);
        if(todayArbitrages.size() >= subscription.getSubscriptionPackage().getOrderCount()) {
            return "tomorrow";
        }
//        var last21Minutes = todayArbitrages.stream().filter(x->x.getCreatedDate().isAfter(LocalDateTime.now().minusMinutes(21))).collect(Collectors.toList());
//        if(last21Minutes.size() >= subscription.getSubscriptionPackage().getOrderCount()) {
//            long minutesRemaining = ChronoUnit.MINUTES.between(LocalDateTime.now(), allowedDate);
//            return String.format("after %d minutes", minutesRemaining);
//        }
        return null;
    }
}
