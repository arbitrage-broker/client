package com.eshop.client.repository;

import com.eshop.client.entity.ParameterGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterGroupRepository extends BaseRepository<ParameterGroupEntity, Long> {
	ParameterGroupEntity findByCode(String role);
}
