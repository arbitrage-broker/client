package com.eshop.client.entity;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.EntityStatusType;
import com.eshop.client.enums.NetworkType;
import com.eshop.client.enums.TransactionType;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "tbl_wallet")
@Audited
public class WalletEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_wallet", sequenceName="seq_wallet",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_wallet")
	private Long id;
	@NotNull
	private BigDecimal amount;
	@Column(name = "actual_amount", nullable = false)
	private BigDecimal actualAmount;
	@Enumerated(value = EnumType.STRING)
	private CurrencyType currency = CurrencyType.USDT;
	@Enumerated(value = EnumType.STRING)
	private NetworkType network = NetworkType.TRC20;
	@Column(name = "transaction_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private TransactionType transactionType;
	@Column(name = "transaction_hash", unique = true)
	private String transactionHash;
	@Column(name = "address", nullable = false)
	private String address;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Enumerated(value = EnumType.STRING)
	private EntityStatusType status;

	private String role;

	@Override
	public String getSelectTitle() {
		if(transactionType == null) return null;
		StringBuilder builder = new StringBuilder();
		if (transactionType.equals(TransactionType.WITHDRAWAL))
			builder.append("-");
		else builder.append("+");
		return builder.append(amount).append(" ").append(currency).append(" ").append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(modifiedDate)).toString();
	}
}
