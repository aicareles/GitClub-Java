package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.domain.User;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;

@CacheConfig(cacheNames = "ArticleController")
@Api(value = "ArticleController", description = "文章管理相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class ArticleController {

    private final static Logger logger = LoggerFactory.getLogger(ArticleController.class);

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

//    @PostMapping("/trasform2OSS")
//    public Result<Boolean> trasformImg2OSS(){
//        File path = null;
//        try {
//            path = new File(ResourceUtils.getURL("classpath:").getPath());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (!path.exists()) path = new File("");
//        System.out.println("path:" + path.getAbsolutePath());
//        //如果上传目录为/static/images/upload/，则可以如下获取：
//        File upload = new File(path.getAbsolutePath(), "static/images/upload/");
//        if (!upload.exists()) upload.mkdirs();
//        System.out.println("upload url:" + upload.getAbsolutePath());
//        File[] files = upload.listFiles();
//
//        //更新图片的url到oss  并把最新oss的图片地址保存到数据库
//        List<Article> articles = articleRepository.findAll();
//        articles.forEach(article -> {
//            if (null != article){
//                for (File file : files){
//                    if(null != file){
//                        if(null == article.getImg_url() || null == file.getName()){
//                            continue;
//                        }
//                        if(article.getImg_url().contains(file.getName())){
//                            //上传到oss
//                            String fileType = file.getName().substring(file.getName().lastIndexOf(".")+1);
//                            String url = OSSUploadUtil.uploadFile(file, fileType);
//                            logger.info("上传到oss地址："+url);
//                            //更新到服务器
//                            article.setImg_url(url);
//                            articleRepository.saveAndFlush(article);
//                            //更新数据到引擎
//                            articleSearchRepository.save(new ESArticle(article));
//                        }
//                    }
//                }
//            }
//        });
//        return ResultUtils.ok(true);
//    }

//    @PostMapping("/updateArticleChildCategory")
//    public Result<Boolean> updateArticleChildCategory(){
//        List<Article> articles = articleRepository.findAll();
//        List<Article> tempArticles = new ArrayList<>();
//        List<ESArticle> tempESArticles = new ArrayList<>();
//        articles.forEach(article -> {
//            if(!article.getLink().contains("https://github.com")){
//                article.setChild_category(1);
//                tempArticles.add(article);
//                tempESArticles.add(new ESArticle(article));
//            }
//        });
//        articleRepository.saveAll(tempArticles);
//        articleSearchRepository.saveAll(tempESArticles);
//        return ResultUtils.ok(true);
//    }

    /**
     * 上传文章图片
     *
     * @param file 文章图片文件
     * @return 图片的url
     */
    @ApiOperation(value = "上传文章图片", notes = "上传文章图片接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_img", value = "文章图片文件", required = true, dataType = "file")
    })
    @PostMapping("/uploadArticleImg")
    public Result<Map<String, String>> uploadArticleImg(@RequestParam(value = "article_img") MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            return ResultUtils.error(ResultCode.UPLOAD_FILE_EMPTY);
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResultUtils.error(ResultCode.UPLOAD_FILE_LIMIT);
        }
        Map<String, String> map = new HashMap<>();
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        map.put(file.getName(), OSSUploadUtil.uploadFile(FileUtils.multi2File(file), fileType));
        return ResultUtils.ok(map);
    }

    /**
     * 上传文章
     * @param articleInfo  文章表单对象
     * @param bindingResult 错误结果
     * @return 文章对象
     */
    @ApiOperation(value = "上传文章", notes = "上传文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "文章标题", required = true, dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = false, dataType = "string"),
            @ApiImplicitParam(name = "tag", value = "文章标签", required = false, dataType = "string"),
            @ApiImplicitParam(name = "contributor_id", value = "贡献者ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "category", value = "文章分类", required = true, dataType = "string"),
            @ApiImplicitParam(name = "child_category", value = "文章子分类", required = false, dataType = "int"),
            @ApiImplicitParam(name = "rank", value = "文章等级", required = true, dataType = "int"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = true, dataType = "string"),
            @ApiImplicitParam(name = "img_url", value = "上传文章图片的url", required = true, dataType = "string")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//上传添加文章，将文章相关缓存清空
    @PostMapping("/uploadArticle")
    public Result<Article> uploadArticle(@Valid Article articleInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        if(!LinkUtils.verifyURL(articleInfo.getLink()) || !LinkUtils.verifyURL(articleInfo.getImg_url())){
            return ResultUtils.error(ResultCode.UPLOAD_LINK_ERROR);
        }
        articleInfo.setDate(new Date());
        articleInfo.setMd_content(MarkdownUtils.getMdContent(articleInfo.getLink()));
        articleInfo.setWrap_link(LinkUtils.gererateShortUrl(articleInfo.getLink()));
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(articleInfo.getContributor_id());
        articleInfo.setUser(user);
        if (null != user) {
            articleInfo.setReview_status(user.getAdmin_status() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
            if(user.getAdmin_status() == AdminEnum.ADMIN.getAdmin_status()){//管理员  直接插入到es引擎  不限制上传次数
                articleInfo.setReview_status(1);
                //插入数据到引擎
                articleSearchRepository.save(new ESArticle(articleInfo));
            }else {//普通用户  限制上传次数（每天一篇）
                ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
                Integer count = 0;
                //验证  普通用户一天上传文章次数不能超过1次
                boolean exists = redisTemplate.hasKey(String.valueOf(articleInfo.getContributor_id()));
                if (exists) {
                    count = operations.get(String.valueOf(articleInfo.getContributor_id()));
                    if (count >= 1) {
                        return ResultUtils.error(ResultCode.UPLOAD_LIMIT);
                    }
                }
                articleInfo.setReview_status(0);
                //保存上传次数到redis  key为userid   value为次数
                operations.set(String.valueOf(articleInfo.getContributor_id()), count + 1, 1, TimeUnit.DAYS);
            }
        }else {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        articleService.saveArticle(articleInfo);
        return ResultUtils.ok(articleInfo);
    }

    /**
     * 文章编辑更新
     */
    @ApiOperation(value = "文章编辑更新", notes = "文章编辑更新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "contributor_id", value = "编辑者ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "title", value = "文章标题", required = false, dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = false, dataType = "string"),
            @ApiImplicitParam(name = "category", value = "文章分类", required = false, dataType = "string"),
            @ApiImplicitParam(name = "child_category", value = "文章子分类", required = false, dataType = "int"),
            @ApiImplicitParam(name = "rank", value = "文章等级", required = false, dataType = "int"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = false, dataType = "string"),
            @ApiImplicitParam(name = "img_url", value = "文章图片url", required = false, dataType = "string")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//更新文章，将文章相关缓存清空
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
        if(!StringUtils.isEmpty(articleInfo.getImg_url())){
            if(!LinkUtils.verifyURL(articleInfo.getImg_url())){
                return ResultUtils.error(ResultCode.UPLOAD_LINK_ERROR);
            }
        }
        //先查询该文章
        Article article = articleService.findArticleByArticleId(articleInfo.getArticle_id());
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(articleInfo.getContributor_id());
        if (null != user) {
            if (user.getAdmin_status() != AdminEnum.ADMIN.getAdmin_status() && articleInfo.getContributor_id() != article.getContributor_id()) {
                return ResultUtils.error(ResultCode.NO_EDIT_PERMITION);
            }
            article.setDate(new Date());
            article.setReview_status(user.getAdmin_status() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
        }else {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        BeanCopyUtil.beanCopyWithIngore(articleInfo, article, "contributor_id");
        articleService.saveArticle(article);
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    /**
     * 删除文章
     * @param article_id
     * @return
     */
    @ApiOperation(value = "删除文章", notes = "删除文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//删除文章，将文章相关缓存清空
    @GetMapping("/deleteArticle")
    public Result<Article> deleteArticle(@RequestParam("article_id") int article_id) {
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

    /**
     * 获取所有文章列表
     *
     * @param page 当前页数
     * @param size 返回数量
     * @return 当前页文章列表
     */
    @ApiOperation(value = "获取所有文章", notes = "获取所有文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleList")
    public Result<Article> getArticleList(@RequestParam("page") Integer page,
                                          @RequestParam("size") Integer size) {
        Page<Article> pages = articleService.findAllReviewedArticles(PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    /**
     * 根据分类获取所有文章列表
     *
     * @param page 当前页数
     * @param size 返回数量
     * @param category 文章分类
     * @return 当前页文章列表
     */
    @ApiOperation(value = "根据分类获取文章", notes = "根据分类获取文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "category", value = "文章分类", required = true, dataType = "string")
    })
    @Cacheable
    @PostMapping("/getArticleListByCategory")
    public Result<Article> getArticleListByCategory(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("category") String category){
        Page<Article> pages = articleService.findAllByCategory(category, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    /**
     * 点赞、取消点赞、反赞、取消反赞
     *
     * @param article_id 文章id
     * @param user_id    用户id
     * @param type       点赞/反赞类型    1文章  2评论
     * @param status     1 点赞  2反赞    0取消点赞/反赞(闲置状态)
     */
    @ApiOperation(value = "点赞或反赞", notes = "点赞或反赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "type", value = "点赞/反赞类型", required = true, dataType = "int"),
            @ApiImplicitParam(name = "status", value = "点赞/反赞状态", required = true, dataType = "int")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//将文章相关缓存清空
    @PostMapping("/starArticle")
    public Result<Stars> starArticle(@RequestParam("article_id") int article_id, @RequestParam("user_id") int user_id,
                                     @RequestParam("type") int type, @RequestParam("status") int status) {
        //先查看该用户是否已经点赞/反赞，如果没有点赞/反赞 则增加一条记录，如果已经点赞/反赞，查看点赞/反赞的状态status
        //如果该值为0  若点赞，则设置为1，若反赞，则设置为2     取消点赞/反赞，则设置为0
        Stars starts = starsService.findByUserIdAndArticleId(user_id, article_id);
        Article article = articleService.findArticleByArticleId(article_id);
        String msg = "操作成功!";
        if(article == null){
            return ResultUtils.error("文章ID不存在!");
        }
        if(starts == null){
            //不存在   插入点赞/反赞
            starts = new Stars();
            starts.setArticle_id(article_id);
            starts.setUser_id(user_id);
            starts.setStatus(status);
            starts.setType(type);
            starts.setDate(new Date());
            //往article表中添加star
            if(status == 1){//点赞
                article.setStars(article.getStars()+1);
                msg = "点赞成功!";
            }else if (status == 2){
                article.setUn_stars(article.getUn_stars()+1);
                msg = "反赞成功!";
            }
        }else {
            //往article表中添加star/unstar
            //存在数据   则判断点赞/反赞状态
            if(starts.getStatus() == StarStatusEnum.STAR_STATUS.getStar_status()){
                if(status == 0){//取消点赞
                    starts.setStatus(0);
                    article.setStars(article.getStars()-1);
                    msg = "取消点赞成功!";
                }else if(status == 2){//反赞
                    starts.setStatus(2);
                    article.setStars(article.getStars()-1);
                    article.setUn_stars(article.getStars()+1);
                    msg = "反赞成功!";
                }
            }else if(starts.getStatus() == StarStatusEnum.UN_STAR_STATUS.getStar_status()){//当前反赞
                if(status == 0){//取消反赞
                    starts.setStatus(0);
                    article.setUn_stars(article.getStars()-1);
                    msg = "取消反赞成功!";
                }else if(status == 1){//点赞
                    starts.setStatus(1);
                    article.setStars(article.getStars()+1);
                    article.setUn_stars(article.getStars()-1);
                    msg = "点赞成功!";
                }
            }else {//当前0（闲置）
                if(status == 1){//点赞
                    starts.setStatus(1);
                    article.setStars(article.getStars()+1);
                    msg = "点赞成功!";
                }else if(status == 2){//反赞
                    starts.setStatus(2);
                    article.setUn_stars(article.getStars()+1);
                    msg = "反赞成功!";
                }
            }
        }
        starsService.saveStar(starts);
        articleService.saveArticle(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(msg);
    }

    /**
     * 获取文章的所有点赞者
     *
     * @param page       当前页数
     * @param size       返回数量
     * @param article_id 文章id
     * @return
     */
    @ApiOperation(value = "获取文章点赞者", notes = "获取文章点赞者接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleStarers")
    public Result<User> getArticleStarers(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("article_id") int article_id) {
        Page<Stars> pages = starsService.findStarsByArticleId(article_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));//
        List<User> users = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> user_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> user_ids.add(stars.getUser_id()));
            users = userService.findUsersByUserIdIn(user_ids);
        }
        return ResultUtils.ok(users);
    }

    /**
     * 获取我  点赞的文章列表
     *
     * @param page
     * @param size
     * @param user_id
     * @return
     */
    @ApiOperation(value = "获取我的点赞文章列表", notes = "获取我的点赞文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getMyStarArticles")
    public Result<Article> getMyStarArticles(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("user_id") int user_id) {
        User user = userService.findUserByUserId(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Stars> pages = starsService.findStarsByUserId(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articles = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> article_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> article_ids.add(stars.getArticle_id()));
            articles = articleService.findArticlesByArticleIdIn(article_ids);
        }
        return ResultUtils.ok(articles);
    }

    /**
     * 获取我  贡献的文章列表
     *
     * @param page    当前页
     * @param size    返回数量
     * @param user_id 用户ID
     * @return
     */
    @ApiOperation(value = "获取我的上传文章列表", notes = "获取我的上传文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getMyContributeArticles")
    public Result<Article> getMyContributeArticles(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("user_id") int user_id) {
        User user = userService.findUserByUserId(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error(ResultCode.INVALID_USER);
        }
        Page<Article> pages = articleService.findAllByContributorId(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articleList = pages.getContent();
        return ResultUtils.ok(articleList);
    }

    /**
     * 获取某用户对某篇文章的点赞/反赞状态   0（未操作）  1已点赞   2已反赞
     * @param user_id 用户id
     * @param article_id 文章id
     * @return  0（未操作）  1已点赞   2已反赞
     */
    @ApiOperation(value = "是否某用户点赞过某文章", notes = "是否某用户点赞过某文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @PostMapping("/getStarStatus")
    public Result getStarStatus(@RequestParam int user_id, int article_id){
        //获取我的点赞文章列表
        Stars star = starsService.findByUserIdAndArticleId(user_id, article_id);
        if(star != null){
            return ResultUtils.ok(star.getStatus());
        }
        return ResultUtils.ok(0);
    }

    /**
     * 更新文章浏览量
     *
     * @param article_id
     * @return
     */
    @ApiOperation(value = "更新文章浏览量", notes = "更新文章浏览量接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @PostMapping("/viewArticle")
    public Result<Article> viewArticle(@RequestParam("article_id") int article_id) {
        //@RequestParam(value = "user_id", required = false)int user_id
        //先查询该文章
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

    /**
     * 获取当天文章总浏览量
     *
     */
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

    /**
     * 获取开源库的总收录数
     *
     */
    @ApiOperation(value = "获取开源库的总收录数", notes = "获取开源库的总收录数接口")
    @Cacheable
    @PostMapping("/getArticleTotals")
    public Result<Integer> getArticleTotals(){
        return ResultUtils.ok(articleService.findAllArticleTotals());
    }

    /**
     * 获取文章详情对应的MD文本
     *
     * @param article_id 文章ID
     * @return MD文本
     */
    @ApiOperation(value = "获取文章详情对应的MD文本", notes = "获取文章详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleDetail")
    public Result<String> getArticleDetail(@RequestParam int article_id) {
        //先查询该文章
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        return ResultUtils.ok(article.getMd_content());
    }

    /**
     * 文章审核
     * @param user_id  审核者id
     * @param article_id  文章id
     * @param is_pass 是否通过审核
     * @return
     */
    @ApiOperation(value = "文章审核", notes = "文章审核接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_id", value = "审核者ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "is_pass", value = "是否通过审核", required = true, dataType = "boolean")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//删除文章，将文章相关缓存清空
    @PostMapping("/reviewArticle")
    public Result<Boolean> reviewArticle(@RequestParam int user_id, @RequestParam int article_id, @RequestParam boolean is_pass){
        if(StringUtils.isEmpty(user_id) || StringUtils.isEmpty(article_id)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        //先查询该文章
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error(ResultCode.NO_FIND_ARTICLE);
        }
        //判断是否为管理员
        User user = userService.findUserByUserId(user_id);
        if (null != user) {
            if (user.getAdmin_status() != AdminEnum.ADMIN.getAdmin_status()) {
                return ResultUtils.error(ResultCode.NO_REVIEW_PERMITION);
            }
            if(is_pass){
                article.setReview_status(1);
            }else {
                article.setReview_status(-1);
            }
            article.setDate(new Date());
            articleService.saveArticle(article);
            //插入数据到引擎
            articleSearchRepository.save(new ESArticle(article));
        }
        return ResultUtils.ok("审核操作成功!");
    }
}
