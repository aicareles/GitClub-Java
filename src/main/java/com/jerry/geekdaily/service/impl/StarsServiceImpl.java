package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.repository.StarsRepository;
import com.jerry.geekdaily.service.StarsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StarsServiceImpl implements StarsService {

    @Autowired
    private StarsRepository starsRepository;

    @Override
    public Stars findByUserIdAndArticleId(int user_id, int article_id) {
        return starsRepository.findByUserIdAndArticleId(user_id, article_id);
    }

    @Override
    public Page<Stars> findStarsByArticleId(int article_id, Pageable pageable) {
        return starsRepository.findStarsByArticleId(article_id, pageable);
    }

    @Override
    public Page<Stars> findStarsByUserId(int user_id, Pageable pageable) {
        return starsRepository.findStarsByUserId(user_id, pageable);
    }

    @Override
    public List<Stars> findAllByUserId(int user_id) {
        return starsRepository.findAllByUserId(user_id);
    }

    @Override
    public void deleteByArticleId(int article_id) {
        starsRepository.deleteByArticleId(article_id);
    }

    @Override
    public Stars saveStar(Stars stars) {
        return starsRepository.saveAndFlush(stars);
    }
}
