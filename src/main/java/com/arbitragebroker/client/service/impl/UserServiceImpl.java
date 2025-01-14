package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.model.*;
import com.arbitragebroker.client.service.*;
import com.arbitragebroker.client.config.Limited;
import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.entity.QUserEntity;
import com.arbitragebroker.client.entity.UserEntity;
import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.filter.UserFilter;
import com.arbitragebroker.client.mapping.UserMapper;
import com.arbitragebroker.client.repository.*;
import com.arbitragebroker.client.util.ReferralCodeGenerator;
import com.arbitragebroker.client.util.SessionHolder;
import com.arbitragebroker.client.exception.BadRequestException;
import com.arbitragebroker.client.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.arbitragebroker.client.util.MapperHelper.get;
import static com.arbitragebroker.client.util.MapperHelper.getOrDefault;
import static com.arbitragebroker.client.util.StringUtils.generateIdKey;


@Service
public class UserServiceImpl extends BaseServiceImpl<UserFilter,UserModel, UserEntity, UUID> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessageConfig messages;
    private final ResourceLoader resourceLoader;
    private final NotificationService notificationService;
    private final WalletRepository walletRepository;
    private final OneTimePasswordService oneTimePasswordService;
    private final SessionHolder sessionHolder;
    private final SubscriptionService subscriptionService;
    private final TelegramService telegramService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MessageConfig messages, ResourceLoader resourceLoader, NotificationService notificationService, CountryRepository countryRepository, WalletRepository walletRepository, OneTimePasswordService oneTimePasswordService, SessionHolder sessionHolder, SubscriptionService subscriptionService, TelegramService telegramService) {
        super(userRepository, userMapper);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.messages = messages;
        this.resourceLoader = resourceLoader;
        this.notificationService = notificationService;
        this.walletRepository = walletRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.sessionHolder = sessionHolder;
        this.subscriptionService = subscriptionService;
        this.telegramService = telegramService;
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'User:' + #login + ':findByUserNameOrEmail'")
    @Limited(requestsPerMinutes = 3)
    public UserModel findByUserNameOrEmail(String login) {
        var entity = userRepository.findByUserNameOrEmail(login, login).orElseThrow(() -> new NotFoundException("User not found with username/email: " + login));
        var model = mapper.toModel(entity);
        model.setPassword(entity.getPassword());
        return model;
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'User:' + #userName + ':findByUserName'")
    public UserModel findByUserName(String userName) {
        return mapper.toModel(userRepository.findByUserName(userName).orElseThrow(() -> new NotFoundException("username: " + userName)));
    }
    @Override
    @Cacheable(cacheNames = "client", key = "'User:' + #email + ':findByEmail'")
    public UserModel findByEmail(String email) {
        return mapper.toModel(userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("email: " + email)));
    }
    @Override
    @Cacheable(cacheNames = "client", key = "'User:findAllUserCountByCountry'")
    public List<CountryUsers> findAllUserCountByCountry() {
        return userRepository.findAllUserCountByCountry();
    }

    @Override
    public boolean verifyEmail(UUID id, String otp) {
        boolean verify = oneTimePasswordService.verify(id, otp);
        if(verify) {
            userRepository.findById(id).ifPresent(user -> {
                user.setEmailVerified(true);
                userRepository.save(user);
                clearCache(String.format("User:%s:findByUserName",user.getUserName()));
            });
        }
        return verify;
    }

    @Override
    @Cacheable(cacheNames = "client", key = "'User:' + #id.toString() + ':countAllActiveChild'")
    public Integer countAllActiveChild(UUID id) {
        return userRepository.countActiveChildrenByUserId(id);
    }

    @Override
    public Page<UserModel> findAll(UserFilter filter, Pageable pageable, String key) {
        return super.findAll(filter, pageable, key).map(m->{
            m.setDeposit(walletRepository.totalDeposit(m.getId()));
            m.setWithdrawal(walletRepository.totalWithdrawal(m.getId()));
            m.setReward(walletRepository.totalProfit(m.getId()));
            return m;
        });
    }

    @Override
    public PageModel<UserModel> findAllTable(UserFilter filter, Pageable pageable,String key) {
        var data = super.findAllTable(filter, pageable,key);
        if(!CollectionUtils.isEmpty(data.getData())) {
            for (UserModel m : data.getData()) {
                m.setDeposit(walletRepository.totalDeposit(m.getId()));
                m.setWithdrawal(walletRepository.totalWithdrawal(m.getId()));
                m.setReward(walletRepository.totalProfit(m.getId()));
            }
        }
        return data;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "client", key = "'User:*'")
    public UserModel register(UserModel model) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserName(model.getUserName());
        if (optionalUserEntity.isPresent())
            throw new BadRequestException("userName is already taken!");

        var entity = mapper.toEntity(model);
        if (StringUtils.hasLength(model.getPassword()))
            entity.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
        else
            entity.setPassword(bCryptPasswordEncoder.encode("12345"));
        entity.setActive(true);
        var role = roleRepository.findByRole(RoleType.USER).get();
        entity.setRoles(new HashSet<>(Arrays.asList(role)));
        if (StringUtils.hasLength(model.getReferralCode())) {
            userRepository.findByUid(model.getReferralCode()).ifPresentOrElse(p -> {
                entity.setParent(p);
                entity.setRole(p.getRole());
            },() -> {throw new NotFoundException("No such user found with referral code <strong>%s</strong>.".formatted(model.getReferralCode()));});
        } else {
          throw new BadRequestException("referralCode must be provided.");
        }
        entity.setUid(getUid());
        var createdUser = mapper.toModel(repository.save(entity));
        sessionHolder.setCurrentUser(createdUser);
        subscriptionService.create(new SubscriptionModel()
                .setUser(createdUser)
                .setSubscriptionPackage(new SubscriptionPackageModel().setSubscriptionPackageId(6L))
                .setStatus(EntityStatusType.Active),"Subscription");
        sendWelcomeNotification(createdUser.getId());
        telegramService.sendToRole(entity.getRole(), """
                *New User Registered*\n
                Username: %s\n
                FirstName: %s\n
                LastName: %s\n
                Email: %s\n
                Parent: %s\n
                """.formatted(entity.getUserName(),entity.getFirstName(),entity.getLastName(),entity.getEmail(),getOrDefault(()->entity.getParent().getSelectTitle(),"---")));
        return createdUser;
    }
    private String getUid() {
        var uid = ReferralCodeGenerator.generateReferralCode();
        if(!userRepository.existsByUid(uid)) {
            return uid;
        }
        return getUid();
    }

    @Override
    @Transactional
    public UserModel update(UserModel model, String key, String allKey) {
        var entity = repository.findById(model.getId()).orElseThrow(() -> new NotFoundException("id: " + model.getId()));

        if(StringUtils.hasLength(model.getEmail()) && !entity.getEmail().equals(model.getEmail())) {
            model.setEmailVerified(false);
            entity.setEmailVerified(false);
        }
        if (StringUtils.hasLength(model.getPassword())) {
            model.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
            entity.setPassword(model.getPassword());
        }

        mapper.updateEntity(model, entity);

        if(get(()->model.getCountry().getId())!=null)
            entity.setCountry(entityManager.getReference(entity.getCountry().getClass(), model.getCountry().getId()));
        if(get(()->model.getParent().getId())!=null)
            entity.setParent(entityManager.getReference(entity.getParent().getClass(), model.getParent().getId()));

        return mapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public UserModel create(UserModel model, String allKey) {
        var entity = mapper.toEntity(model);

        if (StringUtils.hasLength(model.getPassword()))
            entity.setPassword(bCryptPasswordEncoder.encode(model.getPassword()));
        else
            entity.setPassword(bCryptPasswordEncoder.encode("12345"));

        entity.setUid(getUid());
        var role = roleRepository.findByRole(RoleType.USER).get();
        entity.setRoles(Set.of(role));
        entity.setRole(role.getRole());
        var createdUser = mapper.toModel(repository.save(entity));
        sendWelcomeNotification(createdUser.getId());
        return createdUser;
    }

    @Override
    public Predicate queryBuilder(UserFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();
        QUserEntity path = QUserEntity.userEntity;

        if(!RoleType.hasRole(RoleType.ADMIN)) {
            builder.and(path.roles.any().role.ne(RoleType.ADMIN));
        }
        filter.getId().ifPresent(v -> builder.and(path.id.eq(v)));
        filter.getTitle().ifPresent(v -> builder.and(path.firstName.toLowerCase().contains(v.toLowerCase())).or(path.lastName.toLowerCase().contains(v.toLowerCase())));
        filter.getActive().ifPresent(v -> builder.and(path.active.eq(v)));
        filter.getUserName().ifPresent(v -> builder.and(path.userName.eq(v)));
        filter.getEmail().ifPresent(v -> builder.and(path.email.toLowerCase().eq(v.toLowerCase())));
        filter.getUid().ifPresent(v -> builder.and(path.uid.eq(v)));
        filter.getParentId().ifPresent(v -> builder.and(path.parent.id.eq(v)));
        filter.getTreePath().ifPresent(v -> {
            if(v.contains("%"))
                builder.and(Expressions.booleanTemplate("tree_path like {0}", v));
            else builder.and(path.treePath.contains(v));
        });
        filter.getWalletAddress().ifPresent(v -> builder.and(path.walletAddress.eq(v)));
        filter.getFirstName().ifPresent(v -> builder.and(path.firstName.toLowerCase().contains(v.toLowerCase())));
        filter.getLastName().ifPresent(v -> builder.and(path.lastName.toLowerCase().contains(v.toLowerCase())));
        filter.getHasParent().ifPresent(v-> { if(v) builder.and(path.parent.isNotNull()); else builder.and(path.parent.isNull());});
        filter.getProfileImageUrl().ifPresent(v -> builder.and(path.profileImageUrl.eq(v)));
        filter.getCountryId().ifPresent(v -> builder.and(path.country.id.eq(v)));
        filter.getEmailVerified().ifPresent(v-> builder.and(path.emailVerified.eq(v)));
       
        return builder;
    }
    @SneakyThrows
    public void sendWelcomeNotification(UUID recipientId) {
        var recipient = findById(recipientId,generateIdKey("User",recipientId));
        String siteName = messages.getMessage("siteName");
        String siteUrl = messages.getMessage("siteUrl");

        // Load the email template as a stream
        Resource emailTemplateResource = resourceLoader.getResource("classpath:templates/welcome-email.html");
        String emailContent;
        try (InputStream inputStream = emailTemplateResource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            emailContent = scanner.useDelimiter("\\A").next(); // Read the entire file into a String
        }

        // Replace placeholders with actual values
        emailContent = emailContent.replace("[user_first_name]", recipient.getFirstName())
                .replace("[YourAppName]", siteName)
                .replace("[YourSiteUrl]", siteUrl);

        // Send the notification
        notificationService.create(new NotificationModel()
                .setSubject(String.format("Welcome to %s!", siteName))
                .setBody(emailContent)
                .setSender(new UserModel().setUserId(UUID.fromString("6303b84a-04cf-49e1-8416-632ebd84495e")))
                .setRecipient(new UserModel().setUserId(recipientId)),"User:*");
    }
}
