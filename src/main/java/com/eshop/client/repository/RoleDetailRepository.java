package com.eshop.client.repository;

import com.eshop.client.entity.RoleDetailEntity;
import com.eshop.client.enums.CurrencyType;
import com.eshop.client.enums.NetworkType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDetailRepository extends BaseRepository<RoleDetailEntity, Long> {
    Optional<RoleDetailEntity> findByRoleRoleAndNetworkAndCurrency(String role, NetworkType networkType, CurrencyType currencyType);
}
