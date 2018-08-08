package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.domain.Article;
import com.jerry.geekdaily.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;
    /**
     * 查看文章列表的web页面
     * @return
     */
    @GetMapping(value = ("/index"))
    public String index(){
        return "index";
    }

    @GetMapping(value = ("/article"))
    public ModelAndView article(){
        Page<Article> pages = articleRepository.findAll(PageRequest.of(0,15, new Sort(Sort.Direction.DESC, "date")));
        ModelAndView view = new ModelAndView("article");
        view.addObject("articleList", pages.getContent());
        return view;
    }

    @GetMapping(value = ("/addArticle"))
    public String addArticle(){

        return "add-article";
    }
}
