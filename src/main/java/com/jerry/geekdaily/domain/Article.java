package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Integer article_id;

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

    private int comments;//评论数

    private int views;//访问量

    private String tag;//文章标签

    //文章上传更新日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @JSONField(serialize=false)
    private int review_status;//审核状态  0代表审核审核中 1代表审核成功  -1代表审核失败

//    //文章--点赞用户   多对多关系
//    @ManyToMany(mappedBy = "articleList")
//    private List<User> users;


    public Article() {
    }

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
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

    public int getReview_status() {
        return review_status;
    }

    public void setReview_status(int review_status) {
        this.review_status = review_status;
    }

    //    @JsonBackReference
//    public List<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List<User> users) {
//        this.users = users;
//    }

    @Override
    public String toString() {
        return "Article{" +
                "article_id=" + article_id +
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
