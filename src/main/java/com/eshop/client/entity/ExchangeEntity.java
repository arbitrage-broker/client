package com.eshop.client.entity;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "tbl_exchange")
@Audited
public class ExchangeEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_exchange",sequenceName="seq_exchange",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exchange")
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
