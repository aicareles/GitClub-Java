package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import com.jerry.geekdaily.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SheduledTaskController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

//    @Autowired
//    private WeChatController weChatController;

//    @Scheduled(cron = "0/10 * * * * ? ")
//    public void pushWeChatMessage(){
//        weChatController.pushWeChatMessage();
//    }

    /**
     * 每天凌晨执行一次  随机更新五篇文章到最前面
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @CacheEvict(value = "ArticleController", allEntries = true)//将文章相关缓存清空
    public void randomUpdateTimeArticles(){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = format.parse("2018-09-16");
            Date date2 = format.parse("2018-09-17");
            List<Article> articles = articleService.randomFindFiveArticles(date1, date2, PageRequest.of(0, 5)).getContent();
            articles.forEach(article -> {
                article.setDate(new Date());
                articleService.saveArticle(article);
                articleSearchRepository.save(new ESArticle(article));
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
