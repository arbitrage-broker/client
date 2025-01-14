package com.eshop.client.controller.impl;

import com.eshop.client.enums.TransactionType;
import com.eshop.client.filter.WalletFilter;
import com.eshop.client.model.WalletModel;
import com.eshop.client.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "Wallet Rest Service v1")
@RequestMapping(value = "/api/v1/wallet")
public class WalletRestController extends BaseRestControllerImpl<WalletFilter, WalletModel, Long> {

    private WalletService walletService;

    public WalletRestController(WalletService service) {
        super(service);
        this.walletService = service;
    }
    @GetMapping("/total-balance/{userId}")
    public ResponseEntity<BigDecimal> totalBalance(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalBalanceByUserId(userId));
    }
    @GetMapping("/total-deposit/{userId}")
    public ResponseEntity<BigDecimal> totalDeposit(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalDeposit(userId));
    }
    @GetMapping("/total-withdrawal/{userId}")
    public ResponseEntity<BigDecimal> totalWithdrawal(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalWithdrawal(userId));
    }
    @GetMapping("/total-bonus/{userId}")
    public ResponseEntity<BigDecimal> totalBonus(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalBonus(userId));
    }
    @GetMapping("/total-reward/{userId}")
    public ResponseEntity<BigDecimal> totalReward(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalReward(userId));
    }
    @GetMapping("/total-profit/{userId}")
    public ResponseEntity<BigDecimal> totalProfit(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.totalProfit(userId));
    }
    @GetMapping("/daily-profit/{userId}")
    public ResponseEntity<BigDecimal> dailyProfit(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.dailyProfit(userId));
    }
    @GetMapping("/get-date-range/{startDate}/{endDate}/{transactionType}")
    public ResponseEntity<Map<Long, BigDecimal>> findAllWithinDateRange(@PathVariable long startDate, @PathVariable long endDate, @PathVariable TransactionType transactionType){
        return ResponseEntity.ok(walletService.findAllWithinDateRange(startDate,endDate,transactionType));
    }
    @GetMapping("/allowed-withdrawal-balance/{userId}/{transactionType}")
    public ResponseEntity<BigDecimal> allowedWithdrawalBalance(@PathVariable UUID userId, @PathVariable TransactionType transactionType){
        return ResponseEntity.ok(walletService.allowedWithdrawalBalance(userId,transactionType));
    }
    @PostMapping("/claim-referral-reward/{userId}/{count}")
    public ResponseEntity<WalletModel> allowedWithdrawalBalance(@PathVariable UUID userId, @PathVariable Integer count){
        return ResponseEntity.ok(walletService.claimReferralReward(userId,count));
    }
    @GetMapping("/get-claimed-referrals/{userId}")
    public ResponseEntity<Integer> getClaimedReferrals(@PathVariable UUID userId){
        return ResponseEntity.ok(walletService.getClaimedReferrals(userId));
    }
}
