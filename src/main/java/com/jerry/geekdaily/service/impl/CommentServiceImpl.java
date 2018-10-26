package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.repository.CommentRepository;
import com.jerry.geekdaily.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<Comment> getAllByArticleId(int article_id, Pageable pageable) {
        return commentRepository.getAllByArticleId(article_id, pageable);
    }

    @Override
    public Page<Comment> getAllByFromId(int user_id, Pageable pageable) {
        return commentRepository.getAllByFromId(user_id, pageable);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.saveAndFlush(comment);
    }

    @Override
    public void deleteAllByArticleId(int article_id) {
        commentRepository.deleteAllByArticleId(article_id);
    }
}
