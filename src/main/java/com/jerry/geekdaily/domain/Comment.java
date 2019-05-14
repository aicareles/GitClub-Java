package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论表
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class Comment implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    //文章id
    private int articleId;

    //文章type
    private int articleType;

    //评论内容
    private String content;

    //评论用户id
    private int fromUid;

    //评论目标用户id
    private int toUid;

    //评论日期
    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    //以下是冗余的字段  为了避免查询多表   提高性能

    //评论用户昵称
    private String fromNick;
    //评论用户头像
    private String fromAvatar;
    //评论目标用户昵称
    private String toNick;
    //评论目标用户头像
    private String toAvatar;
}
