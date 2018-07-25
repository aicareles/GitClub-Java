package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    Follow findByUserIdAndFansId(int userId, int fansId);

    //获取我关注的对象列表
    Page<Follow> findFollowsByFansIdAndStatusLike(int fansId, int status, Pageable pageable);
    //获取我的关注者列表（我的粉丝）
    Page<Follow> findFollowsByUserIdAndStatusLike(int userId, int status, Pageable pageable);
}
