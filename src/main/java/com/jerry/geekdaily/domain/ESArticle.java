package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Document(indexName="geekdaily",type="article")
public class ESArticle implements Serializable {

    @Id
    private Integer id;

    private String title;

    private String des;

    private String img_url;//头像

    private String link;//源url

    private String wrap_link;//外部url

    private String contributor;//贡献者

    private int contributor_id;//贡献者id(user_id)

    private int stars;//点赞数

    private int comments;//评论数

    private int views;//访问量

    private String tag;//文章标签

    //文章上传更新日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public ESArticle() {
    }

    public ESArticle(Article article) {
        this.id = article.getArticle_id();
        this.title = article.getTitle();
        this.des = article.getDes();
        this.img_url = article.getImg_url();
        this.link = article.getLink();
        this.wrap_link = article.getWrap_link();
        this.contributor = article.getContributor();
        this.contributor_id = article.getContributor_id();
        this.stars = article.getStars();
        this.comments = article.getComments();
        this.views = article.getViews();
        this.tag = article.getTag();
        this.date = article.getDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWrap_link() {
        return wrap_link;
    }

    public void setWrap_link(String wrap_link) {
        this.wrap_link = wrap_link;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public int getContributor_id() {
        return contributor_id;
    }

    public void setContributor_id(int contributor_id) {
        this.contributor_id = contributor_id;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", des='" + des + '\'' +
                ", img_url='" + img_url + '\'' +
                ", link='" + link + '\'' +
                ", wrap_link='" + wrap_link + '\'' +
                ", contributor='" + contributor + '\'' +
                ", contributor_id=" + contributor_id +
                ", stars=" + stars +
                ", comments=" + comments +
                ", views=" + views +
                ", tag='" + tag + '\'' +
                ", date=" + date +
                '}';
    }
}
