package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.User;

import java.util.List;

public interface UserService {

    List<User> findUsersByUserIdIn(List<Integer> user_ids);

    User findUserByUserId(int user_id);

    User findUserByOpenId(String open_id);

    User login(String username, String password);

    User register(User user);

    User findByUserName(String username);
}
