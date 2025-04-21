package com.sunnyhsu.springboottravelproject.service;

import com.sunnyhsu.springboottravelproject.dto.UserLoginRequest;
import com.sunnyhsu.springboottravelproject.dto.UserRegisterRequest;
import com.sunnyhsu.springboottravelproject.model.User;

public interface UserService {

    User getUserById(Integer userId);

    User getUserByEmail(String email);

    Integer register(UserRegisterRequest userRegisterRequest);

    User login(UserLoginRequest userLoginRequest);

    void updateUserAvatar(Integer userId, String avatarPath);

    void upsertUserForOAuth(String email, String name, String avatar);

}
