//package com.jerry.geekdaily.domain;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import lombok.Data;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import javax.persistence.Entity;
//import javax.persistence.EntityListeners;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.util.Date;
//
//@Entity
//@Table(name="goods")
////@EntityListeners(AuditingEntityListener.class)
//public class Goods {
//    @Id
//    private Long goodsId;
//
//    private String goodsName;
//
//    private Long goodsType;
//
//    //文章创建日期
////    @CreatedDate
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    private Date date;
//
//    public Long getGoodsId() {
//        return goodsId;
//    }
//
//    public void setGoodsId(Long goodsId) {
//        this.goodsId = goodsId;
//    }
//
//    public String getGoodsName() {
//        return goodsName;
//    }
//
//    public void setGoodsName(String goodsName) {
//        this.goodsName = goodsName;
//    }
//
//    public Long getGoodsType() {
//        return goodsType;
//    }
//
//    public void setGoodsType(Long goodsType) {
//        this.goodsType = goodsType;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    @Override
//    public String toString() {
//        return "Goods{" +
//                "goodsId=" + goodsId +
//                ", goodsName='" + goodsName + '\'' +
//                ", goodsType=" + goodsType +
//                ", date=" + date +
//                '}';
//    }
//}
