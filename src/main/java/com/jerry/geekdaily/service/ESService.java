package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import org.springframework.data.domain.Page;

public interface ESService {

    Page<ESArticle> search(int page, int size, String query);

    void deleteAllES();

    void updateAllES();

    Page<ESArticle> searchAll();
}
