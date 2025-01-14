package com.eshop.client.entity;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "tbl_coin")
@Audited
public class CoinEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_coin",sequenceName="seq_coin",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_coin")
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private String logo;

	@Override
	public String getSelectTitle() {
		return name;
	}
}
