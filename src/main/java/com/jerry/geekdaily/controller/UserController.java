package com.jerry.geekdaily.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.RawData;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.UserRepository;
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
import org.springframework.web.servlet.ModelAndView;
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
    public Result<User> login(@RequestParam("userName")String userName, @RequestParam("password")String password){//HttpServletRequest request,
        if(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            User user = userRepository.findUserByNick_nameAndPwd(userName, password);
            System.out.println("userName:"+userName);
            if(user != null){
//                request.getSession().setAttribute("user",user);
                return ResultUtils.ok(user);
            }else {
                return ResultUtils.error("用户名或密码错误!");
            }
        }else {
            return ResultUtils.error("用户名或密码不能为空!");
        }
    }

    /**
     * 小程序登陆(注册)
     * @param code 小程序临时登录凭证code
     * @param nickName 用户昵称
     * @param avatarUrl 用户头像
     * @return 登陆成功的用户对象
     */
    @ApiOperation(value = "微信用户登陆", notes = "微信用户登陆接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "小程序临时登录凭证code", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "nickName", value = "用户昵称", required = true ,dataType = "string"),
            @ApiImplicitParam(name = "avatarUrl", value = "用户头像", required = false ,dataType = "string")
//            @ApiImplicitParam(name = "rawData", value = "用户非敏感信息", required = true ,dataType = "string")
    })
    @PostMapping(value = "/WxLogin")
    public Result<User> WxLogin(@RequestParam("code")String code,
                                @RequestParam("nickName")String nickName,
                                @RequestParam("avatarUrl")String avatarUrl){//HttpServletRequest request,
        String openid = null;
        String sessionKey = null;
//        RawData data = null;
        if(!StringUtils.isEmpty(code) && !StringUtils.isEmpty(nickName)){
//            data = JSON.parseObject(rawData, RawData.class);
//            logger.info("data:"+rawData);
            JSONObject jsonObject = getSessionKeyOrOpenId(code);
            if(!StringUtils.isEmpty(jsonObject) && StringUtils.isEmpty(jsonObject.getString("errmsg"))){
                openid = jsonObject.getString("openid");
                sessionKey = jsonObject.getString("session_key");
                User user = userRepository.findUserByOpen_id(openid);
                if (!StringUtils.isEmpty(user)){
                    //已存在用户
                    return ResultUtils.ok(user);
                }else {
                    //不存在   则插入用户
                    user = new User();
                    user.setNick_name(nickName);
                    user.setAvatar(avatarUrl);
//                    user.setCity(data.getCity());
//                    user.setGender(data.getGender());
                    user.setOpen_id(openid);
                    user.setSession_key(sessionKey);
                    user.setDate(new Date());
                    user = userRepository.save(user);
                    return ResultUtils.ok(user);
                }
            }else {
                return ResultUtils.error("invalid code!");
            }
        }else {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
    }

    /**
     * 小程序端获取SessionKeyOrOpenId
     * @param code
     * @return
     */
    public static JSONObject getSessionKeyOrOpenId(String code){
        //微信端登录code
        String wxCode = code;
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String,String> requestUrlParam = new HashMap<String, String>(  );
        requestUrlParam.put( "appid","wx5cd48edea47a1f48" );//小程序appId
        requestUrlParam.put( "secret","b5b627c95cd043681857c6c7d1e3a488" );
        requestUrlParam.put( "js_code",wxCode );//小程序端返回的code
        requestUrlParam.put( "grant_type","authorization_code" );//默认参数

        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject( HttpUtils.sendPost( requestUrl,requestUrlParam ));
        return jsonObject;
    }

    /**
     * 登陆
     * @return
     */
    @GetMapping(value = {"/", "/login"})
    public ModelAndView index(){
        ModelAndView model = new ModelAndView("login");
        return model;
    }

    /**
     * 注册
     * @return
     */
    @GetMapping(value = ("/register"))
    public ModelAndView register(){
        ModelAndView model = new ModelAndView("register");
        return model;
    }

}
