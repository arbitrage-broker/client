package com.eshop.client.service.impl;

import com.eshop.client.entity.QWalletEntity;
import com.eshop.client.entity.UserEntity;
import com.eshop.client.entity.WalletEntity;
import com.eshop.client.enums.*;
import com.eshop.client.filter.WalletFilter;
import com.eshop.client.mapping.WalletMapper;
import com.eshop.client.model.ParameterModel;
import com.eshop.client.model.WalletModel;
import com.eshop.client.repository.UserRepository;
import com.eshop.client.repository.WalletRepository;
import com.eshop.client.service.*;
import com.eshop.client.strategy.TransactionStrategyFactory;
import com.eshop.client.util.DateUtil;
import com.eshop.client.util.SessionHolder;
import com.eshop.client.exception.ExpectationException;
import com.eshop.client.exception.NotAcceptableException;
import com.eshop.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.eshop.client.util.DateUtil.toLocalDate;
import static com.eshop.client.util.StringUtils.generateIdKey;

@Service
public class WalletServiceImpl extends BaseServiceImpl<WalletFilter, WalletModel, WalletEntity, Long> implements WalletService {

    private final WalletRepository walletRepository;
    private final SubscriptionService subscriptionService;
    private final JPAQueryFactory queryFactory;
    private final SessionHolder sessionHolder;
    private final String minWithdrawAmount;
    private final UserService userService;
    private final MailService mailService;
    private final List<ParameterModel> referralRewardParameters;
    private final TransactionStrategyFactory transactionStrategyFactory;
    private final UserRepository userRepository;
    private final String subUserPercentage;
    private final String userPercentage;

    public WalletServiceImpl(WalletRepository repository, WalletMapper mapper, SubscriptionService subscriptionService, JPAQueryFactory queryFactory, SessionHolder sessionHolder, ParameterService parameterService, UserService userService, MailService mailService, TransactionStrategyFactory transactionStrategyFactory, UserRepository userRepository) {
        super(repository, mapper);
        this.walletRepository = repository;
        this.subscriptionService = subscriptionService;
        this.queryFactory = queryFactory;
        this.sessionHolder = sessionHolder;
        this.minWithdrawAmount = parameterService.findByCode("MIN_WITHDRAW").getValue();
        this.userService = userService;
        this.mailService = mailService;
        this.referralRewardParameters = parameterService.findAllByParameterGroupCode("REFERRAL_REWARD");
        this.subUserPercentage = parameterService.findByCode("SUB_USER_PERCENTAGE").getValue();
        this.userPercentage = parameterService.findByCode("USER_PERCENTAGE").getValue();
        this.transactionStrategyFactory = transactionStrategyFactory;
        this.userRepository = userRepository;
    }

    @Override
    public Predicate queryBuilder(WalletFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QWalletEntity path = QWalletEntity.walletEntity;

        if (!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.user.roles.any().role.ne(RoleType.ADMIN));
        }

        filter.getId().ifPresent(value -> builder.and(path.id.eq(value)));
        filter.getAmount().ifPresent(value -> builder.and(path.amount.eq(value)));
        filter.getAmountFrom().ifPresent(value -> builder.and(path.amount.goe(value)));
        filter.getAmountTo().ifPresent(value -> builder.and(path.amount.loe(value)));
        filter.getActualAmount().ifPresent(value -> builder.and(path.actualAmount.eq(value)));
        filter.getActualAmountFrom().ifPresent(value -> builder.and(path.actualAmount.goe(value)));
        filter.getActualAmountTo().ifPresent(value -> builder.and(path.actualAmount.loe(value)));
        filter.getCurrency().ifPresent(value -> builder.and(path.currency.eq(value)));
        filter.getNetwork().ifPresent(value -> builder.and(path.network.eq(value)));
        filter.getTransactionType().ifPresent(value -> builder.and(path.transactionType.eq(value)));
        filter.getTransactionTypes().ifPresent(value -> builder.and(path.transactionType.in(value)));
        filter.getTransactionHash().ifPresent(value -> builder.and(path.transactionHash.eq(value)));
        filter.getUserId().ifPresent(value -> builder.and(path.user.id.eq(value)));
        filter.getStatus().ifPresent(value -> builder.and(path.status.eq(value)));
        filter.getAddress().ifPresent(value -> builder.and(path.address.eq(value)));

