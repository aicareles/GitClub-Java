package com.jerry.geekdaily.config;

import com.jerry.geekdaily.config.SystemConfig;

import java.io.IOException;

public class OSSConfig {

    private  String bucketUrl;  		//原图片服务器地址
    private  String baseUrl;  		//自定义解析后的图片服务器地址
    private  String endpoint;  		//连接区域地址
    private  String accessKeyId;  	//连接keyId
    private  String accessKeySecret;    //连接秘钥
    private  String bucketName;  	//需要存储的bucketName
    private  String picLocation;  	//图片保存路径

    public OSSConfig() {
        try {
            this.bucketUrl = SystemConfig.getConfigResource("bucketUrl");
            this.baseUrl = SystemConfig.getConfigResource("baseUrl");
            this.endpoint = SystemConfig.getConfigResource("endpoint");
            this.bucketName = SystemConfig.getConfigResource("bucketName");
            this.picLocation = SystemConfig.getConfigResource("picLocation");
            this.accessKeyId = SystemConfig.getConfigResource("accessKeyId");
            this.accessKeySecret = SystemConfig.getConfigResource("accessKeySecret");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBucketUrl() {
        return bucketUrl;
    }

    public void setBucketUrl(String bucketUrl) {
        this.bucketUrl = bucketUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public String getAccessKeyId() {
        return accessKeyId;
    }
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    public String getPicLocation() {
        return picLocation;
    }
    public void setPicLocation(String picLocation) {
        this.picLocation = picLocation;
    }

}
