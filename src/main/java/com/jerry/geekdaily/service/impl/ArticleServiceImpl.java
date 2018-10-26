package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.repository.ArticleRepository;
import com.jerry.geekdaily.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public Integer findAllArticleTotals() {
        return articleRepository.findAllArticleTotals();
    }

    @Override
    public Page<Article> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> findAllReviewedArticles(Pageable pageable) {
        return articleRepository.findAllReviewedArticles(pageable);
    }

    @Override
    public Article findArticleByArticleId(int article_id) {
        return articleRepository.findArticleByArticleId(article_id);
    }

    @Override
    public Page<Article> findAllByContributorId(int user_id, Pageable pageable) {
        return articleRepository.findAllByContributorId(user_id, pageable);
    }

    @Override
    public List<Article> findArticlesByArticleIdIn(List<Integer> article_ids) {
        return articleRepository.findArticlesByArticleIdIn(article_ids);
    }

    @Override
    public Page<Article> findAllByCategory(String category, Pageable pageable) {
        return articleRepository.findAllByCategoryIgnoreCase(category, pageable);
    }

    @Override
    public Page<Article> randomFindFiveArticles(Date date, Date endDate, Pageable pageable) {
        return articleRepository.randomFindFiveArticles(date, endDate, pageable);
    }

    @Override
    public Article saveArticle(Article article) {
        return articleRepository.saveAndFlush(article);
    }

    @Override
    public void deleteById(int article_id) {
        articleRepository.deleteById(article_id);
    }
}
