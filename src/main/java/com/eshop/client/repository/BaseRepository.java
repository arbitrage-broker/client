package com.eshop.client.repository;

import com.eshop.client.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>,ID extends Serializable> extends JpaRepository<T, ID>,QuerydslPredicateExecutor<T> {
}
