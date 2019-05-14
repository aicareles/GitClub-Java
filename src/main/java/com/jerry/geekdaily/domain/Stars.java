package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
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
@Data
public class Stars implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private int articleId;//文章id

    private int userId;//用户id

    private int type;//点赞类型    1文章点赞  2评论点赞

    private int status;//点赞状态   0未点赞   1已点赞

    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;//点赞时间

    @LastModifiedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    public Integer getId() {
        return id;
    }
}
