package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Crash;
import com.jerry.geekdaily.domain.StatusInfo;
import com.jerry.geekdaily.repository.AppStatusInfoRepository;
import com.jerry.geekdaily.repository.CrashRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "AppStatusInfoController", description = "上传操作流程信息相关接口")
@RestController
public class AppStatusInfoController {

    private final static Logger logger = LoggerFactory.getLogger(AppStatusInfoController.class);

    @Autowired
    AppStatusInfoRepository appStatusInfoRepository;

    @ApiOperation(value = "上传操作流程信息", notes = "上传操作流程信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone_system", value = "手机系统", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_brands", value = "手机品牌", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_model", value = "手机型号", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "phone_system_version", value = "手机系统版本", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_package", value = "app包名", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_version_name", value = "app版本名", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "app_version_code", value = "app版本号", required = false ,dataType = "string"),
            @ApiImplicitParam(name = "bluetooth_status", value = "蓝牙开关状态", required = false ,dataType = "int"),
            @ApiImplicitParam(name = "wireless_support_status", value = "是否支持2.4G蓝牙", required = false ,dataType = "int"),
            @ApiImplicitParam(name = "gps_status", value = "gps开关状态", required = false ,dataType = "int"),
            @ApiImplicitParam(name = "permission_status", value = "蓝牙权限状态", required = false ,dataType = "int")
    })
    @PostMapping(value = "/uploadStatusInfo")
    public Result<StatusInfo> uploadStatusInfo(@Valid StatusInfo statusInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtils.ok(appStatusInfoRepository.saveAndFlush(statusInfo));
    }
}
