package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.dto.StarsDTO;
import com.jerry.geekdaily.dto.UpdateArticleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ArticleService {
    Integer findAllArticleTotals();

    void uploadArticle(Article article);

    void updateArticle(UpdateArticleDTO articleDTO);

    void deleteArticle(int article_id);

    Page<Article> findAllArticles(Pageable pageable);

    Page<Article> findAllReviewedArticles(Pageable pageable);

    String starArticle(StarsDTO starsDTO);

    void viewArticle(int article_id);

    void reviewArticle(int article_id, boolean is_pass);

    Article findArticleByArticleId(int article_id);

    Page<Article> findAllByContributorId(int user_id, Pageable pageable);

    List<Article> findArticlesByArticleIdIn(List<Integer> article_ids);

    Page<Article> findAllByCategory(String category, Pageable pageable);

    Page<Article> randomFindFiveArticles(Date date, Date endDate, Pageable pageable);

    void saveArticle(Article article);

    int getArticleTotalViews();
}
