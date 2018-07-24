package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Stars;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StarsRepository extends JpaRepository<Stars,Integer> {

    @Query("select u from Stars u where (u.user_id = :user_id) and (u.article_id = :article_id)")
    Stars findByUser_idAndArticle_id(@Param("user_id")int user_id, @Param("article_id")int article_id);

    @Query("select u from Stars u where (u.article_id = :article_id) and (u.status = 1)")
    Page<Stars> findStarsByArticle_id(@Param("article_id")int article_id, Pageable pageable);

    @Query("select u from Stars u where (u.user_id = :user_id) and (u.status = 1)")
    Page<Stars> findStarsByUser_id(@Param("user_id")int user_id, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Stars u where u.article_id = :article_id")
    void deleteAllByArticle_id(@Param("article_id") int article_id);
}
