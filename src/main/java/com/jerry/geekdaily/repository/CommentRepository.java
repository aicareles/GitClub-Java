package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select u from Comment u where u.article_id = :article_id")
    Page<Comment> getAllByArticle_id(@Param("article_id")int article_id, Pageable pageable);

    @Query("select u from Comment u where u.from_uid = :user_id")
    Page<Comment> getAllByFrom_uid(@Param("user_id")int user_id, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Comment u where u.article_id = :article_id")
    void deleteAllByArticle_id(@Param("article_id") int article_id);

}
