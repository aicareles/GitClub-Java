package com.jerry.geekdaily.domain;


import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class StatusInfo implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;
    private String phone_system;//手机系统 Android iOS
    private String phone_brands;//手机品牌  三星
    private String phone_model;//手机型号  SM-G9250
    private String phone_system_version;//手机系统版本  android9.0
    private String app_package;//app包名
    private String app_version_name;//app版本名
    private String app_version_code;//app版本号
    private int bluetooth_status;//0关  1开
    private int wireless_support_status;//0不支持  1支持
    private int gps_status;//0关  1开
    private int permission_status;//0未授权   1已授权

    //上传时间
    @CreatedDate
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone_system() {
        return phone_system;
    }

    public void setPhone_system(String phone_system) {
        this.phone_system = phone_system;
    }

    public String getPhone_brands() {
        return phone_brands;
    }

    public void setPhone_brands(String phone_brands) {
        this.phone_brands = phone_brands;
    }

    public String getPhone_model() {
        return phone_model;
    }

    public void setPhone_model(String phone_model) {
        this.phone_model = phone_model;
    }

    public String getPhone_system_version() {
        return phone_system_version;
    }

    public void setPhone_system_version(String phone_system_version) {
        this.phone_system_version = phone_system_version;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

    public String getApp_version_name() {
        return app_version_name;
    }

    public void setApp_version_name(String app_version_name) {
        this.app_version_name = app_version_name;
    }

    public String getApp_version_code() {
        return app_version_code;
    }

    public void setApp_version_code(String app_version_code) {
        this.app_version_code = app_version_code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBluetooth_status() {
        return bluetooth_status;
    }

    public void setBluetooth_status(int bluetooth_status) {
        this.bluetooth_status = bluetooth_status;
    }

    public int getWireless_support_status() {
        return wireless_support_status;
    }

    public void setWireless_support_status(int wireless_support_status) {
        this.wireless_support_status = wireless_support_status;
    }

    public int getGps_status() {
        return gps_status;
    }

    public void setGps_status(int gps_status) {
        this.gps_status = gps_status;
    }

    public int getPermission_status() {
        return permission_status;
    }

    public void setPermission_status(int permission_status) {
        this.permission_status = permission_status;
    }

    @Override
    public String toString() {
        return "StatusInfo{" +
                "id=" + id +
                ", phone_system='" + phone_system + '\'' +
                ", phone_brands='" + phone_brands + '\'' +
                ", phone_model='" + phone_model + '\'' +
                ", phone_system_version='" + phone_system_version + '\'' +
                ", app_package='" + app_package + '\'' +
                ", app_version_name='" + app_version_name + '\'' +
                ", app_version_code='" + app_version_code + '\'' +
                ", bluetooth_status=" + bluetooth_status +
                ", wireless_support_status=" + wireless_support_status +
                ", gps_status=" + gps_status +
                ", permission_status=" + permission_status +
                ", date=" + date +
                '}';
    }
}
