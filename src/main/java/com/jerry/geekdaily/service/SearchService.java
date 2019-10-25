package com.jerry.geekdaily.service;

import com.jerry.geekdaily.domain.Article;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {

    Page<Article> search(int page, int size, String key);

    List<Article> related(int size, String key);

}
