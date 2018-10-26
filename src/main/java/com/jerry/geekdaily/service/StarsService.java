package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Stars;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StarsService {

    Stars findByUserIdAndArticleId(int user_id, int article_id);

    Page<Stars> findStarsByArticleId(int article_id, Pageable pageable);

    Page<Stars> findStarsByUserId(int user_id, Pageable pageable);

    List<Stars> findAllByUserId(int user_id);

    void deleteByArticleId(int article_id);

    Stars saveStar(Stars stars);

}
