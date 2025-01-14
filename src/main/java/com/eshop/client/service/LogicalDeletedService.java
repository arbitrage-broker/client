package com.eshop.client.service;

import com.eshop.client.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eshop.client.entity.LogicalDeleted;

import java.io.Serializable;

public interface LogicalDeletedService<ID extends Serializable> {
    <E extends LogicalDeleted> JpaRepository<E, ID> getRepository();

    default void logicalDeleteById(ID id) {
        var entity = getRepository().findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        entity.setDeleted(true);
        getRepository().save(entity);
    }
}