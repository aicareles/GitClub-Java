package com.jerry.geekdaily.enums;

public enum StarStatusEnum {

    IDLE_STAR_STATUS(0),//0取消点赞/反赞(闲置状态)
    STAR_STATUS(1),//点赞状态
    UN_STAR_STATUS(2)//2反赞状态
    ;

    private Integer star_status;

    StarStatusEnum(Integer star_status){
        this.star_status = star_status;
    }

    public Integer getStar_status() {
        return star_status;
    }

    public void setStar_status(Integer star_status) {
        this.star_status = star_status;
    }
}
