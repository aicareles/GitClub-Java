package com.jerry.geekdaily.enums;

public enum AdminEnum {

    NOT_ADMIN(0),//普通用户
    ADMIN(1)//管理员
    ;

    private Integer admin_status;

    AdminEnum(Integer admin_status){
        this.admin_status = admin_status;
    }

    public Integer getAdmin_status() {
        return admin_status;
    }

    public void setAdmin_status(Integer admin_status) {
        this.admin_status = admin_status;
    }
}
