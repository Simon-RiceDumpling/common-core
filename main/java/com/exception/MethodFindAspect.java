package com.exception;


import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author renBo
 * @ClassName: MethodFindAspect
 * @Description: 方法属性保存切点
 * @date 2023-04-19 1:34
 */
//@Aspect
//@Service
@Slf4j
public class MethodFindAspect {

    public static InheritableThreadLocal<String> methodName = new InheritableThreadLocal<>();

    /**
     * @return void
     * @Author renBo
     * @Description 切入点 带有ApiOperation方法的控制层
     * @Date 9:00 2023-04-19
     * @Param []
     */
    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    public void controller() {

    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionAspect() {
    }


    /**
     * @return void
     * @Author renBo
     * @Description 切入逻辑 带有ApiOperation方法的控制层
     * @Date 9:00 2023-04-19
     * @Param []
     */
    @Around("controller()")
    public Object doSaveMethodName(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            Optional.of((MethodSignature) pjp.getSignature())
                    .map(MethodSignature::getMethod)
                    .filter(method -> method.isAnnotationPresent(ApiOperation.class))
                    .map(method -> method.getAnnotation(ApiOperation.class))
                    .ifPresent(apiOperation -> methodName.set(StringUtil.isNotEmpty(apiOperation.value()) ? apiOperation.value() : apiOperation.notes()));
            throw e;
        }
    }

    /**
     * 异常前置处理 打印调用、异常 日志
     *
     * @param joinPoint
     */
    @Before("exceptionAspect()")
    public void exceptionBefore(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        log.error("接口【{}】访问出现异常【{}】", request.getRequestURL(), ((Exception) joinPoint.getArgs()[0]).getMessage());
    }

    /**
     * 异常后置处理 移除线程副本
     */
    @AfterReturning("exceptionAspect()")
    public void exceptionAfterReturning() {
        methodName.remove();
    }

}
