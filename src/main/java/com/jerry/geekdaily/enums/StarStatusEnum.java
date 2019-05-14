package com.jerry.geekdaily.enums;

public enum StarStatusEnum {

    IDLE_STAR_STATUS(0),//0取消点赞/反赞(闲置状态)
    STAR_STATUS(1),//点赞状态
//    UN_STAR_STATUS(2)//2反赞状态
    ;

    private Integer starStatus;

    StarStatusEnum(Integer starStatus){
        this.starStatus = starStatus;
    }

    public Integer getStarStatus() {
        return starStatus;
    }

    public void setStarStatus(Integer starStatus) {
        this.starStatus = starStatus;
    }
}
