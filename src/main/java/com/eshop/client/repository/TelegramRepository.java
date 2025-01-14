package com.eshop.client.repository;

import com.eshop.client.entity.TelegramEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramRepository extends BaseRepository<TelegramEntity, Long> {
    boolean existsByChatId(String chatId);
    boolean existsByPhone(String phone);
    List<TelegramEntity> findAllByRole(String role);
}
