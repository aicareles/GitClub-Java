//package com.jerry.geekdaily.controller;
//
//import com.hankcs.hanlp.HanLP;
//import com.jerry.geekdaily.base.Result;
//import com.jerry.geekdaily.base.ResultUtils;
//import com.jerry.geekdaily.config.Constans;
//import com.jerry.geekdaily.domain.Article;
//import com.jerry.geekdaily.domain.ESArticle;
//import com.jerry.geekdaily.repository.ArticleRepository;
//import com.jerry.geekdaily.repository.ESArticleSearchRepository;
//import com.jerry.geekdaily.service.ArticleService;
//import com.jerry.geekdaily.service.ESService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import javafx.scene.control.Pagination;
//import org.apache.shiro.authz.annotation.RequiresRoles;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.QueryStringQueryBuilder;
//import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
//import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
//import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
//import org.springframework.data.elasticsearch.core.query.SearchQuery;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Api(value = "ESController", description = "全局关键字查询相关接口")
//@RestController
//@RequestMapping("/es")
//public class ESController {
//    @Autowired
//    private ESService esService;
//
//    @PostMapping("/deleteAllES")
//    @RequiresRoles(value = Constans.UserRole.ADMIN)
//    public void deleteAllES(){
//        esService.deleteAllES();
//    }
//
//    @PostMapping("/updateAllES")
//    @RequiresRoles(value = Constans.UserRole.ADMIN)
//    public void updateAllES(){
//        esService.updateAllES();
//    }
//
//    /**
//     * 3、查、分页、分数、分域（结果一个也不少）
//     *
//     * @param page  当前页数
//     * @param size  返回的文章数量
//     * @param query 关键字
//     * @return
//     */
//    @ApiOperation(value = "全局关键字查询", notes = "全局关键字查询接口")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "int"),
//            @ApiImplicitParam(name = "size", value = "返回的文章数量", required = true, dataType = "int"),
//            @ApiImplicitParam(name = "query", value = "关键字---标签、文章分类、标题、描述", required = true, dataType = "string")
//    })
//    @RequestMapping("/query")
//    public Result<ESArticle> query(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String query) {
//        Page<ESArticle> searchPageResults = esService.search(page, size, query);
//        return ResultUtils.ok(searchPageResults.getContent());
//    }
//
//    @ApiOperation(value = "相关四篇文章查询", notes = "相关四篇文章查询接口")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "key", value = "关键字---标题、描述", required = true, dataType = "string")
//    })
//    @RequestMapping("/like")
//    public Result<ESArticle> like(@RequestParam String key) {
//        //进行关键字过滤筛选
//        List<String> keywordList = HanLP.extractKeyword(key, 3);
//        StringBuffer tempKey = new StringBuffer();
//        keywordList.forEach(tempKey::append);
//        return query(0, 4, tempKey.toString());
//    }
//
//    @ApiOperation(value = "网页端全局关键字查询", notes = "网页端全局关键字查询接口")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "int"),
//            @ApiImplicitParam(name = "size", value = "返回的文章数量", required = true, dataType = "int"),
//            @ApiImplicitParam(name = "query", value = "关键字---标签、文章分类、标题、描述", required = true, dataType = "string")
//    })
//    @RequestMapping("/queryWeb")
//    public Result<ESArticle> queryWeb(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String query) {
//        Page<ESArticle> searchPageResults = esService.search(page, size, query);
//        return ResultUtils.ok(searchPageResults);
//
//    }
//}
