package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.annotation.AccessLimit;
import com.jerry.geekdaily.annotation.Pass;
import com.jerry.geekdaily.annotation.ValidationParam;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.dto.StarsDTO;
import com.jerry.geekdaily.dto.UpdateArticleDTO;
import com.jerry.geekdaily.enums.AdminEnum;
import com.jerry.geekdaily.enums.StarStatusEnum;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import com.jerry.geekdaily.service.ArticleService;
import com.jerry.geekdaily.service.CommentService;
import com.jerry.geekdaily.service.StarsService;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private CommentService commentService;

    //全文搜索引擎   数据库数据改变时伴随着  搜索引擎中的数据的增删改查
    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "上传文章图片")
    @PostMapping("/uploadArticleImg")
    public Result<Map<String, String>> uploadArticleImg(@RequestParam(value = "article_img") MultipartFile file) {
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
        if(!LinkUtils.verifyURL(articleInfo.getLink()) || !LinkUtils.verifyURL(articleInfo.getImgUrl())){
            return ResultUtils.error(ResultCode.UPLOAD_LINK_ERROR);
        }
        articleInfo.setMdContent(MarkdownUtils.getMdContent(articleInfo.getLink()));
        articleInfo.setWrapLink(LinkUtils.gererateShortUrl(articleInfo.getLink()));
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(articleInfo.getContributorId());
        articleInfo.setUser(user);
        if (null != user) {
            articleInfo.setReviewStatus(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
            if(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status()){//管理员  直接插入到es引擎  不限制上传次数
                articleInfo.setReviewStatus(1);
                //插入数据到引擎
                articleSearchRepository.save(new ESArticle(articleInfo));
            }else {//普通用户  限制上传次数（每天一篇）
                ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
                Integer count = 0;
                //验证  普通用户一天上传文章次数不能超过1次
                boolean exists = redisTemplate.hasKey(String.valueOf(articleInfo.getContributorId()));
                if (exists) {
                    count = operations.get(String.valueOf(articleInfo.getContributorId()));
                    if (count >= 1) {
                        return ResultUtils.error(ResultCode.UPLOAD_LIMIT);
                    }
                }
                articleInfo.setReviewStatus(0);
                //保存上传次数到redis  key为userid   value为次数
                operations.set(String.valueOf(articleInfo.getContributorId()), count + 1, 1, TimeUnit.DAYS);
            }
        }else {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        articleService.saveArticle(articleInfo);
        return ResultUtils.ok(articleInfo);
    }

    @ApiOperation(value = "文章编辑更新")
    @PostMapping("/updateArticle")
    public Result<Article> updateArticle(@Valid UpdateArticleDTO articleInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        if(!StringUtils.isEmpty(articleInfo.getLink())){
            if(!LinkUtils.verifyURL(articleInfo.getLink())){
                return ResultUtils.error(ResultCode.UPLOAD_LINK_ERROR);
            }
        }
        if(!StringUtils.isEmpty(articleInfo.getImgUrl())){
            if(!LinkUtils.verifyURL(articleInfo.getImgUrl())){
                return ResultUtils.error(ResultCode.UPLOAD_LINK_ERROR);
            }
        }
        Article article = articleService.findArticleByArticleId(articleInfo.getArticleId());
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(articleInfo.getContributorId());
        if (null != user) {
            if (user.getAdminStatus() != AdminEnum.ADMIN.getAdmin_status() && articleInfo.getContributorId() != article.getContributorId()) {
                return ResultUtils.error(ResultCode.NO_EDIT_PERMITION);
            }
            article.setReviewStatus(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
        }else {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        BeanCopyUtil.beanCopyWithIngore(articleInfo, article, "contributor_id");
        articleService.saveArticle(article);
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    @ApiOperation(value = "删除文章")
    @RequiresRoles(value = Constans.UserRole.ADMIN)
    @PostMapping("/deleteArticle")
    public Result<Article> deleteArticle(@RequestParam int article_id) {
        if (StringUtils.isEmpty(article_id)) {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        articleService.deleteById(article_id);
        //删除中间表stars中的article_id的所有数据
        starsService.deleteByArticleId(article_id);
        commentService.deleteAllByArticleId(article_id);
        articleSearchRepository.deleteById(article_id);
        return ResultUtils.ok("删除文章成功");
    }

    @ApiOperation(value = "获取文章列表")
    @Pass
    @AccessLimit(perSecond = 50,timeOut = 500)
    @PostMapping("/getArticleList")
    public Result<Article> getArticleList(@RequestParam("page") Integer page,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<Article> pages = articleService.findAllReviewedArticles(PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    @ApiOperation(value = "根据分类获取文章", notes = "根据分类获取文章接口")
    @Pass
    @AccessLimit(perSecond = 50,timeOut = 500)
    @PostMapping("/getArticleListByCategory")
    public Result<Article> getArticleListByCategory(@RequestParam("page") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                    @RequestParam("category") String category){
        Page<Article> pages = articleService.findAllByCategory(category, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    @ApiOperation(value = "点赞/取消点赞接口")
    @PostMapping("/starArticle")
    public Result<Stars> starArticle(@Valid StarsDTO starsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        //先查看该用户是否已经点赞，如果没有点赞 则增加一条star记录
        Article article = articleService.findArticleByArticleId(starsDTO.getArticleId());
        String msg = "操作成功!";
        if(article == null){
            return ResultUtils.error("文章ID不存在!");
        }
        Stars starts = starsService.findByUserIdAndArticleId(starsDTO.getUserId(), starsDTO.getArticleId());
        if(starts == null){
            if(starsDTO.getStatus() == 1){//点赞
                //不存在   插入点赞,article表中添加star
                starts = new Stars();
                BeanCopyUtil.beanCopy(starsDTO, starts);
                log.info("starts:"+starts.getArticleId());
                article.setStars(article.getStars()+1);
                msg = "点赞成功!";
            }
        }else {
            //往article表中添加star/unstar  存在数据,则判断点赞状态
            if(starts.getStatus() == StarStatusEnum.STAR_STATUS.getStarStatus()){
                if(starsDTO.getStatus() == 0){//取消点赞
                    starts.setStatus(0);
                    article.setStars(article.getStars()-1);
                    msg = "取消点赞成功!";
                }
            }else {//当前未点赞
                if(starsDTO.getStatus() == 1){//点赞
                    starts.setStatus(1);
                    article.setStars(article.getStars()+1);
                    msg = "点赞成功!";
                }
            }
        }
        starsService.saveStar(starts);
        articleService.saveArticle(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(msg);
    }

    @ApiOperation(value = "获取文章点赞者", notes = "获取文章点赞者接口")
    @PostMapping("/getArticleStarers")
    public Result<User> getArticleStarers(@RequestParam("page") Integer page,
                                          @RequestParam(value = "size",required = false, defaultValue = "10") Integer size,
                                          @RequestParam("article_id") int article_id) {
        Page<Stars> pages = starsService.findStarsByArticleId(article_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));//
        List<User> users = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> user_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> user_ids.add(stars.getUserId()));
            users = userService.findUsersByUserIdIn(user_ids);
        }
        return ResultUtils.ok(users);
    }

    @ApiOperation(value = "获取我的点赞文章列表", notes = "获取我的点赞文章列表接口")
    @PostMapping("/getMyStarArticles")
    public Result<Article> getMyStarArticles(@RequestParam("page") Integer page,
                                             @RequestParam(value = "size",required = false, defaultValue = "10") Integer size,
                                             @RequestParam("user_id") int user_id) {
        User user = userService.findUserByUserId(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Stars> pages = starsService.findStarsByUserId(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articles = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> article_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> article_ids.add(stars.getArticleId()));
            articles = articleService.findArticlesByArticleIdIn(article_ids);
        }
        return ResultUtils.ok(articles);
    }

    @ApiOperation(value = "获取我的上传文章列表", notes = "获取我的上传文章列表接口")
    @PostMapping("/getMyContributeArticles")
    public Result<Article> getMyContributeArticles(@RequestParam("page") Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                   @RequestParam("user_id") int user_id) {
        User user = userService.findUserByUserId(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Article> pages = articleService.findAllByContributorId(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articleList = pages.getContent();
        return ResultUtils.ok(articleList);
    }

    @ApiOperation(value = "是否某用户点赞过某文章", notes = "是否某用户点赞过某文章接口 0未点赞  1已点赞")
    @PostMapping("/getStarStatus")
    public Result getStarStatus(@RequestParam int user_id, int article_id){
        Stars star = starsService.findByUserIdAndArticleId(user_id, article_id);
        if(star != null){
            return ResultUtils.ok(star.getStatus());
        }
        return ResultUtils.ok(0);
    }

    @ApiOperation(value = "更新文章浏览量", notes = "更新文章浏览量接口")
    @PostMapping("/viewArticle")
    public Result<Article> viewArticle(@RequestParam("article_id") int article_id) {
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        //把当天的阅读数逐个添加到redis中
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        boolean exists = redisTemplate.hasKey(Constans.RedisKey.ARTICLE_TOTAL_VIEWS);
        if(!exists){//当天第一次   赋值初始值为400-800的一个随机数
            int random = new Random().nextInt(400)+400;
            operations.set(Constans.RedisKey.ARTICLE_TOTAL_VIEWS, random, 1, TimeUnit.DAYS);
        }else {
            Integer views = operations.get(Constans.RedisKey.ARTICLE_TOTAL_VIEWS);
            int num = new Random().nextInt(2)+1;
            operations.set(Constans.RedisKey.ARTICLE_TOTAL_VIEWS, views+num);
        }
        article.setViews(article.getViews() + 1);
        articleService.saveArticle(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    @ApiOperation(value = "获取当天文章总浏览量", notes = "获取当天文章总浏览量接口")
    @PostMapping("/getArticleTotalViews")
    public Result<Integer> getArticleTotalViews(){
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        boolean exists = redisTemplate.hasKey(Constans.RedisKey.ARTICLE_TOTAL_VIEWS);
        if(!exists){//当天还没有阅读数   赋值初始值为400-800的一个随机数
            int random = new Random().nextInt(400)+400;
            operations.set(Constans.RedisKey.ARTICLE_TOTAL_VIEWS, random, 1, TimeUnit.DAYS);
            return ResultUtils.ok(random);
        }else {
            return ResultUtils.ok(operations.get(Constans.RedisKey.ARTICLE_TOTAL_VIEWS));
        }
    }

    @ApiOperation(value = "获取开源库的总收录数", notes = "获取开源库的总收录数接口")
    @PostMapping("/getArticleTotals")
    public Result<Integer> getArticleTotals(){
        return ResultUtils.ok(articleService.findAllArticleTotals());
    }

    @ApiOperation(value = "获取文章详情对应的MD文本", notes = "获取文章详情接口")
    @PostMapping("/getArticleDetail")
    public Result<String> getArticleDetail(@RequestParam int article_id) {
        //先查询该文章
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        return ResultUtils.ok(article.getMdContent());
    }

    @ApiOperation(value = "文章审核", notes = "文章审核接口")
    @RequiresRoles(value = Constans.UserRole.ADMIN)
    @RequiresAuthentication
    @PostMapping("/reviewArticle")
    public Result<Boolean> reviewArticle(@RequestParam int article_id, @RequestParam boolean is_pass){
        if(StringUtils.isEmpty(is_pass) || StringUtils.isEmpty(article_id)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        article.setReviewStatus(is_pass ? 1 : -1);
        articleService.saveArticle(article);
        //插入数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok("审核操作成功!");
    }
}
