package com.eshop.client.service;

import com.eshop.client.filter.UserFilter;
import com.eshop.client.model.UserModel;
import com.eshop.client.repository.CountryUsers;

import java.util.List;
import java.util.UUID;

public interface UserService extends BaseService<UserFilter, UserModel, UUID> {
    UserModel findByUserNameOrEmail(String login);
    UserModel findByUserName(String userName);
    UserModel findByEmail(String email);
    UserModel register(UserModel model);
    List<CountryUsers> findAllUserCountByCountry();
    boolean verifyEmail(UUID id, String otp);
    Integer countAllActiveChild(UUID id);
}
