package com.jerry.geekdaily.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class UpdateArticleDTO implements Serializable {

    @NotNull(message="文章ID不能为空！")
    private Integer articleId;

    private String title;

    private String des;

    private String tag;

    private String imgUrl;//上传的图片文件

    private String link;//源url

    @NotNull(message="编辑者ID不能为空！")
    private Integer contributorId;//贡献者id(user_id)

    private String category;//文章分类（Android、iOS、Java等）

    private int childCategory;//文章子分类(开源库0、资讯1、资料2等)

    private int rank;//文章适合等级（0所有人、1初学、2进阶）

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

}
