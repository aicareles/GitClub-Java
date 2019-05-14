package com.jerry.geekdaily.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class Crash implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;
    private String phoneSystem;//手机系统 Android iOS
    private String phoneBrands;//手机品牌  三星
    private String phoneModel;//手机型号  SM-G9250
    private String phoneSystemVersion;//手机系统版本  android9.0
    private String appPackage;//app包名
    private String appChannel;//app渠道
    private String appVersionName;//app版本名
    private String appVersionCode;//app版本号

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text")
    private String exceptionInfo;//异常信息

    //上传时间
    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;
}
