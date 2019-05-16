package com.jerry.geekdaily.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
        public static void addCookie (String userId, String token, HttpServletResponse response, HttpServletRequest request){
            if (StringUtils.isNotBlank(userId)) {
                //创建cookie
                Cookie idCookie = new Cookie("geek_userId", userId);
                Cookie nameCookie = new Cookie("geek_token", token);
                System.out.println("geek_userId"+userId);
                System.out.println("geek_token"+token);
                idCookie.setPath(request.getContextPath() + "/");//设置cookie路径
                nameCookie.setPath(request.getContextPath() + "/");//设置cookie路径
                //设置cookie保存的时间 单位：秒
                idCookie.setMaxAge(1 * 24 * 60 * 60);
                nameCookie.setMaxAge(1 * 24 * 60 * 60);
                //将cookie添加到响应
                response.addCookie(idCookie);
                response.addCookie(nameCookie);
            }
        }

        public static void removeCookie (HttpServletResponse response, HttpServletRequest request){
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length > 0){
                for (int i=0; i < cookies.length; i++){
                    if(cookies[i].getName().equals("geek_userId")){
                        //JAVA代码删除cookie的原理是新建一个同名，值为null的cookie，用这个cookie将request域里面的指定cookie覆盖；
                        Cookie nameCookie = new Cookie("geek_userId", "");
                        nameCookie.setPath(request.getContextPath() + "/");
                        //设置立即超时
                        nameCookie.setMaxAge(0);
                        //将cookie添加到响应
                        response.addCookie(nameCookie);
                    }
                }
            }
        }
}
