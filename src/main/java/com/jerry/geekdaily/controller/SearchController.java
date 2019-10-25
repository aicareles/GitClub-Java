package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@Api(value = "SearchController", description = "全局关键字查询相关接口")
@RestController
public class SearchController {

    @Autowired
    SearchService searchService;

    /**
     * 3、查、分页、分数、分域（结果一个也不少）
     *
     * @param page  当前页数
     * @param size  返回的文章数量
     * @param query 关键字
     * @return
     */
    @ApiOperation(value = "全局关键字查询", notes = "全局关键字查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回的文章数量", required = false, dataType = "int"),
            @ApiImplicitParam(name = "query", value = "关键字---标签、文章分类、标题、描述", required = true, dataType = "string")
    })
    @RequestMapping("/query")
    public Result<Article> query(@RequestParam Integer page, @RequestParam(required = false, defaultValue = "10")int size, @RequestParam String query) {
        Page<Article> searchPageResults = searchService.search(page, size, query);
        return ResultUtils.ok(searchPageResults.getContent());
    }

    @ApiOperation(value = "相关四篇文章查询", notes = "相关四篇文章查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "关键字---标题、描述", required = true, dataType = "string")
    })
    @RequestMapping("/related")
    public Result<ESArticle> like(@RequestParam(required = false, defaultValue = "4")int size, @RequestParam String key) {
        return ResultUtils.ok(searchService.related(size, key));
    }

    @ApiOperation(value = "网页端全局关键字查询", notes = "网页端全局关键字查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回的文章数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "query", value = "关键字---标签、文章分类、标题、描述", required = true, dataType = "string")
    })
    @RequestMapping("/queryWeb")
    public Result<Article> queryWeb(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String query) {
        Page<Article> searchPageResults = searchService.search(page, size, query);
        return ResultUtils.ok(searchPageResults);

    }
}
