package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.annotation.AccessLimit;
import com.jerry.geekdaily.annotation.Pass;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.dto.StarsDTO;
import com.jerry.geekdaily.dto.UpdateArticleDTO;
import com.jerry.geekdaily.service.ArticleService;
import com.jerry.geekdaily.service.StarsService;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.FileUtils;
import com.jerry.geekdaily.util.OSSUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "ArticleController", description = "文章管理相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class ArticleController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StarsService starsService;

    @ApiOperation(value = "上传文章图片")
    @PostMapping("/uploadArticleImg")
    public Result<Map<String, String>> uploadArticleImg(@RequestParam(value = "articleImg") MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            return ResultUtils.error(ResultCode.UPLOAD_FILE_EMPTY);
        }
        if (file.getSize() > Constans.FILE_MAX_LIMIT) {
            return ResultUtils.error(ResultCode.UPLOAD_FILE_LIMIT);
        }
        Map<String, String> map = new HashMap<>();
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        map.put(file.getName(), OSSUploadUtil.uploadFile(FileUtils.multi2File(file), fileType));
        return ResultUtils.ok(map);
    }

    @ApiOperation(value = "上传文章")
    @PostMapping("/uploadArticle")
    public Result<Article> uploadArticle(@Valid Article articleInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        articleService.uploadArticle(articleInfo);
        return ResultUtils.ok("文章上传成功");
    }

    @ApiOperation(value = "文章编辑更新")
    @PostMapping("/updateArticle")
    public Result<Article> updateArticle(@Valid UpdateArticleDTO articleInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        articleService.updateArticle(articleInfo);
        return ResultUtils.ok("文章更新成功");
    }

    @ApiOperation(value = "删除文章")
    @RequiresRoles(value = Constans.UserRole.ADMIN)
    @PostMapping("/deleteArticle")
    public Result<Article> deleteArticle(@RequestParam int articleId) {
        articleService.deleteArticle(articleId);
        return ResultUtils.ok("删除文章成功");
    }

    @ApiOperation(value = "根据分类获取文章", notes = "根据分类获取文章接口")
    @AccessLimit(perSecond = 50,timeOut = 500)//与Pass注解同时使用，Pass注解有时候无效，很奇怪
    @Pass
    @PostMapping("/getArticleListByCategory")
    public Result<Article> getArticleListByCategory(@RequestParam("page") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                    @RequestParam("category") String category){
        Page<Article> pages = articleService.findAllByCategory(category, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    @ApiOperation(value = "获取文章列表")
    @AccessLimit(perSecond = 50,timeOut = 500)
    @Pass
    @PostMapping("/getArticleList")
    public Result<Article> getArticleList(@RequestParam("page") Integer page,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<Article> pages = articleService.findAllReviewedArticles(PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    @ApiOperation(value = "网页端获取文章列表")
    @PostMapping("/getWebArticleList")
    public Result<Article> getWebArticleList(@RequestParam("page") Integer page,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<Article> pages = articleService.findAllReviewedArticles(PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages);
    }

    @ApiOperation(value = "点赞/取消点赞接口")
    @PostMapping("/starArticle")
    public Result<Stars> starArticle(@Valid StarsDTO starsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtils.ok(articleService.starArticle(starsDTO));
    }

    @ApiOperation(value = "获取文章点赞者", notes = "获取文章点赞者接口")
    @PostMapping("/getArticleStarers")
    public Result<User> getArticleStarers(@RequestParam("page") Integer page,
                                          @RequestParam(value = "size",required = false, defaultValue = "10") Integer size,
                                          @RequestParam("articleId") int articleId) {
        Page<Stars> pages = starsService.findStarsByArticleId(articleId, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));//
        List<User> users = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> userIds = new ArrayList<>();
            pages.getContent().forEach(stars -> userIds.add(stars.getUserId()));
            users = userService.findUsersByUserIdIn(userIds);
        }
        return ResultUtils.ok(users);
    }

    @ApiOperation(value = "获取我的点赞文章列表", notes = "获取我的点赞文章列表接口")
    @PostMapping("/getMyStarArticles")
    public Result<Article> getMyStarArticles(@RequestParam("page") Integer page,
                                             @RequestParam(value = "size",required = false, defaultValue = "10") Integer size,
                                             @RequestParam("userId") int userId) {
        User user = userService.findUserByUserId(userId);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Stars> pages = starsService.findStarsByUserId(userId, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articles = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> articleIds = new ArrayList<>();
            pages.getContent().forEach(stars -> articleIds.add(stars.getArticleId()));
            articles = articleService.findArticlesByArticleIdIn(articleIds);
        }
        return ResultUtils.ok(articles);
    }

    @ApiOperation(value = "获取我的上传文章列表", notes = "获取我的上传文章列表接口")
    @PostMapping("/getMyContributeArticles")
    public Result<Article> getMyContributeArticles(@RequestParam("page") Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                   @RequestParam("userId") int userId) {
        User user = userService.findUserByUserId(userId);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Article> pages = articleService.findAllByContributorId(userId, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articleList = pages.getContent();
        return ResultUtils.ok(articleList);
    }

    @ApiOperation(value = "是否某用户点赞过某文章", notes = "是否某用户点赞过某文章接口 0未点赞  1已点赞")
    @PostMapping("/getStarStatus")
    public Result getStarStatus(@RequestParam int userId, int articleId){
        Stars star = starsService.findByUserIdAndArticleId(userId, articleId);
        if(star != null){
            return ResultUtils.ok(star.getStatus());
        }
        return ResultUtils.ok(0);
    }

    @ApiOperation(value = "更新文章浏览量", notes = "更新文章浏览量接口")
    @PostMapping("/viewArticle")
    public Result<Article> viewArticle(@RequestParam("articleId") int articleId) {
        articleService.viewArticle(articleId);
        return ResultUtils.ok("更新成功");
    }

    @ApiOperation(value = "获取当天文章总浏览量", notes = "获取当天文章总浏览量接口")
    @PostMapping("/getArticleTotalViews")
    public Result<Integer> getArticleTotalViews(){
        return ResultUtils.ok(articleService.getArticleTotalViews());
    }

    @ApiOperation(value = "获取开源库的总收录数", notes = "获取开源库的总收录数接口")
    @PostMapping("/getArticleTotals")
    public Result<Integer> getArticleTotals(){
        return ResultUtils.ok(articleService.findAllArticleTotals());
    }

    @ApiOperation(value = "获取文章详情对应的MD文本", notes = "获取文章详情接口")
    @PostMapping("/getArticleDetail")
    public Result<String> getArticleDetail(@RequestParam int articleId) {
        //先查询该文章
        Article article = articleService.findArticleByArticleId(articleId);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        return ResultUtils.ok(article.getMdContent());
    }

    @ApiOperation(value = "文章审核", notes = "文章审核接口")
    @RequiresRoles(value = Constans.UserRole.ADMIN)
    @PostMapping("/reviewArticle")
    public Result<Boolean> reviewArticle(@RequestParam int articleId, @RequestParam boolean isPass){
        articleService.reviewArticle(articleId, isPass);
        return ResultUtils.ok("审核操作成功!");
    }
}
