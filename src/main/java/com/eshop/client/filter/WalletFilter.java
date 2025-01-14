package com.eshop.client.filter;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.enums.TransactionType;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Setter
@ToString
@Accessors(chain = true)
public class WalletFilter {
    private Long id;
    private BigDecimal amount;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal actualAmount;
    private BigDecimal actualAmountFrom;
    private BigDecimal actualAmountTo;
    private TransactionType transactionType;
    private List<TransactionType> transactionTypes;
    private String transactionHash;
    private CurrencyType currency;
    private NetworkType network;
    private UUID userId;
    private EntityStatusType status;
    private String address;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }
    public Optional<BigDecimal> getAmount() {
        return Optional.ofNullable(amount);
    }
    public Optional<BigDecimal> getAmountFrom() {
        return Optional.ofNullable(amountFrom);
    }
    public Optional<BigDecimal> getAmountTo() {
        return Optional.ofNullable(amountTo);
    }
    public Optional<BigDecimal> getActualAmount() {
        return Optional.ofNullable(actualAmount);
    }
    public Optional<BigDecimal> getActualAmountFrom() {
        return Optional.ofNullable(actualAmountFrom);
    }
    public Optional<BigDecimal> getActualAmountTo() {
        return Optional.ofNullable(actualAmountTo);
    }
    public Optional<TransactionType> getTransactionType() {
        return Optional.ofNullable(transactionType);
    }

    public Optional<String> getTransactionHash() {
        if (transactionHash == null || transactionHash.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(transactionHash);
    }

    public Optional<List<TransactionType>> getTransactionTypes() {
        return Optional.ofNullable(transactionTypes);
    }

    public Optional<CurrencyType> getCurrency() {
        return Optional.ofNullable(currency);
    }
    public Optional<NetworkType> getNetwork() {
        return Optional.ofNullable(network);
    }
    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }

    public Optional<EntityStatusType> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<String> getAddress() {
        if (address == null || address.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(address);
    }
}
