package com.eshop.client.repository;

import com.eshop.client.entity.CountryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends BaseRepository<CountryEntity, Long> {
}
