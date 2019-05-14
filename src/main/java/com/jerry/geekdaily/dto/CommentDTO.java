package com.jerry.geekdaily.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class CommentDTO implements Serializable {

    private Integer id;

    //文章id
    @NotNull(message="文章ID不能为空！")
    private Integer articleId;

    //文章type
    @NotNull(message="评论类型不能为空！")
    private Integer articleType;

    //评论内容
    @NotEmpty(message="评论内容不能为空！")
    private String content;

    //评论用户id
    @NotNull(message = "评论用户ID不能为空！")
    private Integer fromUid;

    //评论目标用户id
    private int toUid;

    //评论日期
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
