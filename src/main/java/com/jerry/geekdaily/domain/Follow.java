package com.jerry.geekdaily.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Follow implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    //被关注者的用户id
    private int userId;
    //被关注者的昵称
    private String nickName;
    //被关注者的头像
    private String avatar;
    //粉丝的用户id
    private int fansId;
    //粉丝的昵称
    private String fanNickName;
    //粉丝的头像
    private String fanAvatar;
    //关注日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    //当前关注状态   0为未关注  1为已关注
    private int status;

    public Follow(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFansId() {
        return fansId;
    }

    public void setFansId(int fansId) {
        this.fansId = fansId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFanNickName() {
        return fanNickName;
    }

    public void setFanNickName(String fanNickName) {
        this.fanNickName = fanNickName;
    }

    public String getFanAvatar() {
        return fanAvatar;
    }

    public void setFanAvatar(String fanAvatar) {
        this.fanAvatar = fanAvatar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", userId=" + userId +
                ", fansId=" + fansId +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
