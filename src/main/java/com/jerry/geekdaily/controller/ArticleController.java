package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.*;
import com.jerry.geekdaily.repository.*;
import com.jerry.geekdaily.util.LinkUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 上传文章的web页面
     * @return
     */
    @GetMapping(value = ("/upload_article"))
    public ModelAndView upload_article() {
        return new ModelAndView("upload_article");
    }

    /**
     * 查看文章列表的web页面
     * @return
     */
    @GetMapping(value = ("/index"))
    public ModelAndView index(){
        List<Article> articles = articleRepository.findAll(new Sort(Sort.Direction.DESC, "date"));
        ModelAndView model = new ModelAndView("article_list");
        model.addObject("articleList", articles);
        return model;
    }

    /**
     * 小程序webview跳转
     * @param url
     * @return
     */
    @GetMapping(value = "/wxWebView")
    public ModelAndView wxWebView(@RequestParam("url")String url){
        ModelAndView model = new ModelAndView("wx_web");
        model.addObject("url",url);
        return model;
    }

    /**
     * 上传文章
     * @param title 标题
     * @param des 描述
     * @param tag 文章标签
     * @param file 文章大图
     * @return 文章实体对象
     */
    @ApiOperation(value = "上传文章", notes = "上传文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "文章标题", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = true, dataType = "string"),
            @ApiImplicitParam(name = "tag", value = "文章标签", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "contributor", value = "贡献者", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "contributor_id", value = "贡献者ID", required = false ,dataType = "int"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "md_content", value = "MD风格文本", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "article_img", value = "文章大图", required = true ,dataType = "file")
    })
    @CacheEvict(value="ArticleController", allEntries=true)//上传添加文章，将文章相关缓存清空
    @PostMapping("/uploadArticle")
    public Result<Article> uploadArticle(@RequestParam(value = "title") String title,
                                @RequestParam(value = "des") String des,
                                @RequestParam(value = "tag") String tag,
                                @RequestParam(value = "contributor") String contributor,
                                @RequestParam(value = "contributor_id") int contributor_id,
                                @RequestParam(value = "link") String link,
                                @RequestParam(value = "md_content")String md_content,
                                @RequestParam("article_img") MultipartFile file) {
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(des) || StringUtils.isEmpty(tag)
                || StringUtils.isEmpty(link) || StringUtils.isEmpty(file)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        String fileName = uploadImg(file);
        if(StringUtils.isEmpty(fileName)){
            return ResultUtils.error("上传图片失败");
        }
        Article article = new Article();
        article.setTitle(title);
        article.setDes(des);
        article.setTag(tag);
        article.setImg_url(FILE_FOLDER+fileName);
        article.setContributor(contributor);
        article.setContributor_id(contributor_id);
        article.setLink(link);
        article.setMd_content(md_content);
        String wrap_link = LinkUtils.gererateShortUrl(link);
        article.setWrap_link(wrap_link);
        article.setDate(new Date());
        articleRepository.save(article);
        logger.info("提交成功!");
        //插入数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    /**
     * 文章编辑更新
     * @param title 标题
     * @param des 描述
     * @param tag 文章标签
     * @param file 文章大图
     * @return 文章实体对象
     */
    @ApiOperation(value = "文章编辑更新", notes = "文章编辑更新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "title", value = "文章标题", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "des", value = "文章描述", required = false, dataType = "string"),
            @ApiImplicitParam(name = "tag", value = "文章标签", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "link", value = "文章链接", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "md_content", value = "MD风格文本", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "article_img", value = "文章大图", required = false ,dataType = "file")
    })
    @CacheEvict(value="ArticleController", allEntries=true)//更新文章，将文章相关缓存清空
    @PostMapping("/updateArticle")
    public Result<Article> uploadArticle(@RequestParam(value = "article_id") int article_id,
                                         @RequestParam(value = "title") String title,
                                         @RequestParam(value = "des") String des,
                                         @RequestParam(value = "tag") String tag,
                                         @RequestParam(value = "link") String link,
                                         @RequestParam(value = "md_content")String md_content,
                                         @RequestParam("article_img") MultipartFile file) {
        if(StringUtils.isEmpty(article_id)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        //先查询该文章
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
            return ResultUtils.error("未找到相应文章");
        }
        if(!file.isEmpty()){
            String fileName = uploadImg(file);
            if(StringUtils.isEmpty(fileName)){
                return ResultUtils.error("上传图片失败");
            }
            article.setImg_url(FILE_FOLDER+fileName);
        }
        if(!StringUtils.isEmpty(title)){
            article.setTitle(title);
        }
        if(!StringUtils.isEmpty(des)){
            article.setDes(des);
        }
        if(!StringUtils.isEmpty(tag)){
            article.setTag(tag);
        }
        if(!StringUtils.isEmpty(link)){
            article.setLink(link);
        }
        if(!StringUtils.isEmpty(md_content)){
            article.setMd_content(md_content);
        }
        article.setDate(new Date());
        articleRepository.saveAndFlush(article);
        logger.info("提交成功!");
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(article);
    }

    /**
     * 上传文件
     * @param file
     * @return  文件路径
     */
    private String uploadImg(MultipartFile file) {
        String dateName = null;
        //文章大图文件上传
        try {
            File path = null;
            try {
                path = new File(ResourceUtils.getURL("classpath:").getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(!path.exists()) path = new File("");
            System.out.println("path:"+path.getAbsolutePath());
            //如果上传目录为/static/images/upload/，则可以如下获取：
            File upload = new File(path.getAbsolutePath(),"static/images/upload/");
            if(!upload.exists()) upload.mkdirs();
            System.out.println("upload url:"+upload.getAbsolutePath());
            //保存时的文件名(时间戳生成)
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            dateName = df.format(calendar.getTime())+file.getOriginalFilename();
            Path path1 = Paths.get(upload.getAbsolutePath(), dateName);
            byte[] bytes = file.getBytes();
            Files.write(path1, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return dateName;
    }

    /**
     *  删除文章
     * @param article_id
     * @return
     */
    @ApiOperation(value = "删除文章", notes = "删除文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @CacheEvict(value="ArticleController", allEntries=true)//删除文章，将文章相关缓存清空
    @GetMapping("/deleteArticle")
    public Result<Article> deleteArticle(@RequestParam("article_id")int article_id){
        if(StringUtils.isEmpty(article_id)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
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
     * @param page 当前页数
     * @param size 返回数量
     * @return 当前页文章列表
     */
    @ApiOperation(value = "获取所有文章", notes = "获取所有文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true ,dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleList")
    public Result<Article> getArticleList(@Param("page")Integer page, @RequestParam("size")Integer size) {
        if(StringUtils.isEmpty(page)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Page<Article> pages = articleRepository.findAll(PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));
        return ResultUtils.ok(pages.getContent());
    }

    /**
     * 点赞或取消点赞
     * @param article_id  文章id
     * @param user_id 用户id
     * @param type 点赞类型    1文章点赞  2评论点赞
     * @param status 1 点赞  0取消点赞
     */
    @ApiOperation(value = "点赞或取消点赞", notes = "点赞或取消点赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "type", value = "点赞类型", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "status", value = "点赞状态", required = true ,dataType = "int")
    })
    @PostMapping("/articleStar")
    public Result<Stars> articleStar(@RequestParam("article_id")int article_id, @RequestParam("user_id")int user_id,
                                     @RequestParam("type")int type, @RequestParam("status")int status){
        //先查看该用户是否已经点赞，如果没有点赞 则增加一条记录，如果已经点赞，查看点赞的状态status,如果该值为0 则设置为1，如果为1则设置为0；
        Stars starts = starsRepository.findByUser_idAndArticle_id(user_id, article_id);
        Optional<Article> optional = articleRepository.findById(article_id);
        String msg = "点赞成功!";
        if(!optional.isPresent()){
            return ResultUtils.error("文章ID不存在!");
        }
        Article article = optional.get();
        if(starts == null){
            //不存在   插入点赞
            starts = new Stars();
            starts.setArticle_id(article_id);
            starts.setUser_id(user_id);
            starts.setStatus(1);
            starts.setType(type);
            starts.setDate(new Date());
            //往article表中添加star
            article.setStars(article.getStars()+1);
        }else {
            //往article表中添加star
            //存在数据   则判断点赞状态
            if(starts.getStatus() == 1){
                //取消
                starts.setStatus(0);
                article.setStars(article.getStars()-1);
                msg = "取消点赞成功!";
            }else {
                //点赞
                starts.setStatus(1);
                article.setStars(article.getStars()+1);
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
     * @param page 当前页数
     * @param size  返回数量
     * @param article_id  文章id
     * @return
     */
    @ApiOperation(value = "获取文章点赞者", notes = "获取文章点赞者接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleStarers")
    public Result<User> getArticleStarers(@RequestParam("page")Integer page, @RequestParam("size")Integer size, @RequestParam("article_id")int article_id){
        Page<Stars> pages = starsRepository.findStarsByArticle_id(article_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));//
        List<Stars> contents = pages.getContent();
        List<User> userList = new ArrayList<>();
        for (Stars stars : contents){
            User user = userRepository.findUserByUser_id(stars.getUser_id());
            userList.add(user);
        }
        return ResultUtils.ok(userList);
    }

    /**
     * 获取我  点赞的文章列表
     * @param page
     * @param size
     * @param user_id
     * @return
     */
    @ApiOperation(value = "获取我的点赞文章列表", notes = "获取我的点赞文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getMyStarArticles")
    public Result<Article> getMyStarArticles(@RequestParam("page")Integer page, @RequestParam("size")Integer size, @RequestParam("user_id")int user_id){
        User user = userRepository.findUserByUser_id(user_id);
        if(StringUtils.isEmpty(user)){
            return ResultUtils.error("未找到相关用户!");
        }
        Page<Stars> pages = starsRepository.findStarsByUser_id(user_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));
        List<Stars> contents = pages.getContent();
        List<Article> articleList = new ArrayList<>();
        for (Stars stars: contents) {
            Article article = articleRepository.findArticleByArticle_id(stars.getArticle_id());
            articleList.add(article);
        }
        return ResultUtils.ok(articleList);
    }

    /**
     * 获取我  贡献的文章列表
     * @param page 当前页
     * @param size 返回数量
     * @param user_id 用户ID
     * @return
     */
    @ApiOperation(value = "获取我的上传文章列表", notes = "获取我的上传文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true, dataType = "int")
    })
    @Cacheable
    @PostMapping("/getMyContributeArticles")
    public Result<Article> getMyContributeArticles(@RequestParam("page")Integer page, @RequestParam("size")Integer size, @RequestParam("user_id")int user_id){
        User user = userRepository.findUserByUser_id(user_id);
        if(StringUtils.isEmpty(user)){
            return ResultUtils.error("未找到相关用户!");
        }
        Page<Article> pages = articleRepository.findAllByContributor_id(user_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));
        List<Article> articleList = pages.getContent();
        return ResultUtils.ok(articleList);
    }

    /**
     * 获取我评论过的文章列表
     * @param page 当前页数
     * @param size 返回数量
     * @param user_id 用户id
     * @return 文章列表
     */
    @ApiOperation(value = "获取我评论过的文章列表", notes = "获取我评论过的文章列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true ,dataType = "int")
    })
    @Cacheable
    @PostMapping("/getMyCommentArticles")
    public Result<Article> getMyCommentArticles(@RequestParam("page")Integer page, @RequestParam("size")Integer size, @RequestParam("user_id")int user_id){
        User user = userRepository.findUserByUser_id(user_id);
        if(StringUtils.isEmpty(user)){
            return ResultUtils.error("未找到相关用户!");
        }
        Page<Comment> pages = commentRepository.getAllByFrom_uid(user_id, PageRequest.of(page,size, new Sort(Sort.Direction.DESC, "date")));
        List<Comment> comments = pages.getContent();
        List<Article> articleList = new ArrayList<>();
        for(Comment comment : comments){
            Article article = articleRepository.findArticleByArticle_id(comment.getArticle_id());
            articleList.add(article);
        }
        return ResultUtils.ok(articleList);
    }

    /**
     * 是否某用户点赞过某文章
     * @param user_id 用户id
     * @param article_id 文章id
     * @return  true已点赞  false未点赞
     */
    @ApiOperation(value = "是否某用户点赞过某文章", notes = "是否某用户点赞过某文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_id", value = "用户ID", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @PostMapping("/isStarArticle")
    public Result isStarArticle(@RequestParam int user_id, int article_id){
        //获取我的点赞文章列表
        List<Stars> stars = starsRepository.findAllByUser_id(user_id);
        if(stars.size() > 0){
            for(Stars s : stars){
                if(s.getArticle_id() == article_id){
                    return ResultUtils.ok(true);
                }
            }
        }
        return ResultUtils.ok(false);
    }

    /**
     * 更新文章浏览量
     * @param article_id
     * @return
     */
    @ApiOperation(value = "更新文章浏览量", notes = "更新文章浏览量接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @PostMapping("/viewArticle")
    public Result<Article> viewArticle(@RequestParam("article_id")int article_id){
        //@RequestParam(value = "user_id", required = false)int user_id
        //先查询该文章
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
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
     * @param article_id 文章ID
     * @return MD文本
     */
    @ApiOperation(value = "获取文章详情对应的MD文本", notes = "获取文章详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @Cacheable
    @PostMapping("/getArticleDetail")
    public Result<String> getArticleDetail(@RequestParam int article_id){
        //先查询该文章
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
            return ResultUtils.error("未找到相应文章");
        }
        return ResultUtils.ok(article.getMd_content());
    }

}
