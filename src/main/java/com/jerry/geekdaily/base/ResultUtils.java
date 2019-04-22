package com.jerry.geekdaily.base;

public class ResultUtils {

    //查询成功
    public static <T>Result<T> ok(Object object){
        return new Result(object);
    }

    //查询失败
    public static <T>Result<T> error(ResultCode resultCode){
        Result result = new Result(resultCode.getCode(), resultCode.getMsg());
        result.setData("");
        return result;
    }

    //查询失败  自定义错误信息
    public static <T>Result<T> error(String msg){
        Result result = new Result(-1, msg);
        result.setData("");
        return result;
    }
}
