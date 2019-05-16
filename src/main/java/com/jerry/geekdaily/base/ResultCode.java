package com.jerry.geekdaily.base;

public enum ResultCode {

    /**
     * 服务器异常
     */
    SERVER_EXCEPTION(1001, "服务器状态异常"),
    /**
     * 成功
     */
    SUCCESS(0, "success"),
    /**
     * 失败
     */
    ERROR(1000, "操作失败"),
    /**
     * 失败
     */
    PARAM_ERROR(1003, "参数错误"),

    /**
     * 用户名或密码错误
     */
    INVALID_USERNAME_PASSWORD(1004, "用户名或密码错误"),
    /**
     *
     */
    INVALID_RE_PASSWORD(1005, "两次输入密码不一致"),
    /**
     * 用户名或密码错误
     */
    INVALID_PASSWORD(1006, "旧密码错误"),
    /**
     * 用户名重复
     */
    USERNAME_ALREADY_IN(1007, "用户已存在"),
    /**
     * 用户不存在
     */
    INVALID_USER(1008, "用户不存在"),
    /**
     * 角色不存在
     */
    INVALID_ROLE(1009, "角色不存在"),

    /**
     * 参数错误-已存在
     */
    INVALID_PARAM_EXIST(1010, "请求参数已存在"),
    /**
     * 参数错误
     */
    INVALID_PARAM_EMPTY(1011, "请求参数为空"),

    /**
     * 未查询到相关信息
     */
    NO_FIND_THINGS(1012, "未查询到相关信息"),

    /**
     * 没有修改文章权限
     */
    NO_EDIT_PERMITION(1014, "没有修改文章权限"),

    /**
     * 没有审核文章权限
     */
    NO_REVIEW_PERMITION(1015, "没有审核文章权限"),

    /**
     * 当天文章上传次数已达上限
     */
    UPLOAD_LIMIT(1016, "当天文章上传次数已达上限!"),

    /**
     * 未找到相应文章
     */
    NO_FIND_ARTICLE(1017, "未找到相应文章"),

    /**
     * 上传图片或文章链接格式错误
     */
    UPLOAD_LINK_ERROR(1018, "上传图片或文章链接格式错误"),

    /**
     * 上传内容不能为空
     */
    UPLOAD_FILE_EMPTY(1019, "上传图片或文章链接格式错误"),

    /**
     * 上传内容最大不能超过10M
     */
    UPLOAD_FILE_LIMIT(1020, "上传内容最大不能超过10M"),

    /**
     * 未登录/token过期
     */
    TOKEN_EXPIRED(401, "token验证失败,请重新登录!"),

    /**
     * 没有权限
     */
    UNAUTHORIZED(403, "您没有访问权限");

    public int code;
    public String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(String result) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
