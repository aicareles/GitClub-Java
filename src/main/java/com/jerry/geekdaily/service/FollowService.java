package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    Follow findFollow(int userId, int fansId);
    //获取我关注的对象列表
    Page<Follow> getMyFollows(int fansId, int status, Pageable pageable);
    //获取我的关注者列表（我的粉丝）
    Page<Follow> getMyFans(int userId, int status, Pageable pageable);
    Follow saveFollow(Follow follow);
}
