package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.ESArticle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//@CacheConfig(cacheNames = "UtilsController")
@RestController
public class UtilsController {
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

//    @PostMapping("deleteAllEs")
//    public Result<Boolean> deleteAllEs(){
//        esArticleSearchRepository.deleteAll();
//        return ResultUtils.ok("OK");
//    }
//
//    @PostMapping("addArticlesToEs")
//    public Result<Boolean> addArticlesToEs(){
//        List<Article> all = articleRepository.findAll();
//        List<ESArticle> allES = new ArrayList<>();
//        all.forEach(article -> {
//            allES.add(new ESArticle(article));
//        });
//        esArticleSearchRepository.saveAll(allES);
//        return ResultUtils.ok("OK");
//    }
}
