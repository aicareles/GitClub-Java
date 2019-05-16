package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.jwt.JwtUtil;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(value = "UserController", description = "用户管理相关接口")
@RestController
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public Result<User> register(@RequestParam(value = "nick_name") String nick_name,
                                @RequestParam(value = "password") String password,
                                @RequestParam(value = "avatar", required = false) String avatar,
                                @RequestParam(value = "gender", required = false) String gender,
                                @RequestParam(value = "city", required = false) String city) {

        if(StringUtils.isEmpty(nick_name) || StringUtils.isEmpty(password)){
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
        User user = new User();
        user.setNickName(nick_name);
        user.setPwd(password);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setCity(city);
        user.setDate(new Date());
        userService.register(user);
        logger.info("注册成功!");
        return ResultUtils.ok(user);
    }

    @ApiOperation(value = "用户登陆", notes = "用户登陆接口")
    @PostMapping(value = "/login")
    public Result<User> login(@RequestParam("userName")String userName, @RequestParam("password")String password,
                              HttpServletRequest request, HttpServletResponse response){
        if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            User user = userService.login(userName, password);
            if(user != null){
                String token = JwtUtil.sign(userName, password);
                user.setToken(token);
                //保存到cookie中
                CookieUtils.addCookie(user.getUserId()+"", token, response, request);
                return ResultUtils.ok(user);
            }else {
                return ResultUtils.error(ResultCode.INVALID_USERNAME_PASSWORD);
            }
        }else {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result unauthorized() {
        return ResultUtils.error(ResultCode.UNAUTHORIZED);
    }

}
