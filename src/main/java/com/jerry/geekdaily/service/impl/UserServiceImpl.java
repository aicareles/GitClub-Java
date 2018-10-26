package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.UserRepository;
import com.jerry.geekdaily.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findUsersByUserIdIn(List<Integer> user_ids) {
        return userRepository.findUsersByUserIdIn(user_ids);
    }

    @Override
    public User findUserByUserId(int user_id) {
        return userRepository.findUserByUserId(user_id);
    }

    @Override
    public User findUserByOpenId(String open_id) {
        return userRepository.findUserByOpenId(open_id);
    }

    @Override
    public User login(String userName, String password) {
        return userRepository.findUserByNickNameAndPwd(userName, password);
    }

    @Override
    public User register(User user) {
        return userRepository.saveAndFlush(user);
    }
}
