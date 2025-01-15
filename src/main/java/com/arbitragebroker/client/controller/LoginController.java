package com.arbitragebroker.client.controller;


import com.arbitragebroker.client.config.Limited;
import com.arbitragebroker.client.config.MessageConfig;
import com.arbitragebroker.client.enums.RoleType;
import com.arbitragebroker.client.filter.SubscriptionPackageFilter;
import com.arbitragebroker.client.model.*;
import com.arbitragebroker.client.service.*;
import com.arbitragebroker.client.service.impl.BaseMailService;
import com.arbitragebroker.client.util.SessionHolder;
import com.arbitragebroker.client.exception.NotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import static com.arbitragebroker.client.util.StringUtils.generateFilterKey;
import static com.arbitragebroker.client.util.StringUtils.generateIdKey;


@Slf4j
@Controller
public class LoginController {

    final MessageConfig messages;
    final SessionHolder sessionHolder;
    final HttpServletRequest request;
    final UserService userService;
    final BaseMailService mailService;
    final OneTimePasswordService oneTimePasswordService;
    final NotificationService notificationService;
    final ArbitrageService arbitrageService;
    final SubscriptionPackageService subscriptionPackageService;
    final SubscriptionService subscriptionService;
    final CoinService coinService;
    final ExchangeService exchangeService;
    private final WalletService walletService;
    private final String totalUsers;
    private final List<ParameterModel> referralRewards;

    public LoginController(MessageConfig messages, SessionHolder sessionHolder, HttpServletRequest request, UserService userService,
                           BaseMailService mailService, OneTimePasswordService oneTimePasswordService, NotificationService notificationService,
                           ArbitrageService arbitrageService, SubscriptionPackageService subscriptionPackageService,
                           SubscriptionService subscriptionService, CoinService coinService, ExchangeService exchangeService,
                           WalletService walletService, ParameterService parameterService) {
        this.messages = messages;
        this.sessionHolder = sessionHolder;
        this.request = request;
        this.userService = userService;
        this.mailService = mailService;
        this.oneTimePasswordService = oneTimePasswordService;
        this.notificationService = notificationService;
        this.arbitrageService = arbitrageService;
        this.subscriptionPackageService = subscriptionPackageService;
        this.subscriptionService = subscriptionService;
        this.coinService = coinService;
        this.exchangeService = exchangeService;
        this.walletService = walletService;
        this.totalUsers = parameterService.findValueByCodeOrDefault("TOTAL_USERS", "2.5M");
        this.referralRewards = parameterService.findAllByParameterGroupCode("REFERRAL_REWARD");
    }

