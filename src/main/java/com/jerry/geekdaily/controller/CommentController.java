package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.dto.CommentDTO;
import com.jerry.geekdaily.repository.ESArticleSearchRepository;
import com.jerry.geekdaily.service.ArticleService;
import com.jerry.geekdaily.service.CommentService;
import com.jerry.geekdaily.util.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Api(value = "CommentController", description = "评论或回复的相关接口")
@RestController
public class CommentController {
    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    /**
     * 获取文章评论列表
     * @param page       当前页数
     * @param size       返回数量
     * @param article_id 文章ID
     * @return
     */
    @ApiOperation(value = "获取文章评论列表", notes = "获取文章评论列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "返回数量", required = true, dataType = "int"),
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int")
    })
    @Cacheable(value = "getArticleComments")
    @PostMapping("/getArticleComments")
    public Result<Comment> getArticleComments(@RequestParam("page") Integer page,
                                              @RequestParam("size") Integer size,
                                              @RequestParam("article_id") int article_id) {
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未发现相关文章!");
        }
        Page<Comment> pages = commentService.getAllByArticleId(article_id, PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "date")));
        List<Comment> comments = pages.getContent();
        return ResultUtils.ok(comments);
    }

    /**
     * 评论文章
     *
     * @return
     */
    @ApiOperation(value = "文章评论", notes = "文章评论接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", value = "文章ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "article_type", value = "文章类型", required = true, dataType = "int"),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true, dataType = "string"),
            @ApiImplicitParam(name = "from_uid", value = "评论用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "from_nick", value = "评论用户昵称", required = false, dataType = "string"),
            @ApiImplicitParam(name = "from_avatar", value = "评论用户头像", required = false, dataType = "string"),
            @ApiImplicitParam(name = "to_uid", value = "评论目标用户ID", required = false, dataType = "int"),
            @ApiImplicitParam(name = "to_nick", value = "评论目标用户昵称", required = false, dataType = "string"),
            @ApiImplicitParam(name = "to_avatar", value = "评论目标用户头像", required = false, dataType = "string"),
    })
    @CacheEvict(value = "getArticleComments", allEntries = true)//将文章评论列表相关缓存清空
    @PostMapping("/commentArticle")
    public Result<Comment> commentArticle(@Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        Article article = articleService.findArticleByArticleId(commentDTO.getArticle_id());
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未发现相关文章!");
        }
        Comment comment = new Comment();
        BeanCopyUtil.beanCopy(commentDTO, comment);
        commentService.saveComment(comment);
        article.setComments(article.getComments() + 1);
        articleService.saveArticle(article);
        articleSearchRepository.save(new ESArticle(article));//更新数据到ES
        return ResultUtils.ok(comment);
    }

}
