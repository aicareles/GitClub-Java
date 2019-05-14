package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Integer articleId;

    @NotEmpty(message="标题不能为空！")
    private String title;

    private String des;

    @NotEmpty(message="图片链接不能为空！")
    private String imgUrl;//上传的图片文件

    @NotEmpty(message="文章链接不能为空！")
    private String link;//源url

    //此字段不返回
    @JSONField(serialize = false)
    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text")
    private String mdContent;//md风格的文本

    private String wrapLink;//外部url

    private String contributor;//贡献者

    private int contributorId;//贡献者id(user_id)

    private int stars;//点赞数

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;// 用户表外键

    private int unStars;//反赞数

    private int comments;//评论数

    private int views;//访问量

    private String tag;//文章标签

    @NotEmpty(message="分类不能为空！")
    private String category;//文章分类（Android、iOS、Java等）

    private int childCategory;//文章子分类(开源库0、资讯1、资料2等)

    private int rank;//文章适合等级（0所有人、1初学、2进阶）

    //文章创建日期
    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @LastModifiedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    private int reviewStatus;//审核状态  0代表审核审核中 1代表审核成功  -1代表审核失败
}
