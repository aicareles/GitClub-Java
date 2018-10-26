package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.domain.Follow;
import com.jerry.geekdaily.repository.FollowRepository;
import com.jerry.geekdaily.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Override
    public Follow findFollow(int userId, int fansId) {
        return followRepository.findByUserIdAndFansId(userId, fansId);
    }

    @Override
    public Page<Follow> getMyFollows(int fansId, int status, Pageable pageable) {
        return followRepository.findFollowsByFansIdAndStatusLike(fansId, status, pageable);
    }

    @Override
    public Page<Follow> getMyFans(int userId, int status, Pageable pageable) {
        return followRepository.findFollowsByUserIdAndStatusLike(userId, status, pageable);
    }

    @Override
    public Follow saveFollow(Follow follow) {
        return followRepository.saveAndFlush(follow);
    }
}
