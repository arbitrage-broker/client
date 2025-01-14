package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.SubscriptionPackageEntity;
import com.arbitragebroker.client.enums.CurrencyType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SubscriptionPackageRepository extends BaseRepository<SubscriptionPackageEntity, Long> {
    @Query("SELECT sp FROM SubscriptionPackageEntity sp WHERE :amount BETWEEN sp.price AND sp.maxPrice")
    Optional<SubscriptionPackageEntity> findMatchedPackageByAmount(BigDecimal amount);
    SubscriptionPackageEntity findTopByOrderByMaxPriceDesc();
}
