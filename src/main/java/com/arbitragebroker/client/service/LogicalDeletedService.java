package com.arbitragebroker.client.service;

import com.arbitragebroker.client.entity.LogicalDeleted;
import com.arbitragebroker.client.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface LogicalDeletedService<ID extends Serializable> {
    <E extends LogicalDeleted> JpaRepository<E, ID> getRepository();

    default void logicalDeleteById(ID id) {
        var entity = getRepository().findById(id).orElseThrow(() -> new NotFoundException("id: " + id));
        entity.setDeleted(true);
        getRepository().save(entity);
    }
}