        return builder;
    }

    @Override
    @Transactional
    public WalletModel create(WalletModel model, String allKey) {
        var user = userService.findById(model.getUser().getId(), generateIdKey("User", model.getUser().getId()));
        model.setRole(user.getRole());
        if(!user.getEmailVerified()) {
            try {
                mailService.sendVerification(user.getEmail(), "Email Verification");
                throw new ExpectationException("""
                        We have sent a verification email to <strong>%s</strong>.<br/>
                        Please verify your email before make this transaction!
                        """.formatted(user.getEmail()));
            } catch (IllegalStateException ignored){}
        }
        var transactionStrategy = transactionStrategyFactory.get(model.getTransactionType());
        transactionStrategy.execute(model);
        clearCache("Wallet:");
        return super.create(model, allKey);
    }

    @Override
    @Transactional
    public WalletModel update(WalletModel model, String key, String allKey) {
        var entity = repository.findById(model.getId()).orElseThrow(() -> new NotFoundException(String.format("%s not found by id %d", model.getClass().getName(), model.getId().toString())));
        var user = userRepository.findById(model.getUser().getId()).orElseThrow(()->new NotFoundException("user not found"));
        model.setRole(user.getRole());
        if(!user.getId().equals(entity.getUser().getId()))
            entity.setUser(user);

        var transactionStrategy = transactionStrategyFactory.get(model.getTransactionType());
        transactionStrategy.execute(model);
        var result = mapper.toModel(repository.save(mapper.updateEntity(model, entity)));
        clearCache("Wallet:");
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id, String allKey) {
        WalletEntity entity = repository.findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        repository.delete(entity);

        var balance = totalBalanceByUserId(entity.getUser().getId());
        var subscriptionModel = subscriptionService.findByUserAndActivePackage(entity.getUser().getId());

        if (subscriptionModel.getSubscriptionPackage().getPrice().compareTo(balance) > 0) {
            subscriptionService.logicalDeleteById(subscriptionModel.getId());
        }
        clearCache("Wallet:");
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalBalanceByUserId'")
    public BigDecimal totalBalanceByUserId(UUID userId) {
        return walletRepository.calculateUserBalance(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalDeposit'")
    public BigDecimal totalDeposit(UUID userId) {
        return walletRepository.totalDeposit(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalWithdrawal'")
    public BigDecimal totalWithdrawal(UUID userId) {
        return walletRepository.totalWithdrawal(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalBonus'")
    public BigDecimal totalBonus(UUID userId) {
        return walletRepository.totalBonus(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalReward'")
    public BigDecimal totalReward(UUID userId) {
        return walletRepository.totalReward(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':totalProfit'")
    public BigDecimal totalProfit(UUID userId) {
        return walletRepository.totalProfit(userId);
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':dailyProfit'")
    public BigDecimal dailyProfit(UUID userId) {
        QWalletEntity path = QWalletEntity.walletEntity;
        DateTemplate<Date> truncatedDate = Expressions.dateTemplate(Date.class, "date_trunc('day', {0})", path.createdDate);
        var rewardBonusSum =
                new CaseBuilder()
                        .when(path.transactionType.eq(TransactionType.REWARD)
                                .or(path.transactionType.eq(TransactionType.BONUS))
                                .or(path.transactionType.eq(TransactionType.REWARD_REFERRAL))
                        )
                        .then(path.amount)
                        .otherwise(BigDecimal.ZERO)
                        .sum();

        var withdrawalProfitSum =
                new CaseBuilder()
                        .when(path.transactionType.eq(TransactionType.WITHDRAWAL_PROFIT))
                        .then(path.amount)
                        .otherwise(BigDecimal.ZERO)
                        .sum();


        return queryFactory.select(rewardBonusSum.subtract(withdrawalProfitSum).coalesce(BigDecimal.ZERO))
                .from(path)
                .where(path.user.id.eq(userId))
                .where(path.status.eq(EntityStatusType.Active))
                .where(truncatedDate.eq(DateUtil.truncate(new Date())))
                .fetchOne();
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:findAllWithinDateRange:startDate:' + #startDate + ':endDate:' + #endDate + ':transactionType:' + #transactionType")
    public Map<Long, BigDecimal> findAllWithinDateRange(long startDate, long endDate, TransactionType transactionType) {
        QWalletEntity path = QWalletEntity.walletEntity;
        DateTemplate<Date> truncatedDate = Expressions.dateTemplate(Date.class, "date_trunc('day', {0})", path.createdDate);
        var results = queryFactory.select(truncatedDate, path.amount.sum())
                .from(path)
                .where(truncatedDate.between(new Date(startDate), new Date(endDate)))
                .where(path.transactionType.eq(transactionType))
                .where(path.user.id.eq(sessionHolder.getCurrentUser().getId()))
                .groupBy(truncatedDate)
                .orderBy(truncatedDate.asc())
                .fetch();
        Map<Long, BigDecimal> map = results.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(truncatedDate).getTime(), tuple -> tuple.get(path.amount.sum())));

        var allDates = toLocalDate(startDate).datesUntil(toLocalDate(endDate).plusDays(1)).map(DateUtil::toEpoch);

        return allDates.collect(Collectors.toMap(epoch -> epoch, epoch -> map.getOrDefault(epoch, BigDecimal.ZERO)));
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':' + #transactionType.name() + ':allowedWithdrawalBalance'")
    public BigDecimal allowedWithdrawalBalance(UUID userId, TransactionType transactionType) {
        if (transactionType.equals(TransactionType.WITHDRAWAL)) {
            BigDecimal totalBalance = walletRepository.totalBalance(userId);
            BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(userId).multiply(new BigDecimal(subUserPercentage));
            BigDecimal totalBalanceOfMyPercentage = totalBalance.multiply(new BigDecimal(userPercentage));
            BigDecimal allowedWithdrawal = totalBalanceOfMyPercentage.add(totalDepositOfSubUsersPercentage);
            allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfit(userId));
            if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                allowedWithdrawal = BigDecimal.ZERO;
            else if (allowedWithdrawal.compareTo(totalBalance) > 0)
                allowedWithdrawal = totalBalance;
            return allowedWithdrawal;
        }
        if (transactionType.equals(TransactionType.WITHDRAWAL_PROFIT)) {
            BigDecimal totalProfit = walletRepository.totalProfit(userId);
            BigDecimal totalDepositOfSubUsersPercentage = walletRepository.totalBalanceOfSubUsers(userId).multiply(new BigDecimal(subUserPercentage));
            BigDecimal totalBalanceOfMinePercentage = walletRepository.totalBalance(userId).multiply(new BigDecimal(userPercentage));

            BigDecimal allowedWithdrawal = totalBalanceOfMinePercentage.add(totalDepositOfSubUsersPercentage);
            allowedWithdrawal = allowedWithdrawal.subtract(walletRepository.totalWithdrawalProfit(userId));
            if (allowedWithdrawal.compareTo(BigDecimal.ZERO) < 0)
                allowedWithdrawal = BigDecimal.ZERO;
            else if (allowedWithdrawal.compareTo(totalProfit) > 0)
                allowedWithdrawal = totalProfit;
            return allowedWithdrawal;
        }
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "client", key = "'Wallet:*'")
    public WalletModel claimReferralReward(UUID userId, Integer userCount) {
        BigDecimal amount = referralRewardParameters.stream()
                .filter(f -> f.getTitle().equals(userCount.toString()))
                .map(m -> new BigDecimal(m.getValue()))
                .findAny().orElse(BigDecimal.ZERO);

        WalletEntity entity = new WalletEntity();
        entity.setAmount(amount);
        entity.setActualAmount(amount);
        entity.setCurrency(CurrencyType.USDT);
        entity.setNetwork(NetworkType.TRC20);
        entity.setUser(new UserEntity().setUserId(userId));
        entity.setTransactionType(TransactionType.REWARD_REFERRAL);
        entity.setStatus(EntityStatusType.Active);

        var allowedReferralCount = userService.countAllActiveChild(userId) - getClaimedReferrals(userId);
        if(allowedReferralCount <= 0)
            throw new NotAcceptableException("You have already claimed this referrals reward.");

        var allowedAmount = referralRewardParameters.stream()
                .filter(f -> Long.valueOf(f.getTitle()) <= allowedReferralCount)
                .mapToLong(m -> Long.valueOf(m.getValue()))
                .max()
                .orElse(0L);
        if(BigDecimal.valueOf(allowedAmount).compareTo(entity.getAmount()) < 0)
            throw new NotAcceptableException(String.format("Invalid requested, Amount is greater than the allowed amount (%d USD).", allowedAmount));

        var user = userService.findById(userId, generateIdKey("User",userId));
        entity.setRole(user.getRole());
        clearCache("Wallet:");
        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "client", key = "'Wallet:' + #userId.toString() + ':getClaimedReferrals'")
    public Integer getClaimedReferrals(UUID userId) {
        var rewardReferrals = walletRepository.findAllReferralRewardByUserId(userId);
        AtomicReference<Integer> count = new AtomicReference<>(0);
        rewardReferrals.forEach(w->{
            count.updateAndGet(v -> v + referralRewardParameters.stream()
                    .filter(f -> Integer.valueOf(f.getValue()) <= w.getAmount().intValue())
                    .mapToInt(m -> Integer.valueOf(m.getTitle()))
                    .max()
                    .orElse(0));

        });
        return count.get();
    }
}
