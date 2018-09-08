package com.jerry.geekdaily.domain;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Integer user_id;

    //管理员状态   0普通用户  1管理员
    private int admin_status;

    //微信的openid  唯一
    private String open_id;

    //微信的session_key
    private String session_key;

    private String nick_name;

    //此字段不返回
    @JSONField(serialize=false)
    private String pwd;

    private String avatar;

    private String gender;

    private String city;

    //注册时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    private int score;//个人积分

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

    public int getAdmin_status() {
        return admin_status;
    }

    public void setAdmin_status(int admin_status) {
        this.admin_status = admin_status;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
                "user_id=" + user_id +
                ", admin_status=" + admin_status +
                ", open_id='" + open_id + '\'' +
                ", session_key='" + session_key + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                ", date=" + date +
                ", score=" + score +
                '}';
    }
}
