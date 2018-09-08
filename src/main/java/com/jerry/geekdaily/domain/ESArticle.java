package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
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

    //此字段不返回
    @JSONField(serialize=false)
    @Lob @Basic(fetch = FetchType.LAZY) @Column(columnDefinition = "text")
    private String md_content;//md风格的文本

    private String wrap_link;//外部url

    private String contributor;//贡献者

    private int contributor_id;//贡献者id(user_id)

    private int stars;//点赞数

    private int un_stars;//反赞数

    private int comments;//评论数

    private int views;//访问量

    private String tag;//文章标签

    private String category;//文章分类（Android、iOS、Java等）

    private int rank;//文章适合等级（0所有人、1初学、2进阶）

    //文章上传更新日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    private User user;// 用户表外键

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
        this.category = article.getCategory();
        this.rank = article.getRank();
        this.date = article.getDate();
        this.user = article.getUser();
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

    public String getMd_content() {
        return md_content;
    }

    public void setMd_content(String md_content) {
        this.md_content = md_content;
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

    public int getUn_stars() {
        return un_stars;
    }

    public void setUn_stars(int un_stars) {
        this.un_stars = un_stars;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
