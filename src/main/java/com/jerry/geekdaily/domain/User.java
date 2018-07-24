package com.jerry.geekdaily.domain;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Integer user_id;

    private String nick_name;

    //此字段不返回
    @JSONField(serialize=false)
    private String pwd;

    private String wechat_id;

    private String avatar;

    private String gender;

    private String city;

    //注册时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

//    //文章--点赞用户   多对多关系
//    @ManyToMany(fetch= FetchType.EAGER)
//    @JoinTable(name="UserArticle",joinColumns={@JoinColumn(name="user_id")},inverseJoinColumns={@JoinColumn(name="article_id")})
//    private List<Article> articleList;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getWechat_id() {
        return wechat_id;
    }

    public void setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

//    @JsonBackReference
//    public List<Article> getArticleList() {
//        return articleList;
//    }
//
//    public void setArticleList(List<Article> articleList) {
//        this.articleList = articleList;
//    }

    @Override
    public String toString() {
        return "User{" +
                ", user_id=" + user_id +
                ", nick_name='" + nick_name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", wechat_id='" + wechat_id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
