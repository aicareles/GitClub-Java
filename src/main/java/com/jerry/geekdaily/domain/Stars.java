package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Stars implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private int article_id;//文章id

    private int user_id;//用户id

    private int type;//点赞类型    1文章点赞  2评论点赞

    private int status;//点赞状态   0未点赞   1已点赞

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;//点赞时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Stars{" +
                "id=" + id +
                ", article_id=" + article_id +
                ", user_id=" + user_id +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
