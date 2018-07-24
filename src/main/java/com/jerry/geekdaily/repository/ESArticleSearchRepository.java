package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.ESArticle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESArticleSearchRepository extends ElasticsearchRepository<ESArticle, Integer> {
}
