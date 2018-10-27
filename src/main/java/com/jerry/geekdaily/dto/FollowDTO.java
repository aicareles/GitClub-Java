package com.jerry.geekdaily.dto;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class FollowDTO implements Serializable {

    private Integer id;

    //被关注者的用户id
    @NotNull(message="被关注者ID不能为空！")
    private Integer userId;
    //被关注者的昵称
    private String nickName;
    //被关注者的头像
    private String avatar;
    //粉丝的用户id
    @NotNull(message="关注者ID不能为空！")
    private Integer fansId;
    //粉丝的昵称
    private String fanNickName;
    //粉丝的头像
    private String fanAvatar;
    //当前关注状态   0为未关注  1为已关注
    @NotNull(message="关注状态不能为空！")
    private Integer status;

    public FollowDTO(){}

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
                ", status=" + status +
                '}';
    }
}
