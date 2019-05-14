package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Follow;
import com.jerry.geekdaily.dto.FollowDTO;
import com.jerry.geekdaily.repository.FollowRepository;
import com.jerry.geekdaily.service.FollowService;
import com.jerry.geekdaily.util.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Api(value = "FollowController", description = "关注相关接口")
@RestController
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 关注或取消关注
     * status 当前关注状态   0为未关注（取消关注） 1为已关注（关注）
     */
    @ApiOperation(value = "关注", notes = "关注接口")
    @PostMapping("/follow")
    public Result<Follow> follow(@Valid FollowDTO followDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        if(followDTO.getUserId() == followDTO.getFansId()){
            return ResultUtils.error("不能关注自己!");
        }
        Follow follow = followService.findFollow(followDTO.getUserId(), followDTO.getFansId());
        if(StringUtils.isEmpty(follow)){
            //未找到  代表未关注过则开始关注
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
                }
            }else {
                if (followDTO.getStatus() == 0){
                    //取消关注
                    follow.setStatus(0);
                    followService.saveFollow(follow);
                    return ResultUtils.ok("取消关注成功!");
                }
            }
        }
        return ResultUtils.error("操作失败!");
    }

    @ApiOperation(value = "获取我关注的对象列表", notes = "获取我关注的对象列表接口")
    @PostMapping("/getMyFollows")
    public Result<Follow> getMyFollows(@RequestParam int userId, @RequestParam int page, @RequestParam(required = false, defaultValue = "10") int size){
        Page<Follow> pages = followService.getMyFollows(userId, 1, PageRequest.of(page,size,
                new Sort(Sort.Direction.DESC, "date")));
        List<Follow> follows = pages.getContent();
        return ResultUtils.ok(follows);
    }

    @ApiOperation(value = "获取我的关注者列表", notes = "获取我的关注者列表接口")
    @PostMapping("/getMyFans")
    public Result<Follow> getMyFans(@RequestParam int userId, @RequestParam int page, @RequestParam(required = false, defaultValue = "10") int size){
        Page<Follow> pages = followService.getMyFans(userId, 1, PageRequest.of(page,size,
                new Sort(Sort.Direction.DESC, "date")));
        List<Follow> fans = pages.getContent();
        return ResultUtils.ok(fans);
    }

}
