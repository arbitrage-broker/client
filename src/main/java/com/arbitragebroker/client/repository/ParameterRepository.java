package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.ParameterEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParameterRepository extends BaseRepository<ParameterEntity, Long> {
	Optional<ParameterEntity> findByCode(String code);
}