    @SneakyThrows
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ModelAndView loadPage(@PathVariable String name) {
        if (name == null || name.isEmpty() || name.equals("favicon.ico"))
            return new ModelAndView("dashboard");

        UserModel user = sessionHolder.getCurrentUser();
        ModelAndView modelAndView = new ModelAndView(name);

        if(name.equals("index")) {
            var filter = new SubscriptionPackageFilter();
            filter.setActive(true);
            PageRequest pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("price")));
            var subscriptionPackages = subscriptionPackageService.findAll(filter, pageable,generateFilterKey("SubscriptionPackage","findAll",filter, pageable));
            modelAndView.addObject("subscriptionPackages", subscriptionPackages.getContent());
        }
        if(user == null)
            return modelAndView;

        modelAndView.addObject("currentUser", sessionHolder.getCurrentUserAsJsonString());
        String cacheKey = generateFilterKey("Notification","findAllByRecipientIdAndNotRead",user.getId(),PageRequest.of(0,10));
        modelAndView.addObject("notifications", notificationService.findAllByRecipientIdAndNotRead(user.getId(), PageRequest.of(0,10),cacheKey));
        modelAndView.addObject("fullName", user.getFirstName() + " " + user.getLastName());
        modelAndView.addObject("pageTitle", messages.getMessage(name));
        modelAndView.addObject("errorMsg", null);
        request.getParameterMap().forEach((key, value) -> {
            modelAndView.addObject(key, value[0]);
        });

        if (name.equals("dashboard")) {
            modelAndView.addObject("totalUsers", totalUsers);
        }
        else if(name.equals("referral-reward")) {
            modelAndView.addObject("referralRewards", referralRewards);
            Integer countAllActiveChild = userService.countAllActiveChild(user.getId());
            Integer claimedReferralsCount = walletService.getClaimedReferrals(user.getId());
            modelAndView.addObject("countActiveChild", countAllActiveChild);
            modelAndView.addObject("claimedReferralsCount", claimedReferralsCount);
            modelAndView.addObject("remainedReferralsCount", countAllActiveChild - claimedReferralsCount);
        }
        else if(name.equals("arbitrage")) {
            var limit = arbitrageService.purchaseLimit(user.getId());
            var filter = new SubscriptionPackageFilter();
            filter.setActive(true);
            PageRequest pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("price")));
            var subscriptionPackages = subscriptionPackageService.findAll(filter, pageable,generateFilterKey("SubscriptionPackage","findAll",filter, pageable));
            var subscription = subscriptionService.findByUserAndActivePackage(user.getId());

            modelAndView.addObject("purchaseLimit", limit);
            modelAndView.addObject("selectedSubscriptionPackagePrice", subscription == null ? null : subscription.getSubscriptionPackage().getPrice());
            modelAndView.addObject("selectedSubscriptionPackageName", subscription == null ? null : subscription.getSubscriptionPackage().getName());
            modelAndView.addObject("selectedSubscriptionId", subscription == null ? null : subscription.getId());
            modelAndView.addObject("selectedSubscription", subscription == null ? null : sessionHolder.getObjectMapper().writeValueAsString(subscription));
            modelAndView.addObject("subscriptionPackages", subscriptionPackages.getContent());
            if(limit == null && subscription!=null && subscription.getSubscriptionPackage().getOrderCount()>0) {
                var coins = coinService.findAllByRandom(subscription.getSubscriptionPackage().getOrderCount());
                var exchanges = exchangeService.findAllByRandom(subscription.getSubscriptionPackage().getOrderCount()*2);

                var result = new ArrayList<CoinExchangeModel>();
                int exchangeIndex = 0;
                for (int i = 0; i < coins.size(); i++) {
                    var selectedExchanges = new ArrayList<ExchangeModel>();

                    // Pick the next 2 exchanges (if available)
                    for (int j = 0; j < 2 && exchangeIndex < exchanges.size(); j++) {
                        selectedExchanges.add(exchanges.get(exchangeIndex++));
                    }

                    // Create the CoinExchangeModel
                    var model = new CoinExchangeModel();
                    model.setCoin(coins.get(i));
                    model.setIndex(i + 1);
                    model.setExchanges(selectedExchanges);
                    result.add(model);
                }
                modelAndView.addObject("coinExchanges", result);
            }
        }
        return modelAndView;
    }

    @GetMapping(value = {"/"})
    public String index() {
        var requestWrapper = sessionHolder.getRequestWrapper();
        String targetUrl = "/index";
        if (requestWrapper.isUserInRole(RoleType.ADMIN) || requestWrapper.isUserInRole(RoleType.SUPER_WISER)|| requestWrapper.isUserInRole(RoleType.MANAGER) || requestWrapper.isUserInRole(RoleType.USER))
            targetUrl = "redirect:/dashboard";
        return targetUrl;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        request.getParameterMap().forEach((key, value) -> {
            modelAndView.addObject(key, value[0]);
        });
        return modelAndView;
    }

    @PostMapping("/registration")
    @Limited(requestsPerMinutes = 3)
    public String register(@Valid @ModelAttribute("user") UserModel user) {
        userService.register(user);
        return "redirect:/login#signin";
    }

    @PostMapping("/send-OTP")
    @Limited(requestsPerMinutes = 3)
    public String sendOTP(@Email @NotEmpty String email) {
        try {
            mailService.sendOTP(email,"Reset password OTP");
        }
        catch (NotFoundException e){
            return "redirect:/login?errorMsg=emailNotFound#send_otp";
        } catch(Exception e) {
            return "redirect:/login?errorMsg=failedToSendEmail#send_otp";
        }
        return "redirect:/login#reset_pass";
    }

    @PostMapping("/reset-pass")
    @Limited(requestsPerMinutes = 3)
    public String changePassword(@Valid @ModelAttribute("resetPassModel") ResetPassModel resetPassModel) {
        var user = userService.findByUserNameOrEmail(resetPassModel.getLogin());
        var verify = oneTimePasswordService.verify(user.getId(), resetPassModel.getOtp());
        if (verify) {
            var newUser = new UserModel().setUserId(user.getId());
            newUser.setPassword(resetPassModel.getNewPassword());
            newUser.setEmailVerified(true);
            userService.update(newUser, generateIdKey("User",user.getUid().toString()),"User:*");
            return "redirect:/login#signin";
        }
        return "redirect:/login?errorMsg=invalidOTPCode#reset_pass";
    }
}
