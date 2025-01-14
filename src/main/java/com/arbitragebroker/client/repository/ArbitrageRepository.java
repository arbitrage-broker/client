package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.ArbitrageEntity;
import com.arbitragebroker.client.model.CoinUsageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArbitrageRepository extends BaseRepository<ArbitrageEntity, Long> {
    long countAllByUserId(UUID userId);
    @Query("select a from ArbitrageEntity a where a.user.id=:userId and a.subscription.id=:subscriptionId and DATE(a.createdDate) =:createdDate order by a.createdDate desc")
    List<ArbitrageEntity> findByUserIdAndSubscriptionIdAndCreatedDateOrderByCreatedDateDesc(UUID userId, Long subscriptionId, LocalDateTime createdDate);

    @Query("SELECT NEW com.arbitragebroker.client.model.CoinUsageDTO(" +
            "a.coin.name, " +
            "COUNT(a.coin.name)) " +
            "FROM ArbitrageEntity a " +
            "WHERE a.createdDate >= :date " +
            "GROUP BY a.coin.name " +
            "ORDER BY COUNT(a.coin.name) DESC")
    Page<CoinUsageDTO> findMostUsedCoins(LocalDateTime date, Pageable pageable);

    @Query("SELECT COUNT(a) FROM ArbitrageEntity a WHERE a.createdDate >= :date ")
    long countSince(LocalDateTime date);
}
