package com.arbitragebroker.client.repository;

import org.springframework.stereotype.Repository;
import com.arbitragebroker.client.entity.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(String role);
}
