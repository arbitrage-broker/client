package com.eshop.client.entity;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.NetworkType;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_role_detail", uniqueConstraints = {@UniqueConstraint(columnNames = {"role_id","network", "currency"})})
@Audited
public class RoleDetailEntity extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="seq_role_detail",sequenceName="seq_role_detail",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role_detail")
	public Long id;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleEntity role;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private NetworkType network;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private CurrencyType currency;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String description;

	@Override
	public String getSelectTitle() {
		return String.format("%s-%s",network.name(),currency.name());
	}


}
