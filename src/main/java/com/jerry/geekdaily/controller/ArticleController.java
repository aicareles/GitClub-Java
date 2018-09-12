package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.*;
import com.jerry.geekdaily.util.LinkUtils;
import com.jerry.geekdaily.util.MarkdownUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@CacheConfig(cacheNames = "ArticleController")
@Api(value = "ArticleController", description = "文章管理相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class ArticleController {

    private final static Logger logger = LoggerFactory.getLogger(ArticleController.class);
    //    private final static String FILE_FOLDER = "http://47.104.93.195:8090/geekdaily/images/upload/";
    private final static String FILE_FOLDER = "https://502tech.com/geekdaily/images/upload/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private StarsRepository starsRepository;

    @Autowired
    private CommentRepository commentRepository;

    //全文搜索引擎   数据库数据改变时伴随着  搜索引擎中的数据的增删改查
    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    @Autowired
    private RedisTemplate redisTemplate;

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
        Map<String, String> map = new HashMap<>();
        map.put(file.getName(), uploadImg(file));
        return ResultUtils.ok(map);
    }

    /**
     * 上传文章
     *
     * @param title       标题
     * @param des         描述
     * @param tag         文章标签
     * @param article_img 文章图片url
     * @return 文章实体对象
     */
    @ApiOperation(value = "上传文章", notes = "上传文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "文章标题", required = true, dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = false, dataType = "string"),
            @ApiImplicitParam(name = "tag", value = "文章标签", required = false, dataType = "string"),
            @ApiImplicitParam(name = "contributor_id", value = "贡献者ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "category", value = "文章分类", required = true, dataType = "string"),
            @ApiImplicitParam(name = "rank", value = "文章等级", required = true, dataType = "int"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = true, dataType = "string"),
            @ApiImplicitParam(name = "article_img", value = "上传文章图片的url", required = true, dataType = "string")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//上传添加文章，将文章相关缓存清空
    @PostMapping("/uploadArticle")
    public Result<Article> uploadArticle(@RequestParam(value = "title") String title,
                                         @RequestParam(value = "des") String des,
                                         @RequestParam(value = "tag", required = false) String tag,
                                         @RequestParam(value = "contributor_id") int contributor_id,
                                         @RequestParam(value = "category") String category,
                                         @RequestParam(value = "rank") int rank,
                                         @RequestParam(value = "link") String link,
                                         @RequestParam(value = "article_img") String article_img) {
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(contributor_id) || StringUtils.isEmpty(link)
                || StringUtils.isEmpty(category) || StringUtils.isEmpty(rank) || StringUtils.isEmpty(article_img)) {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Article article = new Article();
        article.setTitle(title);
        article.setDes(des);
        article.setTag(tag);
        article.setContributor_id(contributor_id);
        article.setLink(link);
        article.setCategory(category);
        article.setRank(rank);
        article.setMd_content(MarkdownUtils.getMdContent(link));
        String wrap_link = LinkUtils.gererateShortUrl(link);
        article.setWrap_link(wrap_link);
        article.setDate(new Date());
        article.setImg_url(article_img);
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userRepository.findUserByUser_id(contributor_id);
        article.setUser(user);
        if (null != user) {
            article.setReview_status(user.getAdmin_status() == 1 ? 1 : 0);
            if(user.getAdmin_status() == 1){//管理员  直接插入到es引擎  不限制上传次数
                article.setReview_status(1);
                //插入数据到引擎
                articleSearchRepository.save(new ESArticle(article));
            }else {//普通用户  限制上传次数（每天一篇）
                ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
                Integer count = 0;
                //验证  普通用户一天上传文章次数不能超过1次
                boolean exists = redisTemplate.hasKey(String.valueOf(contributor_id));
                if (exists) {
                    count = operations.get(String.valueOf(contributor_id));
                    if (count >= 1) {
                        return ResultUtils.error("当天文章上传次数已达上限!");
                    }
                }
                article.setReview_status(0);
                //保存上传次数到redis  key为userid   value为次数
                operations.set(String.valueOf(contributor_id), count + 1, 1, TimeUnit.DAYS);
            }
        }
        articleRepository.save(article);
        return ResultUtils.ok(article);
    }

    /**
     * 文章编辑更新
     *
     * @param article_id  文章ID
     * @param editor_id  编辑者ID
     * @param title       标题
     * @param des         描述
     * @param tag         文章标签
     * @param article_img 文章图片url
     * @return 文章实体对象
     */
    @ApiOperation(value = "文章编辑更新", notes = "文章编辑更新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "editor_id", value = "编辑者ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "title", value = "文章标题", required = false, dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = false, dataType = "string"),
            @ApiImplicitParam(name = "tag", value = "文章标签", required = false, dataType = "string"),
            @ApiImplicitParam(name = "category", value = "文章分类", required = false, dataType = "string"),
            @ApiImplicitParam(name = "rank", value = "文章等级", required = false, dataType = "int"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = false, dataType = "string"),
            @ApiImplicitParam(name = "md_content", value = "MD风格文本", required = false, dataType = "string"),
            @ApiImplicitParam(name = "article_img", value = "文章图片url", required = false, dataType = "string")
    })
    @CacheEvict(value = "ArticleController", allEntries = true)//更新文章，将文章相关缓存清空
    @PostMapping("/updateArticle")
    public Result<Article> uploadArticle(@RequestParam(value = "article_id") int article_id,
                                         @RequestParam(value = "editor_id") int editor_id,
                                         @RequestParam(value = "title", required = false) String title,
                                         @RequestParam(value = "des", required = false) String des,
                                         @RequestParam(value = "tag", required = false) String tag,
                                         @RequestParam(value = "category", required = false) String category,
                                         @RequestParam(value = "rank", required = false) int rank,
                                         @RequestParam(value = "link", required = false) String link,
                                         @RequestParam(value = "md_content", required = false) String md_content,
                                         @RequestParam(value = "article_img", required = false) String article_img) {
        if (StringUtils.isEmpty(article_id) || StringUtils.isEmpty(editor_id)) {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        //先查询该文章
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未找到相应文章");
        }
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userRepository.findUserByUser_id(editor_id);
        if (null != user) {
            if (user.getAdmin_status() != 1 && editor_id != article.getContributor_id()) {
                return ResultUtils.error(ResultCode.NO_EDIT_PERMITION);
            }
            article.setReview_status(user.getAdmin_status() == 1 ? 1 : 0);
        }
        if (!StringUtils.isEmpty(article_img)) {
            article.setImg_url(article_img);
        }
        if(!StringUtils.isEmpty(category)){
            article.setCategory(category);
        }
        if(!StringUtils.isEmpty(rank)){
            article.setRank(rank);
        }
        if (!StringUtils.isEmpty(title)) {
            article.setTitle(title);
        }
        if (!StringUtils.isEmpty(des)) {
            article.setDes(des);
        }
        if (!StringUtils.isEmpty(tag)) {
            article.setTag(tag);
        }
        if (!StringUtils.isEmpty(link)) {
            article.setLink(link);
        }
        if (!StringUtils.isEmpty(md_content)) {
            article.setMd_content(md_content);
        }
        article.setDate(new Date());
        articleRepository.saveAndFlush(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return 文件路径
     */
    private String uploadImg(MultipartFile file) {
        String dateName = null;
        if (file == null || file.isEmpty() || file.getSize() == 0) return dateName;
        //文章大图文件上传
        try {
            File path = null;
            try {
                path = new File(ResourceUtils.getURL("classpath:").getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (!path.exists()) path = new File("");
            System.out.println("path:" + path.getAbsolutePath());
            //如果上传目录为/static/images/upload/，则可以如下获取：
            File upload = new File(path.getAbsolutePath(), "static/images/upload/");
            if (!upload.exists()) upload.mkdirs();
            System.out.println("upload url:" + upload.getAbsolutePath());
            //保存时的文件名(时间戳生成)
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            dateName = df.format(calendar.getTime()) + file.getOriginalFilename();
            Path path1 = Paths.get(upload.getAbsolutePath(), dateName);
            byte[] bytes = file.getBytes();
            Files.write(path1, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return FILE_FOLDER+dateName;
    }

    /**
     * 删除文章
     *
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
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未找到相关文章");
        }
        articleRepository.deleteById(article_id);
        //删除中间表stars中的article_id的所有数据
        starsRepository.deleteAllByArticle_id(article_id);
        //删除评论表中的所有关于该文章的评论
        commentRepository.deleteAllByArticle_id(article_id);
        //更新数据到引擎
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
    public Result<Article> getArticleList(@Param("page") Integer page,
                                          @RequestParam("size") Integer size) {
        Page<Article> pages = articleRepository.findAllReviewedArticles(PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
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
        Stars starts = starsRepository.findByUser_idAndArticle_id(user_id, article_id);
        Optional<Article> optional = articleRepository.findById(article_id);
        String msg = "操作成功!";
        if(!optional.isPresent()){
            return ResultUtils.error("文章ID不存在!");
        }
        Article article = optional.get();
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
            if(starts.getStatus() == 1){
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
            }else if(starts.getStatus() == 2){//当前反赞
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
        starsRepository.saveAndFlush(starts);
        articleRepository.saveAndFlush(article);
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
        Page<Stars> pages = starsRepository.findStarsByArticle_id(article_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));//
        List<User> users = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> user_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> user_ids.add(stars.getUser_id()));
            users = userRepository.findUsersByUser_idIn(user_ids);
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
        User user = userRepository.findUserByUser_id(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error("未找到相关用户!");
        }
        Page<Stars> pages = starsRepository.findStarsByUser_id(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articles = new ArrayList<>();
        if(pages.getContent().size() > 0){
            List<Integer> article_ids = new ArrayList<>();
            pages.getContent().forEach(stars -> article_ids.add(stars.getArticle_id()));
            articles = articleRepository.findArticlesByArticle_idIn(article_ids);
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
        User user = userRepository.findUserByUser_id(user_id);
        if (StringUtils.isEmpty(user)) {
            return ResultUtils.error("未找到相关用户!");
        }
        Page<Article> pages = articleRepository.findAllByContributor_id(user_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
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
        Stars star = starsRepository.findByUser_idAndArticle_id(user_id, article_id);
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
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未找到相应文章");
        }
        article.setViews(article.getViews() + 1);
        articleRepository.saveAndFlush(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
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
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未找到相应文章");
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
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未找到相应文章");
        }
        //判断是否为管理员
        User user = userRepository.findUserByUser_id(user_id);
        if (null != user) {
            if (user.getAdmin_status() != 1) {
                return ResultUtils.error(ResultCode.NO_REVIEW_PERMITION);
            }
            if(is_pass){
                article.setReview_status(1);
            }else {
                article.setReview_status(-1);
            }
            article.setDate(new Date());
            articleRepository.saveAndFlush(article);
            //插入数据到引擎
            articleSearchRepository.save(new ESArticle(article));
        }
        return ResultUtils.ok("审核操作成功!");
    }

}
