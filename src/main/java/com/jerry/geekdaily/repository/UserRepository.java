package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
//    @Transactional(readOnly = true)
//    UploadFile getUploadFilesById(@Param("id")int id);

//    Optional<User> login(@Param(value = "user")User user);

    @Query("select u from User u where u.user_id in (:user_ids)")
    List<User> findUsersByUserIdIn(@Param("user_ids")List<Integer> user_ids);

    @Query("select u from User u where u.user_id = :user_id")
    User findUserByUserId(@Param("user_id")int user_id);

    @Query("select u from User u where u.open_id = :open_id")
    User findUserByOpenId(@Param("open_id")String open_id);

    @Query("select u from User u where (u.nick_name = :userName) and (u.pwd = :password)")
    User findUserByNickNameAndPwd(@Param("userName")String userName, @Param("password")String password);
}
