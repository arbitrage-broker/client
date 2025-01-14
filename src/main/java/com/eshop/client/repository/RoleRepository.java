package com.eshop.client.repository;

import org.springframework.stereotype.Repository;
import com.eshop.client.entity.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(String role);
}
