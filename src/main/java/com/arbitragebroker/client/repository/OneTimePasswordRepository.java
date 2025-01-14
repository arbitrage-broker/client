package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.OneTimePasswordEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OneTimePasswordRepository extends BaseRepository<OneTimePasswordEntity, Long> {
    Optional<OneTimePasswordEntity> findByUserIdAndPasswordAndConsumedFalse(UUID userId, String password);
}
