package com.eshop.client.entity;

import com.eshop.client.enums.CurrencyType;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.eshop.client.util.MapperHelper.getOrDefault;

@Data
@Entity
@Table(name = "tbl_arbitrage")
public class ArbitrageEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_arbitrage", sequenceName="seq_arbitrage",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_arbitrage")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeEntity exchange;
	@ManyToOne
	@JoinColumn(name = "coin_id")
	private CoinEntity coin;
	@ManyToOne
	@JoinColumn(name = "subscription_id")
	private SubscriptionEntity subscription;
	private BigDecimal reward;
	@Enumerated(value = EnumType.STRING)
	private CurrencyType currency;

	@Override
	public String getSelectTitle() {
		return getOrDefault(()->user.getSelectTitle(),"").concat(" ").concat(getOrDefault(()->coin.getName(),"")).concat(" ").concat(getOrDefault(()->exchange.getName(),""));
	}
}
