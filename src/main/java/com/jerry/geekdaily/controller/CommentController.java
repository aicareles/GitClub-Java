package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.domain.Comment;
import com.jerry.geekdaily.domain.ESArticle;
import com.jerry.geekdaily.dto.CommentDTO;
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
    private CommentService commentService;

    @ApiOperation(value = "获取文章评论列表")
    @PostMapping("/getArticleComments")
    public Result<Comment> getArticleComments(@RequestParam("page") Integer page,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                              @RequestParam("articleId") int articleId) {
        Page<Comment> pages = commentService.getAllByArticleId(articleId, PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "date")));
        List<Comment> comments = pages.getContent();
        return ResultUtils.ok(comments);
    }

    @ApiOperation(value = "评论文章")
    @PostMapping("/commentArticle")
    public Result<Comment> commentArticle(@Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        commentService.commentArticle(commentDTO);
        return ResultUtils.ok("文章评论成功");
    }

}
