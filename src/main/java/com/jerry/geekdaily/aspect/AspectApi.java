package com.jerry.geekdaily.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 装饰器模式
 */
public interface AspectApi {

    Object doHandlerAspect(ProceedingJoinPoint pjp, Method method)throws Throwable;

}
