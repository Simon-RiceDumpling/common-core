package com.aspect;

import com.annotation.SysPrintlnLog;
import com.utils.IpUtils;
import io.swagger.models.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    public static ThreadLocal<String> t_id = new InheritableThreadLocal<>();






    /**
    *  拦截方法上又拦截到方法所属类上的注解
    */
    @Around("@annotation(sysPrintlnLog)||@within(sysPrintlnLog)")
    public Object logAround(ProceedingJoinPoint joinPoint, SysPrintlnLog sysPrintlnLog) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        t_id.set(UUID.randomUUID().toString());
        if (requestAttributes != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            // Log request details
            log.info("========== Incoming Request  ========== Start ==========");
            log.info("【t_id: {}】HTTP Method           :【{}】", t_id.get(), request.getMethod());
            log.info("【t_id: {}】HTTP RequestTime      :【{}】", t_id.get(), LocalDateTime.now());
           // if (signature.getMethod().isAnnotationPresent(Operation.class)) {
           //     log.info("【t_id: {}】HTTP Method Describe  :【{}】", t_id.get(), signature.getMethod().getAnnotation(Operation.class).description());
           // }6
            log.info("【t_id: {}】Request URL           :【{}】", t_id.get(), request.getRequestURL());
            log.info("【t_id: {}】Request URI           :【{}】", t_id.get(), request.getRequestURI());
            log.info("【t_id: {}】Remote Address        :【{}】", t_id.get(), request.getRemoteAddr());
            log.info("【t_id: {}】Class Method          :【{}.{}】", t_id.get(), joinPoint.getTarget().getClass().getName(), signature.getName());
            log.info("【t_id: {}】Request Headers       :【{}】", t_id.get(), getHeadersInfo(request));
            log.info("【t_id: {}】Request Params        :【{}】", t_id.get(), request.getParameterMap());
            log.info("【t_id: {}】Request IP            :【{}】", t_id.get(), IpUtils.getIpAddr(request));
            //log.info("Request Body          :【{}】", Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", ")));
           // log.info("【t_id: {}】Request Body          :【{}】", t_id.get(), ExceptionNoteAspect.requestBody.get());
            log.info("========== Incoming Request  ========== End ============");
        }
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        // Log response details
        log.info("========== Outgoing Response ========== Start =====");
        log.info("Class Method     : 【{}】.【{}】", className, methodName);
        log.info("Response         : 【{}】", result);
        log.info("Time Taken       : 【{}】 ms", (endTime - startTime));
        log.info("========== Outgoing Response ========== End =====");
        return result;
    }

    private String getHeadersInfo(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining("; "));
    }
}
