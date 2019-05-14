package com.jerry.geekdaily.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class StarsDTO implements Serializable {

    private Integer id;

    @NotNull(message="文章ID不能为空！")
    private Integer articleId;//文章id

    @NotNull(message="用户ID不能为空！")
    private Integer userId;//用户id

    @NotNull(message="点赞类型不能为空！")
    private Integer type;//点赞类型    1文章点赞  2评论点赞

    @NotNull(message="点赞状态不能为空！")
    private Integer status;//点赞状态   0未点赞   1已点赞

}
