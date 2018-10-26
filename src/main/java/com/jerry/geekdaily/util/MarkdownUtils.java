package com.jerry.geekdaily.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MarkdownUtils {
    private final static String BASE_URL = "https://raw.githubusercontent.com/";
    private final static String SUFFIX = "/master/README.md";
    private final static String GITHUB_URL = "https://github.com/";

    //根据github地址获取对应的仓库名  如通过"https://github.com/Alex-Jerry/Android-BLE"  获取：Alex-Jerry/Android-BLE"
    public static String getRepositoryName(String link){
        String repositoryName = "";
        if(link.contains(GITHUB_URL)){
            repositoryName = link.replaceAll(" ","")
                    .substring(GITHUB_URL.length());//如：Alex-Jerry/Android-BLE
        }
        return repositoryName;
    }

    //根据md的网络地址获取html文本
    public static String getMdContent(String link) {//如："https://github.com/Alex-Jerry/Android-BLE"
        //最终拼接成：https://raw.githubusercontent.com/Alex-Jerry/Android-BLE/master/README.md
        int HttpResult; // 服务器返回的状态
        String md_content = "";
        if(link.contains(GITHUB_URL)){//github地址  开始拼接
            String github_name = link.replaceAll(" ","").substring(GITHUB_URL.length());//如：Alex-Jerry/Android-BLE
            try
            {
                URL url =new URL(BASE_URL+github_name+SUFFIX); // 创建URL
                URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
                urlconn.connect();
                HttpURLConnection httpconn =(HttpURLConnection)urlconn;
                HttpResult = httpconn.getResponseCode();
                if(HttpResult != HttpURLConnection.HTTP_OK) {
                    System.out.print("无法连接到");
                } else {
                    int filesize = urlconn.getContentLength(); // 取数据长度
                    InputStreamReader isReader = new InputStreamReader(urlconn.getInputStream(),"UTF-8");
                    BufferedReader reader = new BufferedReader(isReader);
                    StringBuffer buffer = new StringBuffer();
                    String line; // 用来保存每行读取的内容
                    line = reader.readLine(); // 读取第一行
                    while (line != null) { // 如果 line 为空说明读完了
                        buffer.append(line); // 将读到的内容添加到 buffer 中
                        buffer.append("\n"); // 添加换行符
                        line = reader.readLine(); // 读取下一行
                    }
                    md_content = buffer.toString();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return  md_content;
    }
}
