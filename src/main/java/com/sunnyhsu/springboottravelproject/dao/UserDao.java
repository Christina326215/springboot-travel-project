package com.sunnyhsu.springboottravelproject.dao;

import com.sunnyhsu.springboottravelproject.dto.UserRegisterRequest;
import com.sunnyhsu.springboottravelproject.model.Role;
import com.sunnyhsu.springboottravelproject.model.User;

import java.util.List;

public interface UserDao {

    User getUserById(Integer userId);

    User getUserByEmail(String email);

    Integer createUser(UserRegisterRequest userRegisterRequest);

    void updateUserAvatar(Integer userId, String avatarPath);

    List<Role> getRolesByMemberId(Integer memberId);

    void upsertUserForOAuth(String email, String name, String avatar);
}
