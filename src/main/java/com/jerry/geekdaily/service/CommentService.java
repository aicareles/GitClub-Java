package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<Comment> getAllByArticleId(int article_id, Pageable pageable);

    void deleteAllByArticleId(int article_id);

    void commentArticle(CommentDTO commentDTO);
}
