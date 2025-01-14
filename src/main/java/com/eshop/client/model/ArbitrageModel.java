package com.eshop.client.model;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.validation.Create;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class ArbitrageModel extends BaseModel<Long> {
    @NotNull(groups = Create.class)
    private UserModel user;
    @NotNull(groups = Create.class)
    private ExchangeModel exchange;
    @NotNull(groups = Create.class)
    private CoinModel coin;
    @NotNull(groups = Create.class)
    private SubscriptionModel subscription;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal reward;
    private CurrencyType currency;
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ArbitrageModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
