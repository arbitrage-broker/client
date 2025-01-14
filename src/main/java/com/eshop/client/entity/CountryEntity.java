package com.eshop.client.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_country")
public class CountryEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="seq_country",sequenceName="seq_country",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_country")
	private Long id;
	private String name;
	private String iso3;
	private String iso2;
	@Column(name = "numeric_code")
	private String numericCode;
	@Column(name = "phone_code")
	private String phoneCode;
	private String capital;
	private String currency;
	@Column(name = "currency_symbol")
	private String currencySymbol;
	private String tld;
	@Column(name = "native_name")
	private String nativeName;
	private String region;
	private String subregion;
	private String latitude;
	private String longitude;
	private String emoji;
	private String emojiu;

	@Override
	public String getSelectTitle() {
		return name;
	}

	public CountryEntity setId_(Long id) {
		this.id = id;
		return this;
	}
}
