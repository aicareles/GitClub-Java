package com.jerry.geekdaily.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录验证拦截
 *
 */
@Controller
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String userId = "";
        //从cookie中去取
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for (int i=0; i < cookies.length; i++){
                if(cookies[i].getName().equals("geek_userId")){
                    userId = cookies[i].getValue();
                }
            }
        }
        if(StringUtils.isEmpty(userId)){
            logger.info("用户未登陆！");
            response.sendRedirect(request.getContextPath()+"/login");
            return false;
        }else {
            logger.info("用户已登陆！"+userId);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}