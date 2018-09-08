package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javafx.scene.control.Pagination;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Api(value = "ESController", description = "全局关键字查询相关接口")
@RestController
public class ESController {

//    @Autowired
//    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

//    //全文关键字搜索
//    @RequestMapping("/query")
//    public Result<ESArticle> testSearch(@RequestParam("query")String query) {
//        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(query);
//        Iterable<ESArticle> searchResult = articleSearchRepository.search(builder);
//        Iterator<ESArticle> iterator = searchResult.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
//        return ResultUtils.ok(searchResult);
//    }

    /**
     * 3、查、分页、分数、分域（结果一个也不少）
     * @param page 当前页数
     * @param size 返回的文章数量
     * @param query 关键字
     * @return
     */
    @ApiOperation(value = "全局关键字查询", notes = "全局关键字查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回的文章数量", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "query", value = "关键字---标题、描述", required = true ,dataType = "string")
    })
    @RequestMapping("/query")
    public Result<ESArticle> query(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String query) {
        // 分页参数
        Pageable pageable = new PageRequest(page, size);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must()
                .add(QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", query))
                .should(QueryBuilders.matchQuery("des", query)));

//        queryBuilder.must(QueryBuilders.matchQuery("title", query));
//                .must(QueryBuilders.matchQuery("des", query))
//                .must(QueryBuilders.matchQuery("contributor", query));
//        ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.weightFactorFunction(1000);
        // 分数，并自动按分排序
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder);
//                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", query)),
//                        ScoreFunctionBuilders.weightFactorFunction(1000)) // 权重：name 1000分
//                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("message", query)),
//                        ScoreFunctionBuilders.weightFactorFunction(100)); // 权重：message 100分

        // 分数、分页
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<ESArticle> searchPageResults = articleSearchRepository.search(searchQuery);
        return ResultUtils.ok(searchPageResults.getContent());

    }




    /**
     * 查询所有
     * @throws Exception
     */
//    @GetMapping("/all")
//    public List<Map<String, Object>> searchAll() throws Exception {
//        //这一步是最关键的
//        Client client = elasticsearchTemplate.getClient();
//        // @Document(indexName = "product", type = "book")
//        SearchRequestBuilder srb = client.prepareSearch("product").setTypes("book");
//        SearchResponse sr = srb.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet(); // 查询所有
//        SearchHits hits = sr.getHits();
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (SearchHit hit : hits) {
//            Map<String, Object> source = hit.getSource();
//            list.add(source);
//            System.out.println(hit.getSourceAsString());
//        }
//        return list;
//    }
}
