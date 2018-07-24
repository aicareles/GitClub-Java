package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Article;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article,Integer> {
    @Query("select u from Article u where u.article_id = :article_id")
    Article findArticleByArticle_id(@Param("article_id")int article_id);

    @Query("select u from Article u where u.contributor_id = :user_id")
    Page<Article> findAllByContributor_id(@Param("user_id")int user_id, Pageable pageable);

}
