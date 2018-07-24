//package com.jerry.geekdaily.interceptor;
//
//import com.jerry.geekdaily.domain.User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * 登录验证拦截
// *
// */
//@Controller
//@Component
//public class LoginInterceptor implements HandlerInterceptor {
//
//    private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        boolean flag = true;
//        User user = (User) request.getSession().getAttribute("user");
//        if(null == user){
//            logger.info("用户未登陆！");
//            response.sendRedirect("/");
//            flag = false;
//        }else {
//            flag = true;
//            logger.info("用户已登陆！");
//        }
//        return flag;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//
//    }
//}