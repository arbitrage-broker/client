package com.eshop.client.model;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class WalletModel extends BaseModel<Long> {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal actualAmount;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String transactionHash;
    @NotNull
    private CurrencyType currency = CurrencyType.USDT;
    private NetworkType network = NetworkType.TRC20;
    private UserModel user;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String address;
    private EntityStatusType status = EntityStatusType.Pending;
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (WalletModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
