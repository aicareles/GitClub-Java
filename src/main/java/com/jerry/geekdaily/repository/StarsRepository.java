package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Stars;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StarsRepository extends JpaRepository<Stars,Integer> {

    @Query("select u from Stars u where (u.userId = :user_id) and (u.articleId = :article_id)")
    Stars findByUserIdAndArticleId(@Param("user_id")int user_id, @Param("article_id")int article_id);

    @Query("select u from Stars u where (u.articleId = :article_id) and (u.status = 1)")
    Page<Stars> findStarsByArticleId(@Param("article_id")int article_id, Pageable pageable);

    @Query("select u from Stars u where (u.userId = :user_id) and (u.status = 1)")
    Page<Stars> findStarsByUserId(@Param("user_id")int user_id, Pageable pageable);

    @Query("select u from Stars u where (u.userId = :user_id) and (u.status = 1)")
    List<Stars> findAllByUserId(@Param("user_id")int user_id);

    @Modifying
    @Transactional
    @Query("delete from Stars u where u.articleId = :article_id")
    void deleteByArticleId(@Param("article_id") int article_id);
}
