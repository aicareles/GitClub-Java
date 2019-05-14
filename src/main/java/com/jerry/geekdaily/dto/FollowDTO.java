package com.jerry.geekdaily.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
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
}
