package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserRepository extends JpaRepository<User,Integer> {
//    @Transactional(readOnly = true)
//    UploadFile getUploadFilesById(@Param("id")int id);

//    Optional<User> login(@Param(value = "user")User user);

    @Query("select u from User u where u.user_id = :user_id")
    User findUserByUser_id(@Param("user_id")int user_id);

    @Query("select u from User u where u.open_id = :open_id")
    User findUserByOpen_id(@Param("open_id")String open_id);

    @Query("select u from User u where (u.nick_name = :userName) and (u.pwd = :password)")
    User findUserByNick_nameAndPwd(@Param("userName")String userName, @Param("password")String password);
}
