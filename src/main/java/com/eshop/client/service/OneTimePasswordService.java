package com.eshop.client.service;

import java.util.UUID;

public interface OneTimePasswordService  extends LogicalDeletedService<Long>{
    String create(UUID userId);
    boolean verify(UUID userId, String password);
}
