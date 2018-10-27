package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Stars implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private int article_id;//文章id

    private int user_id;//用户id

    private int type;//点赞类型    1文章点赞  2评论点赞

    private int status;//点赞状态   0未点赞   1已点赞

    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;//点赞时间

    @LastModifiedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_date;

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

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
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
