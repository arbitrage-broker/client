package com.eshop.client.repository;

import com.eshop.client.entity.CoinEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends BaseRepository<CoinEntity, Long> {
    @Query(value = "SELECT * FROM tbl_coin ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    CoinEntity findByRandom();
    @Query(value = "SELECT * FROM tbl_coin ORDER BY RANDOM() LIMIT ?", nativeQuery = true)
    List<CoinEntity> findAllByRandom(int count);
}
