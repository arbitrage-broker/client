package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.RoleDetailEntity;
import com.arbitragebroker.client.enums.CurrencyType;
import com.arbitragebroker.client.enums.NetworkType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDetailRepository extends BaseRepository<RoleDetailEntity, Long> {
    Optional<RoleDetailEntity> findByRoleRoleAndNetworkAndCurrency(String role, NetworkType networkType, CurrencyType currencyType);
}
