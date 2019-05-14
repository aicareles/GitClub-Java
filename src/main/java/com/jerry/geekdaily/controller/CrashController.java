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

    @PostMapping(value = "/uploadCrashInfo")
    public Result<Crash> uploadCrashInfo(@Valid Crash crashInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResultUtils.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtils.ok(crashRepository.saveAndFlush(crashInfo));
    }
}
