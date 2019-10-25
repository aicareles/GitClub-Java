//package com.jerry.geekdaily.service.impl;
//
//import com.jerry.geekdaily.domain.Article;
//import com.jerry.geekdaily.domain.ESArticle;
//import com.jerry.geekdaily.repository.ArticleRepository;
//import com.jerry.geekdaily.repository.ESArticleSearchRepository;
//import com.jerry.geekdaily.service.ESService;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
//import org.springframework.data.elasticsearch.core.query.SearchQuery;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ESServiceImpl implements ESService {
//    @Autowired
//    private ESArticleSearchRepository esArticleSearchRepository;
//
//    @Autowired
//    private ArticleRepository articleRepository;
//
//    @Override
//    public Page<ESArticle> search(int page, int size, String query) {
//        // 分页参数
//        Pageable pageable = PageRequest.of(page, size);
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must()
//                .add(QueryBuilders.boolQuery()
//                        .should(QueryBuilders.matchQuery("tag", query))
//                        .should(QueryBuilders.matchQuery("category", query))
//                        .should(QueryBuilders.matchQuery("title", query))
//                        .should(QueryBuilders.matchQuery("des", query)));
////        queryBuilder.must(QueryBuilders.matchQuery("title", query));
////                .must(QueryBuilders.matchQuery("des", query))
////                .must(QueryBuilders.matchQuery("contributor", query));
////        ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.weightFactorFunction(1000);
//        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder);
////                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", query)),
////                        ScoreFunctionBuilders.weightFactorFunction(1000)) // 权重：name 1000分
////                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("message", query)),
////                        ScoreFunctionBuilders.weightFactorFunction(100)); // 权重：message 100分
//        // 分数、分页
//        SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
//                .withQuery(functionScoreQueryBuilder).build();
//        return esArticleSearchRepository.search(searchQuery);
//    }
//
//    @Override
//    public void deleteAllES() {
//        esArticleSearchRepository.deleteAll();
//    }
//
//    @Override
//    public void updateAllES() {
//        List<Article> articles = articleRepository.findAll();
//        articles.forEach(article -> {
//            esArticleSearchRepository.save(new ESArticle(article));
//        });
//    }
//
//    @Override
//    public Page<ESArticle> searchAll() {
//        return null;
//    }
//
//}
