package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<Comment> getAllByArticleId(int article_id, Pageable pageable);

    Page<Comment> getAllByFromId(int user_id, Pageable pageable);

    Comment saveComment(Comment comment);

    void deleteAllByArticleId(int article_id);
}
