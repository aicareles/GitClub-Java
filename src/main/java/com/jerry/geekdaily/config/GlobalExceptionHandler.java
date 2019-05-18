package com.jerry.geekdaily.config;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultCode;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.exception.ParamJsonException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//全局铺获异常类
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result resultError(Exception ex){
        return ResultUtils.error(String.valueOf(ex.getMessage()));
    }

    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    @ResponseBody
    public Result handle401(ShiroException e) {
        return ResultUtils.error(ResultCode.UNAUTHORIZED);
    }

    //在抛出参数异常时  会统一回调该方法
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = ParamJsonException.class)
    @ResponseBody
    public Result<String> handleParam(Exception e){
        if(e instanceof ParamJsonException){
            logger.info("参数错误："+e.getMessage());
            return ResultUtils.error(ResultCode.PARAM_ERROR);
        }
        return ResultUtils.error(ResultCode.SERVER_EXCEPTION);
    }
}
