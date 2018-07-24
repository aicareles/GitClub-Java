package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.repository.ArticleRepository;
import com.jerry.geekdaily.repository.CommentRepository;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api(value = "CommentController", description = "评论或回复的相关接口")
@RestController
public class CommentController {
    private final static Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    //全文搜索引擎   数据库数据改变时伴随着  搜索引擎中的数据的增删改查
    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    /**
     * 获取文章评论列表
     * @param page
     * @param article_id
     * @return
     */
    @ApiOperation(value = "获取文章评论列表", notes = "获取文章评论列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int")
    })
    @Cacheable(value="getArticleComments")
    @PostMapping("/getArticleComments")
    public Result<Comment> getArticleComments(@RequestParam("page")Integer page, @RequestParam("article_id")int article_id){
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
            return ResultUtils.error("未发现相关文章!");
        }
        Page<Comment> pages = commentRepository.getAllByArticle_id(article_id, PageRequest.of(page,10, new Sort(Sort.Direction.DESC, "date")));
        List<Comment> comments = pages.getContent();
        return ResultUtils.ok(comments);
    }

    /**
     * 评论文章
     * @param article_id
     * @param article_type
     * @param content
     * @param from_uid
     * @param from_nick
     * @param from_avatar
     * @param to_uid
     * @param to_nick
     * @param to_avatar
     * @return
     */
    @ApiOperation(value = "文章评论", notes = "文章评论接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "article_type", value = "文章类型", required = true, dataType = "int"),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true, dataType = "string"),
            @ApiImplicitParam(name = "from_uid", value = "评论用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "from_nick", value = "评论用户昵称", required = false, dataType = "string"),
            @ApiImplicitParam(name = "from_avatar", value = "评论用户头像", required = false, dataType = "string"),
            @ApiImplicitParam(name = "to_uid", value = "评论目标用户ID", required = false, dataType = "int"),
            @ApiImplicitParam(name = "to_nick", value = "评论目标用户昵称", required = false, dataType = "string"),
            @ApiImplicitParam(name = "to_avatar", value = "评论目标用户头像", required = false, dataType = "string"),
    })
    @PostMapping("/commentArticle")
    public Result<Comment> commentArticle(@RequestParam("article_id")int article_id,
                                          @RequestParam("article_type")int article_type,
                                          @RequestParam("content")String content,
                                          @RequestParam("from_uid")int from_uid,
                                          @RequestParam(value = "from_nick", required = false)String from_nick,
                                          @RequestParam(value = "from_avatar", required = false)String from_avatar,
                                          @RequestParam(value = "to_uid", required = false, defaultValue = "0")int to_uid,
                                          @RequestParam(value = "to_nick", required = false)String to_nick,
                                          @RequestParam(value = "to_avatar", required = false)String to_avatar){
        if(StringUtils.isEmpty(article_id) || StringUtils.isEmpty(content) || StringUtils.isEmpty(from_uid)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        Article article = articleRepository.findArticleByArticle_id(article_id);
        if(StringUtils.isEmpty(article)){
            return ResultUtils.error("未发现相关文章!");
        }
        Comment comment = new Comment(article_id,article_type,content,from_uid,to_uid,new Date(),
                from_nick,from_avatar,to_nick,to_avatar);
        commentRepository.save(comment);
        //评论数+1
        article.setComments(article.getComments() + 1);
        articleRepository.saveAndFlush(article);
        //更新数据到引擎
        articleSearchRepository.save(new ESArticle(article));
        return ResultUtils.ok(comment);
    }

}
