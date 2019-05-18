package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.annotation.ValidationParam;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.jwt.JwtUtil;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "UserController", description = "用户管理相关接口")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public Result<User> register(@RequestParam String username,
                                @RequestParam String password) {
        User user = new User();
        user.setUserName(username);
        user.setPwd(password);
        userService.register(user);
        return ResultUtils.ok("注册成功");
    }

    @ApiOperation(value = "用户登陆", notes = "用户登陆接口")
    @PostMapping(value = "/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password,
                              HttpServletRequest request, HttpServletResponse response){
        User user = userService.login(username, password);
        if(user != null){
            String token = JwtUtil.sign(username, password);
            user.setToken(token);
            //保存到cookie中
            CookieUtils.addCookie(String.valueOf(user.getUserId()), token, response, request);
            return ResultUtils.ok(user);
        }else {
            return ResultUtils.error(ResultCode.INVALID_USERNAME_PASSWORD);
        }
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result unauthorized() {
        return ResultUtils.error(ResultCode.UNAUTHORIZED);
    }

}
