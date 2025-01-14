package com.eshop.client.repository;

import com.eshop.client.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends BaseRepository<ExchangeEntity, Long> {
    @Query(value = "SELECT * FROM tbl_exchange ORDER BY RANDOM() LIMIT 2", nativeQuery = true)
    List<ExchangeEntity> findByRandom();
    @Query(value = "SELECT * FROM tbl_exchange ORDER BY RANDOM() LIMIT ?", nativeQuery = true)
    List<ExchangeEntity> findAllByRandom(int count);
}
