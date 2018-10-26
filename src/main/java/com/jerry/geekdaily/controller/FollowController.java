package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Follow;
import com.jerry.geekdaily.repository.FollowRepository;
import com.jerry.geekdaily.service.FollowService;
import com.jerry.geekdaily.util.BeanCopyUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
@CacheConfig(cacheNames = "FollowController")
@Api(value = "FollowController", description = "关注相关接口")
@RestController
public class FollowController {

    private final static Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowService followService;

    /**
     * 关注或取消关注
     * status 当前关注状态   0为未关注（取消关注） 1为已关注（关注）
     */
    @ApiOperation(value = "关注", notes = "关注接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "被关注者用户id", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "nickName", value = "被关注者的昵称", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "avatar", value = "被关注者的头像", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "fansId", value = "粉丝的用户id（当前用户id）", required = true ,dataType = "int"),
            @ApiImplicitParam(name = "fanNickName", value = "粉丝的昵称（当前用户昵称）", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "fanAvatar", value = "粉丝的头像(当前用户头像）", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "status", value = "关注状态", required = true ,dataType = "int"),
    })
    @PostMapping("/follow")
    public Result<Follow> follow(@Valid Follow followDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        if(followDTO.getUserId() == followDTO.getFansId()){
            return ResultUtils.error("不能关注自己!");
        }
        Follow follow = followService.findFollow(followDTO.getUserId(), followDTO.getFansId());
        if(StringUtils.isEmpty(follow)){
            //未找到  代表未关注过则开始关注
            follow.setDate(new Date());
            follow.setStatus(1);
            BeanCopyUtil.beanCopyWithIngore(followDTO, follow, "status");
            followService.saveFollow(follow);
            return ResultUtils.ok(follow);
        }else {
            //找到对应表中的数据（曾经关注过）   查看关注状态
            if(follow.getStatus() == 0){//未关注
                if (followDTO.getStatus() == 1){
                    follow.setStatus(1);
                    followService.saveFollow(follow);
                    return ResultUtils.ok("关注用户成功!");
                }else {
                    return ResultUtils.error("未关注该用户，不能取消关注");
                }
            }else {
                //当前已关注
                if (followDTO.getStatus() == 1){
                    return ResultUtils.error("已关注用户，不可重复关注!");
                }else {
                    //取消关注
                    follow.setStatus(0);
                    followService.saveFollow(follow);
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
        Page<Follow> pages = followService.getMyFollows(userId, 1, PageRequest.of(page,size,
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
        Page<Follow> pages = followService.getMyFans(userId, 1, PageRequest.of(page,size,
                new Sort(Sort.Direction.DESC, "date")));
        List<Follow> fans = pages.getContent();
        return ResultUtils.ok(fans);
    }

}
