package com.jerry.geekdaily.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StarsDTO implements Serializable {

    private Integer id;

    @NotNull(message="文章ID不能为空！")
    private Integer article_id;//文章id

    @NotNull(message="用户ID不能为空！")
    private Integer user_id;//用户id

    @NotNull(message="点赞类型不能为空！")
    private Integer type;//点赞类型    1文章点赞  2评论点赞

    @NotNull(message="点赞状态不能为空！")
    private Integer status;//点赞状态   0未点赞   1已点赞

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
