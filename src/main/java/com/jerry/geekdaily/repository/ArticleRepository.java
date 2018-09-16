package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Article;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article,Integer> {

    @Query("select count(u) from Article u where u.review_status = 1")
    Integer findAllArticleTotals();

    @Query("select u from Article u where u.review_status = 1")
    Page<Article> findAllReviewedArticles(Pageable pageable);

    @Query("select u from Article u where u.article_id = :article_id")
    Article findArticleByArticle_id(@Param("article_id")int article_id);

    @Query("select u from Article u where u.contributor_id = :user_id")
    Page<Article> findAllByContributor_id(@Param("user_id")int user_id, Pageable pageable);

    @Query("select u from Article u where u.article_id in (:article_ids) order by u.date desc")
    List<Article> findArticlesByArticle_idIn(@Param("article_ids")List<Integer> article_ids);

}
