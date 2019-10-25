package com.jerry.geekdaily.service.impl;

import com.hankcs.hanlp.HanLP;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.repository.ArticleRepository;
import com.jerry.geekdaily.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public Page<Article> search(int page, int size, String query) {
        // 分页参数
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.search(query, pageable);
    }

    @Override
    public List<Article> related(int size, String key) {
        //进行关键字过滤筛选
        List<String> keywordList = HanLP.extractKeyword(key, 3);
        StringBuffer tempKey = new StringBuffer();
        keywordList.forEach(tempKey::append);
        Pageable pageable = PageRequest.of(0, size);
        Page<Article> result = articleRepository.search(tempKey.toString(), pageable);
        return result.getContent();
    }

}
