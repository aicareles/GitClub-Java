package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.dto.CommentDTO;
import com.jerry.geekdaily.exception.ValidException;
import com.jerry.geekdaily.repository.CommentRepository;
import com.jerry.geekdaily.service.ArticleService;
import com.jerry.geekdaily.service.CommentService;
import com.jerry.geekdaily.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleService articleService;

    @Override
    public Page<Comment> getAllByArticleId(int articleId, Pageable pageable) {
        Article article = articleService.findArticleByArticleId(articleId);
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
        }
        return commentRepository.getAllByArticleId(articleId, pageable);
    }

    @Override
    public void deleteAllByArticleId(int article_id) {
        commentRepository.deleteAllByArticleId(article_id);
    }

    @Override
    public void commentArticle(CommentDTO commentDTO) {
        Article article = articleService.findArticleByArticleId(commentDTO.getArticleId());
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
        }
        Comment comment = new Comment();
        BeanCopyUtil.beanCopy(commentDTO, comment);
        commentRepository.saveAndFlush(comment);
        article.setComments(article.getComments() + 1);
        articleService.saveArticle(article);
    }
}
