package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论表
 */
@Entity
public class Comment implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    //文章id
    private int article_id;

    //文章type
    private int article_type;

    //评论内容
    private String content;

    //评论用户id
    private int from_uid;

    //评论目标用户id
    private int to_uid;

    //评论日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    //以下是冗余的字段  为了避免查询多表   提高性能

    //评论用户昵称
    private String from_nick;
    //评论用户头像
    private String from_avatar;
    //评论目标用户昵称
    private String to_nick;
    //评论目标用户头像
    private String to_avatar;

    public Comment() {
    }

    public Comment(int article_id, int article_type, String content, int from_uid, int to_uid, Date date, String from_nick, String from_avatar, String to_nick, String to_avatar) {
        this.article_id = article_id;
        this.article_type = article_type;
        this.content = content;
        this.from_uid = from_uid;
        this.to_uid = to_uid;
        this.date = date;
        this.from_nick = from_nick;
        this.from_avatar = from_avatar;
        this.to_nick = to_nick;
        this.to_avatar = to_avatar;
    }

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

    public int getArticle_type() {
        return article_type;
    }

    public void setArticle_type(int article_type) {
        this.article_type = article_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(int from_uid) {
        this.from_uid = from_uid;
    }

    public int getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(int to_uid) {
        this.to_uid = to_uid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFrom_nick() {
        return from_nick;
    }

    public void setFrom_nick(String from_nick) {
        this.from_nick = from_nick;
    }

    public String getFrom_avatar() {
        return from_avatar;
    }

    public void setFrom_avatar(String from_avatar) {
        this.from_avatar = from_avatar;
    }

    public String getTo_nick() {
        return to_nick;
    }

    public void setTo_nick(String to_nick) {
        this.to_nick = to_nick;
    }

    public String getTo_avatar() {
        return to_avatar;
    }

    public void setTo_avatar(String to_avatar) {
        this.to_avatar = to_avatar;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", article_id=" + article_id +
                ", article_type=" + article_type +
                ", content='" + content + '\'' +
                ", from_uid=" + from_uid +
                ", to_uid=" + to_uid +
                ", from_nick='" + from_nick + '\'' +
                ", from_avatar='" + from_avatar + '\'' +
                ", to_nick='" + to_nick + '\'' +
                ", to_avatar='" + to_avatar + '\'' +
                '}';
    }
}
