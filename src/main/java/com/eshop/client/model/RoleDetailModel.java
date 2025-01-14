package com.eshop.client.model;

import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.NetworkType;
import lombok.Data;

import java.util.Objects;

@Data
public class RoleDetailModel extends BaseModel<Long> {
	private RoleModel role;
	private NetworkType network;
	private CurrencyType currency;
	private String address;
	private String description;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		var that = (RoleDetailModel) o;
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
