package com.jerry.geekdaily.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.UserRepository;
import com.jerry.geekdaily.util.HttpUtils;
import com.jerry.geekdaily.util.SignUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "WeChatController", description = "微信相关接口")
@RestController
public class WeChatController {
    private final static Logger logger = LoggerFactory.getLogger(WeChatController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 小程序登陆(注册)
     *
     * @param code      小程序临时登录凭证code
     * @param nickName  用户昵称
     * @param avatarUrl 用户头像
     * @return 登陆成功的用户对象
     */
    @ApiOperation(value = "微信用户登陆", notes = "微信用户登陆接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "小程序临时登录凭证code", required = true, dataType = "string"),
            @ApiImplicitParam(name = "nickName", value = "用户昵称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "avatarUrl", value = "用户头像", required = false, dataType = "string")
//            @ApiImplicitParam(name = "rawData", value = "用户非敏感信息", required = true ,dataType = "string")
    })
    @PostMapping(value = "/WxLogin")
    public Result<User> WxLogin(@RequestParam("code") String code,
                                @RequestParam("nickName") String nickName,
                                @RequestParam("avatarUrl") String avatarUrl) {//HttpServletRequest request,
        String openid = null;
        String sessionKey = null;
//        RawData data = null;
        if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(nickName)) {
//            data = JSON.parseObject(rawData, RawData.class);
//            logger.info("data:"+rawData);
            JSONObject jsonObject = getSessionKeyOrOpenId(code);
            if (!StringUtils.isEmpty(jsonObject) && StringUtils.isEmpty(jsonObject.getString("errmsg"))) {
                openid = jsonObject.getString("openid");
                sessionKey = jsonObject.getString("session_key");
                User user = userRepository.findUserByOpen_id(openid);
                if (!StringUtils.isEmpty(user)) {
                    //已存在用户
                    return ResultUtils.ok(user);
                } else {
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
            } else {
                return ResultUtils.error("invalid code!");
            }
        } else {
            return ResultUtils.error(ResultCode.INVALID_PARAM_EMPTY);
        }
    }

    /**
     * 小程序端获取access_token
     *
     * @param grant_type 获取 access_token 填写 client_credential
     * @param appid      第三方用户唯一凭证
     * @param secret     第三方用户唯一凭证密钥，即appsecret
     * @return
     */
    public static JSONObject getAccess_token(String grant_type, String appid, String secret) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token";
        StringBuffer requestUrlParam = new StringBuffer();
        requestUrlParam.append("grant_type" + "=" + "client_credential");
        requestUrlParam.append("&" + "appid" + "=" + Constans.WeChat.WECHAT_APP_ID);
        requestUrlParam.append("&" + "secret" + "=" + Constans.WeChat.WECHAT_SECRET);

        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject(HttpUtils.sendGet(requestUrl, String.valueOf(requestUrlParam)));
        return jsonObject;
    }

    /**
     * 小程序端获取SessionKeyOrOpenId
     *
     * @param code
     * @return
     */
    public static JSONObject getSessionKeyOrOpenId(String code) {
        //微信端登录code
        String wxCode = code;
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("appid", Constans.WeChat.WECHAT_APP_ID);//小程序appId
        requestUrlParam.put("secret", Constans.WeChat.WECHAT_SECRET);
        requestUrlParam.put("js_code", wxCode);//小程序端返回的code
        requestUrlParam.put("grant_type", "authorization_code");//默认参数

        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject(HttpUtils.sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }


    //用户发给小程序的消息以及开发者需要的事件推送，都将被微信转发至该服务器地址中
    @RequestMapping(value = "/checkWeixinValid", method = RequestMethod.GET)
    public String checkWeixinValid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("html/text;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        // 获取输出流
        PrintWriter printWriter = new PrintWriter(resp.getOutputStream());
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        if (SignUtils.checkSignature(signature, timestamp, nonce)) {
            logger.info("签名验证成功!");
            printWriter.print(echostr);
            printWriter.flush();
        } else {
            logger.info("签名验证失败!");
        }
        printWriter.close();
        printWriter = null;
        return echostr;
    }

    @PostMapping("/pushWeChatMessage")
    public void pushWeChatMessage() {
        JSONObject jsonObject = pushMsg("oIXfM4ubqNiCaIB65YXl1cuUVuDk");
        logger.info("推送消息结果:"+jsonObject.toJSONString());
    }

    public static JSONObject pushMsg(String openId) {
        String ACCESS_TOKEN = getAccess_token("", "", "").getString("access_token");
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + ACCESS_TOKEN;
        logger.info("requestUrl++++:"+requestUrl);
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("touser", openId);//接收者（用户）的 openid
        requestUrlParam.put("template_id", "pv367gzNj51mGBi1jLUcv7fpgV0eJC0MEne6BqZ1B2c");//所需下发的模板消息的id
        requestUrlParam.put("page", "index");//点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
        requestUrlParam.put("form_id", "1");//表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
        requestUrlParam.put("data", null);//模板内容，不填则下发空模板
        String json = "{\n" +
                "  \"touser\": \"oIXfM4ubqNiCaIB65YXl1cuUVuDk\",\n" +
                "  \"template_id\": \"pv367gzNj51mGBi1jLUcv7fpgV0eJC0MEne6BqZ1B2c\",\n" +
                "  \"page\": \"index\",\n" +
                "  \"form_id\": \"\",\n" +
                "  \"data\": {\n" +
                "      \"keyword1\": {\n" +
                "          \"value\": \"339208499\"\n" +
                "      },\n" +
                "      \"keyword2\": {\n" +
                "          \"value\": \"2015年01月05日 12:30\"\n" +
                "      },\n" +
                "      \"keyword3\": {\n" +
                "          \"value\": \"粤海喜来登酒店\"\n" +
                "      } ,\n" +
                "      \"keyword4\": {\n" +
                "          \"value\": \"广州市天河区天河路208号\"\n" +
                "      }\n" +
                "  },\n" +
                "  \"emphasis_keyword\": \"keyword1.DATA\"\n" +
                "}";

        //发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject(HttpUtils.sendJsonPost(requestUrl, json));
        return jsonObject;
    }


}
