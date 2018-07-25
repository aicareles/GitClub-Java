package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Follow;
import com.jerry.geekdaily.repository.FollowRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
@CacheConfig(cacheNames = "FollowController")
@Api(value = "FollowController", description = "关注相关接口")
@RestController
public class FollowController {

    private final static Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowRepository followRepository;

    /**
     * 关注
     * @param userId 被关注者用户id
     * @param nickName 被关注者的昵称
     * @param avatar 被关注者的头像
     * @param fansId 粉丝的用户id（当前用户id）
     * @param fanNickName 粉丝的昵称（当前用户昵称）
     * @param fanAvatar 粉丝的头像(当前用户头像）
     * @param isFollow 是否关注   true关注    false取消关注
     * status 当前关注状态   0为未关注（取消关注） 1为已关注
     * @return
     */
    @ApiOperation(value = "关注", notes = "关注接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "被关注者用户id", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "nickName", value = "被关注者的昵称", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "avatar", value = "被关注者的头像", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "fansId", value = "粉丝的用户id（当前用户id）", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "fanNickName", value = "粉丝的昵称（当前用户昵称）", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "fanAvatar", value = "粉丝的头像(当前用户头像）", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "isFollow", value = "被关注者的头像", required = true ,dataType = "string"),
    })
    @PostMapping("/follow")
    public Result<Follow> follow(@RequestParam int userId, @RequestParam String nickName, @RequestParam String avatar,
                                 @RequestParam int fansId, @RequestParam String fanNickName, @RequestParam String fanAvatar,
                                 @RequestParam boolean isFollow){
        if(userId == fansId){
            return ResultUtils.error("不能关注自己!");
        }
        Follow follow = followRepository.findByUserIdAndFansId(userId, fansId);
        if(StringUtils.isEmpty(follow)){
            //未找到  代表未关注过则开始关注
            follow.setUserId(userId);
            follow.setNickName(nickName);
            follow.setAvatar(avatar);
            follow.setFansId(fansId);
            follow.setFanNickName(fanNickName);
            follow.setFanAvatar(fanAvatar);
            follow.setDate(new Date());
            follow.setStatus(1);
            followRepository.save(follow);
            return ResultUtils.ok(follow);
        }else {
            //找到对应表中的数据（曾经关注过）   查看关注状态
            if(follow.getStatus() == 0){//未关注
                if (isFollow){
                    follow.setStatus(1);
                    followRepository.saveAndFlush(follow);
                    return ResultUtils.ok("关注用户成功!");
                }else {
                    return ResultUtils.error("未关注该用户，不能取消关注");
                }
            }else {
                //当前已关注
                if (isFollow){
                    return ResultUtils.error("已关注用户，不可重复关注!");
                }else {
                    //取消关注
                    follow.setStatus(0);
                    followRepository.saveAndFlush(follow);
                    return ResultUtils.ok("取消关注成功!");
                }
            }
        }
    }

    /**
     * 获取我关注的对象列表
     * @param userId  当前用户id(fansId)
     * @param page 当前页数
     * @param size 每页返回数据量
     * @return
     */
    @ApiOperation(value = "获取我关注的对象列表", notes = "获取我关注的对象列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户id(fansId)", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "page", value = "当前页数", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页返回数据量", required = true ,dataType = "int")
    })
    @PostMapping("/getMyFollows")
    public Result<Follow> getMyFollows(@RequestParam int userId, @RequestParam int page, @RequestParam int size){
        Page<Follow> pages = followRepository.findFollowsByFansIdAndStatusLike(userId, 1, PageRequest.of(page,size,
                new Sort(Sort.Direction.DESC, "date")));
        List<Follow> follows = pages.getContent();
        return ResultUtils.ok(follows);
    }

    /**
     * 获取我的关注者列表（我的粉丝）
     * @param userId 我的用户id
     * @param page 当前页数
     * @param size 每页返回数据量
     * @return
     */
    @ApiOperation(value = "获取我的关注者列表", notes = "获取我的关注者列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "我的用户id", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "page", value = "当前页数", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页返回数据量", required = true ,dataType = "int")
    })
    @PostMapping("/getMyFans")
    public Result<Follow> getMyFans(@RequestParam int userId, @RequestParam int page, @RequestParam int size){
        Page<Follow> pages = followRepository.findFollowsByUserIdAndStatusLike(userId, 1, PageRequest.of(page,size,
                new Sort(Sort.Direction.DESC, "date")));
        List<Follow> fans = pages.getContent();
        return ResultUtils.ok(fans);
    }

}
