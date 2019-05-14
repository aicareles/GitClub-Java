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
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(value = "CommentController", description = "评论或回复的相关接口")
@RestController
public class CommentController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ESArticleSearchRepository articleSearchRepository;

    @ApiOperation(value = "获取文章评论列表")
    @PostMapping("/getArticleComments")
    public Result<Comment> getArticleComments(@RequestParam("page") Integer page,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                              @RequestParam("article_id") int article_id) {
        Article article = articleService.findArticleByArticleId(article_id);
        if (StringUtils.isEmpty(article)) {
            return ResultUtils.error("未发现相关文章!");
        }
        Page<Comment> pages = commentService.getAllByArticleId(article_id, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Comment> comments = pages.getContent();
        return ResultUtils.ok(comments);
    }

    @ApiOperation(value = "评论文章")
    @PostMapping("/commentArticle")
    public Result<Comment> commentArticle(@Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        Article article = articleService.findArticleByArticleId(commentDTO.getArticleId());
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
