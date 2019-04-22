package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Crash;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.dto.UpdateArticleDTO;
import com.jerry.geekdaily.repository.CrashRepository;
import com.jerry.geekdaily.service.UserService;
import com.jerry.geekdaily.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;

@Api(value = "CrashController", description = "上传APP错误信息相关接口")
@RestController
public class CrashController {

    private final static Logger logger = LoggerFactory.getLogger(CrashController.class);

    @Autowired
    CrashRepository crashRepository;

    @ApiOperation(value = "上传APP错误信息", notes = "上传APP错误信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone_system", value = "手机系统", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_brands", value = "手机品牌", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_model", value = "手机型号", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_system_version", value = "手机系统版本", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_package", value = "app包名", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_channel", value = "app渠道", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_version_name", value = "app版本名", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_version_code", value = "app版本号", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "exception_info", value = "异常崩溃日志信息", required = true ,dataType = "string"),
//            @ApiImplicitParam(name = "memory_info", value = "手机内存信息", required = false ,dataType = "string"),
//            @ApiImplicitParam(name = "device_info", value = "手机设备信息", required = false ,dataType = "string"),
//            @ApiImplicitParam(name = "system_info", value = "手机系统信息", required = false ,dataType = "string"),
//            @ApiImplicitParam(name = "secure_info", value = "安全信息", required = false ,dataType = "string")
    })
    @PostMapping(value = "/uploadCrashInfo")
    public Result<Crash> uploadCrashInfo(@Valid Crash crashInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtils.ok(crashRepository.saveAndFlush(crashInfo));
    }
}
