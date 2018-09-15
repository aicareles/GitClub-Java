package com.jerry.geekdaily.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.UserRepository;
import com.jerry.geekdaily.util.CookieUtils;
import com.jerry.geekdaily.util.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "UserController", description = "用户管理相关接口")
@RestController
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 添加用户   （相当于注册）
     * @param nick_name 用户名
     * @param avatar 头像url
     * @param gender 性别 0男   1女
     * @param city 城市
     * @return  注册成功的用户对象
     */
    @ApiOperation(value = "用户注册", notes = "用户注册接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nick_name", value = "用户名", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "avatar", value = "头像", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "gender", value = "性别", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "city", value = "城市", required = false ,dataType = "string"),
    })
    @PostMapping(value = "/register")
    public Result<User> register(@RequestParam(value = "nick_name", required = true) String nick_name,
                                @RequestParam(value = "password", required = true) String password,
                                @RequestParam(value = "avatar", required = false) String avatar,
                                @RequestParam(value = "gender", required = false) String gender,
                                @RequestParam(value = "city", required = false) String city) {

        if(StringUtils.isEmpty(nick_name) || StringUtils.isEmpty(password)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        User user = new User();
        user.setNick_name(nick_name);
        user.setPwd(password);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setCity(city);
        user.setDate(new Date());
        userRepository.save(user);
        logger.info("注册成功!");
        return ResultUtils.ok(user);
    }

    /**
     * 用户登陆
     * @param userName 用户名
     * @param password 密码
     * @return  登陆成功的用户对象
     */
    @ApiOperation(value = "用户登陆", notes = "用户登陆接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true ,dataType = "string"),
    })
    @PostMapping(value = "/login")
    public Result<User> login(@RequestParam("userName")String userName, @RequestParam("password")String password,
                              HttpServletRequest request, HttpServletResponse response){
        if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            User user = userRepository.findUserByNick_nameAndPwd(userName, password);
            if(user != null){
                //保存到cookie中
                CookieUtils.addCookie(user.getUser_id()+"", user.getNick_name(), response, request);
                return ResultUtils.ok(user);
            }else {
                return ResultUtils.error(ResultCode.INVALID_USERNAME_PASSWORD);
            }
        }else {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
    }

}
