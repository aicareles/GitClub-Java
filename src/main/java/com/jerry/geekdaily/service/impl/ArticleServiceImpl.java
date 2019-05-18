package com.jerry.geekdaily.service.impl;

import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.domain.Stars;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.dto.StarsDTO;
import com.jerry.geekdaily.dto.UpdateArticleDTO;
import com.jerry.geekdaily.enums.AdminEnum;
import com.jerry.geekdaily.enums.StarStatusEnum;
import com.jerry.geekdaily.exception.ParamJsonException;
import com.jerry.geekdaily.exception.ValidException;
import com.jerry.geekdaily.repository.ArticleRepository;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import com.jerry.geekdaily.service.ArticleService;
import com.jerry.geekdaily.service.CommentService;
import com.jerry.geekdaily.service.StarsService;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.BeanCopyUtil;
import com.jerry.geekdaily.util.LinkUtils;
import com.jerry.geekdaily.util.MarkdownUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StarsService starsService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Integer findAllArticleTotals() {
        return articleRepository.findAllArticleTotals();
    }

    @Override
    public void uploadArticle(Article article) {
        if(!LinkUtils.verifyURL(article.getLink()) || !LinkUtils.verifyURL(article.getImgUrl())){
            throw new ValidException("上传图片或文章链接格式错误");
        }
        article.setMdContent(MarkdownUtils.getMdContent(article.getLink()));
        article.setWrapLink(LinkUtils.gererateShortUrl(article.getLink()));
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(article.getContributorId());
        article.setUser(user);
        if (null != user) {
            article.setReviewStatus(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
            if(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status()){//管理员  直接插入到es引擎  不限制上传次数
                article.setReviewStatus(1);
            }else {//普通用户  限制上传次数（每天一篇）
                ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
                Integer count = 0;
                //验证  普通用户一天上传文章次数不能超过1次
                boolean exists = redisTemplate.hasKey(String.valueOf(article.getContributorId()));
                if (exists) {
                    count = operations.get(String.valueOf(article.getContributorId()));
                    if (count >= 1) {
                        throw new ValidException("当天文章上传次数已达上限");
                    }
                }
                article.setReviewStatus(0);
                //保存上传次数到redis  key为userid   value为次数
                operations.set(String.valueOf(article.getContributorId()), count + 1, 1, TimeUnit.DAYS);
            }
        }else {
            throw new ValidException("用户不存在");
        }
        saveArticle(article);
    }

    @Override
    public void updateArticle(UpdateArticleDTO articleDTO) {
        if(!StringUtils.isEmpty(articleDTO.getLink())){
            if(!LinkUtils.verifyURL(articleDTO.getLink())){
                throw new ValidException("上传图片或文章链接格式错误");
            }
        }
        if(!StringUtils.isEmpty(articleDTO.getImgUrl())){
            if(!LinkUtils.verifyURL(articleDTO.getImgUrl())){
                throw new ValidException("上传图片或文章链接格式错误");
            }
        }
        Article article = findArticleByArticleId(articleDTO.getArticleId());
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
        }
        //判断是否为管理员   若为管理员则直接通过审核
        User user = userService.findUserByUserId(articleDTO.getContributorId());
        if (null != user) {
            if (user.getAdminStatus() != AdminEnum.ADMIN.getAdmin_status() && articleDTO.getContributorId() != article.getContributorId()) {
                throw new ValidException("没有修改文章权限");
            }
            article.setReviewStatus(user.getAdminStatus() == AdminEnum.ADMIN.getAdmin_status() ? 1 : 0);
        }else {
            throw new ValidException("用户不存在");
        }
        BeanCopyUtil.beanCopyWithIngore(articleDTO, article, "contributorId");
        saveArticle(article);
    }

    @Override
    public void deleteArticle(int article_id) {
        Article article = findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
        }
        articleRepository.deleteById(article_id);
        //删除中间表stars中的article_id的所有数据
        starsService.deleteByArticleId(article_id);
        commentService.deleteAllByArticleId(article_id);
        articleSearchRepository.deleteById(article_id);
    }

    @Override
    public Page<Article> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> findAllReviewedArticles(Pageable pageable) {
        return articleRepository.findAllReviewedArticles(pageable);
    }

    @Override
    public String starArticle(StarsDTO starsDTO) {
        //先查看该用户是否已经点赞，如果没有点赞 则增加一条star记录
        Article article = findArticleByArticleId(starsDTO.getArticleId());
        String msg = "操作成功!";
        if(article == null){
           throw new ValidException("未找到相应文章");
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
        saveArticle(article);
        return msg;
    }

    @Override
    public void viewArticle(int article_id) {
        Article article = findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
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
        saveArticle(article);
    }

    @Override
    public void reviewArticle(int article_id, boolean is_pass) {
        if(StringUtils.isEmpty(is_pass) || StringUtils.isEmpty(article_id)){
            throw new ParamJsonException();
        }
        Article article = findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            throw new ValidException("未找到相应文章");
        }
        article.setReviewStatus(is_pass ? 1 : -1);
        saveArticle(article);
    }

    @Override
    public Article findArticleByArticleId(int article_id) {
        return articleRepository.findArticleByArticleId(article_id);
    }

    @Override
    public Page<Article> findAllByContributorId(int user_id, Pageable pageable) {
        return articleRepository.findAllByContributorId(user_id, pageable);
    }

    @Override
    public List<Article> findArticlesByArticleIdIn(List<Integer> article_ids) {
        return articleRepository.findArticlesByArticleIdIn(article_ids);
    }

    @Override
    public Page<Article> findAllByCategory(String category, Pageable pageable) {
        return articleRepository.findAllByCategoryIgnoreCase(category, pageable);
    }

    @Override
    public Page<Article> randomFindFiveArticles(Date date, Date endDate, Pageable pageable) {
        return articleRepository.randomFindFiveArticles(date, endDate, pageable);
    }

    @Override
    public void saveArticle(Article article) {
        articleRepository.saveAndFlush(article);
        articleSearchRepository.save(new ESArticle(article));
    }

    @Override
    public int getArticleTotalViews() {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        boolean exists = redisTemplate.hasKey(Constans.RedisKey.ARTICLE_TOTAL_VIEWS);
        if(!exists){//当天还没有阅读数   赋值初始值为400-800的一个随机数
            int random = new Random().nextInt(400)+400;
            operations.set(Constans.RedisKey.ARTICLE_TOTAL_VIEWS, random, 1, TimeUnit.DAYS);
            return random;
        }else {
            return operations.get(Constans.RedisKey.ARTICLE_TOTAL_VIEWS);
        }
    }
}
