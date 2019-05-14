package com.jerry.geekdaily.domain;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Data
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Integer userId;

    //管理员状态   0普通用户  1管理员
    private int adminStatus;

    //微信的openid  唯一
    private String openId;

    //微信的session_key
    private String sessionKey;

    private String nickName;

    private String email;

    private String phoneNumber;

    //此字段不返回
    @JSONField(serialize=false)
    private String pwd;

    private String avatar;

    private String gender;

    private String city;

    //注册时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    private int score;//个人积分

}
