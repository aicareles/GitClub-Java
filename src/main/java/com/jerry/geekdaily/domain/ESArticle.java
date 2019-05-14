package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.jerry.geekdaily.util.BeanCopyUtil;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Document(indexName="geekdaily",type="article")
@Data
public class ESArticle implements Serializable {

    @Id
    private Integer id;

    private Integer articleId;

    private String title;

    private String des;

    private String imgUrl;//上传的图片文件

    private String link;//源url

    //此字段不返回
    @JSONField(serialize=false)
    @Lob @Basic(fetch = FetchType.LAZY) @Column(columnDefinition = "text")
    private String mdContent;//md风格的文本

    private String wrapLink;//外部url

    private String contributor;//贡献者

    private int contributorId;//贡献者id(user_id)

    private int stars;//点赞数

    private int unStars;//反赞数

    private int comments;//评论数

    private int views;//访问量

    private String tag;//文章标签

    private String category;//文章分类（Android、iOS、Java等）

    private int childCategory;//文章子分类(开源库0、资讯1、资料2等)

    private int rank;//文章适合等级（0所有人、1初学、2进阶）

    //文章上传更新日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    private User user;// 用户表外键

    public ESArticle() {
    }

    public ESArticle(Article article) {
        this.id = article.getArticleId();
        BeanCopyUtil.beanCopy(article, this);
    }
}
