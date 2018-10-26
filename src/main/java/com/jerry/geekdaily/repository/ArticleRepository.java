package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Article;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article,Integer> {

    @Query("select count(u) from Article u where u.review_status = 1")
    Integer findAllArticleTotals();

    @Query("select u from Article u where u.review_status = 1")
    Page<Article> findAllReviewedArticles(Pageable pageable);

    @Query("select u from Article u where u.article_id = :article_id")
    Article findArticleByArticleId(@Param("article_id")int article_id);

    @Query("select u from Article u where u.contributor_id = :user_id")
    Page<Article> findAllByContributorId(@Param("user_id")int user_id, Pageable pageable);

    @Query("select u from Article u where u.article_id in (:article_ids) order by u.date desc")
    List<Article> findArticlesByArticleIdIn(@Param("article_ids")List<Integer> article_ids);

    @Query("select u from Article u where upper(u.category) = upper(:category)")
    Page<Article> findAllByCategoryIgnoreCase(@Param("category")String category, Pageable pageable);

    @Query("select u from Article u where u.date between :date and :endDate")
    Page<Article> randomFindFiveArticles(@Param("date")Date date, @Param("endDate")Date endDate, Pageable pageable);

//    @Modifying
//    @Transactional
//    @Query("update Article u set u.date = :currentDate where u.article_id in (:article_ids)")
//    List<Article> updateFindFiveArticles(@Param("currentDate")Date currentDate, @Param("article_ids")List<Integer> article_ids);

}
