package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.ParameterGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterGroupRepository extends BaseRepository<ParameterGroupEntity, Long> {
	ParameterGroupEntity findByCode(String role);
}
