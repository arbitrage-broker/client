package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.entity.QSubscriptionEntity;
import com.arbitragebroker.client.entity.SubscriptionEntity;
import com.arbitragebroker.client.entity.WalletEntity;
import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.enums.TransactionType;
import com.arbitragebroker.client.filter.SubscriptionFilter;
import com.arbitragebroker.client.mapping.SubscriptionMapper;
import com.arbitragebroker.client.model.SubscriptionModel;
import com.arbitragebroker.client.repository.SubscriptionPackageRepository;
import com.arbitragebroker.client.repository.SubscriptionRepository;
import com.arbitragebroker.client.repository.UserRepository;
import com.arbitragebroker.client.repository.WalletRepository;
import com.arbitragebroker.client.service.ParameterService;
import com.arbitragebroker.client.service.SubscriptionService;
import com.arbitragebroker.client.util.DateUtil;
import com.arbitragebroker.client.exception.BadRequestException;
import com.arbitragebroker.client.exception.InsufficentBalanceException;
import com.arbitragebroker.client.exception.NotAcceptableException;
import com.arbitragebroker.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.arbitragebroker.client.util.MapperHelper.get;

@Service
public class SubscriptionServiceImpl extends BaseServiceImpl<SubscriptionFilter,SubscriptionModel, SubscriptionEntity, Long> implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final String selfFreeBonusAmount;

    public SubscriptionServiceImpl(SubscriptionRepository repository, SubscriptionMapper mapper,
                                   SubscriptionPackageRepository subscriptionPackageRepository, UserRepository userRepository,
                                   WalletRepository walletRepository, ParameterService parameterService) {
        super(repository, mapper);
        this.subscriptionRepository = repository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.selfFreeBonusAmount = parameterService.findByCode("SELF_FREE_BONUS_AMOUNT").getValue();
    }
    @Override
    public JpaRepository<SubscriptionEntity,Long> getRepository() {
        return subscriptionRepository;
    }

    @Override
    public Predicate queryBuilder(SubscriptionFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSubscriptionEntity path = QSubscriptionEntity.subscriptionEntity;

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.user.roles.any().role.ne(RoleType.ADMIN));
        }

        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getUserId().ifPresent(v -> builder.and(path.user.id.eq(v)));
        filter.getSubscriptionPackageId().ifPresent(v -> builder.and(path.subscriptionPackage.id.eq(v)));
        filter.getExpireDateFrom().ifPresent(v -> builder.and(path.expireDate.goe(DateUtil.toLocalDateTime(v))));
        filter.getExpireDateTo().ifPresent(v -> builder.and(path.expireDate.loe(DateUtil.toLocalDateTime(v))));
        filter.getDiscountPercentage().ifPresent(v -> builder.and(path.discountPercentage.eq(v)));
        filter.getFinalPriceFrom().ifPresent(v -> builder.and(path.finalPrice.goe(v)));
        filter.getFinalPriceTo().ifPresent(v -> builder.and(path.finalPrice.loe(v)));
        filter.getStatus().ifPresent(v -> builder.and(path.status.eq(v)));

        return builder;
    }

    @Override
    @Transactional
    public SubscriptionModel create(SubscriptionModel model, String allKey){
        var entity = mapper.toEntity(model);
        var subscriptionPackage = subscriptionPackageRepository.findById(model.getSubscriptionPackage().getId()).orElseThrow(()-> new NotFoundException("No such subscriptionPackage with " + model.getSubscriptionPackage().getId()));

        entity.setStatus(EntityStatusType.Pending);
        if(subscriptionPackage.getDuration() <= 0)
            entity.setExpireDate(DateUtil.toLocalDateTime(4102444800000L));
        else {
            entity.setExpireDate(LocalDateTime.now().plusDays(subscriptionPackage.getDuration()));
        }
        entity.setFinalPrice(calculatePrice(subscriptionPackage.getPrice(), model.getDiscountPercentage()));
        var user = userRepository.findById(model.getUser().getId()).orElseThrow(()-> new NotFoundException("No such user found with " + model.getUser().getId()));
        entity.setUser(user);
        entity.setRole(user.getRole());

        if(model.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(entity.getUser().getId());
            if(entity.getFinalPrice().compareTo(balance) > 0)
                throw new InsufficentBalanceException();
            deactivateOldActive(entity.getUser().getId());
            entity.setStatus(EntityStatusType.Active);
            addBonus(entity);
        }
        return mapper.toModel(subscriptionRepository.save(entity));
    }
    @Override
    @Transactional
    public SubscriptionModel update(SubscriptionModel model, String key, String allKey){
        var entity = subscriptionRepository.findById(model.getId()).orElseThrow(()-> new NotFoundException("No such subscription with " + model.getId()));
        if(entity.getStatus().equals(EntityStatusType.Active))
            throw new BadRequestException("subscription with Active status could not be update");

        if(model.getDiscountPercentage() != null)
            entity.setDiscountPercentage(model.getDiscountPercentage());
        if (get(()->model.getUser().getId()) != null)
            entity.setUser(userRepository.findById(model.getUser().getId()).orElseThrow(() -> new NotFoundException("No such user found with " + model.getUser().getId())));

        if(!get(()-> model.getSubscriptionPackage().getId()).equals(entity.getSubscriptionPackage().getId())) {//subscription package changed
            var subscriptionPackage = subscriptionPackageRepository.findById(model.getSubscriptionPackage().getId()).orElseThrow(()-> new NotFoundException("No such subscriptionPackage with " + model.getSubscriptionPackage().getId()));
            entity.setSubscriptionPackage(subscriptionPackage);
            if(entity.getSubscriptionPackage().getDuration() <= 0)
                entity.setExpireDate(DateUtil.toLocalDateTime(4102444800000L));
            else {
                entity.setExpireDate(LocalDateTime.now().plusDays(entity.getSubscriptionPackage().getDuration()));//The remaining days of previous subscription will be burned
            }
            BigDecimal finalPrice = calculatePrice(entity.getSubscriptionPackage().getPrice(), model.getDiscountPercentage());
            entity.setFinalPrice(finalPrice);
            entity.setStatus(EntityStatusType.Passive);
        }
        if(model.getStatus().equals(EntityStatusType.Active)) {
            var balance = walletRepository.calculateUserBalance(entity.getUser().getId());
            if(balance.compareTo(BigDecimal.ZERO) <= 0)
                throw new InsufficentBalanceException();
            if(entity.getFinalPrice().compareTo(balance) > 0)
                throw new InsufficentBalanceException();
            deactivateOldActive(entity.getUser().getId());
            entity.setStatus(EntityStatusType.Active);
            addBonus(entity);
        }
        return mapper.toModel(subscriptionRepository.save(entity));
    }

    private BigDecimal calculatePrice(BigDecimal originalPrice, Integer discountPercentage) {
        if(discountPercentage == null)
            return originalPrice;
        var discountAmount = originalPrice.multiply(new BigDecimal(discountPercentage).divide(new BigDecimal("100")));
        return originalPrice.subtract(discountAmount);
    }

    private void deactivateOldActive(UUID userId) {
        var userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user with " + userId));
        var oldActive = subscriptionRepository.findByUserIdAndStatus(userEntity.getId(), EntityStatusType.Active);
        if(oldActive != null) {
            oldActive.setStatus(EntityStatusType.Passive);
            subscriptionRepository.saveAndFlush(oldActive);
        }
        clearCache("Subscription:" + userId.toString());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "client", key = "'Subscription:' + #userId.toString() + ':findByUserAndActivePackage'")
    public SubscriptionModel findByUserAndActivePackage(UUID userId) {
        return mapper.toModel(subscriptionRepository.findByUserIdAndStatus(userId, EntityStatusType.Active));
    }

    @Override
    @Transactional
    public SubscriptionModel purchase(SubscriptionModel model) {
        var balance = walletRepository.calculateUserBalance(model.getUser().getId());
        var subscriptionPackage = subscriptionPackageRepository.findMatchedPackageByAmount(balance);
        if(subscriptionPackage.isEmpty())
            throw new NotFoundException("No subscription package found with your balance " + balance.toString());
        if(!subscriptionPackage.get().getId().equals(model.getSubscriptionPackage().getId()))
            throw new NotAcceptableException("""
        <strong>You can not purchase this subscription package.</strong><br/>
        With your current balance, you can purchase the %s subscription package instead!""".formatted(subscriptionPackage.get().getName()));
        model.setStatus(EntityStatusType.Active);
        return create(model,"Subscription");
    }

    private void addBonus(SubscriptionEntity entity) {
        var subscriptionPackage = subscriptionPackageRepository.findById(entity.getSubscriptionPackage().getId()).orElseThrow(()->new NotFoundException("SubscriptionPackage not found " + entity.getSubscriptionPackage().getId()));
        entity.setSubscriptionPackage(subscriptionPackage);
        if(entity.getSubscriptionPackage().getName().equals("Free")) {
            if(walletRepository.countAllByUserIdAndTransactionTypeAndStatus(entity.getUser().getId(),TransactionType.BONUS,EntityStatusType.Active) == 0) {
                var bonusAmount = new BigDecimal(selfFreeBonusAmount);
                WalletEntity selfWallet = new WalletEntity();
                selfWallet.setStatus(EntityStatusType.Active);
                selfWallet.setAmount(bonusAmount);
                selfWallet.setActualAmount(bonusAmount);
                selfWallet.setUser(entity.getUser());
                selfWallet.setCurrency(CurrencyType.USDT);
                selfWallet.setTransactionType(TransactionType.REWARD);
                selfWallet.setRole(entity.getRole());
                walletRepository.save(selfWallet);
            }
        } else if(entity.getUser().getParent() != null) {
            var parentReferralBonus = entity.getSubscriptionPackage().getParentReferralBonus();
            WalletEntity parentWallet = new WalletEntity();
            parentWallet.setStatus(EntityStatusType.Active);
            parentWallet.setAmount(new BigDecimal(parentReferralBonus));
            parentWallet.setActualAmount(new BigDecimal(parentReferralBonus));
            parentWallet.setUser(entity.getUser().getParent());
            parentWallet.setCurrency(entity.getSubscriptionPackage().getCurrency());
            parentWallet.setTransactionType(TransactionType.BONUS);
            parentWallet.setRole(entity.getRole());
            walletRepository.save(parentWallet);
        }
        clearCache("Wallet:" + entity.getUser().getId().toString());
    }
}
